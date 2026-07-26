package org.example.client;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.List;
import org.example.dto.YandexSpellerError;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.client.JdkClientHttpRequestFactory;
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
    HttpClient httpClient =
        HttpClient.newBuilder()
            .connectTimeout(connectTimeout)
            .build();
    JdkClientHttpRequestFactory requestFactory =
        new JdkClientHttpRequestFactory(httpClient);
    requestFactory.setReadTimeout(readTimeout);

    this.restClient = restClientBuilder.requestFactory(requestFactory).build();
    this.endpoint = endpoint;
  }

  public List<List<YandexSpellerError>> checkTexts(
      List<String> texts, String language, int options) {
    MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
    texts.forEach(text -> parameters.add("text", text));
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
