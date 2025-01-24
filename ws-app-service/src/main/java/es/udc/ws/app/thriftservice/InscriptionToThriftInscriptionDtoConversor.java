package es.udc.ws.app.thriftservice;

import es.udc.ws.app.model.inscription.Inscription;
import es.udc.ws.app.thrift.ThriftInscriptionDto;
import java.util.ArrayList;
import java.util.List;

public class InscriptionToThriftInscriptionDtoConversor {
    public static ThriftInscriptionDto toThriftInscriptionDto(Inscription inscription) {

        return new ThriftInscriptionDto(inscription.getInscriptionId(), inscription.getCourseId(), inscription.getInscriptionDate(), inscription.getCancelationDate(),inscription.getUserEmail(),inscription.getCreditCard());

    }

    public static List<ThriftInscriptionDto> toThriftInscriptionDtos(List<Inscription> inscriptions) {

        List<ThriftInscriptionDto> dtos = new ArrayList<>(inscriptions.size());

        for (Inscription inscription: inscriptions){
            dtos.add(toThriftInscriptionDto(inscription));
        }

        return dtos;

    }
}