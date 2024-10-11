from gi import require_version
require_version('Gtk', '4.0')
from gi.repository import Gtk, Gdk


class Buttons:
    def __init__(self):
        self.theme = Gtk.IconTheme.get_for_display(Gdk.Display.get_default())

    def _createButton(self, icon_name, handler) -> Gtk.Button:
        icon = self.theme.lookup_icon(
            icon_name,
            None,
            24,
            1,
            Gtk.TextDirection.NONE,
            Gtk.IconLookupFlags.FORCE_SYMBOLIC
        )
        image = Gtk.Image.new_from_paintable(icon)
        button = Gtk.Button()
        button.set_child(image)
        button.connect("clicked", handler)

        return button

    def deleteButton(self, handler) -> Gtk.Button:
        return self._createButton("user-trash-symbolic", handler)
    
    def addButton(self, handler) -> Gtk.Button:
        return self._createButton("list-add-symbolic", handler)


    def editButton(self, handler) -> Gtk.Button:
        return self._createButton("document-edit-symbolic", handler)
    
    def refreshButton(self, handler) -> Gtk.Button:
        return self._createButton("view-refresh-symbolic", handler)

    def expandButton(self, handler) -> Gtk.Button:
        return self._createButton("go-down-symbolic", handler)

    def switchExpandableButton(self, button):
        iconButton = button.get_child()

        if iconButton.get_icon_name() == "go-down-symbolic":
            iconButton.set_from_icon_name("go-up-symbolic")
        else:
            iconButton.set_from_icon_name("go-down-symbolic")
