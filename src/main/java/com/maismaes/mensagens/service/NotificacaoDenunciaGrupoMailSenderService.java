package com.maismaes.mensagens.service;

import com.maismaes.mensagens.dto.NotificacaoDenunciaGrupoRequest;
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
public class NotificacaoDenunciaGrupoMailSenderService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.from:nao-responda@maismaes.com.br}")
    private String from;

    public void enviarNotificacaoDenunciaGrupo(NotificacaoDenunciaGrupoRequest request) {
        log.info("[REQUISIÇÃO][NotificacaoDenunciaGrupoMailSenderService] - Enviando notificação de denúncias do grupo '{}' para: {}", request.nomeGrupo(), request.email());
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(from);
            helper.setTo(request.email());
            helper.setSubject("+Mães - Atenção: grupo com muitas denúncias");
            helper.setText(construirMensagemTexto(request), construirMensagemHtml(request));

            mailSender.send(message);
            log.info("[REQUISIÇÃO][NotificacaoDenunciaGrupoMailSenderService] - Notificação de denúncias enviada para: {}", request.email());
        } catch (Exception e) {
            log.error("[REQUISIÇÃO][NotificacaoDenunciaGrupoMailSenderService] - Falha ao enviar notificação de denúncias para: {}", request.email(), e);
            throw new RuntimeException(e);
        }
    }

    private String construirMensagemHtml(NotificacaoDenunciaGrupoRequest request) {
        Context context = new Context();
        context.setVariable("nomeGrupo", request.nomeGrupo());
        context.setVariable("qtdeDenuncias", request.qtdeDenuncias());
        return templateEngine.process("notificacao-denuncia-grupo", context);
    }

    private String construirMensagemTexto(NotificacaoDenunciaGrupoRequest request) {
        return "Olá, Administrador!\n\n"
                + "O grupo \"" + request.nomeGrupo() + "\" acumulou um total de "
                + request.qtdeDenuncias() + " denúncia(s).\n\n"
                + "Acesse seu painel e analise a veracidade dessas denúncias.\n\n"
                + "Equipe +Mães.";
    }
}

