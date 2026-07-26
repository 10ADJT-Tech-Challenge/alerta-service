package com.adjt.alertaservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Envia um e-mail de alerta para a equipa médica quando um sinal vital crítico é detectado.
     * Realiza envio assíncrono para evitar bloqueio do consumo da fila do sistema.
     *
     * @param destinatario O endereço de e-mail do destinatário (equipa médica).
     * @param identificadorPaciente O nome do paciente que apresentou o sinal vital crítico.
     * @param sinalVital O tipo de sinal vital medido (ex: "Frequência Cardíaca").
     * @param valorMedido O valor medido do sinal vital que está fora dos limites normais.
     **/
    @Async
    public void enviarAlertaRisco(String destinatario, String identificadorPaciente, String sinalVital, Float valorMedido) {
        try {
            SimpleMailMessage mensagem = new SimpleMailMessage();

            mensagem.setFrom("Alerta Saúde SUS <o.teu.email.do.projeto@gmail.com>");
            mensagem.setTo(destinatario);
            mensagem.setSubject("⚠️ ALERTA CLÍNICO: Alteração Crítica - Paciente " + identificadorPaciente);

            String corpoEmail = String.format(
                    """
                            Atenção Equipa Médica,
                            
                            Foi detectada uma medição crítica para o paciente %s.
                            
                            Parâmetro: %s
                            Valor Registado: %.2f
                            
                            Por favor, verifiquem a situação clínica do paciente com urgência.
                            
                            Sistema de Monitorização Contínua - Módulo Preditivo""",
                    identificadorPaciente, sinalVital, valorMedido
            );

            mensagem.setText(corpoEmail);

            mailSender.send(mensagem);
            log.info("E-mail de alerta enviado com sucesso para: {}", destinatario);

        } catch (Exception e) {
            log.error("Falha ao enviar e-mail de alerta: {}", e.getMessage());
        }
    }
}