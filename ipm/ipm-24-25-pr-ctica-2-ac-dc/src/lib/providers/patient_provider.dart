import 'package:flutter/material.dart';
import 'package:src/models/patient_model.dart';
import 'package:src/models/medication_model.dart';
import 'package:src/models/posology_model.dart';
import 'package:src/models/intake_model.dart';

class PatientProvider with ChangeNotifier {
  Map<String, dynamic>? _patient;
  List<Map<String, dynamic>> _medications = [];
  List<Map<String, dynamic>> _filteredMedications = [];
  Map<int, bool> _isExpanded = {};
  Map<int, List<Map<String, dynamic>>> _posologies = {};
  Map<int, bool> _isPosologyEmpty = {};
  bool _isLoading = true;

  final PatientModel _patientModel = PatientModel();
  final MedicationModel _medicationModel = MedicationModel();
  final PosologyModel _posologyModel = PosologyModel();
  final IntakeModel _intakeModel = IntakeModel();

  Map<String, dynamic>? get patient => _patient;
  List<Map<String, dynamic>> get medications => _medications;
  List<Map<String, dynamic>> get filteredMedications => _filteredMedications;
  bool get isLoading => _isLoading;

  List<Map<String, dynamic>> getAllMedications() {
    return _medications;
  }

  List<Map<String, dynamic>> getPosologiesForMedication(int medicationId) {
    return _posologies[medicationId] ?? [];
  }

  bool isMedicationExpanded(int medicationId) => _isExpanded[medicationId] ?? false;
  bool isPosologyEmpty(int medicationId) => _isPosologyEmpty[medicationId] ?? false;

  Future<void> loadPatientData(int patientId) async {
    _setLoading(true);
    try {
      _patient = await _patientModel.getPatient(patientId);
      _medications = await _medicationModel.getMedications(patientId);
      _initializeMedicationState();
    } catch (e) {
      debugPrint("Error al cargar los datos del paciente: $e");
      throw Exception("No se pudieron cargar los datos del paciente.");
    } finally {
      _setLoading(false);
    }
  }

  Future<void> loadAllPosologies(int patientId) async {
    for (var medication in _medications) {
      final medicationId = medication["id"];
      await loadPosologies(patientId, medicationId);
    }
  }

  Future<void> loadPosologies(int patientId, int medicationId) async {
    try {
      final posologies = await _posologyModel.getPosologies(patientId, medicationId);
      _posologies[medicationId] = posologies;
      _isPosologyEmpty[medicationId] = posologies.isEmpty;
      toggleExpandedState(medicationId); // Use the method here
    } catch (e) {
      debugPrint("Error al cargar las posologías: $e");
      throw Exception("No se pudieron cargar las posologías.");
    }
  }

  void clearPosologies() {
    _posologies.clear();
    _isExpanded.clear();
    _isPosologyEmpty.clear();
    notifyListeners();
  }

  Future<void> addMedicationIntake(int patientId, int medicationId, Map<String, dynamic> intakeData) async {
    try {
      await _intakeModel.addIntake(patientId, medicationId, intakeData);
      debugPrint("Toma añadida exitosamente para el medicamento $medicationId");
    } catch (e) {
      debugPrint("Error al añadir la toma: $e");
      throw Exception("No se pudo añadir la toma.");
    }
  }

  List<Map<String, dynamic>> filterMedications({
    required int patientId,
    int? filterHours,
    DateTimeRange? dateRange,
  }) {
    final now = DateTime.now();
    final cutoff = filterHours != null ? now.add(Duration(hours: filterHours)) : null;

    bool _isWithinRange(DateTime time) {
      if (cutoff != null) {
        return time.isAfter(now) && time.isBefore(cutoff);
      }
      if (dateRange != null) {
        return time.isAfter(dateRange.start) && time.isBefore(dateRange.end);
      }
      return false;
    }

    final filteredMedications = _medications.where((medication) {
      if (medication["patient_id"] != patientId) {
        debugPrint("Medicamento ${medication['id']} filtrado: No pertenece al paciente.");
        return false;
      }

      final startDateString = medication["start_date"];
      final treatmentDuration = medication["treatment_duration"];
      if (startDateString == null || treatmentDuration == null) {
        debugPrint("Medicamento ${medication['id']} filtrado: Falta start_date o treatment_duration.");
        return false;
      }

      final startDate = DateTime.parse(startDateString);
      final endDate = startDate.add(Duration(days: treatmentDuration));
      if (now.isAfter(endDate)) {
        debugPrint("Medicamento ${medication['id']} filtrado: Tratamiento vencido.");
        return false;
      }

      final medicationId = medication["id"];
      final medicationPosologies = getPosologiesForMedication(medicationId);

      if (medicationPosologies.isEmpty) {
        debugPrint("Medicamento ${medication['id']} filtrado: Sin posologías.");
      }

      final validPosologies = <DateTime>[];
      for (var posology in medicationPosologies) {
        DateTime currentBaseDate = startDate;
        while (currentBaseDate.isBefore(endDate)) {
          final posologyTime = DateTime(
            currentBaseDate.year,
            currentBaseDate.month,
            currentBaseDate.day,
            posology["hour"] ?? 0,
            posology["minute"] ?? 0,
          );
          if (_isWithinRange(posologyTime)) {
            validPosologies.add(posologyTime);
          }
          currentBaseDate = currentBaseDate.add(Duration(days: 1));
        }
      }
      medication["valid_posologies"] = validPosologies;

      if (validPosologies.isEmpty) {
        debugPrint("Medicamento ${medication['id']} filtrado: Sin posologías válidas.");
      }

      final medicationStartsInRange = cutoff != null
          ? startDate.isBefore(cutoff) && startDate.isAfter(now)
          : dateRange != null &&
          startDate.isAfter(dateRange.start) &&
          startDate.isBefore(dateRange.end);

      if (!validPosologies.isNotEmpty && !medicationStartsInRange) {
        debugPrint("Medicamento ${medication['id']} filtrado: Fuera del rango.");
      }

      return validPosologies.isNotEmpty || medicationStartsInRange;
    }).toList();


    filteredMedications.sort((a, b) {
      final posologyA = a["valid_posologies"] as List<DateTime>;
      final posologyB = b["valid_posologies"] as List<DateTime>;

      final nextDateA = posologyA.isNotEmpty ? posologyA.first : DateTime.parse(a["start_date"]);
      final nextDateB = posologyB.isNotEmpty ? posologyB.first : DateTime.parse(b["start_date"]);

      return nextDateA.compareTo(nextDateB);
    });

    return filteredMedications;
  }

  void _initializeMedicationState() {
    _isExpanded = {for (var med in _medications) med["id"]: false};
    _posologies = {};
    _isPosologyEmpty = {for (var med in _medications) med["id"]: false};
    notifyListeners();
  }

  Future<void> filterMedicationsByDateRange(DateTime start, DateTime end) async {
    _filteredMedications = _medications.where((medication) {
      final startDate = DateTime.parse(medication["start_date"]);
      final treatmentDuration = medication["treatment_duration"];
      if (treatmentDuration == null) return false;

      final endDate = startDate.add(Duration(days: treatmentDuration));

      return startDate.isBefore(end) && endDate.isAfter(start);
    }).toList();
    notifyListeners();
  }

  void toggleExpandedState(int medicationId) {
    _isExpanded[medicationId] = !_isExpanded[medicationId]!;
    notifyListeners();
  }

  void _setLoading(bool value) {
    _isLoading = value;
    notifyListeners();
  }
}