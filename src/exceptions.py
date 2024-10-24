from requests.exceptions import RequestException
from enum import Enum
from src.utils import get_locales

_ = get_locales()

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
    def __init__(self, status: int, data_type: DataType, operation_type: OperationType) -> str:
        self.status = status
        self.data_type = data_type
        self.operation_type = operation_type

    def title_message(self):
        return _("Data Error")
    
    def body_message(self):
        match self.data_type:
            case DataType.PATIENT:
                match self.operation_type:
                    case OperationType.GET:
                        return "{}{}".format(_("Cannot get patients, status "), self.status)
                    case OperationType.POST:
                        return "{}{}}".format(_("Cannot add patient, status"), self.status)
                    case OperationType.PATCH:
                        return "{}{}".format(_("Cannot update patient, status"), self.status)
                    case OperationType.DELETE:
                        return "{}{}".format(_("Cannot delete patient, status"), self.status)

            case DataType.MEDICATION:
                match self.operation_type:
                    case OperationType.GET:
                        return "{}{}".format(_("Cannot get medications, status"), self.status)
                    case OperationType.POST:
                        return "{}{}".format(_("Cannot add medication, status"), self.status)
                    case OperationType.PATCH:
                        return "{}{}".format(_("Cannot update medication, status"), self.status)
                    case OperationType.DELETE:
                        return "{}{}".format(_("Cannot delete medication, status"), self.status)

            case DataType.POSOLOGY:
                match self.operation_type:
                    case OperationType.GET:
                        return "{}{}".format(_("Cannot get posologies, status"), self.status)
                    case OperationType.POST:
                        return "{}{}".format(_("Cannot add posology, status"), self.status)
                    case OperationType.DELETE:
                        return "{}{}".format(_("Cannot delete posology, status"), self.status)
                    case _:
                        return "{}{}".format(_("Cannot get data, status"), self.status)

