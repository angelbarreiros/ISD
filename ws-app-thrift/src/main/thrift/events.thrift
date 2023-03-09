namespace java es.udc.ws.app.thrift

struct ThriftEventDto {
    1: i64 eventId
    2: string name
    3: string description
    4: string initCelebrationDate
    5: i32 nresponses
    6: i32 participantYes
    7: i16 duration
    8: bool cancelled
}

struct ThriftReplyDto {
    1: i64 replyId
    2: i64 eventId
    3: string email
    4: bool answered
}

exception ThriftInstanceNotFoundException {
    1: string instanceId
    2: string instanceType
}
exception ThriftInputValidationException {
    1: string message
}
exception ThriftAlreadyRepliedException {
    1: i64 eventId
}
exception ThriftEventCancelledException {
    1: i64 eventId
}
//No se usa "when" para las fechas
//(como en el modelo) porque
//es una palabra reservada en el thrift
exception ThriftEventHasStartedException {
    1: string startedAt
    2: i64 eventId
}
exception ThriftReplysClosedException {
    1: string closedAt
    2: i64 eventId
}

service ThriftEventService {
        ThriftEventDto addEvent(1:ThriftEventDto event) throws (1:ThriftInputValidationException e);//1

        list<ThriftEventDto> findEvents(1:string initCelebrationDate,
                                   2:string endCelebrationDate, 3:string keywords) throws (1:ThriftInputValidationException e);//2
        ThriftEventDto findEvent(1:i64 eventId)  throws (1:ThriftInputValidationException e ,2:ThriftInstanceNotFoundException ee);//3

        ThriftReplyDto replyEvent(1:i64 eventId, 2:string email, 3:bool reply) throws(
            1:ThriftInputValidationException e, 2:ThriftInstanceNotFoundException ee,
            3:ThriftEventCancelledException eee, 4:ThriftReplysClosedException eeee,
            5:ThriftAlreadyRepliedException eeeee
        );//4

        void cancelEvent(1:i64 Id) throws (1:ThriftInputValidationException e, 2:ThriftEventHasStartedException ee,
                        3:ThriftInstanceNotFoundException eee, 4:ThriftEventCancelledException eeee);//5

        list<ThriftReplyDto> findReplies(1:string email, 2:bool onlyAfirmativeAnswers) throws (1:ThriftInputValidationException e);//6
}