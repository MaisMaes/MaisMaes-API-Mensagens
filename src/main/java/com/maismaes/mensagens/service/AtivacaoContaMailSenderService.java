package com.maismaes.mensagens.service;

import com.maismaes.mensagens.dto.AtivacaoContaRequest;
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
public class AtivacaoContaMailSenderService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.from:nao-responda@maismaes.com.br}")
    private String from;

    @Value("${app.maismaes-api.host:http://localhost:8080}")
    private String maisMaesApiHost;

    public void enviarEmailDeAtivacao(AtivacaoContaRequest request) {
        log.info("[REQUISIÇÃO][AtivacaoContaMailSenderService] - Enviando email de ativação para: {}", request.email());
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(from);
            helper.setTo(request.email());
            helper.setSubject("+Mães - Ative sua conta");
            helper.setText(construirMensagemTexto(request), construirMensagemHtml(request));

            mailSender.send(message);
            log.info("[REQUISIÇÃO][AtivacaoContaMailSenderService] - Email de ativação enviado para: {}", request.email());
        } catch (Exception e) {
            log.error("[REQUISIÇÃO][AtivacaoContaMailSenderService] - Falha ao enviar email de ativação para: {}", request.email(), e);
            throw new RuntimeException(e);
        }
    }

    private String construirMensagemHtml(AtivacaoContaRequest request) {
        Context context = new Context();
        context.setVariable("email", request.email());
        context.setVariable("linkAtivacao", montarLinkAtivacao(request));
        return templateEngine.process("ativacao-conta", context);
    }

    private String construirMensagemTexto(AtivacaoContaRequest request) {
        return "Olá!\n\n"
                + "Seja bem-vinda ao +Mães. Para ativar sua conta, acesse o link abaixo:\n\n"
                + montarLinkAtivacao(request)
                + "\n\n"
                + "Se você não criou essa conta, ignore esta mensagem.\n\n"
                + "Equipe +Mães.";
    }

    private String montarLinkAtivacao(AtivacaoContaRequest request) {
        String hostNormalizado = maisMaesApiHost.endsWith("/")
                ? maisMaesApiHost.substring(0, maisMaesApiHost.length() - 1)
                : maisMaesApiHost;
        return hostNormalizado + "/usuario/ativar-conta/" + request.idUsuario();
    }
}

