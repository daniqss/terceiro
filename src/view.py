import gi
gi.require_version("Gtk", "4.0")
gi.require_version('Adw', '1')
from gi.repository import Adw, Gtk, Pango # type: ignore
from src.views.buttons import Buttons

from src.utils import APPLICATION_ID

class View(Adw.Application):
    def __init__(self, handler, *args, **kwargs):
        super().__init__(*args, application_id=APPLICATION_ID, **kwargs)
        self.buttons = Buttons()
        self.selected_patient = None
        self.handler = handler
        self.patients_index_relations = []

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
        left_box = self.update_patient_list_panel()
        paned.set_start_child(left_box)

        # Right panel: Medication list
        right_box = self.create_empty_medication_list_panel()
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

    def create_split_panel(self) -> Gtk.Paned:
        return Gtk.Paned(orientation=Gtk.Orientation.HORIZONTAL)

    def update_patient_list_panel(self) -> Gtk.Box:
        left_box = Gtk.Box(orientation=Gtk.Orientation.VERTICAL, spacing=20, margin_start=8, margin_end=8, margin_top=0, margin_bottom=8)
        patients_label = Gtk.Label(label="Patients")
        patients_search = Gtk.SearchEntry()
        patients_search.set_placeholder_text("Search patients by code")

        scrolled = Gtk.ScrolledWindow()
        scrolled.set_policy(Gtk.PolicyType.NEVER, Gtk.PolicyType.AUTOMATIC)
        scrolled.set_vexpand(True)
        scrolled.set_hexpand(True)

        # List of patients, we'll add patients boxes for each patient
        self.listbox_patients = Gtk.ListBox()
        self.listbox_patients.add_css_class("boxed-list")

        self.patients = self.handler.get_patients()

        def on_row_activated(_, row):
            # get the patient from the tuple that relates the index in the patient list and the index in the listbox after a search
            self.handler.on_patient_selected(self.patients[self.patients_index_relations[row.get_index()][0]])
        self.listbox_patients.connect("row-activated", on_row_activated)

        
        # if its the first time we show the patients, we filter them
        self.filter_patients(patients_search)
        # Connect the search entry to the filter function
        patients_search.connect("search-changed", self.filter_patients)

        scrolled.set_child(self.listbox_patients)
        left_box.append(patients_label)
        left_box.append(patients_search)
        left_box.append(scrolled)
        return left_box

    def update_patient_list(self, filtered_patients=None):
        # Remove all rows from the ListBox
        while row := self.listbox_patients.get_first_child():
            self.listbox_patients.remove(row)

        # Add patients to the list
        patients_to_display = filtered_patients if filtered_patients is not None else self.patients
        index_relations = []
        
        for new_index, patient in enumerate(patients_to_display):
            patient_box = Gtk.Box(orientation=Gtk.Orientation.HORIZONTAL, spacing=6)
            patient_box.append(self.create_patient_row(patient))
            self.listbox_patients.append(patient_box)
            
            # Find the original index of this patient
            original_index = next((i for i, p in enumerate(self.patients) if p['id'] == patient['id']), None)
            if original_index is not None:
                index_relations.append((original_index, new_index))

        self.patients_index_relations = index_relations

    def filter_patients(self, search_entry):
        search_text = search_entry.get_text().lower()
        filtered_patients = [
            patient for patient in self.patients
            if search_text in patient.get('code', '').lower()
        ]
        self.update_patient_list(filtered_patients)

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

    def create_empty_medication_list_panel(self):
        right_box = Gtk.Box(orientation=Gtk.Orientation.VERTICAL, spacing=6)

        label_medications = Gtk.Label(label="Medications")
        right_box.append(label_medications)

        self.label_select_patient = Gtk.Label(label="Select a patient")
        self.label_select_patient.set_halign(Gtk.Align.CENTER)
        self.label_select_patient.set_vexpand(True) 
        self.label_select_patient.set_markup('<span font="20">Select a patient</span>') 

        right_box.append(self.label_select_patient)

        return right_box
    
    def update_medication_list_panel_patient(self, patient_id, medications):
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
        
        self.medication_scroll = Gtk.ScrolledWindow()
        self.medication_list_box = Gtk.ListBox()
        self.right_box.append(self.medication_list_box) 
        
        print(f"Medications: {medications}")
        if medications:  
            for medication in medications:
                self.medication_list_box.append(self.create_medication_row(patient_id, medication))
        else:
            empty_label = Gtk.Label(label="No medications available for this patient.")
            self.medication_list_box.append(empty_label)

        self.add_medication_box = Gtk.Box(orientation=Gtk.Orientation.VERTICAL, spacing=6)

        self.add_button = Gtk.Button(label="Add Medication")
        self.add_button.connect("clicked", lambda _: self.handler.on_add_medication(patient_id))
        self.add_medication_box.append(self.add_button)

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

        expander_button = self.buttons.expandButton(
            handler=lambda _: self.handler.on_expand_medication(
                expander_button,
                container,
                patient_id,
                medication['id']
            )
        )

        buttons = Gtk.Box(orientation=Gtk.Orientation.HORIZONTAL, spacing=6)
        buttons.set_halign(Gtk.Align.START)
        buttons.set_margin_start(6)
        buttons.set_margin_end(6)
        buttons.set_margin_top(6)
        buttons.set_margin_bottom(6)
        
        buttons.set_halign(Gtk.Align.END)
        button_update = self.buttons.editButton(handler=lambda _: self.handler.on_edit_medication(patient_id, medication))
        button_delete = self.buttons.deleteButton(handler=lambda _: self.handler.on_delete_medication(patient_id, medication['id']))
        buttons.append(button_update)
        buttons.append(button_delete)
        buttons.append(expander_button)

        row.append(buttons)
        container.append(row)

        return container
    
    def create_medication_label(self, text):
        label = Gtk.Label(label=text)
        label.set_use_markup(True)
        label.set_halign(Gtk.Align.START)
        label.set_hexpand(True)
        label.set_ellipsize(Pango.EllipsizeMode.END)  
        label.set_max_width_chars(30)
        return label

    def update_posology_list_panel(self, button, container, patient_id, medication_id, posologies):

        if hasattr(self, 'title_row') and self.title_row in container:
            container.remove(self.title_row)
            
            if hasattr(self, 'posology_rows'):
                for posology_row in self.posology_rows:
                    if posology_row in container:
                        container.remove(posology_row)
                self.posology_rows.clear()

            if hasattr(self, 'add_posologie_box') and self.add_posologie_box in container:
                container.remove(self.add_posologie_box)
                
            self.buttons.switchExpandableButton(button)
            return  

        self.title_row = Gtk.Box(orientation=Gtk.Orientation.HORIZONTAL, spacing=10)
        self.title_row.set_halign(Gtk.Align.CENTER)
        title_content = Gtk.Label(label="<span font_size='12000'><b>Posologies:</b></span>")
        title_content.set_use_markup(True)
        self.title_row.append(title_content)

        if not hasattr(self, 'posology_rows'):
            self.posology_rows = []

        container.append(self.title_row)

        if posologies:
            for posology in posologies:
                posology_row = Gtk.Box(orientation=Gtk.Orientation.HORIZONTAL, spacing=10)
                posology_row.set_halign(Gtk.Align.CENTER)

                # Etiquetas de hora y minuto
                label_hour = Gtk.Label(label=f"<span font_size='12000'><b>Hour:</b> {posology.get('hour')}</span>")
                label_hour.set_use_markup(True)
                label_minute = Gtk.Label(label=f"<span font_size='12000'><b>Minute:</b> {posology.get('minute')}</span>")
                label_minute.set_use_markup(True)

                posology_row.append(label_hour)
                posology_row.append(label_minute)

                # Botones de acción
                buttons = Gtk.Box(orientation=Gtk.Orientation.HORIZONTAL, spacing=6)
                buttons.set_halign(Gtk.Align.START)
                button_delete = self.buttons.deleteButton(handler=lambda _: self.handler.on_delete_posology(button, container, patient_id, medication_id, posology['id']))
                buttons.append(button_delete)
                posology_row.append(buttons)

                self.posology_rows.append(posology_row)

        else:
            no_posology_label = self.create_medication_label(f"<i>No posologies available</i>")
            posology_row = Gtk.Box(orientation=Gtk.Orientation.HORIZONTAL, spacing=10)
            posology_row.append(no_posology_label)

            self.posology_rows.append(posology_row)

        for posology_row in self.posology_rows:
            container.append(posology_row)

        self.add_posologie_box = Gtk.Box(orientation=Gtk.Orientation.VERTICAL, spacing=6)
        self.add_button = Gtk.Button(label="Add Posologies")
        self.add_button.connect("clicked", lambda _: self.handler.on_add_posology(button, container, patient_id, medication_id))
        self.add_posologie_box.append(self.add_button)
        container.append(self.add_posologie_box)

        self.buttons.switchExpandableButton(button)

    def create_medication_input_row(self, patient_id, name, dosage, duration, start_date):
        # Verificar si ya hay una fila de entrada y eliminarla si existe
        if hasattr(self, 'input_row') and self.input_row is not None:
            self.add_medication_box.remove(self.input_row)
            self.input_row = None
        
        if hasattr(self, 'label_select_patient'):
            self.right_box.remove(self.label_select_patient)

        if hasattr(self, 'medication_list_box'):
            self.right_box.remove(self.medication_list_box)

        # Crear un contenedor vertical
        container = Gtk.Box(orientation=Gtk.Orientation.VERTICAL, spacing=10)
        container.set_margin_top(10)
        container.set_margin_bottom(10)
        container.set_margin_start(10)
        container.set_margin_end(10)

        # Crear las filas de entrada usando Adw.EntryRow (con valores iniciales)
        entry_name = Adw.EntryRow()
        entry_name.set_title("Medication Name")
        entry_name.set_text(name)  # Establecer el nombre de la medicación
        entry_name.show()
        
        entry_dosage = Adw.EntryRow()
        entry_dosage.set_title("Dosage (mg)")
        entry_dosage.set_text(dosage)  # Establecer la dosis

        entry_duration = Adw.EntryRow()
        entry_duration.set_title("Duration (days)")
        entry_duration.set_text(duration)  # Establecer la duración

        entry_start_date = Adw.EntryRow()
        entry_start_date.set_title("Start Date (YYYY-MM-DD)")
        entry_start_date.set_text(start_date)  # Establecer la fecha de inicio

        # Añadir las EntryRows al contenedor
        container.append(entry_name)
        container.append(entry_dosage)
        container.append(entry_duration)
        container.append(entry_start_date)

        # Crear una caja para los botones
        buttons = Gtk.Box(orientation=Gtk.Orientation.HORIZONTAL, spacing=6)
        buttons.set_halign(Gtk.Align.END)
        buttons.set_margin_start(6)
        buttons.set_margin_end(6)
        buttons.set_margin_top(6)
        buttons.set_margin_bottom(6)

        # Botón para confirmar la adición de la medicación
        button_save = Gtk.Button(label="Confirm")
        button_save.connect("clicked", lambda _: self.handler.on_save_medication(patient_id, 
                                                                                entry_name.get_text(), 
                                                                                entry_dosage.get_text(), 
                                                                                entry_duration.get_text(), 
                                                                                entry_start_date.get_text()))

        # Botón para cancelar la acción y eliminar el formulario
        button_cancel = Gtk.Button(label="Cancel")
        button_cancel.connect("clicked", lambda _: self.handler.on_cancel_medication(patient_id))

        # Añadir botones al contenedor de botones
        buttons.append(button_save)
        buttons.append(button_cancel)

        # Añadir el contenedor de botones al contenedor principal
        container.append(buttons)

        # Guardar la fila de entrada
        self.input_row = container

        # Añadir el contenedor a la caja principal de medicaciones
        self.add_medication_box.append(container)


    def create_posology_input_row(self, button, container, patient_id, medication_id):
        # Verificar si ya hay una fila de entrada y eliminarla si existe
        if hasattr(self, 'input_row') and self.input_row is not None:
            container.remove(self.input_row)  # Remover del container pasado como parámetro
            self.input_row = None

        # Si existía una label de selección de paciente o una caja de posología previa, eliminarlas
        if hasattr(self, 'label_select_patient') and self.label_select_patient in container:
            container.remove(self.label_select_patient)

        if hasattr(self, 'add_posologie_box') and self.add_posologie_box in container:
            container.remove(self.add_posologie_box)

        # Crear un nuevo contenedor vertical
        self.add_posologie_box.set_margin_top(10)
        self.add_posologie_box.set_margin_bottom(10)
        self.add_posologie_box.set_margin_start(10)
        self.add_posologie_box.set_margin_end(10)

        # Crear las filas de entrada usando Adw.EntryRow
        entry_hour = Adw.EntryRow()
        entry_hour.set_title("Hour")
        entry_hour.set_text("")  # Puedes establecer un texto inicial si es necesario
        entry_hour.show()

        entry_minute = Adw.EntryRow()
        entry_minute.set_title("Minute")
        entry_minute.set_text("")  # Puedes establecer un texto inicial si es necesario
        entry_minute.show()

        # Añadir las EntryRows al contenedor
        self.add_posologie_box.append(entry_hour)
        self.add_posologie_box.append(entry_minute)

        # Crear una caja para los botones
        buttons = Gtk.Box(orientation=Gtk.Orientation.HORIZONTAL, spacing=6)
        buttons.set_halign(Gtk.Align.END)
        buttons.set_margin_start(6)
        buttons.set_margin_end(6)
        buttons.set_margin_top(6)
        buttons.set_margin_bottom(6)

        # Botón para confirmar la adición de la medicación
        button_save = Gtk.Button(label="Confirm")
        button_save.connect("clicked", lambda _: self.handler.on_save_posology(button, 
                                                                            container,
                                                                            patient_id, 
                                                                            medication_id,
                                                                            int(entry_hour.get_text()), 
                                                                            int(entry_minute.get_text())))
        button_save.show()

        # Botón para cancelar la acción y eliminar el formulario
        button_cancel = Gtk.Button(label="Cancel")
        button_cancel.connect("clicked", lambda _: self.handler.on_cancel_posology(patient_id))
        button_cancel.show()

        # Añadir botones al contenedor de botones
        buttons.append(button_save)
        buttons.append(button_cancel)

        # Añadir el contenedor de botones al contenedor principal
        self.add_posologie_box.append(buttons)

        # Guardar la fila de entrada
        self.input_row = self.add_posologie_box

        # Añadir el contenedor posology_box al contenedor principal pasado por parámetro
        container.append(self.add_posologie_box)


    def update_medication(self, patient_id, name, dosage, duration, start_date):
        if hasattr(self, 'add_button') and self.add_button is not None:
            self.add_medication_box.remove(self.add_button)
        self.create_medication_input_row(patient_id, name, dosage, duration, start_date)