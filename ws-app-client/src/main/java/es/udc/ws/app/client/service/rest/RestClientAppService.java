package es.udc.ws.app.client.service.rest;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import es.udc.ws.app.client.service.ClientAppService;
import es.udc.ws.app.client.service.dto.ClientCourseDto;
import es.udc.ws.app.client.service.dto.ClientInscriptionDto;
import es.udc.ws.app.client.service.exceptions.*;
import es.udc.ws.app.client.service.rest.json.JsonToClientCourseDtoConversor;
import es.udc.ws.app.client.service.rest.json.JsonToClientExceptionDtoConversor;
import es.udc.ws.app.client.service.rest.json.JsonToClientInscriptionDtoConversor;
import es.udc.ws.util.configuration.ConfigurationParametersManager;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import es.udc.ws.util.json.ObjectMapperFactory;
import es.udc.ws.util.json.exceptions.ParsingException;
import org.apache.hc.client5.http.fluent.Form;
import org.apache.hc.client5.http.fluent.Request;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpStatus;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class RestClientAppService implements ClientAppService {
    private final static String ENDPOINT_ADDRESS_PARAMETER = "RestClientAppService.endpointAddress";
    private String endpointAddress;

    @Override
    public ClientCourseDto addCourse(ClientCourseDto course) throws InputValidationException, RuntimeException, ClientCourseStartTooSoonException {
        return null;
    }

    @Override
    public List<ClientCourseDto> findCourses(String city) throws RuntimeException, InputValidationException {
        try {
            String encodedCity = URLEncoder.encode(city, StandardCharsets.UTF_8);
            LocalDateTime date = LocalDateTime.now(); //Current Date
            String encodedDate = URLEncoder.encode(date.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), StandardCharsets.UTF_8);

            ClassicHttpResponse response = (ClassicHttpResponse) Request.get(getEndpointAddress() + "courses?city="
                            + encodedCity + "&date=" + encodedDate)
                    .execute()
                    .returnResponse();

            validateStatusCode(HttpStatus.SC_OK, response);

            // Parse JSON array manually
            ObjectMapper objectMapper = ObjectMapperFactory.instance();
            JsonNode rootNode = objectMapper.readTree(response.getEntity().getContent());

            if (rootNode.getNodeType() != JsonNodeType.ARRAY) {
                throw new ParsingException("Unrecognized JSON (array expected)");
            }

            List<ClientCourseDto> courseDtos = new ArrayList<>();
            for (JsonNode courseNode : rootNode) {
                // Use existing method to convert each course
                courseDtos.add(JsonToClientCourseDtoConversor.toClientCourseDto(
                        new ByteArrayInputStream(objectMapper.writeValueAsBytes(courseNode))));
            }

            return courseDtos;

        } catch (InputValidationException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ClientCourseDto findCourse(Long courseId) throws InputValidationException, InstanceNotFoundException {
        try {
            ClassicHttpResponse response = (ClassicHttpResponse) Request.get(getEndpointAddress() + "courses/" + courseId)
                    .execute()
                    .returnResponse();

            validateStatusCode(HttpStatus.SC_OK, response);

            return JsonToClientCourseDtoConversor.toClientCourseDto(response.getEntity().getContent());

        } catch (InputValidationException | InstanceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ClientInscriptionDto addInscription(Long courseId, String userEmail, String bankCardNumber)
            throws InputValidationException, InstanceNotFoundException, ClientCourseAlreadyStartedException, ClientCourseFullException {

        try {

            String url = getEndpointAddress() + "courses/" + courseId + "/inscriptions";


            ClassicHttpResponse response = (ClassicHttpResponse) Request.post(url)
                    .bodyForm(Form.form()
                            .add("courseId", Long.toString(courseId))
                            .add("userEmail", userEmail)
                            .add("bankCardNumber", bankCardNumber)
                            .build())
                    .execute()
                    .returnResponse();


            validateStatusCode(HttpStatus.SC_CREATED, response);

            return JsonToClientInscriptionDtoConversor.toClientInscriptionDto(response.getEntity().getContent());

        } catch (InputValidationException | InstanceNotFoundException | ClientCourseAlreadyStartedException | ClientCourseFullException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error adding the inscription: " + e.getMessage(), e);
        }
    }

    @Override
    public void cancelInscription(Long inscriptionId, String userEmail) throws InputValidationException,
            InstanceNotFoundException, ClientIncorrectUserException, ClientInscriptionAlreadyCancelledException, ClientCancelTooCloseToCourseStartException {

        try {

            ClassicHttpResponse response = (ClassicHttpResponse) Request.post(getEndpointAddress() + "inscriptions/cancel/" + inscriptionId + "?userEmail="+ URLEncoder.encode(userEmail, StandardCharsets.UTF_8)).execute().returnResponse();

            validateStatusCode(HttpStatus.SC_NO_CONTENT, response);

        } catch (InputValidationException | InstanceNotFoundException | ClientIncorrectUserException |
                 ClientInscriptionAlreadyCancelledException | ClientCancelTooCloseToCourseStartException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ClientInscriptionDto> findInscriptions(String userEmail) {
        return List.of();
    }

    private synchronized String getEndpointAddress() {
        if (endpointAddress == null) {
            endpointAddress = ConfigurationParametersManager
                    .getParameter(ENDPOINT_ADDRESS_PARAMETER);
        }
        return endpointAddress;
    }

    private InputStream toInputStream(ClientCourseDto course) {

        try {

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ObjectMapper objectMapper = ObjectMapperFactory.instance();
            objectMapper.writer(new DefaultPrettyPrinter()).writeValue(outputStream, JsonToClientCourseDtoConversor.toObjectNode(course));

            return new ByteArrayInputStream(outputStream.toByteArray());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void validateStatusCode(int successCode, ClassicHttpResponse response) throws Exception {

        try {

            int statusCode = response.getCode();

            /* Success? */
            if (statusCode == successCode) {
                return;
            }

            /* Handler error. */
            switch (statusCode) {
                case HttpStatus.SC_NOT_FOUND -> throw JsonToClientExceptionDtoConversor.fromNotFoundErrorCode(
                        response.getEntity().getContent());
                case HttpStatus.SC_BAD_REQUEST -> throw JsonToClientExceptionDtoConversor.fromBadRequestErrorCode(
                        response.getEntity().getContent());
                case HttpStatus.SC_FORBIDDEN -> throw JsonToClientExceptionDtoConversor.fromForbiddenErrorCode(
                        response.getEntity().getContent());
                case HttpStatus.SC_GONE -> throw JsonToClientExceptionDtoConversor.fromGoneErrorCode(
                        response.getEntity().getContent());
                default -> throw new RuntimeException("HTTP error; status code = "
                        + statusCode);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

