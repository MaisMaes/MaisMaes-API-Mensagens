package com.maismaes.mensagens.kafka;

import com.maismaes.mensagens.mensagem.KafkaMessage;
import com.maismaes.mensagens.strategy.EmailStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
public class StrategyDispatcher {

    private final Map<String, EmailStrategy> strategies;

    public StrategyDispatcher(Map<String, EmailStrategy> strategies) {
        this.strategies = strategies;
    }

    public void dispatch(KafkaMessage mensagem) {
        String strategyKey = mensagem.getStrategy();
        EmailStrategy strategy = strategies.get(strategyKey);

        if (Objects.nonNull(strategy)) {
            log.info("[STRATEGY] - Entrou com o strategy {}", strategyKey);
            strategy.enviar(mensagem);
        } else {
            throw new IllegalArgumentException("Strategy não encontrada: " + strategyKey);
        }
    }
}

