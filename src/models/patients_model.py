from typing import Optional
from src.utils import request_data, PORT

class PatientModel:
    @staticmethod
    def get_patients() -> Optional[list]:
        url = f"http://localhost:{PORT}/patients"
        patients, status = request_data(url, "GET")
        if status == 200:
            return patients
        return None

    @staticmethod
    def add_patient(patient_id: int, code: str, name: str, surname: str) -> Optional[dict]:
        url = f"http://localhost:{PORT}/patients"
        data = {"id": patient_id, "code": code, "name": name, "surname": surname}
        patient, status = request_data(url, "POST", data)
        if status == 201:
            return patient
        return None
    
    @staticmethod
    def update_patient(patient_id: int, code: str, name: str, surname: str) -> Optional[dict]:
        url = f"http://localhost:{PORT}/patients/{patient_id}"
        data = {"id": id, "code": code, "name": name, "surname": surname}
        patient, status = request_data(url, "PATCH", data)
        if status == 204:
            return patient
        return None
    
    @staticmethod
    def delete_patient(patient_id: int) -> Optional[dict]:
        url = f"http://localhost:{PORT}/patients/{patient_id}"
        response, status = request_data(url, "DELETE")
        if status == 204:
            return response
        return None

