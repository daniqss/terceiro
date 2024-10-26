import locale
import gettext

lang, encoding = locale.getdefaultlocale()
try:
    translation = gettext.translation('patients-acdc', localedir='locales', languages=[lang])
except FileNotFoundError as e:
    print(e)
    translation = gettext.NullTranslations()
_ = translation.gettext