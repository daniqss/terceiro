from typing import Optional
from src.utils import request_data, PORT

class PosologyModel:
    @staticmethod
    def get_posologies(patient_id: int, medication_id: int) -> Optional[list]:
        url = f"http://localhost:{PORT}/{patient_id}/medications/{medication_id}/posologies"
        posologies, status = request_data(url, "GET")
        if status == 200:
            return posologies
        return None
    
    @staticmethod
    def add_posology(patient_id: int, medication_id: int, posology_id: int, hour: int, minute: int) -> Optional[dict]:
        url = f"http://localhost:{PORT}/{patient_id}/medications/{medication_id}/posologies"
        hour = max(0, min(hour, 23))
        minute = max(0, min(minute, 59))
        data = {
            "id": posology_id,
            "hour": hour,
            "minute": minute,
            "medication_id": medication_id
        }
        posology, status = request_data(url, "POST", data)
        if status == 201:
            return posology
        return None
    
    @staticmethod
    def update_posology(patient_id: int, medication_id: int, posology_id: int, hour: int, minute: int) -> Optional[dict]:
        url = f"http://localhost:{PORT}/{patient_id}/medications/{medication_id}/posologies"
        hour = max(0, min(hour, 23))
        minute = max(0, min(minute, 59))
        data = {
            "id": posology_id,
            "hour": hour,
            "minute": minute,
            "medication_id": medication_id
        }
        posology, status = request_data(url, "PATCH", data)
        if status is 204:
            return posology
        return None
    
    @staticmethod
    def delete_posology(patient_id: int, medication_id: int, posology_id: int) -> Optional[dict]:
        url = f"http://localhost:{PORT}/{patient_id}/medications/{medication_id}/posologies/{posology_id}"
        response, status = request_data(url, "DELETE")
        if status == 204:
            return response
        return None