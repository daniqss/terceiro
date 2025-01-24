namespace java es.udc.ws.app.thrift

struct ThriftCourseDto {
    1: i64 courseId
    2: string name
    3: string city
    4: string creationDate
    5: string startDate
    6: double price
    7: i32 maxSpots
    8: i32 vacantSpots
}

struct ThriftInscriptionDto {
    1: i64 inscriptionId
    2: i64 courseId
    3: string inscriptionDate
    4: string cancelationDate
    5: string userEmail
    6: string creditCard
}

exception ThriftInputValidationException {
    1: string message
}

exception ThriftInstanceNotFoundException {
    1: string instanceId
    2: string instanceType
}

exception ThriftCancelTooCloseToCourseStartException {
    1: i64 inscriptionId
    2: i64 courseId
    3: string startDate
    4: string cancellationDate
}

exception ThriftCourseAlreadyStartedException {
    1: i64 courseId
    2: string startDate
}

exception ThriftCourseFullException {
    1: i64 courseId
}

exception ThriftCourseStartTooSoonException {
    1: i64 courseId
    2: string startDate
    3: string creationDate
}

exception ThriftIncorrectUserException {
    1: i64 inscriptionId
    2: string userEmail
}

exception ThriftInscriptionAlreadyCancelledException {
    1: i64 inscriptionId
    2: string userEmail
    3: string cancellationDate
}

exception ThriftCourseNotFoundException {
    1: i64 courseId
}

service ThriftCourseService {

    ThriftCourseDto addCourse(1: ThriftCourseDto courseDto) throws (1: ThriftInputValidationException e, 2: ThriftCourseStartTooSoonException ee);

    list<ThriftCourseDto> findCourses(1: string city) throws (1: ThriftInputValidationException e);

    ThriftCourseDto findCourse(1: i64 courseId) throws (1: ThriftInputValidationException e, 2: ThriftInstanceNotFoundException ee);

    ThriftInscriptionDto addInscription(1: i64 courseId, 2: string userEmail, 3: string bankCardNumber) throws (1: ThriftInputValidationException e, 2: ThriftInstanceNotFoundException ee, 3: ThriftCourseAlreadyStartedException eee, 4: ThriftCourseFullException eeee);

    void cancelInscription(1: i64 inscriptionId, 2: string userEmail) throws (1: ThriftInstanceNotFoundException e, 2: ThriftInputValidationException ee, 3: ThriftIncorrectUserException eee, 4: ThriftInscriptionAlreadyCancelledException eeee, 5: ThriftCancelTooCloseToCourseStartException eeeee);

    list<ThriftInscriptionDto> findInscriptions(1: string userEmail) throws (1: ThriftInputValidationException e);
}