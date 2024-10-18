from requests.exceptions import RequestException

class NetworkErrorException(RequestException):
    def __init__(self, message):
        super().__init__(message)
        self.message = message

class DataErrorException(Exception):
    def __init__(self, message):
        super().__init__(message)
        self.message = message

