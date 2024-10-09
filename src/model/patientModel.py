from typing import Optional
from utils import request_data, PORT

class PatientModel:
    @staticmethod
    def getPatients() -> Optional[list]:
        url = f"http://localhost:{PORT}/patients"
        patients, status = request_data(url, "GET")
        if status is 200:
            return patients
        return None

    def addPatient(id: int, code: str, name: str, surname: str) -> Optional[dict]:
        url = f"http://localhost:{PORT}/patients"
        data = {"id": id, "code": code, "name": name, "surname": surname}
        response, status = request_data(url, "POST", data)
        if status is 201:
            return response
        return None
    
    def updatePatient(id: int, code: str, name: str, surname: str) -> Optional[dict]:
        url = f"http://localhost:{PORT}/patients/{id}"
        data = {"id": id, "code": code, "name": name, "surname": surname}
        response, status = request_data(url, "PATCH", data)
        if status is 204:
            return response
        return None
    
    def deletePatient(id: int) -> Optional[dict]:
        url = f"http://localhost:{PORT}/patients/{id}"
        response, status = request_data(url, "DELETE")
        if status is 204:
            return response
        return None

