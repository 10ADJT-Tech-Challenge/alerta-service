package com.adjt.alertaservice.client;

import java.util.UUID;

public record EventoResponse(UUID id, String nome, Float valor_ref_min, Float valor_ref_max) {
}
