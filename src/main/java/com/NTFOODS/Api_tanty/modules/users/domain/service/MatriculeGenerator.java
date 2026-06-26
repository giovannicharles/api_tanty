package com.NTFOODS.Api_tanty.modules.users.domain.service;

import java.time.LocalDate;

import com.NTFOODS.Api_tanty.modules.users.domain.valueobject.UserMatricule;

import org.springframework.stereotype.Component;

/**
 * MatriculeGenerator - Service de génération de matricules utilisateurs
 * Génère des matricules uniques pour les utilisateurs
 */
@Component
public class MatriculeGenerator {

    /**
     * Génère un matricule utilisateur unique
     * @param sequence Numéro de séquence
     * @param prefix Préfixe du matricule
     * @return Matricule généré
     */
    public UserMatricule generate(
            Long sequence,
            String prefix
    ){

        String matricule=
                prefix +
                "-"+
                LocalDate.now()
                        .getYear()
                +
                "-"
                +
                String.format(
                        "%04d",
                        sequence
                );

        return new UserMatricule(
                matricule
        );

    }

}
