package es.udc.ws.app.restservice.servlets;

import es.udc.ws.app.model.courseservice.CourseServiceFactory;
import es.udc.ws.app.model.courseservice.exceptions.CourseAlreadyStartedException;
import es.udc.ws.app.model.courseservice.exceptions.CourseFullException;
import es.udc.ws.app.model.inscription.Inscription;
import es.udc.ws.app.restservice.dto.RestInscriptionDto;
import es.udc.ws.app.restservice.dto.InscriptionToRestInscriptionDtoConversor;
import es.udc.ws.app.restservice.json.JsonToRestInscriptionDtoConversor;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import es.udc.ws.util.servlet.RestHttpServletTemplate;
import es.udc.ws.util.servlet.ServletUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class InscriptionServlet extends RestHttpServletTemplate {
    @Override
    protected void processPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, InputValidationException {
        ServletUtils.checkEmptyPath(req);
        RestInscriptionDto inscriptionDto = JsonToRestInscriptionDtoConversor.toRestInscriptionDto(req.getInputStream());
        Inscription inscription = InscriptionToRestInscriptionDtoConversor.toInscription(inscriptionDto);

        try {
            inscription = CourseServiceFactory.getService().addInscription(
                    inscription.getCourseId(),
                    inscription.getUserEmail(),
                    inscription.getCreditCard()
            );
        }
        // exceptions should perhaps be handled correctly later
        catch (CourseAlreadyStartedException | CourseFullException | InstanceNotFoundException e) {
            throw new RuntimeException(e);
        }

        inscriptionDto = InscriptionToRestInscriptionDtoConversor.toRestInscriptionDto(inscription);
        String inscriptionURL = ServletUtils.normalizePath(req.getRequestURL().toString()) + "/" + inscription.getInscriptionId();
        Map<String, String> headers = Map.of("Location", inscriptionURL);
        ServletUtils.writeServiceResponse(
                resp, HttpServletResponse.SC_CREATED,
                JsonToRestInscriptionDtoConversor.toObjectNode(inscriptionDto),
                headers
        );
    }

    @Override
    protected void processGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, InputValidationException {
        ServletUtils.checkEmptyPath(req);
        String email = req.getParameter("userEmail");

        List<Inscription> inscription = CourseServiceFactory.getService().findInscriptions(email);
        List<RestInscriptionDto> inscriptionDtos = InscriptionToRestInscriptionDtoConversor.toRestInscriptionDtos(inscription);

        ServletUtils.writeServiceResponse(
                resp, HttpServletResponse.SC_OK,
                JsonToRestInscriptionDtoConversor.toArrayNode(inscriptionDtos),
                null
        );
    }
}
