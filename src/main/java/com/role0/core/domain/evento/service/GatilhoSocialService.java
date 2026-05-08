package com.role0.core.domain.evento.service;

import java.util.List;
import java.util.Set;

import com.role0.core.domain.evento.entity.Evento;
import com.role0.core.domain.usuario.entity.Usuario;
import com.role0.core.domain.usuario.valueobject.VibeTag;

/**
 * Domain Service puramente focado em regras de negócio que abrangem multiplas
 * agregações.
 */
public class GatilhoSocialService {

    public String gerarIceBreaker(Evento evento, List<Usuario> participantes) {
        if (participantes == null || participantes.size() < 2) {
            return "Bem-vindos ao rolê!";
        }

        // Acha a intersecção de VibeTags de todo mundo
        Set<VibeTag> tagsEmComum = new java.util.HashSet<>(participantes.get(0).getVibeTags());
        for (int i = 1; i < participantes.size(); i++) {
            tagsEmComum.retainAll(participantes.get(i).getVibeTags());
        }

        if (!tagsEmComum.isEmpty()) {
            String tagDesc = tagsEmComum.iterator().next().name();
            return String.format("Notamos que vários de vocês curtem %s! Que tal falarem sobre isso?", tagDesc);
        }

        return "O grupo fechou! Prontos para o rolê?";
    }
}
