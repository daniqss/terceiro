from typing import List, Any, Optional
from src.utils import request_data, PORT
from requests import request
from src.exceptions import NetworkErrorException
from src.exceptions import DataErrorException
import json

path = f"http://localhost:{PORT}"

headers = {
    "Content-Type": "application/json"  # Ensure the request sends JSON data
}


class Model:
    def __init__(self):
        pass
    
    #region Patient

    def get_patients(self) -> List[dict]:
        url = f"{path}/patients"
        patients, status = request_data(url, "GET")
        if status != 200:
            raise DataErrorException("Error getting patients")
        return patients
    
    def get_patient_by_code(self, code: str) -> Optional[dict]:
        patients = self.get_patients()
        for patient in patients:
            if patient["code"] == code:
                return patient

    def get_patient(self, patient_id: int) -> Optional[dict]:
        url = f"{path}/patients/{patient_id}"
        patient, status = request_data(url, "GET")
        if status != 200:
            raise DataErrorException("Error getting patient")
        return patient
    #endregion
    
    #region Medication

    def get_medications(self, patient_id: int) -> Optional[List[dict]]:
        url = f"{path}/patients/{patient_id}/medications"
        medications, status = request_data(url, "GET")

        if status != 200:
            raise DataErrorException("Error getting medications")
        return medications

    def get_medication(self, patient_id: int, medication_id: int) -> Optional[dict]:
        url = f"{path}/patients/{patient_id}/medications/{medication_id}"
        medication, status = request_data(url, "GET")
        
        if status != 200:
            raise DataErrorException("Error getting medication")
        return medication

    def delete_medication(self, patient_id: int, medication_id: int):
        
        url = f"{path}/patients/{patient_id}/medications/{medication_id}"
        response, status = request_data(url, "DELETE")
        #FIXME api returns 404 but the medication is deleted
        if status != 204:
            raise DataErrorException("Error deleting medication")
        

    # Returns the lowest medication id avaliable for a patient

    def add_medication(self, patient_id: int, name: str, dosage:int, start_date: str, treatement_duration: int):
        response = request(
            url=f"{path}/patients/{patient_id}/medications", 
            method="POST", 
            data=json.dumps({
                "name": name, 
                "dosage": dosage,             
                "start_date": start_date,
                "treatment_duration": treatement_duration,  
                "patient_id": patient_id
            }),
            headers=headers
        )
        if response.status_code != 201:
            raise DataErrorException("Error adding medication")

    def update_medication(self, patient_id: int, medication_id: int, name: str, dosage:int, start_date: str, treatement_duration: int):
        response = request(
            url=f"{path}/patients/{patient_id}/medications/{medication_id}", 
            method="PATCH", 
            data=json.dumps({
                "name": name, 
                "dosage": dosage,             
                "start_date": start_date,
                "treatment_duration": treatement_duration,  
                "patient_id": patient_id
            }),
            headers=headers
        )
        if response.status_code != 204:
            raise DataErrorException("Error updating medication")
    
    #endregion

    #region Posogies

    def get_posologies(self, patient_id: int, medication_id: int) -> Optional[List[dict]]:
        url = f"{path}/patients/{patient_id}/medications/{medication_id}/posologies"
        posologies, status = request_data(url, "GET")
        if status == 200:
            return posologies
        else:
            raise DataErrorException("Error getting posologies")

    def delete_posology(self, patient_id: int, medication_id: int, posology_id: int) -> bool:
        url = f"{path}/patients/{patient_id}/medications/{medication_id}/posologies/{posology_id}"
        _, status = request_data(url, "DELETE")
        if status != 204:
            raise DataErrorException("Error deleting posology")
        
    def add_posology(self, patient_id: int, medication_id: int, minute:int, hour:int) -> bool:
        response = request(
            url=f"{path}/patients/{patient_id}/medications/{medication_id}/posologies", 
            method="POST", 
            data=json.dumps({
                "medication_id": medication_id,
                "minute": minute,             
                "hour": hour
            }),
            headers=headers
        )
        if response.status_code != 201:
            raise DataErrorException("Error adding posology")

    def update_posology(self, patient_id: int, medication_id: int, posology_id:int, minute:int, hour:int):
        response = request(
            url=f"{path}/patients/{patient_id}/medications/{medication_id}/posologies/{posology_id}", 
            method="PATCH",
            data=json.dumps({
                "id": posology_id,
                "hour": hour,
                "minute": minute,             
                "medication_id": medication_id
            }),
            headers=headers
        )
        if response.status_code != 204:
            raise DataErrorException("Error updating posology")

    #endregion