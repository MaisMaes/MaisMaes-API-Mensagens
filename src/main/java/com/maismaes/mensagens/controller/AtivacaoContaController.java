package com.maismaes.mensagens.controller;

import com.maismaes.mensagens.dto.AtivacaoContaRequest;
import com.maismaes.mensagens.service.AtivacaoContaMailSenderService;
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
public class AtivacaoContaController {

    private final AtivacaoContaMailSenderService service;

    @PostMapping("/ativacao-conta")
    public ResponseEntity<Void> enviarEmailAtivacao(@RequestBody AtivacaoContaRequest request) {
        log.info("[REQUISIÇÃO][AtivacaoContaController] - Chegada de requisição de envio de email para ativação de conta.");
        try {
            service.enviarEmailDeAtivacao(request);
        } catch (Exception e) {
            log.error("[REQUISIÇÃO][AtivacaoContaController] - Falha ao enviar email de ativação para: {}", request.email(), e);
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok().build();
    }
}

