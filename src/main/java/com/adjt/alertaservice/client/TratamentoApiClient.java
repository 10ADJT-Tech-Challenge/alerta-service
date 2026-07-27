package com.adjt.alertaservice.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.UUID;

@Service
public class TratamentoApiClient {

    private final RestClient restClient;

    public TratamentoApiClient(
            @Value("${client.tratamento-api.base-url}") String baseUrl
    ) {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .requestInterceptor((request, body, execution) -> {
                    // ISSO VAI IMPRIMIR A URL EXATA ANTES DE ENVIAR
                    System.out.println("DEBUG RESTCLIENT - Método: " + request.getMethod());
                    System.out.println("DEBUG RESTCLIENT - URL: " + request.getURI());

                    return execution.execute(request, body);
                })
                .build();
    }

    public EventoResponse buscarEventoPorId(UUID id) {
        return restClient.get()
                .uri("eventos/{id}", id)
                .retrieve()
                .body(EventoResponse.class);
    }
}