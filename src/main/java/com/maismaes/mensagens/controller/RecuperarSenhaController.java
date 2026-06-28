package com.maismaes.mensagens.controller;

import com.maismaes.mensagens.dto.RecuperacaoSenhaRequest;
import com.maismaes.mensagens.service.RecuperarSenhaMailSenderSerivce;
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
public class RecuperarSenhaController {

    private final RecuperarSenhaMailSenderSerivce service;

    @PostMapping("/recuperar-senha")
    public ResponseEntity<Void>  recuperarSenha(@RequestBody RecuperacaoSenhaRequest request) {
        log.info("[REQUISIÇÃO][RecuperarSenhaController] - Chegada de requisição de envio de email para recuperação de senha.");
        try {
            service.enviarEmailDeRecuperacao(request);
        } catch (Exception e) {
            log.error("[REQUISIÇÃO][RecuperarSenhaController] - Falha ao enviar email de recuperação de senha para: {}", request.email(), e);
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok().build();
    }
}
