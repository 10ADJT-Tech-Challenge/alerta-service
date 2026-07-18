package com.adjt.alertaservice.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record MedicaoRealizadaEvent(UUID id, String cpfPaciente, UUID idEvento, Float valorMedicao,
                                    OffsetDateTime dataHora, String origemRegistro) {
}
