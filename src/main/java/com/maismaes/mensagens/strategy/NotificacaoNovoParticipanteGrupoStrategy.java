package com.maismaes.mensagens.strategy;

import com.maismaes.mensagens.mensagem.KafkaMessage;
import com.maismaes.mensagens.mensagem.NotificacaoNovoParticipanteGrupoMessage;
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
@Service("novo-participante")
@RequiredArgsConstructor
public class NotificacaoNovoParticipanteGrupoStrategy implements EmailStrategy {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.from:nao-responda@maismaes.com.br}")
    private String from;

    public void enviar(KafkaMessage mensagem) {
        NotificacaoNovoParticipanteGrupoMessage novoParticipanteGrupoMessage = (NotificacaoNovoParticipanteGrupoMessage) mensagem;

        log.info("[REQUISIÇÃO][NotificacaoNovoParticipanteGrupoMailSenderService] - Enviando notificação de novo participante do grupo '{}' para: {}",
                novoParticipanteGrupoMessage.getNomeGrupo(),
                novoParticipanteGrupoMessage.getEmail());
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(from);
            helper.setTo(novoParticipanteGrupoMessage.getEmail());
            helper.setSubject("+Mães - Novo participante no seu grupo");
            helper.setText(construirMensagemTexto(novoParticipanteGrupoMessage), construirMensagemHtml(novoParticipanteGrupoMessage));

            mailSender.send(message);
            log.info("[REQUISIÇÃO][NotificacaoNovoParticipanteGrupoMailSenderService] - Notificação de novo participante enviada para: {}",
                    novoParticipanteGrupoMessage.getEmail());
        } catch (Exception e) {
            log.error("[REQUISIÇÃO][NotificacaoNovoParticipanteGrupoMailSenderService] - Falha ao enviar notificação de novo participante para: {}",
                    novoParticipanteGrupoMessage.getEmail(), e);
            throw new RuntimeException(e);
        }
    }

    private String construirMensagemHtml(NotificacaoNovoParticipanteGrupoMessage novoParticipanteGrupoMessage) {
        Context context = new Context();
        context.setVariable("nomeGrupo", novoParticipanteGrupoMessage.getNomeGrupo());
        context.setVariable("nomeParticipante", novoParticipanteGrupoMessage.getNomeParticipante());
        return templateEngine.process("notificacao-novo-participante-grupo", context);
    }

    private String construirMensagemTexto(NotificacaoNovoParticipanteGrupoMessage novoParticipanteGrupoMessage) {
        return "Olá, Administrador!\n\n"
                + "O participante \"" + novoParticipanteGrupoMessage.getNomeParticipante() + "\" acabou de entrar no grupo \""
                + novoParticipanteGrupoMessage.getNomeGrupo() + "\".\n\n"
                + "Acesse seu painel para acompanhar as atividades do grupo.\n\n"
                + "Equipe +Mães.";
    }
}

