package com.maismaes.mensagens.strategy;

import com.maismaes.mensagens.mensagem.KafkaMessage;

public interface EmailStrategy {
    void enviar(KafkaMessage message) throws RuntimeException;
}
