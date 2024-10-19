from typing import List, Optional
from src.utils import request_data, PORT, HOST
from requests import request
from src.exceptions import DataErrorException

PATH = f"http://{HOST}:{PORT}"

class Model:
    def __init__(self):
        pass
    
    #region Patient

    def get_patients(self) -> List[dict]:
        url = f"{PATH}/patients"
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
        url = f"{PATH}/patients/{patient_id}"
        patient, status = request_data(url, "GET")
        if status != 200:
            raise DataErrorException("Error getting patient")
        return patient
    #endregion
    
    #region Medication

    def get_medications(self, patient_id: int) -> Optional[List[dict]]:
        url = f"{PATH}/patients/{patient_id}/medications"
        medications, status = request_data(url, "GET")

        if status != 200:
            raise DataErrorException("Error getting medications")
        return medications

    def get_medication(self, patient_id: int, medication_id: int) -> Optional[dict]:
        medication, status = request_data(
            url=f"{PATH}/patients/{patient_id}/medications/{medication_id}",
            method="GET"
        )
        
        if status != 200:
            raise DataErrorException("Error getting medication")
        return medication

    def delete_medication(self, patient_id: int, medication_id: int):
        response, status = request_data(
            f"{PATH}/patients/{patient_id}/medications/{medication_id}",
            "DELETE"
        )

        if status != 204:
            raise DataErrorException("Error deleting medication")
        

    def add_medication(
            self,
            patient_id: int,
            name: str,
            dosage:int,
            start_date: str,
            treatement_duration: int
        ) -> dict:
        response, status = request_data(
            f"{PATH}/patients/{patient_id}/medications", 
            "POST", 
            {
                "name": name, 
                "dosage": dosage,             
                "start_date": start_date,
                "treatment_duration": treatement_duration,  
                "patient_id": patient_id
            }
        )
        if status != 201:
            raise DataErrorException("Error adding medication")
        return response

    def update_medication(
            self,
            patient_id: int,
            medication_id: int,
            name: str,
            dosage:int,
            start_date: str,
            treatement_duration: int
        ) -> dict:
        response, status = request_data(
            f"{PATH}/patients/{patient_id}/medications/{medication_id}", 
            "PATCH", 
            {
                "name": name, 
                "dosage": dosage,             
                "start_date": start_date,
                "treatment_duration": treatement_duration,  
                "patient_id": patient_id
            }
        )

        if status != 204:
            raise DataErrorException("Error updating medication")
        return response
    
    #endregion

    #region Posogies

    def get_posologies(self, patient_id: int, medication_id: int) -> Optional[List[dict]]:
        posologies, status = request_data(
            f"{PATH}/patients/{patient_id}/medications/{medication_id}/posologies",
            "GET"
        )
        
        if status != 200:
            raise DataErrorException("Error getting posologies")
        return posologies

    def delete_posology(self, patient_id: int, medication_id: int, posology_id: int) -> bool:
        response, status = request_data(
            f"{PATH}/patients/{patient_id}/medications/{medication_id}/posologies/{posology_id}",
            "DELETE"
        )

        if status != 204:
            raise DataErrorException("Error deleting posology")
        return response
        
    def add_posology(self, patient_id: int, medication_id: int, minute:int, hour:int) -> bool:
        response, status = request_data(
            f"{PATH}/patients/{patient_id}/medications/{medication_id}/posologies", 
            "POST", 
            {
                "medication_id": medication_id,
                "minute": minute,             
                "hour": hour
            }
        )

        if status != 201:
            raise DataErrorException("Error adding posology")
        return response

    def update_posology(self, patient_id: int, medication_id: int, posology_id:int, minute:int, hour:int):
        response, status = request_data(
            f"{PATH}/patients/{patient_id}/medications/{medication_id}/posologies/{posology_id}", 
            "PATCH",
            {
                "id": posology_id,
                "hour": hour,
                "minute": minute,             
                "medication_id": medication_id
            }
        )

        if status != 204:
            raise DataErrorException("Error updating posology")
        return response

    #endregion