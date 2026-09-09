package com.maismaes.mensagens.strategy;

import com.maismaes.mensagens.mensagem.KafkaMessage;
import com.maismaes.mensagens.mensagem.NotificacaoDenunciaGrupoMessage;
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
@Service("denuncia-grupo")
@RequiredArgsConstructor
public class NotificacaoDenunciaGrupoStrategy implements EmailStrategy {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.from:nao-responda@maismaes.com.br}")
    private String from;

    public void enviar(KafkaMessage mensagem) {
        NotificacaoDenunciaGrupoMessage denunciaGrupoMessage = (NotificacaoDenunciaGrupoMessage) mensagem;

        log.info("[REQUISIÇÃO][NotificacaoDenunciaGrupoStrategy] - Enviando notificação de denúncias do grupo '{}' para: {}",
                denunciaGrupoMessage.getNomeGrupo(),
                denunciaGrupoMessage.getEmail());
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(from);
            helper.setTo(denunciaGrupoMessage.getEmail());
            helper.setSubject("+Mães - Atenção: grupo com muitas denúncias");
            helper.setText(construirMensagemTexto(denunciaGrupoMessage), construirMensagemHtml(denunciaGrupoMessage));

            mailSender.send(message);
            log.info("[REQUISIÇÃO][NotificacaoDenunciaGrupoMailSenderService] - Notificação de denúncias enviada para: {}", denunciaGrupoMessage.getEmail());
        } catch (Exception e) {
            log.error("[REQUISIÇÃO][NotificacaoDenunciaGrupoMailSenderService] - Falha ao enviar notificação de denúncias para: {}", denunciaGrupoMessage.getEmail(), e);
            throw new RuntimeException(e);
        }
    }

    private String construirMensagemHtml(NotificacaoDenunciaGrupoMessage request) {
        Context context = new Context();
        context.setVariable("nomeGrupo", request.getNomeGrupo());
        context.setVariable("qtdeDenuncias", request.getQtdeDenuncias());
        return templateEngine.process("notificacao-denuncia-grupo", context);
    }

    private String construirMensagemTexto(NotificacaoDenunciaGrupoMessage request) {
        return "Olá, Administrador!\n\n"
                + "O grupo \"" + request.getNomeGrupo() + "\" acumulou um total de "
                + request.getQtdeDenuncias() + " denúncia(s).\n\n"
                + "Acesse seu painel e analise a veracidade dessas denúncias.\n\n"
                + "Equipe +Mães.";
    }
}

