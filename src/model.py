from typing import List, Any, Optional
from src.utils import request_data, PORT
from requests import request
import json

path = f"http://localhost:{PORT}"

headers = {
    "Content-Type": "application/json"  # Ensure the request sends JSON data
}

#TODO: Improve logs messages, return more significant values on functions that update the server

class Model:
    def __init__(self):
        pass
    
    #region Patient

    def get_patients(self) -> List[dict]:
        url = f"{path}/patients"
        patients, status = request_data(url, "GET")
        if status == 200:
            return patients
        return []
    
    def get_patient_by_code(self, code: str) -> Optional[dict]:
        patients = self.get_patients()
        for patient in patients:
            if patient["code"] == code:
                return patient

    def get_patient(self, patient_id: int) -> Optional[dict]:
        url = f"{path}/patients/{patient_id}"
        patient, status = request_data(url, "GET")
        if status == 200:
            return patient

    #endregion
    
    #region Medication

    def get_medications(self, patient_id: int) -> Optional[List[dict]]:
        url = f"{path}/patients/{patient_id}/medications"
        medications, status = request_data(url, "GET")
        if status == 200:
            return medications

    def get_medication(self, patient_id: int, medication_id: int) -> Optional[dict]:
        url = f"{path}/patients/{patient_id}/medications/{medication_id}"
        medication, status = request_data(url, "GET")
        if status == 200:
            return medication

    def delete_medication(self, patient_id: int, medication_id: int) -> bool:
        url = f"{path}/patients/{patient_id}/medications/{medication_id}"
        _, status = request_data(url, "DELETE")
        if status == 204:
            print(f"Medication {medication_id} deleted successfully for patient {patient_id}.")
            return True
        
        #FIXME borra pero muestra el errores de todos modos
        
        print(f"Failed to delete medication {medication_id} for patient {patient_id}. Status code: {status}")
        return False

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
        print(f"--> {response}")

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
        print(f"--> {response}")
    
    #endregion

    #region Posogies

    def get_posologies(self, patient_id: int, medication_id: int) -> Optional[List[dict]]:
        url = f"{path}/patients/{patient_id}/medications/{medication_id}/posologies"
        posologies, status = request_data(url, "GET")
        if status == 200:
            return posologies

    def delete_posology(self, patient_id: int, medication_id: int, posology_id: int) -> bool:
        url = f"{path}/patients/{patient_id}/medications/{medication_id}/posologies/{posology_id}"
        _, status = request_data(url, "DELETE")
        if status == 204:
            print(f"Posology deleted successfully.")
            return True
        
        #FIXME borra pero muestra el errores de todos modos
        print(f"Failed to delete posology")
        return False
        
    def add_posology(self, patient_id: int, medication_id: int, minute:int, hour:int):
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
        print(f"--> {response}")

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
        print(f"--> {response}")

    #endregion