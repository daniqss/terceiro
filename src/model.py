from typing import List, Optional
from src.utils import request_data, PORT, HOST, block_execution
from src.exceptions import DataErrorException, DataType, OperationType

PATH = f"http://{HOST}:{PORT}"


class Model:
    def __init__(self):
        pass
    
    #region Patient
    @block_execution
    def get_patients(self) -> List[dict]:
        url = f"{PATH}/patients"
        patients, status = request_data(url, "GET")
        if status != 200:
            raise DataErrorException(status, DataType.PATIENT, OperationType.GET)
        return patients
    
    @block_execution
    def get_patient_by_code(self, code: str) -> Optional[dict]:
        patients = self.get_patients()
        for patient in patients:
            if patient["code"] == code:
                return patient

    @block_execution
    def get_patient(self, patient_id: int) -> Optional[dict]:
        url = f"{PATH}/patients/{patient_id}"
        patient, status = request_data(url, "GET")
        if status != 200:
            raise DataErrorException(status, DataType.PATIENT, OperationType.GET)
        return patient
    #endregion
    
    #region Medication
    @block_execution
    def get_medications(self, patient_id: int) -> Optional[List[dict]]:
        url = f"{PATH}/patients/{patient_id}/medications"
        medications, status = request_data(url, "GET")

        if status != 200:
            raise DataErrorException(status, DataType.MEDICATION, OperationType.GET)
        return medications

    @block_execution
    def get_medication(self, patient_id: int, medication_id: int) -> Optional[dict]:
        medication, status = request_data(
            url=f"{PATH}/patients/{patient_id}/medications/{medication_id}",
            method="GET"
        )
        
        if status != 200:
            raise DataErrorException(status, DataType.MEDICATION, OperationType.GET)
        return medication

    @block_execution
    def delete_medication(self, patient_id: int, medication_id: int):
        response, status = request_data(
            f"{PATH}/patients/{patient_id}/medications/{medication_id}",
            "DELETE"
        )

        if status != 204:
            raise DataErrorException(status, DataType.MEDICATION, OperationType.DELETE)
        
    # Returns the lowest medication id avaliable for a patient
    @block_execution
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
            raise DataErrorException(status, DataType.MEDICATION, OperationType.POST)
        return response

    @block_execution
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
            raise DataErrorException(status, DataType.MEDICATION, OperationType.PATCH)
        return response
    
    #endregion

    #region Posogies
    @block_execution
    def get_posologies(self, patient_id: int, medication_id: int) -> Optional[List[dict]]:
        posologies, status = request_data(
            f"{PATH}/patients/{patient_id}/medications/{medication_id}/posologies",
            "GET"
        )
        
        if status != 200:
            raise DataErrorException(status, DataType.POSOLOGY, OperationType.GET)
        return posologies

    @block_execution
    def delete_posology(self, patient_id: int, medication_id: int, posology_id: int) -> bool:
        response, status = request_data(
            f"{PATH}/patients/{patient_id}/medications/{medication_id}/posologies/{posology_id}",
            "DELETE"
        )

        if status != 204:
            raise DataErrorException(status, DataType.POSOLOGY, OperationType.DELETE)
        return response
        
    
    @block_execution
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
            raise DataErrorException(status, DataType.POSOLOGY, OperationType.POST)
        return response

    @block_execution
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
            raise DataErrorException(status, DataType.POSOLOGY, OperationType.PATCH)
        return response

    #endregion
