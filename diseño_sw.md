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
    LoginProvider <|-- PatientModel
    PatientProvider <|-- PatientModel
    LoginView <|-- LoginProvider
    PatientView <|-- PatientProvider

```

## Diseño dinámico

```mermaid

sequenceDiagram
    participant User
    participant MI as MedicationInterface
    participant MP as MedicationProvider
    participant RS as ReminderService

    User->>MI: login(username, password)
    MI->>MP: validateCredentials(username, password)
    MP-->>MI: true/false
    alt Login successful
        MI-->>User: Welcome
        User->>MI: showMedications()
        MI->>MP: getMedications()
        MP-->>MI: List of Medications

        User->>MI: filterMedicationsByHour(hour)
        MI->>MP: getMedicationsFilteredByHour(hour)
        MP-->>MI: Filtered List
    else Login failed
        MI-->>User: Invalid credentials
    end

    User->>MI: showMedicationDetails(medicationId)
    MI->>MP: getPosologies(medicationId)
    MP-->>MI: List of Posologies

    User->>MI: confirmIntake(posologyId)
    MI->>MP: logIntake(posologyId)

```
