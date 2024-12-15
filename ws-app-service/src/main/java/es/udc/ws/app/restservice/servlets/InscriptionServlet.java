package es.udc.ws.app.restservice.servlets;

import es.udc.ws.app.model.courseservice.CourseServiceFactory;
import es.udc.ws.app.model.courseservice.exceptions.*;
import es.udc.ws.app.model.inscription.Inscription;
import es.udc.ws.app.restservice.dto.RestInscriptionDto;
import es.udc.ws.app.restservice.dto.InscriptionToRestInscriptionDtoConversor;
import es.udc.ws.app.restservice.json.AppExceptionToJsonConversor;
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
    protected void processPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, InputValidationException {
        ServletUtils.checkEmptyPath(req);

        String inscriptionIdParam = req.getParameter("inscriptionId");

        if (inscriptionIdParam == null) {
            Long courseId = ServletUtils.getMandatoryParameterAsLong(req, "courseId");
            String userEmail = ServletUtils.getMandatoryParameter(req, "userEmail");
            String creditCard = ServletUtils.getMandatoryParameter(req, "creditCard");

            try {
                Inscription inscription = CourseServiceFactory.getService().addInscription(courseId, userEmail, creditCard);

                RestInscriptionDto inscriptionDto = InscriptionToRestInscriptionDtoConversor.toRestInscriptionDto(inscription);

                ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_CREATED,
                        JsonToRestInscriptionDtoConversor.toObjectNode(inscriptionDto), null);
            } catch (CourseAlreadyStartedException e) {
                ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_FORBIDDEN,
                        AppExceptionToJsonConversor.toCourseAlreadyStartedException(e), null);
            } catch (CourseFullException e) {
                ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_FORBIDDEN,
                        AppExceptionToJsonConversor.toCourseFullException(e), null);
            } catch (InstanceNotFoundException e) {
                throw new InputValidationException("Invalid Request: not found course with id " + courseId + " to add inscription");
            }
        } else {
            Long inscriptionId = ServletUtils.getMandatoryParameterAsLong(req, "inscriptionId");
            String userEmail = ServletUtils.getMandatoryParameter(req, "userEmail");

            try {
                CourseServiceFactory.getService().cancelInscription(inscriptionId, userEmail);
                ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_OK,
                        JsonToRestInscriptionDtoConversor.toObjectNode(inscriptionId), null);
            } catch (IncorrectUserException e) {
                ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_FORBIDDEN,
                        AppExceptionToJsonConversor.toIncorrectUserException(e), null);
            } catch (InscriptionAlreadyCancelledException e) {
                ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_FORBIDDEN,
                        AppExceptionToJsonConversor.toInscriptionAlreadyCancelledException(e), null);
            } catch (CancelTooCloseToCourseStartException e) {
                ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_FORBIDDEN,
                        AppExceptionToJsonConversor.toCancelTooCloseToCourseStartException(e), null);
            } catch (InstanceNotFoundException e) {
                throw new InputValidationException("Invalid Request: not found inscription with id " + inscriptionId + " to cancel");
            }
        }
    }

    @Override
    protected void processGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, InputValidationException {
        ServletUtils.checkEmptyPath(req);
        String email = req.getParameter("userEmail");
        if (email == null || email.isEmpty()) {
            throw new InputValidationException("Invalid Request: missing or empty userEmail parameter");
        }

        List<Inscription> inscription = CourseServiceFactory.getService().findInscriptions(email);
        List<RestInscriptionDto> inscriptionDtos = InscriptionToRestInscriptionDtoConversor.toRestInscriptionDtos(inscription);

        ServletUtils.writeServiceResponse(
                resp, HttpServletResponse.SC_OK,
                JsonToRestInscriptionDtoConversor.toArrayNode(inscriptionDtos),
                null
        );
    }
}
