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
    @Override
    protected void processPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, InputValidationException {
        ServletUtils.checkEmptyPath(req);
        RestInscriptionDto inscriptionDto = JsonToRestInscriptionDtoConversor.toRestInscriptionDto(req.getInputStream());
        Inscription inscription = InscriptionToRestInscriptionDtoConversor.toInscription(inscriptionDto);

        // if inscription doesn't exist, add it
        if (inscription.getInscriptionId() == null) {
            try {
                inscription = CourseServiceFactory.getService().addInscription(
                        inscription.getCourseId(),
                        inscription.getUserEmail(),
                        inscription.getCreditCard()
                );
            } catch (CourseAlreadyStartedException e) {
                ServletUtils.writeServiceResponse(
                        resp,
                        HttpServletResponse.SC_FORBIDDEN,
                        AppExceptionToJsonConversor.toCourseAlreadyStartedException(e),
                        null
                );
            } catch (CourseFullException e) {
                ServletUtils.writeServiceResponse(
                        resp,
                        HttpServletResponse.SC_FORBIDDEN,
                        AppExceptionToJsonConversor.toCourseFullException(e),
                        null
                );
            } catch (InstanceNotFoundException e) {
                ServletUtils.writeServiceResponse(
                        resp,
                        HttpServletResponse.SC_NOT_FOUND,
                        AppExceptionToJsonConversor.toCourseNotFoundException(inscription.getCourseId()),
                        null
                );
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

        // if inscription exists, update it by cancelling it
        else {
            try {
                CourseServiceFactory.getService().cancelInscription(
                        inscription.getInscriptionId(),
                        inscription.getUserEmail()
                );
            } catch (IncorrectUserException e) {
                ServletUtils.writeServiceResponse(
                        resp,
                        // 403 Forbidden -> Entiendo lo que quieres hacer,
                        // pero violas una de las reglas de negocio
                        HttpServletResponse.SC_FORBIDDEN,
                        AppExceptionToJsonConversor.toIncorrectUserException(e),
                        null
                );
            } catch (InscriptionAlreadyCancelledException e) {
                ServletUtils.writeServiceResponse(
                        resp,
                        HttpServletResponse.SC_FORBIDDEN,
                        AppExceptionToJsonConversor.toInscriptionAlreadyCancelledException(e),
                        null
                );
            } catch (CancelTooCloseToCourseStartException e) {
                ServletUtils.writeServiceResponse(
                        resp,
                        HttpServletResponse.SC_FORBIDDEN,
                        AppExceptionToJsonConversor.toCancelTooCloseToCourseStartException(e),
                        null
                );
            } catch (InstanceNotFoundException e) {
                ServletUtils.writeServiceResponse(
                        resp,
                        HttpServletResponse.SC_NOT_FOUND,
                        AppExceptionToJsonConversor.toCourseNotFoundException(inscription.getCourseId()),
                        null
                );
            }

            inscriptionDto = InscriptionToRestInscriptionDtoConversor.toRestInscriptionDto(inscription);
            String inscriptionURL = ServletUtils.normalizePath(req.getRequestURL().toString()) + "/" + inscription.getInscriptionId();
            Map<String, String> headers = Map.of("Location", inscriptionURL);
            ServletUtils.writeServiceResponse(
                    resp, HttpServletResponse.SC_OK,
                    JsonToRestInscriptionDtoConversor.toObjectNode(inscriptionDto),
                    headers
            );
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
