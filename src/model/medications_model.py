from typing import Optional
from src.utils import request_data, PORT

class MedicationsModel:
    @staticmethod
    def get_medications(patient_id: int) -> Optional[list]:
        url = f"http://localhost:{PORT}/{patient_id}/medications"
        medications, status = request_data(url, "GET")
        if status == 200:
            return medications
        return None
    
    @staticmethod
    def add_medication(patient_id: int, medication_id: int, name: str, dosage: int, start_date: str, treatment_duration: int) -> Optional[dict]:
        url = f"http://localhost:{PORT}/{patient_id}/medications"
        data = {
            "id": medication_id,
            "name": name,
            "dosage": dosage,
            "start_date": start_date,
            "treatment_duration": treatment_duration,
            "patient_id": patient_id
        }
        medication, status = request_data(url, "POST", data)
        if status == 201:
            return medication
        return None
    
    @staticmethod
    def update_medication(patient_id: int, medication_id: int, name: str, dosage: int, start_date: str, treatment_duration: int) -> Optional[dict]:
        url = f"http://localhost:{PORT}/{patient_id}/medications/{id}"
        data = {
            "id": medication_id,
            "name": name,
            "dosage": dosage,
            "start_date": start_date,
            "treatment_duration": treatment_duration,
            "patient_id": patient_id
        }
        medication, status = request_data(url, "PATCH", data)
        if status == 204:
            return medication
        return None
    
    @staticmethod
    def deleteMedication(patient_id: int, medication_id: int) -> Optional[dict]:
        url = f"http://localhost:{PORT}/{patient_id}/medications/{medication_id}"
        response, status = request_data(url, "DELETE")
        if status is 204:
            return response
        return None
