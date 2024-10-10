from typing import Optional
from src.utils import request_data, PORT

class IntakesModel:
    @staticmethod
    def get_intakes(patient_id: int, medication_id: int) -> Optional[list]:
        url = f"http://localhost:{PORT}/{patient_id}/medications/{medication_id}/intakes"
        intakes, status = request_data(url, "GET")
        if status == 200:
            return intakes
        return None
    
    @staticmethod
    def add_intake(patient_id: int, medication_id: int, intake_id: int, date: str) -> Optional[dict]:
        url = f"http://localhost:{PORT}/{patient_id}/medications/{medication_id}/intakes"
        data = {
            "id": intake_id,
            "intake_date": date,
            "medication_id": medication_id
        }
        intake, status = request_data(url, "POST", data)
        if status == 201:
            return intake
        return None
    
    @staticmethod
    def delete_intake(patient_id: int, medication_id: int, intake_id: int) -> Optional[dict]:
        url = f"http://localhost:{PORT}/{patient_id}/medications/{medication_id}/intakes/{intake_id}"
        response, status = request_data(url, "DELETE")
        if status == 204:
            return response
        return None