package com.maismaes.mensagens.strategy;

import com.maismaes.mensagens.mensagem.AtivacaoContaMessage;
import com.maismaes.mensagens.mensagem.KafkaMessage;
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
@Service("ativar-conta")
@RequiredArgsConstructor
public class AtivacaoContaStrategy implements EmailStrategy {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.from:nao-responda@maismaes.com.br}")
    private String from;

    @Value("${app.maismaes-api.host:http://localhost:8080}")
    private String maisMaesApiHost;

    @Override
    public void enviar(KafkaMessage mensagem) {
        AtivacaoContaMessage ativacaoContaMessage = (AtivacaoContaMessage) mensagem;

        log.info("[REQUISIÇÃO][AtivacaoContaMailSenderService] - Enviando email de ativação para: {}", ativacaoContaMessage.getEmail());
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(from);
            helper.setTo(ativacaoContaMessage.getEmail());
            helper.setSubject("+Mães - Ative sua conta");
            helper.setText(construirMensagemTexto(ativacaoContaMessage), construirMensagemHtml(ativacaoContaMessage));

            mailSender.send(message);
            log.info("[REQUISIÇÃO][AtivacaoContaMailSenderService] - Email de ativação enviado para: {}", ativacaoContaMessage.getEmail());
        } catch (Exception e) {
            log.error("[REQUISIÇÃO][AtivacaoContaMailSenderService] - Falha ao enviar email de ativação para: {}", ativacaoContaMessage.getEmail(), e);
            throw new RuntimeException(e);
        }
    }

    private String construirMensagemHtml(AtivacaoContaMessage ativacaoContaMessage) {
        Context context = new Context();
        context.setVariable("email", ativacaoContaMessage.getEmail());
        context.setVariable("linkAtivacao", montarLinkAtivacao(ativacaoContaMessage));
        return templateEngine.process("ativacao-conta", context);
    }

    private String construirMensagemTexto(AtivacaoContaMessage ativacaoContaMessage) {
        return "Olá!\n\n"
                + "Seja bem-vinda ao +Mães. Para ativar sua conta, acesse o link abaixo:\n\n"
                + montarLinkAtivacao(ativacaoContaMessage)
                + "\n\n"
                + "Se você não criou essa conta, ignore esta mensagem.\n\n"
                + "Equipe +Mães.";
    }

    private String montarLinkAtivacao(AtivacaoContaMessage ativacaoContaMessage) {
        String hostNormalizado = maisMaesApiHost.endsWith("/")
                ? maisMaesApiHost.substring(0, maisMaesApiHost.length() - 1)
                : maisMaesApiHost;
        return hostNormalizado + "/usuario/ativar-conta/" + ativacaoContaMessage.getIdUsuario();
    }
}

