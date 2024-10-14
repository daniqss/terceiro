from src.model import Model
from src.view import View

class Controller:
    def __init__(self):
        self.view = View(self)
        self.model = Model()

    def run(self):
        self.view.run()

    def on_patient_selected(self, patient: dict):
        self.selected_patient = patient
        self.view.update_medication_list_panel_patient(self.selected_patient["id"], self.model.get_medications(self.selected_patient["id"]))

    def on_add_medication(self, patient_id):
        name = ""
        dosage = ""
        duration = ""
        start_date = ""
        self.view.create_medication_input_row(patient_id, name, dosage, duration, start_date)

    def on_save_medication(self, patient_id, name, dosage, duration, start_date):
        #nextId = self.model.next_medication_id(patient_id)
        self.model.add_medication(patient_id, name, dosage, start_date, duration)
        self.view.update_medication_list_panel_patient(patient_id, self.model.get_medications(patient_id))

    def on_update_medication(self, patient_id, medication_id, name, dosage, duration, start_date):
        self.model.update_medication(patient_id, medication_id, name, dosage, start_date, duration)
        self.view.update_medication_list_panel_patient(patient_id, self.model.get_medications(patient_id))

    def on_cancel_medication(self, patient_id):
        medication = self.model.get_medications(patient_id)
        self.view.update_medication_list_panel_patient(patient_id, medication)
    
    def on_edit_medication(self, patient_id, medication):
        id = medication["id"]
        name = medication["name"]
        dosage = str(medication["dosage"])
        duration = str(medication["treatment_duration"])
        start_date = str(medication["start_date"])
        self.view.update_medication(patient_id, id, name, dosage, duration, start_date)

    def on_expand_medication(self, button, container, patient_id, medication_id):
        posologies = self.model.get_posologies(patient_id, medication_id)
        self.view.update_posology_list_panel(button, container, patient_id, medication_id, posologies)

    def on_delete_medication(self, paciente_id, medication_id):
        self.model.delete_medication(paciente_id, medication_id)
        self.view.update_medication_list_panel_patient(paciente_id, self.model.get_medications(paciente_id))
        
    def on_add_posology(self, button, container, patient_id, medication_id):
        self.view.create_posology_input_row(button, container, patient_id, medication_id)

    def on_delete_posology(self, button, container, patient_id, medication_id, posology_id):
        self.model.delete_posology(patient_id, medication_id, posology_id)
        posologies = self.model.get_posologies(patient_id, medication_id)
        self.view.update_posology_list_panel(button, container, patient_id, medication_id, posologies)
        
    def on_save_posology(self, button, container, patient_id, medication_id, hour, minute):
        # nextId = self.model.next_posology_id(patient_id, medication_id)
        self.model.add_posology(patient_id, medication_id, minute, hour)
        posologies = self.model.get_posologies(patient_id, medication_id)
        self.view.update_posology_list_panel(button, container, patient_id, medication_id, posologies)

    def on_cancel_posology(self, button, container, patient_id, medication_id):
        posologies = self.model.get_posologies(patient_id, medication_id)
        self.view.update_posology_list_panel(button, container, patient_id, medication_id, posologies)

    def get_patients(self):
        return self.model.get_patients()
    
    def get_medications(self, patient_id):
        return self.model.get_medications(patient_id)
    
    def get_posologies(self, patient_id, medication_id):
        return self.model.get_posologies(patient_id, medication_id)
