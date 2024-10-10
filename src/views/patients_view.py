import gi

from src.controller import Controller
gi.require_version("Gtk", "4.0")
from gi.repository import Gtk # type: ignore

class PatientsView:
    def __init__(self):
        self.window = Gtk.Window(name="Patients")
        self.handler = None
        self.patient_list = []

    def set_handler(self, handler: Controller):
        self.handler = handler


    def run():

