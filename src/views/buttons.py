from gi import require_version
require_version('Gtk', '4.0')
from gi.repository import Gtk, Gdk


class Buttons:
    def __init__(self):
        self.theme = Gtk.IconTheme.get_for_display(Gdk.Display.get_default())

    def deleteButton(self, handler) -> Gtk.Button:
        trash_icon = self.theme.lookup_icon(
            "user-trash-symbolic",
            None,
            24,
            1,
            Gtk.TextDirection.NONE,
            Gtk.IconLookupFlags.FORCE_SYMBOLIC
        )
        image_delete = Gtk.Image.new_from_paintable(trash_icon)
        button_delete = Gtk.Button()
        button_delete.set_child(image_delete)
        button_delete.connect("clicked", handler)

        return button_delete
    
    def editButton(self, handler) -> Gtk.Button:
        edit_icon = self.theme.lookup_icon(
            "document-edit-symbolic",
            None,
            24,
            1,
            Gtk.TextDirection.NONE,
            Gtk.IconLookupFlags.FORCE_SYMBOLIC
        )
        image_edit = Gtk.Image.new_from_paintable(edit_icon)
        button_edit = Gtk.Button()
        button_edit.set_child(image_edit)
        button_edit.connect("clicked", handler)

        return button_edit
    
    def refreshButton(self, handler) -> Gtk.Button:
        refresh_icon = self.theme.lookup_icon(
            "view-refresh-symbolic",
            None,
            24,
            1,
            Gtk.TextDirection.NONE,
            Gtk.IconLookupFlags.FORCE_SYMBOLIC
        )
        image_refresh = Gtk.Image.new_from_paintable(refresh_icon)
        button_refresh = Gtk.Button()
        button_refresh.set_child(image_refresh)
        button_refresh.connect("clicked", handler)

        return button_refresh

