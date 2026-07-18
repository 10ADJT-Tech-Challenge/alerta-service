package com.adjt.alertaservice.messaging.consumer;

import com.adjt.alertaservice.dto.MedicaoRealizadaEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import com.adjt.alertaservice.configuration.RabbitMQConfig;

@Slf4j
@Service
public class MedicaoConsumer {

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void consumirMedicao(MedicaoRealizadaEvent evento) {
        log.info("Recebida nova medição para avaliação. ID medião: {}", evento.id());

        // PASSO 1: Fazer um GET para o acompanhamento-service para pegar os limites
        // Ex: GET /tratamentos/paciente/{idPaciente}/eventos

        // PASSO 2: Avaliar se 'evento.valor()' está fora dos limites de segurança

        // PASSO 3: Se for grave, disparar o log de notificação!
        log.warn("ALERTA GRAVE! A medição {} está com parâmetros fora do normal.", evento.id());
    }
}
