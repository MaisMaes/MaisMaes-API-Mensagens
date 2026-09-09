package com.maismaes.mensagens.kafka;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maismaes.mensagens.mensagem.AtivacaoContaMessage;
import com.maismaes.mensagens.mensagem.KafkaMessage;
import com.maismaes.mensagens.mensagem.NotificacaoDenunciaGrupoMessage;
import com.maismaes.mensagens.mensagem.NotificacaoNovoParticipanteGrupoMessage;
import com.maismaes.mensagens.mensagem.RecuperacaoSenhaMessage;
import org.springframework.stereotype.Component;

/**
 * Responsável por transformar o JSON bruto recebido do tópico Kafka na
 * subclasse correta de {@link KafkaMessage}, usando o campo "strategy"
 * como discriminador. Isso evita depender da (de)serialização automática
 * do Spring Kafka, que não sabe para qual subtipo converter o payload.
 */
@Component
public class KafkaMessageFactory {

    private final ObjectMapper objectMapper = new ObjectMapper()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    public KafkaMessage criar(String json) {
        try {
            JsonNode jsonNode = objectMapper.readTree(json);
            String strategy = jsonNode.get("strategy").asText();

            return switch (strategy) {
                case "ativar-conta" -> objectMapper.treeToValue(jsonNode, AtivacaoContaMessage.class);
                case "recuperar-senha" -> objectMapper.treeToValue(jsonNode, RecuperacaoSenhaMessage.class);
                case "novo-participante" -> objectMapper.treeToValue(jsonNode, NotificacaoNovoParticipanteGrupoMessage.class);
                case "denuncia-grupo" -> objectMapper.treeToValue(jsonNode, NotificacaoDenunciaGrupoMessage.class);
                default -> throw new IllegalArgumentException("Strategy desconhecida: " + strategy);
            };
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao interpretar mensagem Kafka: " + json, e);
        }
    }
}
