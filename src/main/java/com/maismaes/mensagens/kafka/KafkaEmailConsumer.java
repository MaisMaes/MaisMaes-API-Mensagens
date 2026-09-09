package com.maismaes.mensagens.kafka;

import com.maismaes.mensagens.mensagem.KafkaMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaEmailConsumer {

    private final StrategyDispatcher dispatcher;
    private final KafkaMessageFactory messageFactory;

    @KafkaListener(topics = "topico-emails", groupId = "grupo-emails")
    public void consumir(String payload) {
        KafkaMessage mensagem = messageFactory.criar(payload);
        log.info("[CONSUMER] - Consumindo mensagem, Strategy: {}", mensagem.getStrategy());
        dispatcher.dispatch(mensagem);
    }
}
