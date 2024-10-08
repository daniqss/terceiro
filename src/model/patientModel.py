from typing import Optional
from utils import request_data, PORT

class PatientModel:
    @staticmethod
    def getPatients() -> Optional[dict]:
        url = f"http://localhost:{PORT}/patients"
        return request_data(url, "GET")

