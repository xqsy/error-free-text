package org.example.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record YandexSpellerError(
    @JsonProperty("pos") Integer position,
    @JsonProperty("len") Integer length,
    @JsonProperty("s") List<String> suggestions) {}
