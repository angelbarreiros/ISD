package es.udc.ws.app.client.ui;

import es.udc.ws.app.client.service.ClientEventService;
import es.udc.ws.app.client.service.ClientEventServiceFactory;
import es.udc.ws.app.client.service.dto.ClientEventDto;
import es.udc.ws.app.client.service.dto.ClientReplyDto;
import es.udc.ws.app.client.service.exceptions.ClientAlreadyRepliedException;
import es.udc.ws.app.client.service.exceptions.ClientEventCancelledException;
import es.udc.ws.app.client.service.exceptions.ClientEventHasStartedException;
import es.udc.ws.app.client.service.exceptions.ClientReplysClosedException;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;

import java.time.LocalDateTime;
import java.util.List;

public class AppServiceClient {
    public static void main(String[] args) {
        if (args.length == 0) {
            printUsageAndExit();
        }
        ClientEventService clientEventService = ClientEventServiceFactory.getService();
        if ("-a".equalsIgnoreCase(args[0])) {
            validateArgs(args, 5, new int[]{});
            try{
                Long eventId = clientEventService.addEvent(new ClientEventDto(
                        null,args[1],args[2], LocalDateTime.parse(args[3]),0,0,LocalDateTime.parse(args[4]),false

                ));
                System.out.println("Event " + eventId + " created sucessfully");

            } catch (Exception ex) {
                ex.printStackTrace(System.err);
            }
        } else if ("-g".equalsIgnoreCase(args[0])) {
            validateArgs(args, 2, new int[]{});
            try{
                ClientEventDto eventDto = clientEventService.findEvent(Long.valueOf(args[1]));
                System.out.println("Found!");
                System.out.println(eventDto.toString());


            } catch (Exception ex) {
                ex.printStackTrace(System.err);
            }
        } else if ("-f".equalsIgnoreCase(args[0])){
            try {
                List<ClientEventDto> eventDtos ;
                if(args.length==2){
                    eventDtos=clientEventService.findEvents("",args[1]);
                }
                else{
                    eventDtos=clientEventService.findEvents(args[1],args[2]);
                }


                if (eventDtos.size()==0){
                    System.out.printf("Ningun evento pudo ser encontrado con esas especificaciones.");
                }
                else{
                    for (ClientEventDto event : eventDtos){
                        System.out.println(event+"\n");
                    }
                }

            } catch (InputValidationException e) {
                e.printStackTrace(System.err);
            }
        } else if ("-d".equalsIgnoreCase(args[0])){
            validateArgs(args,2,new int[]{});
            try{
                clientEventService.cancelEvent(Long.valueOf(args[1]));
                System.out.println("Evento " + args[1] + " cancelado satisfactoriamente!");

            } catch (ClientEventCancelledException | InputValidationException | InstanceNotFoundException |
                     ClientEventHasStartedException | RuntimeException e) {
                e.printStackTrace(System.err);
            }
        } else if ("-r".equalsIgnoreCase(args[0])){
            validateArgs(args,4,new int[]{1});
            try {
                Long replyId=clientEventService.replyEvent(Long.valueOf(args[1]),args[2],Boolean.parseBoolean(args[3]));
                System.out.println("Replyed " + replyId + " created sucessfully");

            } catch (ClientEventCancelledException | ClientAlreadyRepliedException | ClientReplysClosedException |
                     InputValidationException | InstanceNotFoundException e) {
                e.printStackTrace(System.err);
            }
        } else if ("-fr".equalsIgnoreCase(args[0])){
            validateArgs(args,3,new int[]{});
            try{
                List<ClientReplyDto> replyDtos=clientEventService.findReplies(args[1],Boolean.parseBoolean(args[2]));
                if (replyDtos.size()==0){
                    System.out.printf("Ninguna respuesta pudo ser encontrado con esas especificaciones.");
                }
                else{
                    for (ClientReplyDto reply : replyDtos){
                        System.out.println(reply+"\n");
                    }
                }
            } catch (InputValidationException e) {
                e.printStackTrace(System.err);
            }
        } else printUsageAndExit();
    }

    public static void validateArgs(String[] args, int expectedArgs, int[] numericArguments) {
        if (expectedArgs != args.length) {
            printUsageAndExit();
        }
        for (int i = 0; i < numericArguments.length; i++) {
            int position = numericArguments[i];
            try {
                Double.parseDouble(args[position]);
            } catch (NumberFormatException n) {
                printUsageAndExit();
            }
        }
    }

    public static void printUsageAndExit() {
        printUsage();
        System.exit(-1);
    }

    public static void printUsage() {
        System.err.println(
        "Usage:\n" +
        "[add] AppServiceClient -a <name> <description> <initCelebrationDate> <endingTimeEvent>\n"+
        "[find] AppServiceClient -g <eventId>\n"+
        "[findByKeywords] AppServiceClient -f <keywords> <endDate>\n"+
        "[cancel] AppServiceClient -d <eventId>\n"+
        "[reply] AppServiceClient -r <eventId> <email> <answered>\n"+
        "[findReplies] AppServiceClient -fr <email> <onlyAffirmativeAnswers>\n"
        );
    }
}