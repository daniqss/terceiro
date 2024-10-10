import gi
gi.require_version("Gtk", "4.0")
gi.require_version('Adw', '1')
from gi.repository import Adw, Gio, Gtk

from src.utils import APPLICATION_ID
# from src.views.patient_view import PatientView


class View(Adw.Application):
    def __init__(self, handler, *args, **kwargs):
        super().__init__(*args, application_id=APPLICATION_ID, **kwargs)
        self.selected_patient = None
        self.handler = handler

    def do_activate(self):
        window = Adw.ApplicationWindow(application=self)
        window.set_title("Patients - ACDC")
        window.set_default_size(800, 600)
        
        main_box = Gtk.Box(orientation=Gtk.Orientation.VERTICAL, spacing=6)
        window.set_content(main_box)

        header_bar = Adw.HeaderBar()
        main_box.append(header_bar)

        menu_button = Gtk.MenuButton()
        popover_menu = Gtk.PopoverMenu()
        menu_model = Gio.Menu()
        menu_model.append("Settings", "app.settings")
        popover_menu.set_menu_model(menu_model)
        menu_button.set_popover(popover_menu)
        header_bar.pack_end(menu_button)

        # Create a main box with a split panel
        paned = Gtk.Paned.new(Gtk.Orientation.HORIZONTAL)
        main_box.append(paned)

        left_box = Gtk.Box(orientation=Gtk.Orientation.VERTICAL, spacing=6)
        paned.set_start_child(left_box)

        label_patients = Gtk.Label(label="Patients")
        left_box.append(label_patients)

        scrolled_window_patients = Gtk.ScrolledWindow()
        left_box.append(scrolled_window_patients)

        listbox_patients = Gtk.ListBox()
        scrolled_window_patients.set_child(listbox_patients)

        for patient_name in ["John Doe", "Jane Roe", "Alice Smith"]:
            row = Gtk.Box(orientation=Gtk.Orientation.HORIZONTAL, spacing=6)
            label = Gtk.Label(label=patient_name)
            button = Gtk.Button(label="Select")
            button.connect("clicked", self.on_patient_selected, patient_name)
            row.append(label)
            row.append(button)
            listbox_patients.append(row)

        right_box = Gtk.Box(orientation=Gtk.Orientation.VERTICAL, spacing=6)
        paned.set_end_child(right_box)

        label_medications = Gtk.Label(label="Medications")
        right_box.append(label_medications)

        listbox_medications = Gtk.ListBox()
        right_box.append(listbox_medications)


        window.show()

    def on_patient_selected(self, button, patient_name):
        self.selected_patient = patient_name
        print(f"Selected patient: {self.selected_patient}")
