package com.maismaes.mensagens.dto;

import java.util.UUID;

public record AtivacaoContaRequest(String email, UUID idUsuario) {
}

