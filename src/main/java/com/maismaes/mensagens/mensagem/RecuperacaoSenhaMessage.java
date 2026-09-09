package com.maismaes.mensagens.mensagem;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RecuperacaoSenhaMessage extends KafkaMessage{
    private String codigo;
}
