# Curso 24/25. Práctica 1. Interfaces gráficas para aplicaciones de escritorio

![Image of the assigment](social-image.png)

_Repositorio dedicado al desarrollo de la primera práctica de equipo
de IPM_


La práctica consiste en el desarrollo de una aplicación de escritorio
con su _interface gráfica_.

Este repositorio contiene:

  - Un documento que describe los _roles_ a desempeñar en el desarrollo
    de la práctica.
    
  - Un enunciado que describe el trabajo a realizar en el desarrollo
    de la práctica.
    
  - Un conjunto de rúbricas válidas tanto para la evaluación como para
    la autoevaluación del trabajo realizado.

> :warning: No conocer el contenido de este repositorio, README,
> enunciado, roles, ... conllevará una calificación de cero puntos.


## Miembros del equipo:

- Manotas Ruiz, Javier : github javiermanotasruiz : udc j.manotas : Curador
- Garea Cidre, Santiago : github yagogarea : udc s.garea : Facilitador-Administrador
- Queijo Seoane, Daniel : github daniqss : udc daniel.queijo.seoane : Analista


## Pasos para realizar la práctica

1. Si estás leyendo este _README_ es porque has creado el repositorio
   correspondiente desde el enlace de _github classroom_. Asegurate de
   esta punto sea cierto.
	 
2. Asignar los roles a cada miembro del equipo. La información
   relativa a los roles se ecuentra en el fichero [roles.md](roles.md).
   
3. Cubrir el apartado "Miembros del equipo" siguiendo el formato
   establecido.
   
4. Leer y comprender el enunciado de la práctica, disponible en el
   fichero [enunciado.md](enunciado.md).

5. Leer y comprender las rúbricas disponibles en el fichero
   [rubricas.md](rubricas.md).

6. Planificar y coordinar el trabajo entre los miembros del equipo.

6. Realizar la tarea 1.

8. Presentar la tarea 1 y realizar las correcciones indicadas.

9. Realizar la tarea 2.

10. Presentar la tarea 2 y realizar las correcciones indicadas.

11. Realizar la tarea 3.

12. Presentar la tarea 3 y realizar las correcciones indicadas.

13. Revisar el contenido del repositorio en github.

14. Presentar la práctica ya finializada.
 

## Cómo ejecutar

```bash
# Preparamos el entorno
python3 -m venv venv
source venv/bin/activate # lo tendremos que hacer en cada terminal

# Instalamos las dependencias(las que instalamos con pip)
pip install -r requirements.txt

# Ejecutamos la aplicación
python3 -m src.main

# Para salir del entorno
deactivate
```
Para que funcione correctamente, debe estar corriendo la API en el puerto 8000

### Cómo ejecutar la API

En otro terminal ejecutamos
```bash
cd medications-backend
python3 -m venv venv
source venv/bin/activate # lo tendremos que hacer cada vez que queramos arrancar la API
pip install -r requirements.txt

# Para ejecutar
fastapi run

# Para salir del entorno
deactivate
```

Para comprobar que funciona miramos en http://localhost:8000/patients si devuelve JSON.
En http://localhost:8000/docs tenemos los esquemas y los endpoints de la API

### Setup and Usage

Para inicializar el entorno de la aplicación, puedes ejecutar el siguiente comando:

```bash
make setup
```

Esto configurará el entorno necesario para la aplicación.

Para compilar los archivos de idiomas, utiliza el siguiente comando:

```bash
make compile
```

Esto generará los archivos de traducción necesarios a partir de los archivos fuente.

Para iniciar la aplicación, puedes usar el siguiente comando:
```bash
make run
```

Este comando ejecutará la aplicación en el idioma predeterminado de tu máquina. 

> :warning: Si estas en macOS, es posible que necesites exportar las variables de entorno para los idiomas antes de ejecutar la aplicación. Por ejemplo si quieres que sea en inglés, puedes usar:

```bash
export LC_ALL=en_US.UTF-8
export LANG=en_US.UTF-8
export LANGUAGE=en_US
```

Si deseas especificar un idioma, puedes hacerlo utilizando la variable lang. Por ejemplo, para ejecutar la aplicación en español, puedes usar:

```bash
make run lang=es_ES
```
