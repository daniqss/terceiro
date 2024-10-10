from typing import List
from src.model import Model
from src.view import View

class Controller:
    def __init__(self):
        self.view = View(self)
        self.model = Model()

    def run(self):
        self.view.run()
        
    def get_medications(self, patient_id):
        medications = [
            {"name": "OFTAGEN COMPUESTO", "dosage": 2.0, "treatment_duration": 13, "id": 1, "start_date": "2010-11-23", "patient_id": 1},
            {"name": "BAJATEN-D", "dosage": 1.5, "treatment_duration": 62, "id": 2, "start_date": "2023-02-05", "patient_id": 1},
            {"name": "HELIOFOL 1 MG Y 5 MG Comprimidos", "dosage": 0.75, "treatment_duration": 44, "id": 3, "start_date": "2004-01-06", "patient_id": 1}
        ]

        # Filtrar los medicamentos por patient_id
        return medications

    def get_posologies(self, patient_id, medication_id):
        return [{"medication_id":1,"id":1,"hour":22,"minute":0},{"medication_id":1,"id":2,"hour":10,"minute":0}]

    def get_patients(self) -> List[dict]:
        return [
            {"name": "Jessica", "surname": "Horne", "id": 1, "code": "597-35-8499"},
            {"name": "Joy", "surname": "Lozano", "id": 2, "code": "873-08-4337"},
            {"name": "Mary", "surname": "Kelly", "id": 3, "code": "677-75-4864"}
        ]

    def delete_patient(self, patient: dict):
        print(f"Deleting patient {patient.get('name')}")

    def update_patient(self, patient: dict):
        print(f"Updating patient {patient.get('name')}")    
