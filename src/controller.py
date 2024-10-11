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
        medication = {
            "name": name,
            "dosage": dosage,
            "treatment_duration": duration,
            "start_date": start_date,
            "patient_id": patient_id
        }
        # self.model.add_medication(medication)
        self.view.update_medication_list_panel_patient(patient_id, self.model.get_medications(patient_id))

    def on_cancel_medication(self, patient_id):
        medication = self.model.get_medications(patient_id)
        self.view.update_medication_list_panel_patient(patient_id, medication)
    
    def on_edit_medication(self, patient_id, medication):
        name = medication["name"]
        dosage = str(medication["dosage"])
        duration = str(medication["treatment_duration"])
        start_date = str(medication["start_date"])
        self.view.update_medication(patient_id, name, dosage, duration, start_date)

    def on_expand_medication(self, button, container, patient_id, medication_id):
        posologies = self.model.get_posologies(patient_id, medication_id)
        self.view.update_posology_list_panel(button, container, posologies)

    def on_delete_medication(self, button):
        # Lógica para eliminar un medicamento
        medication = self.view.get_selected_medication()
        if medication:
            self.model.delete_medication(medication.id)
            self.view.update_medication_list(self.model.get_medications())

    def on_add_posology(self, button):
        # Lógica para añadir una posología
        posology = self.view.get_posology_data()
        self.model.add_posology(posology)
        self.view.update_posology_list(self.model.get_posologies())

    def on_delete_posology(self, button):
        # Lógica para eliminar una posología
        posology = self.view.get_selected_posology()
        if posology:
            self.model.delete_posology(posology.id)
            self.view.update_posology_list(self.model.get_posologies())

    def get_patients(self):
        return self.model.get_patients()
    
    def get_medications(self, patient_id):
        return self.model.get_medications(patient_id)
    
    def get_posologies(self, patient_id, medication_id):
        return self.model.get_posologies(patient_id, medication_id)
