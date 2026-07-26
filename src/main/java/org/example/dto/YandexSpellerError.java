package org.example.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record YandexSpellerError(
    Integer code,
    @JsonProperty("pos") Integer position,
    @JsonProperty("len") Integer length,
    String word,
    @JsonProperty("s") List<String> suggestions) {}
