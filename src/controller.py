from src.model import Model
from src.view import View

class Controller:
    def __init__(self):
        self.view = View(self)
        self.model = Model()

    def run(self):
        self.view.run()

    def on_patient_selected(self, _listbox, row, patients):
        self.selected_patient = patients[row.get_index()]
        print(f"Selected patient: {self.selected_patient["id"]}")
        self.view.update_medication_list_panel_patient(self.selected_patient["id"], self.model.get_medications(self.selected_patient["id"]))

    def on_add_medication(self, button):
        medication = self.view.get_medication_data()
        self.model.add_medication(medication)
        self.view.update_medication_list(self.model.get_medications())
    
    def on_edit_medication(self, button):
        medication = self.view.get_selected_medication()
        if medication:
            updated_medication = self.view.get_medication_data(medication)
            self.model.update_medication(medication.id, updated_medication)
            self.view.update_medication_list(self.model.get_medications())

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
