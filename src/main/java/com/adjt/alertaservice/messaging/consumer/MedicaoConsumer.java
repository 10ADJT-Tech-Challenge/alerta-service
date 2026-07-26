package com.adjt.alertaservice.messaging.consumer;

import com.adjt.alertaservice.client.EventoTratamentoResponse;
import com.adjt.alertaservice.client.TratamentoApiClient;
import com.adjt.alertaservice.dto.MedicaoRealizadaEvent;
import com.adjt.alertaservice.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.adjt.alertaservice.configuration.RabbitMQConfig;

@Slf4j
@Service
@RequiredArgsConstructor
public class MedicaoConsumer {

    private final TratamentoApiClient tratamentoApiClient;
    private final EmailService emailService;

    @Value("${alerta.email.destinatario}")
    private String emailDestinatario;


    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void consumirMedicao(MedicaoRealizadaEvent evento) {
        log.info("Recebida nova medição para avaliação. ID medião: {}", evento.id());

        log.info("Buscando parâmetros referência para tratamento evento com ID: {}", evento.idEvento());
        EventoTratamentoResponse eventoTratamentoResponse = tratamentoApiClient.buscarTratamentosEventoPorId(evento.idEvento());
        log.info("Referencia do evento encontrado: {}", eventoTratamentoResponse);

        if (eventoTratamentoResponse == null || eventoTratamentoResponse.evento() == null) {
            log.warn("Evento de tratamento com ID {} não encontrado. Ignorando medição.", evento.idEvento());
            return;
        }

        if (!isParametrosAlterados(evento, eventoTratamentoResponse)) {
            log.info("A medição {} está com parâmetros normal.", evento.id());
            return;
        }

        log.warn("ALERTA GRAVE! A medição {} está com parâmetros fora do normal.", evento.id());
        emailService.enviarAlertaRisco(
                emailDestinatario,
                evento.cpfPaciente(),
                eventoTratamentoResponse.evento().nome(),
                evento.valorMedicao()
        );
    }

    private static boolean isParametrosAlterados(MedicaoRealizadaEvent evento, EventoTratamentoResponse eventoTratamentoResponse) {
        return evento.valorMedicao() > eventoTratamentoResponse.evento().valor_ref_max()
                || evento.valorMedicao() < eventoTratamentoResponse.evento().valor_ref_min();
    }
}
