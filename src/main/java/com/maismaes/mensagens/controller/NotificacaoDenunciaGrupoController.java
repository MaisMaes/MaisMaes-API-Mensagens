package com.maismaes.mensagens.controller;

import com.maismaes.mensagens.dto.NotificacaoDenunciaGrupoRequest;
import com.maismaes.mensagens.service.NotificacaoDenunciaGrupoMailSenderService;
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
public class NotificacaoDenunciaGrupoController {

    private final NotificacaoDenunciaGrupoMailSenderService service;

    @PostMapping("/notificacao-denuncia-grupo")
    public ResponseEntity<Void> notificarDenunciaGrupo(@RequestBody NotificacaoDenunciaGrupoRequest request) {
        log.info("[REQUISIÇÃO][NotificacaoDenunciaGrupoController] - Chegada de requisição de notificação de denúncias do grupo: {}", request.nomeGrupo());
        try {
            service.enviarNotificacaoDenunciaGrupo(request);
        } catch (Exception e) {
            log.error("[REQUISIÇÃO][NotificacaoDenunciaGrupoController] - Falha ao enviar notificação de denúncias do grupo: {}", request.nomeGrupo(), e);
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok().build();
    }
}

