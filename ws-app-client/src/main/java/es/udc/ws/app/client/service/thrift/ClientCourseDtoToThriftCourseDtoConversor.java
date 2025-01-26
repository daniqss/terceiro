package es.udc.ws.app.client.service.thrift;

import es.udc.ws.app.client.service.dto.ClientCourseDto;
import es.udc.ws.app.thrift.ThriftCourseDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ClientCourseDtoToThriftCourseDtoConversor {
    public static ClientCourseDto toClientCourseDto(ThriftCourseDto course) {
        return new ClientCourseDto(
                course.getCourseId(),
                course.getName(),
                course.getCity(),
                LocalDateTime.parse(course.getStartDate(), DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                (float) course.getPrice(),
                course.getVacantSpots(),
                course.getMaxSpots()
        );
    }

    public static ThriftCourseDto toThriftCourseDto(ClientCourseDto clientCourseDto) {
        return new ThriftCourseDto(
                clientCourseDto.getCourseId(),
                clientCourseDto.getName(),
                clientCourseDto.getCity(),
                null,
                clientCourseDto.getStartDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                clientCourseDto.getPrice(),
                clientCourseDto.getVacantSpots(),
                clientCourseDto.getMaxSpots()
        );
    }

    public static List<ClientCourseDto> toClientCourseDtos(List<ThriftCourseDto> courses) {
        List<ClientCourseDto> clientCourseDtos = new ArrayList<>(courses.size());

        for (ThriftCourseDto course : courses) {
            clientCourseDtos.add(toClientCourseDto(course));
        }
        return clientCourseDtos;
    }

}
