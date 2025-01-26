package es.udc.ws.app.client.service.thrift;

import es.udc.ws.app.client.service.dto.ClientCourseDto;
import es.udc.ws.app.thrift.ThriftCourseDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ClientCourseDtoToThriftCourseDtoConversor {
    public static ThriftCourseDto toThriftCourseDto(ClientCourseDto clientCourseDto) {
        Long courseId = clientCourseDto.getCourseId();
        return new ThriftCourseDto(
                courseId == null ? -1 : courseId.longValue(),
                clientCourseDto.getName(),
                clientCourseDto.getCity(),
                null,
                clientCourseDto.getStartDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                clientCourseDto.getPrice(),
                clientCourseDto.getMaxSpots(),
                clientCourseDto.getVacantSpots()

        );
    }

    public static ClientCourseDto toClientCourseDto(ThriftCourseDto course) {
        return new ClientCourseDto(
                course.getCourseId(),
                course.getName(),
                course.getCity(),
                LocalDateTime.parse(course.getStartDate(), DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                (float) course.getPrice(),
                course.getMaxSpots(),
                course.getVacantSpots()
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
