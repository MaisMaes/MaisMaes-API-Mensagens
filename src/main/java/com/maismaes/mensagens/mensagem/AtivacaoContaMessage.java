package com.maismaes.mensagens.mensagem;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AtivacaoContaMessage extends KafkaMessage{
    private java.util.UUID idUsuario;
}

