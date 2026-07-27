package org.example.client;

import java.time.Duration;
import java.util.List;
import org.example.dto.YandexSpellerError;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.http.client.ClientHttpRequestFactoryBuilder;
import org.springframework.boot.http.client.ClientHttpRequestFactorySettings;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Component
public class YandexSpellerClient {

  private static final ParameterizedTypeReference<List<List<YandexSpellerError>>> RESPONSE_TYPE =
      new ParameterizedTypeReference<>() {};

  private final RestClient restClient;
  private final String endpoint;

  public YandexSpellerClient(
      RestClient.Builder restClientBuilder,
      @Value("${yandex.speller.endpoint}") String endpoint,
      @Value("${yandex.speller.connect-timeout}") Duration connectTimeout,
      @Value("${yandex.speller.read-timeout}") Duration readTimeout) {
    ClientHttpRequestFactorySettings settings =
        ClientHttpRequestFactorySettings.defaults().withTimeouts(connectTimeout, readTimeout);

    this.restClient =
        restClientBuilder
            .requestFactory(ClientHttpRequestFactoryBuilder.jdk().build(settings))
            .build();
    this.endpoint = endpoint;
  }

  public List<List<YandexSpellerError>> checkTexts(
      List<String> texts, String language, int options) {
    MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
    parameters.addAll("text", texts);
    parameters.add("lang", language);
    parameters.add("options", Integer.toString(options));

    return restClient
        .post()
        .uri(endpoint)
        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .body(parameters)
        .retrieve()
        .body(RESPONSE_TYPE);
  }
}
