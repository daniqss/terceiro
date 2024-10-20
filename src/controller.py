from typing import List
from src.model import Model
from src.view import View
from src.exceptions import NetworkErrorException
from src.exceptions import DataErrorException

class Controller:
    def __init__(self):
        self.view = View(self)
        self.model = Model()

    def run(self):
        self.view.run()

    def on_get_patients(self):
        patients = self.get_patients()
        if patients == []:
            self.view.show_dialog("Network Error", "No se han encontrado pacientes")
        return patients 
        
    def on_patient_selected(self, patient: dict):
        self.selected_patient = patient
        try: 
            medications = self.model.get_medications(patient["id"])
            self.view.update_medication_list_panel_patient(self.selected_patient["id"], medications)
        except NetworkErrorException as e:
            print(f"Error: {e}")
            self.view.show_dialog("Network Error", "Cannot get medications")
        except DataErrorException as e:
            print(f"Error: {e}")
            self.view.show_dialog("Error", "Cannot get medications")

    def on_refresh_patients(self):
        patients = self.get_patients()
        self.view.update_patients(patients)
        if patients != []:
            self.view.update_patient_list(patients)
        else: 
            self.view.show_dialog("Network Error", "Cannot connect to the server")

    def on_add_medication(self, patient_id):
        name = ""
        dosage = ""
        duration = ""
        start_date = ""
        self.view.create_medication_input_row(patient_id, name, dosage, duration, start_date)

    def on_save_medication(self, patient_id, name, dosage, duration, start_date):
        try:
            self.model.add_medication(patient_id, name, dosage, start_date, duration)
            self.view.update_medication_list_panel_patient(patient_id, self.model.get_medications(patient_id))

        except NetworkErrorException as e:
            print(f"Error: {e}")
            self.view.show_dialog("Network Error", "Medication could not be added")
        except DataErrorException as e:
            print(f"Error: {e}")
            self.view.show_dialog("Error", "Medication could not be added")

    def on_update_medication(self, patient_id, medication_id, name, dosage, duration, start_date):
        try:
            self.model.update_medication(patient_id, medication_id, name, dosage, start_date, duration)
            self.view.update_medication_list_panel_patient(patient_id, self.model.get_medications(patient_id))
            
        except NetworkErrorException as e:
            print(f"Error: {e}")
            self.view.show_dialog("Network Error", "Cannot get medications")
        except DataErrorException as e:
            print(f"Error: {e}")
            self.view.show_dialog("Error", "Cannot get medications")

    def on_cancel_medication(self, patient_id):
        try: 
            medication = self.model.get_medications(patient_id)
            self.view.update_medication_list_panel_patient(patient_id, medication)

        except NetworkErrorException as e:
            print(f"Error: {e}")
            self.view.show_dialog("Network Error", "Cannot get medications")
        except DataErrorException as e:
            print(f"Error: {e}")
            self.view.show_dialog("Error", "Cannot get medications")
    
    def on_edit_medication(self, patient_id, medication):
        id = medication["id"]
        name = medication["name"]
        dosage = str(medication["dosage"])
        duration = str(medication["treatment_duration"])
        start_date = str(medication["start_date"])
        self.view.update_medication(patient_id, id, name, dosage, duration, start_date)

    def on_expand_medication(self, button, container, patient_id, medication_id):
        try:
            posologies = self.model.get_posologies(patient_id, medication_id)
            self.view.update_posology_list_panel(button, container, patient_id, medication_id, posologies)

        except NetworkErrorException as e:
            print(f"Error: {e}")
            self.view.show_dialog("Network Error", "Cannot get posologies")
        except DataErrorException as e:
            print(f"Error: {e}")
            self.view.show_dialog("Error", "Cannot get posologies")

    def on_delete_medication(self, paciente_id, medication_id):
        try:
            self.model.delete_medication(paciente_id, medication_id)
            self.view.update_medication_list_panel_patient(paciente_id, self.model.get_medications(paciente_id))

        except NetworkErrorException as e:
            print(f"Error: {e}")
            self.view.show_dialog("Network Error", "Cannot delete medication")
        except DataErrorException as e:
            print(f"Error: {e}")
            self.view.show_dialog("Error", "Cannot delete medication")
        
    def on_add_posology(self, button, container, patient_id, medication_id):
        self.view.create_posology_input_row(button, container, patient_id, medication_id)

    def on_delete_posology(self, button, container, patient_id, medication_id, posology_id):
        try:
            self.model.delete_posology(patient_id, medication_id, posology_id)
            
            try:
                posologies = self.model.get_posologies(patient_id, medication_id)
                self.view.update_posology_list_panel(button, container, patient_id, medication_id, posologies)
                
            except NetworkErrorException as e:
                print(f"Error: {e}")
                self.view.show_dialog("Network Error", "Cannot get posologies")
            except DataErrorException as e:
                self.view.show_dialog("Error", "Cannot get posologies")

        except NetworkErrorException as e:
            print(f"Error: {e}")
            self.view.show_dialog("Network Error", "Cannot get posologies")
        except DataErrorException as e:
            print(f"Error: {e}")
            self.view.show_dialog("Error", "Cannot get posologies")
        
    def on_save_posology(self, button, container, patient_id, medication_id, hour, minute):
        try:
            self.model.add_posology(patient_id, medication_id, minute, hour)
            try:
                posologies = self.model.get_posologies(patient_id, medication_id)
                self.view.update_posology_list_panel(button, container, patient_id, medication_id, posologies)
                
            except NetworkErrorException as e:
                print(f"Error: {e}")
                self.view.show_dialog("Network Error", "Cannot get posologies")
            except DataErrorException as e:
                print(f"Error: {e}")
                self.view.show_dialog("Error", "Cannot get posologies")
            
        except NetworkErrorException as e:
            print(f"Error: {e}")
            self.view.show_dialog("Network Error", "Cannot add posology")
        except DataErrorException as e:
            print(f"Error: {e}")
            self.view.show_dialog("Error", "Cannot add posology")

    def on_cancel_posology(self, button, container, patient_id, medication_id):
        try:
            posologies = self.model.get_posologies(patient_id, medication_id)
            self.view.update_posology_list_panel(button, container, patient_id, medication_id, posologies)

        except NetworkErrorException as e:
            print(f"Error: {e}")
            self.view.show_dialog("Network Error", "Cannot get posologies")
        except DataErrorException as e:
            print(f"Error: {e}")
            self.view.show_dialog("Error", "Cannot get posologies")

    def get_patients(self):
        try:
            return self.model.get_patients()
        except Exception as e:
            print(f"Error: {e}")
            return []    
        
    def get_medications(self, patient_id) -> List[dict]:
        try:
            return self.model.get_medications(patient_id)
        except Exception as e:
            print(f"Error: {e}")
            return []
    
    def get_posologies(self, patient_id, medication_id):
        try:
            return self.model.get_posologies(patient_id, medication_id)
        except Exception as e:
            print(f"Error: {e}")
            return []
