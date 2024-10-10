from src.model import Model
from src.view import View

class Controller:
    def __init__(self):
        self.view = View(self)
        self.model = Model()


    def run(self):
        self.view.run()

