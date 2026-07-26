package org.example.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.example.entity.TaskStatus;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record TaskResponse(
    TaskStatus status,
    String correctedText,
    String errorMessage) {}
