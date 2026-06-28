package com.maismaes.mensagens.controller;

import com.maismaes.mensagens.dto.NotificacaoNovoParticipanteGrupoRequest;
import com.maismaes.mensagens.service.NotificacaoNovoParticipanteGrupoMailSenderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/v1/api-mensagens")
@RequiredArgsConstructor
public class NotificacaoNovoParticipanteGrupoController {

    private final NotificacaoNovoParticipanteGrupoMailSenderService service;

    @PostMapping("/notificacao-novo-participante-grupo")
    public ResponseEntity<Void> notificarNovoParticipanteGrupo(@RequestBody NotificacaoNovoParticipanteGrupoRequest request) {
        log.info("[REQUISIÇÃO][NotificacaoNovoParticipanteGrupoController] - Chegada de requisição de notificação de novo participante no grupo: {}", request.nomeGrupo());
        try {
            service.enviarNotificacaoNovoParticipanteGrupo(request);
        } catch (Exception e) {
            log.error("[REQUISIÇÃO][NotificacaoNovoParticipanteGrupoController] - Falha ao enviar notificação de novo participante no grupo: {}", request.nomeGrupo(), e);
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok().build();
    }
}

