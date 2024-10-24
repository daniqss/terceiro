from requests.exceptions import RequestException
from enum import Enum

import gettext
gettext.bindtextdomain('ipm-2425-p_escritorio-ac-dc', 'locales')
gettext.textdomain('ipm-2425-p_escritorio-ac-dc')
_ = gettext.gettext


class DataType(Enum):
    PATIENT = "patient"
    MEDICATION = "medication"
    POSOLOGY = "posology"

class OperationType(Enum):
    GET = "GET"
    POST = "POST"
    PATCH = "PATCH"
    DELETE = "DELETE"


class NetworkErrorException(RequestException):
    def __init__(self, message):
        super().__init__(message)
        self.message = message

    def title_message(self):
        return _("Network Error")
    
    def body_message(self):
        return _("Cannot connect to the server")

class DataErrorException(Exception):
    def __init__(self, data_type: DataType, operation_type: OperationType) -> str:
        self.data_type = data_type
        self.operation_type = operation_type

    def title_message(self):
        return _("Data Error")
    
    def body_message(self):
        match self.data_type:
            case DataType.PATIENT:
                match self.operation_type:
                    case OperationType.GET:
                        return _("Cannot get patients")
                    case OperationType.POST:
                        return _("Cannot add patient")
                    case OperationType.PATCH:
                        return _("Cannot update patient")
                    case OperationType.DELETE:
                        return _("Cannot delete patient")

            case DataType.MEDICATION:
                match self.operation_type:
                    case OperationType.GET:
                        return _("Cannot get medications")
                    case OperationType.POST:
                        return _("Cannot add medication")
                    case OperationType.PATCH:
                        return _("Cannot update medication")
                    case OperationType.DELETE:
                        return _("Cannot delete medication")
            case DataType.POSOLOGY:
                match self.operation_type:
                    case OperationType.GET:
                        return _("Cannot get posologies")
                    case OperationType.POST:
                        return _("Cannot add posology")
                    case OperationType.DELETE:
                        return _("Cannot delete posology")
            case _:
                return _("Cannot get data")
