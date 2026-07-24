package org.example.task.api;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.example.task.validation.ValidCorrectionText;

public record CreateTaskRequest(
    @NotNull @ValidCorrectionText String text,
    @NotNull @Pattern(regexp = "en|ru") String language) {}
