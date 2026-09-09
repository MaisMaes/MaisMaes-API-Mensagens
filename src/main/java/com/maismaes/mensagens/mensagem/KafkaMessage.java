package com.maismaes.mensagens.mensagem;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class KafkaMessage {
    private String strategy;
    private String email;
}
