package br.com.grupo.ClassInsight.service;

import br.com.grupo.ClassInsight.model.Avaliacao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Async
    public void enviarNotificacaoCriticaAsync(Avaliacao avaliacao) {
        try {
            enviarNotificacaoCritica(avaliacao);
            log.info("Notificação crítica enviada com sucesso para avaliação ID: {}", avaliacao.getId());
        } catch (Exception e) {
            log.error("Erro ao enviar notificação crítica para avaliação ID: {}", avaliacao.getId(), e);
        }
    }

    public void enviarNotificacaoCritica(Avaliacao avaliacao) throws MessagingException {
        log.info("Enviando notificação crítica para avaliação ID: {}", avaliacao.getId());

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom("noreply@classinsight.com");
        helper.setTo("admin@classinsight.com");
        helper.setSubject("🚨 Feedback CRÍTICO Recebido - ID: " + avaliacao.getId());

        Context context = new Context();
        context.setVariable("avaliacao", avaliacao);
        context.setVariable("dataFormatada", avaliacao.getDataEnvio().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));

        String htmlContent = templateEngine.process("email/notificacao-critica", context);
        helper.setText(htmlContent, true);

        mailSender.send(message);
        log.info("E-mail de notificação crítica enviado com sucesso");
    }

    public void enviarRelatorioSemanal(String htmlRelatorio) throws MessagingException {
        log.info("Enviando relatório semanal por e-mail");

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom("noreply@classinsight.com");
        helper.setTo("admin@classinsight.com");
        helper.setSubject("📊 Relatório Semanal de Feedbacks");

        helper.setText(htmlRelatorio, true);

        mailSender.send(message);
        log.info("Relatório semanal enviado com sucesso por e-mail");
    }

    public void enviarNotificacaoManual(Avaliacao avaliacao) throws MessagingException {
        log.info("Enviando notificação manual para avaliação ID: {}", avaliacao.getId());

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("noreply@classinsight.com");
        message.setTo("admin@classinsight.com");
        message.setSubject("Notificação Manual - Feedback ID: " + avaliacao.getId());
        message.setText(buildEmailContent(avaliacao));

        mailSender.send(message);
        log.info("Notificação manual enviada com sucesso");
    }

    private String buildEmailContent(Avaliacao avaliacao) {
        StringBuilder content = new StringBuilder();
        content.append("Olá Administrador,\n\n");
        content.append("Uma notificação manual foi solicitada para o seguinte feedback:\n\n");
        content.append("ID: ").append(avaliacao.getId()).append("\n");
        content.append("Descrição: ").append(avaliacao.getDescricao()).append("\n");
        content.append("Nota: ").append(avaliacao.getNota()).append("\n");
        content.append("Urgência: ").append(avaliacao.getUrgencia()).append("\n");
        content.append("Data de Envio: ").append(avaliacao.getDataEnvio().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))).append("\n");
        content.append("Notificação Enviada: ").append(avaliacao.getNotificacaoEnviada() ? "Sim" : "Não").append("\n\n");
        content.append("Atenciosamente,\n");
        content.append("Sistema ClassInsight");

        return content.toString();
    }

    @Async
    public void enviarMultiplosEmails(List<String> destinatarios, String assunto, String conteudo) {
        for (String destinatario : destinatarios) {
            try {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setFrom("noreply@classinsight.com");
                message.setTo(destinatario);
                message.setSubject(assunto);
                message.setText(conteudo);

                mailSender.send(message);
                log.info("E-mail enviado com sucesso para: {}", destinatario);
            } catch (Exception e) {
                log.error("Erro ao enviar e-mail para: {}", destinatario, e);
            }
        }
    }
}
