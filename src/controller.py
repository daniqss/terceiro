from typing import List
from src.utils import run_async
from src.model import Model
from src.view import View
from src.translations import _

class Controller:
    def __init__(self):
        self.view = View(self)
        self.model = Model()
        self.selected_patient = None
        self.abbort_operation = False

    def run(self):
        self.view.run()
    
    def on_get_patients(self):
        patients = self.get_patients()
        return patients 
    
    @run_async
    def on_patient_selected(self, patient: dict):
        self.selected_patient = patient["id"]
        self.abbort_operation = True
        try:
            medications = self.model.get_medications(patient["id"])
            if self.selected_patient == patient["id"]:
                self.abbort_operation = False
                self.view.update_medication_list_panel_patient(patient["id"], medications)
        except Exception as e:
            self.view.show_dialog(e)

    @run_async
    def on_refresh_patients(self):
        patients = self.get_patients()
        self.view.run_on_main(lambda: self.view.update_patients(patients))
        if self.abbort_operation:
            return
        if patients != []:
            self.view.run_on_main(lambda: self.view.update_patient_list(patients))


    def on_add_medication(self, patient_id):
        name = ""
        dosage = ""
        duration = ""
        start_date = ""
        self.view.create_medication_input_row(patient_id, name, dosage, duration, start_date)

    @run_async
    def on_save_medication(self, patient_id, name, dosage, duration, start_date):
        try:
            self.model.add_medication(patient_id, name, dosage, start_date, duration)
            if self.abbort_operation:
                return
            self.view.update_medication_list_panel_patient(patient_id, self.model.get_medications(patient_id))

        except Exception as e:
            self.view.show_dialog(e)

    @run_async
    def on_update_medication(self, patient_id, medication_id, name, dosage, duration, start_date):
        try:
            self.model.update_medication(patient_id, medication_id, name, dosage, start_date, duration)
            if self.abbort_operation:
                return
            self.view.update_medication_list_panel_patient(patient_id, self.model.get_medications(patient_id))
            
        except Exception as e:
            self.view.show_dialog(e)

    @run_async
    def on_cancel_medication(self, patient_id):
        try: 
            medication = self.model.get_medications(patient_id)
            if self.abbort_operation:
                return
            self.view.update_medication_list_panel_patient(patient_id, medication)

        except Exception as e:
            self.view.show_dialog(e)
    
    def on_edit_medication(self, patient_id, medication):
        id = medication["id"]
        name = medication["name"]
        dosage = str(medication["dosage"])
        duration = str(medication["treatment_duration"])
        start_date = str(medication["start_date"])
        self.view.update_medication(patient_id, id, name, dosage, duration, start_date)

    def on_edit_posology(self, button, container, patient_id, medication_id, posology):
        hour = posology["hour"]
        minute = posology["minute"]
        self.view.update_posology_input_row(button, container, patient_id, medication_id, posology, hour, minute)

    @run_async
    def on_expand_medication(self, button, container, patient_id, medication_id):
        try:
            posologies = self.model.get_posologies(patient_id, medication_id)
            if self.abbort_operation:
                return
            self.view.update_posology_list_panel(button, container, patient_id, medication_id, posologies)

        except Exception as e:
            self.view.show_dialog(e)

    def on_delete_medication(self, paciente_id, medication_id):
        @run_async
        def on_confirmation():
            try:
                self.model.delete_medication(paciente_id, medication_id)
                if self.abbort_operation:
                    return
                self.view.update_medication_list_panel_patient(paciente_id, self.model.get_medications(paciente_id))

            except Exception as e:
                self.view.show_dialog(e)

        self.view.show_confirmation_dialog(
            _("Confirm deletion"), 
            _("Are you sure you want to delete de medication?"), 
            on_confirmation
        )
        
    def on_add_posology(self, button, container, patient_id, medication_id):
        hour = ""
        minute = ""
        self.view.create_posology_input_row(button, container, patient_id, medication_id, hour, minute)

    def on_confirm_update_posology(self, button, container, patient_id, medication_id, posology_id, hour, minute):
        try:
            self.model.update_posology(patient_id, medication_id, posology_id, minute, hour)
            if self.abbort_operation:
                return
            try:
                posologies = self.model.get_posologies(patient_id, medication_id)
                if self.abbort_operation:
                    return
                self.view.update_posology_list_panel(button, container, patient_id, medication_id, posologies)
                
            except Exception as e:
                self.view.show_dialog(e)
        
        except Exception as e:
            self.view.show_dialog(e)
                
    def on_delete_posology(self, button, container, patient_id, medication_id, posology_id):
        @run_async
        def on_confirmation(): 
            try:
                self.model.delete_posology(patient_id, medication_id, posology_id)
                if self.abbort_operation:
                    return
                try:
                    posologies = self.model.get_posologies(patient_id, medication_id)
                    if self.abbort_operation:
                        return
                    self.view.update_posology_list_panel(button, container, patient_id, medication_id, posologies)
                
                except Exception as e:
                    self.view.show_dialog(e)

            except Exception as e:
                self.view.show_dialog(e)
        
        self.view.show_confirmation_dialog(
            _("Confirm deletion"),
            _("Are you sure you want to delete the posology?"),
            on_confirmation
        )
    
    @run_async
    def on_save_posology(self, button, container, patient_id, medication_id, hour, minute):
        try:
            self.model.add_posology(patient_id, medication_id, minute, hour)
            if self.abbort_operation:
                return
            try:
                posologies = self.model.get_posologies(patient_id, medication_id)
                if self.abbort_operation:
                    return
                self.view.update_posology_list_panel(button, container, patient_id, medication_id, posologies)
                
            except Exception as e:
                self.view.show_dialog(e)
            
        except Exception as e:
            self.view.show_dialog(e)

    @run_async
    def on_cancel_posology(self, button, container, patient_id, medication_id):
        try:
            posologies = self.model.get_posologies(patient_id, medication_id)
            if self.abbort_operation:
                return
            self.view.update_posology_list_panel(button, container, patient_id, medication_id, posologies)

        except Exception as e:
            self.view.show_dialog(e)
    
    def get_patients(self):
        try:
            return self.model.get_patients()
        except Exception as e:
            self.view.show_dialog(e)
            return []    
    
    @run_async
    def get_medications(self, patient_id) -> List[dict]:
        try:
            return self.model.get_medications(patient_id)
        except Exception as e:
            self.view.show_dialog(e)
            return []
    
    @run_async
    def get_posologies(self, patient_id, medication_id):
        try:
            return self.model.get_posologies(patient_id, medication_id)
        except Exception as e:
            self.view.show_dialog(e)
            return []
