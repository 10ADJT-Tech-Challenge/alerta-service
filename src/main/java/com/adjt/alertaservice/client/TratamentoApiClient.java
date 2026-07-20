package com.adjt.alertaservice.client;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.UUID;

@Service
public class TratamentoApiClient {

    private final RestClient restClient;

    public TratamentoApiClient() {
        this.restClient = RestClient.builder()
                .baseUrl("http://localhost:8080/api/v1/") // Substitua pela properti da URL base real
                .requestInterceptor((request, body, execution) -> {
                    // ISSO VAI IMPRIMIR A URL EXATA ANTES DE ENVIAR
                    System.out.println("DEBUG RESTCLIENT - Método: " + request.getMethod());
                    System.out.println("DEBUG RESTCLIENT - URL: " + request.getURI());

                    return execution.execute(request, body);
                })
                .build();
    }

    public EventoTratamentoResponse buscarTratamentosEventoPorId(UUID id) {
        return restClient.get()
                .uri("tratamentos/tratamento-evento/{id}", id)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }
}