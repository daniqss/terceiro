package es.udc.ws.app.restservice.dto;

import es.udc.ws.app.model.inscription.Inscription;

import java.util.ArrayList;
import java.util.List;

public class InscriptionToRestInscriptionDtoConversor {
    public static RestInscriptionDto toRestInscriptionDto(Inscription inscription) {
        return new RestInscriptionDto(
                inscription.getInscriptionId(),
                inscription.getCourseId(),
                inscription.getInscriptionDate(),
                inscription.getCancelationDate(),
                inscription.getUserEmail(),
                inscription.getCreditCard().substring(inscription.getCreditCard().length() - 4)
        );
    }

    public static Inscription toInscription(RestInscriptionDto inscription) {
        return new Inscription(
                inscription.getInscriptionId(),
                inscription.getCourseId(),
                inscription.getInscriptionDate(),
                inscription.getCancelationDate(),
                inscription.getUserEmail(),
                inscription.getCreditCard()
        );
    }

    public static List<RestInscriptionDto> toRestInscriptionDtos(List<Inscription> inscriptions) {
        List<RestInscriptionDto> inscriptionDtos = new ArrayList<>(inscriptions.size());
        for (int i = 0; i < inscriptions.size(); i++) {
            Inscription inscription = inscriptions.get(i);
            inscriptionDtos.add(toRestInscriptionDto(inscription));
        }
        return inscriptionDtos;
    }

}
