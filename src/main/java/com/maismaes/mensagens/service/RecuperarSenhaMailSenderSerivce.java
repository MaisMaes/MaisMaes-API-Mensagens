package com.maismaes.mensagens.service;

import com.maismaes.mensagens.dto.RecuperacaoSenhaRequest;
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
public class RecuperarSenhaMailSenderSerivce {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.from:nao-responda@maismaes.com.br}")
    private String from;

    public void enviarEmailDeRecuperacao(RecuperacaoSenhaRequest request) {
        log.info("[REQUISIÇÃO][RecuperarSenhaMailSenderSerivce] - Enviando email de recuperação de senha para: {}", request.email());
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(from);
            helper.setTo(request.email());
            helper.setSubject("+Mães - Código de recuperação de senha");
            helper.setText(construirMensagemTexto(request), construirMensagemHtml(request));

            mailSender.send(message);
            log.info("[REQUISIÇÃO][RecuperarSenhaMailSenderSerivce] - Email de recuperação de senha enviado para: {}", request.email());
        } catch (Exception e) {
            log.error("[REQUISIÇÃO][RecuperarSenhaMailSenderSerivce] - Falha ao enviar email de recuperação de senha para: {}", request.email(), e);
            throw new RuntimeException(e);
        }
    }

    private String construirMensagemHtml(RecuperacaoSenhaRequest request) {
        Context context = new Context();
        context.setVariable("email", request.email());
        context.setVariable("codigo", request.codigo());
        return templateEngine.process("recuperacao-senha", context);
    }

    private String construirMensagemTexto(RecuperacaoSenhaRequest request) {
        return "Olá!\n\n"
                + "Recebemos uma solicitação para redefinir a sua senha no +Mães.\n"
                + "Use o código abaixo para concluir o processo:\n\n"
                + request.codigo()
                + "\n\n"
                + "Este código expira em alguns minutos. Caso não tenha solicitado, ignore esta mensagem.\n\n"
                + "Equipe +Mães.";
    }
}
