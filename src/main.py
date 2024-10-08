#!/usr/bin/env python3
import gi
gi.require_version("Gtk", "4.0")
from gi.repository import Gtk # type: ignore

from model.patientModel import PatientModel


APPLICATION_ID: str = "es.udc.fic.ipm.holamundo"
WINDOW_PADDING: int = 24

class HolaMundoApp(Gtk.Application):

    def __init__(self, *args, **kwargs):
        super().__init__(*args, application_id=APPLICATION_ID, **kwargs)
        self.count = 0  # Inicializamos el contador

    def do_activate(self):
        win = Gtk.ApplicationWindow(application=self, title="Hola Mundo")
        
        header_bar = Gtk.HeaderBar()
        win.set_titlebar(header_bar)
        button = Gtk.Button(label='Button')
        header_bar.pack_start(button)
        icon_button = Gtk.Button(icon_name='open-menu-symbolic')
        header_bar.pack_end(icon_button)

        box = Gtk.Box(
            orientation=Gtk.Orientation.VERTICAL,
            homogeneous=False,
            spacing=WINDOW_PADDING
        )
        box.set_margin_top(WINDOW_PADDING)
        box.set_margin_bottom(WINDOW_PADDING)
        box.set_margin_start(WINDOW_PADDING)
        box.set_margin_end(WINDOW_PADDING)

        self.label = Gtk.Label(
            label="Has pulsado el botón 0 veces",
            halign=Gtk.Align.CENTER,
            vexpand=True,
        )

        self.button = Gtk.Button(
            label="¡Haz clic aquí!",
            halign=Gtk.Align.CENTER,
        )

        self.button.connect("clicked", self.on_count_up)

        box.append(self.label)
        box.append(self.button)

        win.set_child(box)
        win.present()

    def on_count_up(self, button: Gtk.Button) -> None:
        self.count += 1
        self.label.set_text(self.get_label_text(self.count))

    def get_label_text(self, count: int) -> str:
        return f"Has pulsado el botón {count} veces"

if __name__ == "__main__":
    print(PatientModel.getPatients())
    app = HolaMundoApp()
    app.run(None)

