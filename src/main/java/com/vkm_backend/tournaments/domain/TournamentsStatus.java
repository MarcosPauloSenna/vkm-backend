package com.vkm_backend.tournaments.domain;

public enum TournamentsStatus {
    /*
    DRAFT - Torneio em elaboração. Ainda está sendo configurado e não está aberto para inscrições.
    REGISTRATION - Inscrições abertas para as equipes participantes.
    IN_PROGRESS - Torneio em andamento, com partidas sendo realizadas.
    FINISHED - Torneio concluído, com resultados finais definidos.
    CANCELLED - Torneio cancelado antes ou durante sua realização.
     */
    DRAFT,
    REGISTRATION,
    IN_PROGRESS,
    FINISHED,
    CANCELLED
}
