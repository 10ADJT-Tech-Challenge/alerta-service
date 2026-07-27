package com.adjt.alertaservice.messaging.consumer;

import com.adjt.alertaservice.client.EventoResponse;
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

        log.info("Buscando parâmetros de referência para o evento com ID: {}", evento.idEvento());
        EventoResponse eventoResponse = tratamentoApiClient.buscarEventoPorId(evento.idEvento());
        log.info("Referencia do evento encontrada: {}", eventoResponse);

        if (eventoResponse == null) {
            log.warn("Evento com ID {} não encontrado. Ignorando medição.", evento.idEvento());
            return;
        }

        if (!isParametrosAlterados(evento, eventoResponse)) {
            log.info("A medição {} está com parâmetros normal.", evento.id());
            return;
        }

        log.warn("ALERTA GRAVE! A medição {} está com parâmetros fora do normal.", evento.id());
        emailService.enviarAlertaRisco(
                emailDestinatario,
                evento.cpfPaciente(),
                eventoResponse.nome(),
                evento.valorMedicao()
        );
    }

    private static boolean isParametrosAlterados(MedicaoRealizadaEvent evento, EventoResponse eventoResponse) {
        return evento.valorMedicao() > eventoResponse.valor_ref_max()
                || evento.valorMedicao() < eventoResponse.valor_ref_min();
    }
}
