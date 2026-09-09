package com.maismaes.mensagens.mensagem;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NotificacaoDenunciaGrupoMessage extends KafkaMessage{
    private String nomeGrupo;
    private String qtdeDenuncias;
}
