package es.udc.ws.app.client.service.thrift;

import es.udc.ws.app.client.service.dto.ClientInscriptionDto;
import es.udc.ws.app.thrift.ThriftInscriptionDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ClientInscriptionDtoToThriftInscriptionDtoConversor {

    public static ClientInscriptionDto toClientInscriptionDto(ThriftInscriptionDto inscription) {
        return new ClientInscriptionDto(inscription.getInscriptionId(), inscription.getCourseId(), LocalDateTime.parse(inscription.getInscriptionDate(), DateTimeFormatter.ISO_LOCAL_DATE_TIME), inscription.getUserEmail(), inscription.getCreditCard());
    }

    public static ThriftInscriptionDto toThriftInscriptionDto(ThriftInscriptionDto clientInscriptionDto) {
        return new ThriftInscriptionDto(
                clientInscriptionDto.getInscriptionId(), clientInscriptionDto.getCourseId(), clientInscriptionDto.getInscriptionDate(), clientInscriptionDto.getCancelationDate(), clientInscriptionDto.getUserEmail(), clientInscriptionDto.getCreditCard()
        );
    }

    public static List<ClientInscriptionDto> toClientInscriptionDtos(List<ThriftInscriptionDto> thriftList) {
        List<ClientInscriptionDto> inscriptionList = new ArrayList<>();
        for (ThriftInscriptionDto inscription : thriftList) {
            inscriptionList.add(toClientInscriptionDto(inscription));
        }
        return inscriptionList;
    }
}