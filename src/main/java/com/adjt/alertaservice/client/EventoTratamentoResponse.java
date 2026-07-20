package com.adjt.alertaservice.client;

import java.util.UUID;

public record EventoTratamentoResponse(EventoResponse evento, Integer frequencia_horas, UUID id_tratamento_evento) {
}