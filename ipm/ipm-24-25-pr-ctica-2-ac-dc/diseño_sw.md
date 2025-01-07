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
    %% Models
    class PatientModel {
        +getPatient(patientId: int): Future<Map<String, dynamic>?>
        +getPatientByCode(patientCode: String): Future<Map<String, dynamic>?>
        +getPatients(): Future<List<Map<String, dynamic>>>
    }
    class MedicationModel {
        +getMedications(patientId: int): Future<List<Map<String, dynamic>>>
        +getMedication(patientId: int, medicationId: int): Future<Map<String, dynamic>>
    }
    class PosologyModel {
        +getPosologies(patientId: int, medicationId: int): Future<List<Map<String, dynamic>>>
        +addPosology(patientId: int, medicationId: int, minute: int, hour: int): Future<Map<String, dynamic>>
        +deletePosology(patientId: int, medicationId: int, posologyId: int): void
    }
    class IntakeModel {
        +addIntake(patientId: int, medicationId: int, intakeData: Map<String, dynamic>): Future<void>
        +getIntakesByPatientAndMedication(patientId: int, medicationId: int): Future<List<Map<String, dynamic>>>
        +getIntakesByPatient(patientId: int): Future<List<Map<String, dynamic>>>
        +deleteIntake(patientId: int, medicationId: int, intakeId: int): void
    }

    %% Providers
    class LoginProvider {
        -isLoading: bool
        -errorMessage: String
        -patient: Map<String, dynamic>?
        +isLoading: bool
        +errorMessage: String
        +patient: Map<String, dynamic>?
        +login(patientCode: String): Future<Map<String, dynamic>?>
        +logout(context: BuildContext): void
    }
    class PatientProvider {
        -patient: Map<String, dynamic>?
        -isLoading: bool
        +isLoading: bool
        +patient: Map<String, dynamic>?
        +loadPatientData(patientId: int): Future<void>
        +clearData(): void
    }

    %% Views
    class LoginView {
        +build(context: BuildContext): Widget
        +logInto(loginProvider: LoginProvider, patientCodeController: TextEditingController, context: BuildContext, isWatch: bool): Future<void>
        +showErrorMessage(context: BuildContext, message: String, color: Color, isWatch: bool): void
        +watchLoginView(TextEditingController, bool): Widget
        +loginView(TextEditingController, bool): Widget
        +PatientCodeInput(TextEditingController, bool): Widget
        +LoginButton(isWatch: bool, onPressed: VoidCallback): Widget
    }
    class PatientView {
        +build(context: BuildContext): Widget
        +loadPatientData(patientProvider: PatientProvider): Future<void>
        +applyDateFilter(startDate: DateTime, endDate: DateTime): void
        +clearData(): void
        +applyDateRangeFilter(PatientProvider): Future<void>
        +selectDateRange(): Future<void>
        +toggleMedicationsList(): void
    }

    %% Relationships
    PatientModel <|-- LoginProvider
    PatientModel <|-- PatientProvider
    MedicationModel <|-- PatientProvider
    PosologyModel <|-- PatientProvider
    IntakeModel <|-- PatientProvider
    LoginView <|-- LoginProvider
    PatientView <|-- PatientProvider
```

## Diseño dinámico

```mermaid

sequenceDiagram
    participant User
    participant MI as MedicationInterface
    participant MP as MedicationProvider
    participant PM as PatientModel
    participant MM as MedicationModel
    participant PSM as PosologyModel
    participant IM as IntakeModel

    User->>MI: login(patient_code)
    MI->>PM: validatePatientCode(patient_code)
    PM-->>MI: true/false
    alt Login successful
        User->>MI: showMedications()
        MI->>MP: getMedications()
        MP->>MM: fetchMedications(patientId)
        MM-->>MP: List of Medications
        MP-->>MI: List of Medications

        User->>MI: filterMedicationsByHour(hour)
        MI->>MP: getMedicationsFilteredByHour(hour)
        MP-->>MI: Filtered List
    User->>MI: showMedicationDetails(medicationId)
    MI->>MP: getPosologies(medicationId)
    MP->>PSM: fetchPosologies(patientId, medicationId)
    PSM-->>MP: List of Posologies
    MP-->>MI: List of Posologies

    User->>MI: confirmIntake(posologyId)
    MI->>MP: logIntake(posologyId)
    MP->>IM: addIntake(patientId, medicationId, intakeData)
    IM-->>MP: Confirmation
    MP-->>MI: Confirmation
    else Login failed
        MI-->>User: Patient not found
    end

```
