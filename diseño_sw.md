# Diseño software

<!-- ## Notas para el desarrollo de este documento
En este fichero debeis documentar el diseño software de la práctica.

> :warning: El diseño en un elemento "vivo". No olvideis actualizarlo
> a medida que cambia durante la realización de la práctica.

> :warning: Recordad que el diseño debe separar _vista_ y
> _estado/modelo_.
	 

El lenguaje de modelado es UML y debeis usar Mermaid para incluir los
diagramas dentro de este documento. Por ejemplo:

```mermaid
classDiagram
    class Model {
	}
	class View {
	}
	View ..> Gtk : << uses >>
	class Gtk
	<<package>> Gtk
```
-->

## Diseño estático
```mermaid
classDiagram
    class MedicationInterface {
        +showMedications() List
        +showMedicationDetails(medicationId: int) void
        +confirmIntake(posologyId: int) void
    }

    class MedicationProvider {
        -medications: List
        +getMedications() List
        +getPosologies(medicationId: int) List
        +getIntakes(medicationId: int) List
        +logIntake(posologyId: int) void
    }

    class Medication {
        +id: int
        +name: String
        +dosage: float
        +startDate: Date
        +treatmentDuration: int
        +patientId: int
    }

    class Posology {
        +id: int
        +hour: int
        +minute: int
        +medication_id: int
    }

    class Intake {
        +id: int
        +date: String
        +medication_id: int
    }

    class ReminderService {
        +sendReminder(medication: Medication, posology: Posology) bool
    }

    MedicationInterface --> MedicationProvider : interacts
    MedicationProvider --o Medication : provides
    MedicationProvider --o Posology : manages
    MedicationProvider --o Intake : logs
    MedicationProvider --> ReminderService : uses
```

## Diseño dinámico

```mermaid

sequenceDiagram
    
    participant User
    participant MI as MedicationInterface
    participant MP as MedicationProvider
    participant RS as ReminderService

    User->>MI: showMedications()
    MI->>MP: getMedications()
    MP-->>MI: List of Medications
    
    User->>MI: showMedicationDetails(medicationId)
    MI->>MP: getPosologies(medicationId)
    MP-->>MI: List of Posologies
    
    User->>MI: confirmIntake(posologyId)
    MI->>MP: logIntake(posologyId)
    MP->>RS: sendReminder(medication, posology)
    RS-->>MP: Reminder sent (bool)

```
