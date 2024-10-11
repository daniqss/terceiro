import gi
gi.require_version("Gtk", "4.0")
gi.require_version('Adw', '1')
from gi.repository import Adw, Gtk, Pango
from src.views.buttons import Buttons

from src.utils import APPLICATION_ID

class View(Adw.Application):
    def __init__(self, handler, *args, **kwargs):
        super().__init__(*args, application_id=APPLICATION_ID, **kwargs)
        self.buttons = Buttons()
        self.selected_patient = None
        self.handler = handler

    def do_activate(self):
        window = self.create_main_window()
        window.set_title("Patients - ACDC")
        window.set_default_size(1300, 1200)
        main_box = self.create_main_layout(window)
        
        # Create header and split panel
        header_bar = self.create_header_bar()
        paned = self.create_split_panel()

        main_box.append(header_bar)
        main_box.append(paned)
        
        # Left panel: Patient list
        left_box = self.create_patient_list_panel()
        paned.set_start_child(left_box)

        # Right panel: Medication list
        right_box = self.create_medication_list_panel()
        paned.set_end_child(right_box)
        self.right_box = right_box

        window.show()

    def create_main_window(self):
        return Adw.ApplicationWindow(application=self)

    def create_main_layout(self, window):
        main_box = Gtk.Box(orientation=Gtk.Orientation.VERTICAL, spacing=6)
        window.set_content(main_box)
        return main_box

    def create_header_bar(self):
        header_bar = Adw.HeaderBar()
        return header_bar

    def create_split_panel(self):
        return Gtk.Paned.new(Gtk.Orientation.HORIZONTAL)

    def create_patient_list_panel(self) -> Gtk.Box:
        left_box = Gtk.Box(orientation=Gtk.Orientation.VERTICAL, spacing=20)
        # scrolled = Gtk.ScrolledWindow()
        # scrolled.set_policy(Gtk.PolicyType.NEVER, Gtk.PolicyType.AUTOMATIC)
        # scrolled.set_vexpand(True)
        # scrolled.set_hexpand(True)
        
        # List of patients, we'll add patients boxes for each patient
        listbox_patients = Gtk.ListBox()
        patients = self.handler.get_patients()
        for patient in patients:
            print(patient)
            patient_box = Gtk.Box(orientation=Gtk.Orientation.HORIZONTAL, spacing=6)
            patient_box.append(self.create_patient_row(patient))
            listbox_patients.append(patient_box)
        listbox_patients.connect(
            "row-activated",
            lambda listbox, row: self.on_patient_selected(listbox, row, patients)
        )
        
        left_box.append(listbox_patients)
        return left_box

    def create_patient_row(self, patient) -> Gtk.Box:
        row = Gtk.Box(orientation=Gtk.Orientation.HORIZONTAL, spacing=6)

        # patient data at the left
        patient_data = Gtk.Box(orientation=Gtk.Orientation.VERTICAL, spacing=6)
        patient_data.set_halign(Gtk.Align.START)
        patient_data.set_margin_start(6)
        patient_data.set_margin_end(6)
        patient_data.set_margin_top(6)
        patient_data.set_margin_bottom(6)
        patient_data.set_hexpand(True)
        patient_name = Gtk.Label()
        patient_name.set_markup("<big>{} {}</big>".format(patient["name"], patient["surname"]))
        patient_code = Gtk.Label()
        patient_code.set_markup("<small>{}</small>".format(patient["code"]))
        patient_code.set_halign(Gtk.Align.START)
        patient_code.set_margin_start(4)
        patient_data.append(patient_name)
        patient_data.append(patient_code)

        # update and delete buttons at the right
        buttons = Gtk.Box(orientation=Gtk.Orientation.HORIZONTAL, spacing=6)
        buttons.set_halign(Gtk.Align.START)
        buttons.set_margin_start(6)
        buttons.set_margin_end(6)
        buttons.set_margin_top(6)
        buttons.set_margin_bottom(6)
        
        buttons.set_halign(Gtk.Align.END)
        button_update = self.buttons.editButton(handler=lambda _: self.handler.update_patient(patient))
        button_delete = self.buttons.deleteButton(handler=lambda _: self.handler.delete_patient(patient))
        buttons.append(button_update)
        buttons.append(button_delete)

        row.append(patient_data)
        row.append(buttons)
        row.set_header = patient["id"]
        return row

    def create_medication_list_panel(self):
        right_box = Gtk.Box(orientation=Gtk.Orientation.VERTICAL, spacing=6)

        label_medications = Gtk.Label(label="Medications")
        right_box.append(label_medications)

        self.label_select_patient = Gtk.Label(label="Select a patient")
        self.label_select_patient.set_halign(Gtk.Align.CENTER)
        self.label_select_patient.set_vexpand(True) 
        self.label_select_patient.set_markup('<span font="20">Select a patient</span>') 

        right_box.append(self.label_select_patient)

        return right_box
    
    def create_medication_list_panel_patient(self, patient_id):
        print(f"Creating medication list for patient: {patient_id}")

        if hasattr(self, 'label_select_patient'):
            self.right_box.remove(self.label_select_patient)

        if hasattr(self, 'medication_list_box'):
            self.right_box.remove(self.medication_list_box)

        if hasattr(self, 'add_medication_box'):
            self.right_box.remove(self.add_medication_box)

        self.label_select_patient = Gtk.Label(label=f'Selected patient: {patient_id}')
        self.label_select_patient.set_halign(Gtk.Align.CENTER)
        self.label_select_patient.set_markup(f'<span font="20">Selected patient: {patient_id}</span>')
        self.right_box.append(self.label_select_patient)
        
        self.medication_list_box = Gtk.ListBox()  
        self.right_box.append(self.medication_list_box) 
        
        medications = self.handler.get_medications(patient_id)  
        print(f"Medications: {medications}")
        if medications:  
            for medication in medications:
                self.medication_list_box.append(self.create_medication_row(patient_id, medication))
        else:
            empty_label = Gtk.Label(label="No medications available for this patient.")
            self.medication_list_box.append(empty_label)

        self.add_medication_box = Gtk.Box(orientation=Gtk.Orientation.VERTICAL, spacing=6)

        add_button = Gtk.Button(label="Add Medication")
        self.add_medication_box.append(add_button)

        self.right_box.append(self.add_medication_box)

    def create_medication_row(self, patient_id, medication):
        container = Gtk.Box(orientation=Gtk.Orientation.VERTICAL, spacing=10)
        row = Gtk.Box(orientation=Gtk.Orientation.HORIZONTAL, spacing=10)
        row.set_margin_top(10)
        row.set_margin_bottom(10)
        row.set_margin_start(10)
        row.set_margin_end(10)

        label_name = self.create_medication_label(f"<b>Name:</b> {medication['name']}")
        label_dosage = self.create_medication_label(f"<b>Dosage:</b> {medication['dosage']} mg")
        label_duration = self.create_medication_label(f"<b>Duration:</b> {medication['treatment_duration']} days")
        label_start_date = self.create_medication_label(f"<b>Start Date:</b> {medication['start_date']}")

        row.append(label_name)
        row.append(label_dosage)
        row.append(label_duration)
        row.append(label_start_date)

        posology_rows = []
        expander_button = self.buttons.expandButton(
            handler=lambda _: self.on_expander_clicked(
                expander_button,
                patient_id,
                medication,
                container,
                posology_rows
            )
        )


        buttons = Gtk.Box(orientation=Gtk.Orientation.HORIZONTAL, spacing=6)
        buttons.set_halign(Gtk.Align.START)
        buttons.set_margin_start(6)
        buttons.set_margin_end(6)
        buttons.set_margin_top(6)
        buttons.set_margin_bottom(6)
        
        buttons.set_halign(Gtk.Align.END)
        button_update = self.buttons.editButton(handler=lambda _: self.handler.update_medication(patient_id, medication))
        button_delete = self.buttons.deleteButton(handler=lambda _: self.handler.delete_medication(patient_id, medication))
        buttons.append(button_update)
        buttons.append(button_delete)
        buttons.append(expander_button)

        row.append(buttons)
        container.append(row)

        return container

    def on_expander_clicked(self, button, patient_id, medication, container, posology_rows):
        if hasattr(self, 'title_row') and self.title_row in container:
            container.remove(self.title_row)

        if hasattr(self, 'add_posologie_box') and self.add_posologie_box in container:
                container.remove(self.add_posologie_box)
        
        if posology_rows:
            for posology_row in posology_rows:
                container.remove(posology_row)
            posology_rows.clear()
            self.buttons.switchExpandableButton(button)
        else:
            self.title_row = Gtk.Box(orientation=Gtk.Orientation.HORIZONTAL, spacing=10)
            self.title_row.set_halign(Gtk.Align.CENTER)
            title_content = Gtk.Label(label=f"<span font_size='12000'><b>{medication['name']} Posologies:</b></span>")
            title_content.set_use_markup(True)
            self.title_row.append(title_content)
            container.append(self.title_row)

            posologies = self.handler.get_posologies(patient_id, medication['id'])
            if posologies:
                for posology in posologies:
                    posology_row = Gtk.Box(orientation=Gtk.Orientation.HORIZONTAL, spacing=10)
                    posology_row.set_halign(Gtk.Align.CENTER)
                    label_hour = Gtk.Label(label=f"<span font_size='12000'><b>Hour:</b> {posology.get('hour')}</span>")
                    label_hour.set_use_markup(True)

                    label_minute = Gtk.Label(label=f"<span font_size='12000'><b>Minute:</b> {posology.get('minute')}</span>")
                    label_minute.set_use_markup(True)

                    posology_row.append(label_hour)
                    posology_row.append(label_minute)

                    buttons = Gtk.Box(orientation=Gtk.Orientation.HORIZONTAL, spacing=6)
                    buttons.set_halign(Gtk.Align.START)
                    buttons.set_margin_start(6)
                    buttons.set_margin_end(6)
                    buttons.set_margin_top(6)
                    buttons.set_margin_bottom(6)
                    
                    buttons.set_halign(Gtk.Align.END)
                    
                    button_delete = self.buttons.deleteButton(handler=lambda _: self.handler.delete_medication(patient_id, medication))
                    buttons.append(button_delete)

                    posology_row.append(buttons)

                    container.append(posology_row)
                    posology_rows.append(posology_row)
            else:
                no_posology_label = self.create_medication_label(f"<i>No posologies available</i>")
                posology_row = Gtk.Box(orientation=Gtk.Orientation.HORIZONTAL, spacing=10)
                posology_row.append(no_posology_label)
                container.append(posology_row)
                posology_rows.append(posology_row)

            self.add_posologie_box = Gtk.Box(orientation=Gtk.Orientation.VERTICAL, spacing=6)

            add_button = Gtk.Button(label="Add Posologies")
            self.add_posologie_box.append(add_button)

            container.append(self.add_posologie_box)

            self.buttons.switchExpandableButton(button)

    def create_medication_label(self, text):
        label = Gtk.Label(label=text)
        label.set_use_markup(True)
        label.set_halign(Gtk.Align.START)
        label.set_hexpand(True)
        label.set_ellipsize(Pango.EllipsizeMode.END)  
        label.set_max_width_chars(30)
        return label

    def on_patient_selected(self, _listbox, row, patients):
        self.selected_patient = patients[row.get_index()]
        print(f"Selected patient: {self.selected_patient["id"]}")
        self.create_medication_list_panel_patient(self.selected_patient["id"])
