package org.example.controller;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;
import org.example.exception.GlobalExceptionHandler;
import org.example.exception.TaskNotFoundException;
import org.example.service.CorrectionTaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@ExtendWith(MockitoExtension.class)
class CorrectionTaskControllerTest {

  @Mock private CorrectionTaskService taskService;

  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
    validator.afterPropertiesSet();
    mockMvc =
        MockMvcBuilders.standaloneSetup(new CorrectionTaskController(taskService))
            .setControllerAdvice(new GlobalExceptionHandler())
            .setValidator(validator)
            .build();
  }

  @Test
  void invalidRequestReturnsUnifiedErrorAndDoesNotCreateTask() throws Exception {
    mockMvc
        .perform(
            post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"text":"valid text","language":"de"}
                    """))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorMessage").value("Request validation failed"))
        .andExpect(jsonPath("$.errorCode").value(40001))
        .andExpect(jsonPath("$.timestamp").isNotEmpty())
        .andExpect(jsonPath("$.path").value("/tasks"));

    verifyNoInteractions(taskService);
  }

  @Test
  void missingTaskReturnsUnifiedNotFoundError() throws Exception {
    UUID taskId = UUID.randomUUID();
    when(taskService.getTask(taskId)).thenThrow(new TaskNotFoundException(taskId));

    mockMvc
        .perform(get("/tasks/{id}", taskId))
        .andExpect(status().isNotFound())
        .andExpect(
            jsonPath("$.errorMessage")
                .value("Task with id: " + taskId + " not found"))
        .andExpect(jsonPath("$.errorCode").value(40401))
        .andExpect(jsonPath("$.timestamp").isNotEmpty())
        .andExpect(jsonPath("$.path").value("/tasks/" + taskId));
  }

  @Test
  void unexpectedFailureReturnsUnifiedInternalError() throws Exception {
    UUID taskId = UUID.randomUUID();
    when(taskService.getTask(taskId)).thenThrow(new RuntimeException("database unavailable"));

    mockMvc
        .perform(get("/tasks/{id}", taskId))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.errorMessage").value("Unexpected internal error"))
        .andExpect(jsonPath("$.errorCode").value(50001))
        .andExpect(jsonPath("$.timestamp").isNotEmpty())
        .andExpect(jsonPath("$.path").value("/tasks/" + taskId));
  }
}
