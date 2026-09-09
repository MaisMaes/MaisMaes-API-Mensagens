package com.maismaes.mensagens.strategy;

import com.maismaes.mensagens.mensagem.KafkaMessage;
import com.maismaes.mensagens.mensagem.RecuperacaoSenhaMessage;
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
@Service("recuperar-senha")
@RequiredArgsConstructor
public class RecuperarSenhaStrategy implements EmailStrategy {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.from:nao-responda@maismaes.com.br}")
    private String from;

    public void enviar(KafkaMessage mensagem) {
        RecuperacaoSenhaMessage recuperacaoSenhaMessage = (RecuperacaoSenhaMessage) mensagem;

        log.info("[REQUISIÇÃO][RecuperarSenhaMailSenderSerivce] - Enviando email de recuperação de senha para: {}", recuperacaoSenhaMessage.getEmail());
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(from);
            helper.setTo(recuperacaoSenhaMessage.getEmail());
            helper.setSubject("+Mães - Código de recuperação de senha");
            helper.setText(construirMensagemTexto(recuperacaoSenhaMessage), construirMensagemHtml(recuperacaoSenhaMessage));

            mailSender.send(message);
            log.info("[REQUISIÇÃO][RecuperarSenhaMailSenderSerivce] - Email de recuperação de senha enviado para: {}", recuperacaoSenhaMessage.getEmail());
        } catch (Exception e) {
            log.error("[REQUISIÇÃO][RecuperarSenhaMailSenderSerivce] - Falha ao enviar email de recuperação de senha para: {}", recuperacaoSenhaMessage.getEmail(), e);
            throw new RuntimeException(e);
        }
    }

    private String construirMensagemHtml(RecuperacaoSenhaMessage recuperacaoSenhaMessage) {
        Context context = new Context();
        context.setVariable("email", recuperacaoSenhaMessage.getEmail());
        context.setVariable("codigo", recuperacaoSenhaMessage.getCodigo());
        return templateEngine.process("recuperacao-senha", context);
    }

    private String construirMensagemTexto(RecuperacaoSenhaMessage recuperacaoSenhaMessage) {
        return "Olá!\n\n"
                + "Recebemos uma solicitação para redefinir a sua senha no +Mães.\n"
                + "Use o código abaixo para concluir o processo:\n\n"
                + recuperacaoSenhaMessage.getCodigo()
                + "\n\n"
                + "Este código expira em alguns minutos. Caso não tenha solicitado, ignore esta mensagem.\n\n"
                + "Equipe +Mães.";
    }
}
