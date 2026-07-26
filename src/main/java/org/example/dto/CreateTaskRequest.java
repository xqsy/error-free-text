package org.example.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.example.validation.ValidCorrectionText;

public record CreateTaskRequest(
    @NotNull @ValidCorrectionText String text,
    @NotNull @Pattern(regexp = "en|ru") String language) {}
