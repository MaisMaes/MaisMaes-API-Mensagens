package com.maismaes.mensagens.service;

import com.maismaes.mensagens.dto.NotificacaoNovoParticipanteGrupoRequest;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificacaoNovoParticipanteGrupoMailSenderService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.from:nao-responda@maismaes.com.br}")
    private String from;

    public void enviarNotificacaoNovoParticipanteGrupo(NotificacaoNovoParticipanteGrupoRequest request) {
        log.info("[REQUISIÇÃO][NotificacaoNovoParticipanteGrupoMailSenderService] - Enviando notificação de novo participante do grupo '{}' para: {}", request.nomeGrupo(), request.email());
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(from);
            helper.setTo(request.email());
            helper.setSubject("+Mães - Novo participante no seu grupo");
            helper.setText(construirMensagemTexto(request), construirMensagemHtml(request));

            mailSender.send(message);
            log.info("[REQUISIÇÃO][NotificacaoNovoParticipanteGrupoMailSenderService] - Notificação de novo participante enviada para: {}", request.email());
        } catch (Exception e) {
            log.error("[REQUISIÇÃO][NotificacaoNovoParticipanteGrupoMailSenderService] - Falha ao enviar notificação de novo participante para: {}", request.email(), e);
            throw new RuntimeException(e);
        }
    }

    private String construirMensagemHtml(NotificacaoNovoParticipanteGrupoRequest request) {
        Context context = new Context();
        context.setVariable("nomeGrupo", request.nomeGrupo());
        context.setVariable("nomeParticipante", request.nomeParticipante());
        return templateEngine.process("notificacao-novo-participante-grupo", context);
    }

    private String construirMensagemTexto(NotificacaoNovoParticipanteGrupoRequest request) {
        return "Olá, Administrador!\n\n"
                + "O participante \"" + request.nomeParticipante() + "\" acabou de entrar no grupo \""
                + request.nomeGrupo() + "\".\n\n"
                + "Acesse seu painel para acompanhar as atividades do grupo.\n\n"
                + "Equipe +Mães.";
    }
}

