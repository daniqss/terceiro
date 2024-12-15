package es.udc.ws.app.client.ui;

import es.udc.ws.app.client.service.ClientAppService;
import es.udc.ws.app.client.service.ClientAppServiceFactory;
import es.udc.ws.app.client.service.dto.ClientCourseDto;
import es.udc.ws.app.client.service.dto.ClientInscriptionDto;
import es.udc.ws.app.client.service.exceptions.*;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;

import java.time.LocalDateTime;
import java.util.List;

public class AppServiceClient {
    public static void main(String[] args) {
        if (args.length == 0) {
            printUsageAndExit();
        }
        ClientAppService clientCourseService = ClientAppServiceFactory.getService();
        if ("-addCourse".equalsIgnoreCase(args[0])) {
            validateArgs(args, 6, new int[]{5});

            //[addCourse] CursoServiceClient -addCourse <name> <city> <startDate> <price> <maxPlaces>
            try {
                ClientCourseDto cursoId = clientCourseService.addCourse(new ClientCourseDto(
                        null,
                        args[1],
                        args[2],
                        LocalDateTime.parse(args[3]),
                        Float.parseFloat(args[4]),
                        Integer.parseInt(args[5])
                ));
                System.out.println("Course " + cursoId + " created successfully");

            } catch (InputValidationException | ClientCourseStartTooSoonException ex) {
                ex.printStackTrace(System.err);
            }

        } else if ("-inscribe".equalsIgnoreCase(args[0])) {
            validateArgs(args, 4, new int[]{3});
            //[inscribe] CursoServiceClient -inscribe <courseId> <userEmail> <creditCardNumber>
            try {
                Long courseId = Long.parseLong(args[1]);
                String userEmail = args[2];
                String creditCardNumber = args[3];
                ClientInscriptionDto inscription = clientCourseService.addInscription(courseId, userEmail, creditCardNumber);
                System.out.println("Inscription " + inscription.getInscriptionId() + " created successfully");
            } catch (InputValidationException | InstanceNotFoundException | ClientCourseAlreadyStartedException |
                     ClientCourseFullException ex) {
                ex.printStackTrace(System.err);
            }

        } else if ("-cancel".equalsIgnoreCase(args[0])) {
            validateArgs(args, 3, new int[]{1});
            //[cancel] CursoServiceClient -cancel <inscriptionId> <userEmail>
            try {
                Long inscriptionId = Long.parseLong(args[1]);
                String userEmail = args[2];
                clientCourseService.cancelInscription(inscriptionId, userEmail);
                System.out.println("Inscription " + inscriptionId + " cancelled");
            } catch (InstanceNotFoundException | InputValidationException | ClientIncorrectUserException |
                     ClientInscriptionAlreadyCancelledException | ClientCancelTooCloseToCourseStartException ex) {
                ex.printStackTrace(System.err);
            }

        } else if ("-findCourses".equalsIgnoreCase(args[0])) {
            //[findCourses]       CursoServiceClient -findCourses <city>
            validateArgs(args, 2, new int[]{});
            try {
                List<ClientCourseDto> courses = clientCourseService.findCourses(args[1]);
                System.out.println("Found " + courses.size() +
                        " course(s) by city '" + args[1] + "'");
                for (ClientCourseDto courseDto : courses) {
                    int reservedSpots = courseDto.getMaxSpots() - courseDto.getVacantSpots();
                    System.out.println("Taken spots: " + reservedSpots +
                            ", Max spots: " + courseDto.getMaxSpots() +
                            ", Price: " + courseDto.getPrice() +
                            ", Description: " + courseDto.getName() +
                            ", Startdate: " + courseDto.getStartDate());
                }
            } catch (InputValidationException ex) {
                ex.printStackTrace(System.err);
            }

        } else if ("-findCourse".equalsIgnoreCase(args[0])) {
            validateArgs(args, 2, new int[]{1});
            //[findCourse] CursoServiceClient -findCourse <courseId>
            try {
                if (args[1].matches("\\d+")) {
                    ClientCourseDto courseDto = clientCourseService.findCourse(Long.parseLong(args[1]));
                    int reservedSpots = courseDto.getMaxSpots() - courseDto.getVacantSpots();
                    System.out.println("Taken spots: " + reservedSpots +
                            ", Max spots: " + courseDto.getMaxSpots() +
                            ", Price: " + courseDto.getPrice() +
                            ", Description: " + courseDto.getName() +
                            ", Startdate: " + courseDto.getStartDate());
                }
            } catch (InstanceNotFoundException | InputValidationException ex) {
                ex.printStackTrace(System.err);
            }

        } else if ("-findInscriptions".equalsIgnoreCase(args[0])) {
            validateArgs(args, 2, null);
            //[findInscriptions]  CursoServiceClient -findInscriptions <userEmail>
            try {
                List<ClientInscriptionDto> inscriptions = clientCourseService.findInscriptions(args[1]);
                System.out.println("Encontradas " + inscriptions.size() + " inscripciones con email: " + args[1] + ".");
                for (int i = 0; i < inscriptions.size(); i++) {
                    ClientInscriptionDto inscription = inscriptions.get(i);

                    System.out.println("Id Curso: " + inscription.getCourseId() +
                            ", email: " + inscription.getUserEmail() +
                            ", Tarjeta : " + inscription.getCreditCard() +
                            ", Fecha de inscripcion: " + inscription.getInscriptionDate() +
                            ", Fecha de cancelación: " + ((inscription.getCancelationDate()) != (null) ?
                            inscription.getCancelationDate().toString() : "No se ha cancelado"));
                }
            } catch (InputValidationException  ex) {
                ex.printStackTrace(System.err);
            }

        } else {
            printUsageAndExit();
        }
    }

    public static void validateArgs(String[] args, int expectedArgs,
                                    int[] numericArguments) {
        if (expectedArgs != args.length) {
            printUsageAndExit();
        }
        if (numericArguments != null) {
            for (int i = 0; i < numericArguments.length; i++) {
                int position = numericArguments[i];
                try {
                    Double.parseDouble(args[position]);
                } catch (NumberFormatException n) {
                    printUsageAndExit();
                }
            }
        }
    }

    public static void printUsageAndExit() {
        printUsage();
        System.exit(-1);
    }

    public static void printUsage() {
        System.err.println("Usage:\n" +
                "    [addCourse]         CursoServiceClient -addCourse <name> <city> <startDate> <price> <maxPlaces>\n" +
                "    [inscribe]          CursoServiceClient -inscribe <courseId> <userEmail> <creditCardNumber>\n" +
                "    [cancel]            CursoServiceClient -cancel <inscriptionId> <userEmail>\n" +
                "    [findCourses]       CursoServiceClient -findCourses <city>\n" +
                "    [findCourse]        CursoServiceClient -findCourse <courseId>\n" +
                "    [findInscriptions]  CursoServiceClient -findInscriptions <userEmail>\n");
    }

}