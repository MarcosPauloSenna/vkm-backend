package com.vkm_backend.tournaments.domain;

public enum TourrnamentsFormat {
    /*
    KNOCKOUT -Eliminatória: quem perde uma partida é eliminado, até restar o campeão.

    ROUND_ROBIN - Todos contra todos: cada equipe enfrenta todas as outras equipes participantes.

    GROUPS - Fase de grupos: as equipes são divididas em grupos e disputam partidas dentro de
    seus respectivos grupos. A classificação depende das regras definidas para o torneio.

    GROUPS_AND_KNOCKOUT - Grupos + eliminatória: as equipes disputam uma fase de grupos e as
     melhores classificadas avançam para a fase eliminatória.
    */

    KNOCKOUT,
    ROUND_ROBIN,
    GROUPS,
    GROUPS_AND_KNOCKOUT
}
