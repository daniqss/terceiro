#!/bin/bash

echo "Generating the POT file..."
xgettext -o locales/patients-acdc.pot --from-code=UTF-8 src/*.py

echo "Compiling MO files..."
for lang in locales/*.po; do
    lang_dir="locales/$(basename "$lang" .po)/LC_MESSAGES"
    mkdir -p "$lang_dir"  # Crear el directorio si no existe
    msgmerge --update --backup=off $lang locales/patients-acdc.pot
    msgfmt "$lang" -o "$lang_dir/patients-acdc.mo"
done

echo "Compilation completed."

