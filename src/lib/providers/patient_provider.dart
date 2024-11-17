import 'package:flutter/material.dart';
import 'package:src/models/patient_model.dart';
import 'package:src/models/medication_model.dart';
import 'package:src/models/posology_model.dart';

class PatientProvider with ChangeNotifier {
  Map<String, dynamic>? _patient;
  List<Map<String, dynamic>> _medications = [];
  Map<int, bool> _isExpanded = {};
  Map<int, List<Map<String, dynamic>>> _posologies = {};
  Map<int, bool> _isPosologyEmpty = {};
  bool _isLoading = true;

  final PatientModel _patientModel = PatientModel();
  final MedicationModel _medicationModel = MedicationModel();
  final PosologyModel _posologyModel = PosologyModel();

  Map<String, dynamic>? get patient => _patient;
  List<Map<String, dynamic>> get medications => _medications;
  bool get isLoading => _isLoading;

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
      _toggleExpandedState(medicationId);
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

  List<Map<String, dynamic>> filterMedications({
    required int patientId,
    int? filterHours,
    DateTimeRange? dateRange,
  }) {
    print("Iniciando filtrado de medicamentos para paciente $patientId");
    final now = DateTime.now();
    final cutoff = filterHours != null ? now.add(Duration(hours: filterHours)) : null;

    print("Fecha y hora actual: $now");
    if (filterHours != null) print("Filtrando hasta: $cutoff");
    if (dateRange != null) {
      print("Filtrando en rango: ${dateRange.start} - ${dateRange.end}");
    }

    DateTime _getPosologyDateTime(Map<String, dynamic> posology, DateTime baseDate) {
      final dateTime = DateTime(
        baseDate.year,
        baseDate.month,
        baseDate.day,
        posology["hour"] ?? 0,
        posology["minute"] ?? 0,
      );
      print("Posología calculada: $dateTime para baseDate: $baseDate");
      return dateTime;
    }

    bool _isWithinRange(DateTime time) {
      if (cutoff != null) {
        final result = time.isAfter(now) && time.isBefore(cutoff);
        print("Posología $time dentro del rango de corte ($now - $cutoff): $result");
        return result;
      }
      if (dateRange != null) {
        final result = time.isAfter(dateRange.start) && time.isBefore(dateRange.end);
        print("Posología $time dentro del rango ($dateRange): $result");
        return result;
      }
      return false;
    }

    final filteredMedications = _medications.where((medication) {
      print("Evaluando medicamento: ${medication['name']}, ID: ${medication['id']}");

      if (medication["patient_id"] != patientId) {
        print("Medicamento ${medication['id']} no pertenece al paciente $patientId");
        return false;
      }

      final startDateString = medication["start_date"];
      final treatmentDuration = medication["treatment_duration"];
      if (startDateString == null || treatmentDuration == null) {
        print("Medicamento ${medication['id']} tiene datos incompletos.");
        return false;
      }

      final startDate = DateTime.parse(startDateString);
      final endDate = startDate.add(Duration(days: treatmentDuration));
      print("Tratamiento del medicamento ${medication['id']} inicia en $startDate y termina en $endDate");

      if (now.isAfter(endDate)) {
        print("Medicamento ${medication['id']} ya terminó.");
        return false;
      }

      final medicationId = medication["id"];
      final medicationPosologies = getPosologiesForMedication(medicationId);
      print("Posologías para medicamento ${medication['id']}: $medicationPosologies");

      // Genera posologías válidas que estén dentro del rango de fechas
      final validPosologies = <DateTime>[];
      for (var posology in medicationPosologies) {
        DateTime currentBaseDate = startDate;
        while (currentBaseDate.isBefore(endDate)) {
          final posologyTime = _getPosologyDateTime(posology, currentBaseDate);
          if (_isWithinRange(posologyTime)) {
            validPosologies.add(posologyTime);
          }
          currentBaseDate = currentBaseDate.add(Duration(days: 1));
        }
      }

      print("Posologías válidas para medicamento ${medication['id']}: $validPosologies");
      medication["valid_posologies"] = validPosologies;

      // Incluir medicamentos con posologías válidas o si el inicio está dentro del rango
      final medicationStartsInRange = cutoff != null
          ? startDate.isBefore(cutoff) && startDate.isAfter(now)
          : dateRange != null && startDate.isAfter(dateRange.start) && startDate.isBefore(dateRange.end);

      return validPosologies.isNotEmpty || medicationStartsInRange;
    }).toList();

    // Ordenar los medicamentos filtrados por la próxima posología o la fecha de inicio
    filteredMedications.sort((a, b) {
      final posologyA = a["valid_posologies"] as List<DateTime>;
      final posologyB = b["valid_posologies"] as List<DateTime>;

      final nextDateA = posologyA.isNotEmpty ? posologyA.first : DateTime.parse(a["start_date"]);
      final nextDateB = posologyB.isNotEmpty ? posologyB.first : DateTime.parse(b["start_date"]);

      return nextDateA.compareTo(nextDateB);
    });

    print("Medicamentos filtrados y ordenados: ${filteredMedications.map((med) => med['id']).toList()}");

    return filteredMedications;
  }

  List<Map<String, dynamic>> filterMedicationsByDateRange({
    required int patientId,
    required DateTimeRange dateRange,
  }) {
    print("Filtrando medicamentos en rango de fechas ${dateRange.start} - ${dateRange.end}");
    final filteredMedications = _medications.where((medication) {
      print("Evaluando medicamento: ${medication['name']}, ID: ${medication['id']}");

      if (medication["patient_id"] != patientId) {
        print("Medicamento ${medication['id']} no pertenece al paciente $patientId");
        return false;
      }

      final startDateString = medication["start_date"];
      final treatmentDuration = medication["treatment_duration"];
      if (startDateString == null || treatmentDuration == null) {
        print("Medicamento ${medication['id']} tiene datos incompletos.");
        return false;
      }

      final startDate = DateTime.parse(startDateString);
      final endDate = startDate.add(Duration(days: treatmentDuration));
      print("Tratamiento del medicamento ${medication['id']} inicia en $startDate y termina en $endDate");

      final isInDateRange = dateRange.start.isBefore(endDate) && dateRange.end.isAfter(startDate);
      if (!isInDateRange) {
        print("Medicamento ${medication['id']} no está en el rango de fechas.");
        return false;
      }

      final medicationId = medication["id"];
      final medicationPosologies = getPosologiesForMedication(medicationId);

      final validPosologies = <DateTime>[];
      for (var posology in medicationPosologies) {
        DateTime currentBaseDate = startDate.isAfter(dateRange.start) ? startDate : dateRange.start;
        while (currentBaseDate.isBefore(endDate) && currentBaseDate.isBefore(dateRange.end)) {
          final posologyTime = DateTime(
            currentBaseDate.year,
            currentBaseDate.month,
            currentBaseDate.day,
            posology["hour"] ?? 0,
            posology["minute"] ?? 0,
          );
          validPosologies.add(posologyTime);
          currentBaseDate = currentBaseDate.add(Duration(days: 1));
        }
      }

      print("Posologías válidas para medicamento ${medication['id']}: $validPosologies");
      medication["valid_posologies"] = validPosologies;

      return validPosologies.isNotEmpty;
    }).toList();

    return filteredMedications;
  }

  void _initializeMedicationState() {
    _isExpanded = {for (var med in _medications) med["id"]: false};
    _posologies = {};
    _isPosologyEmpty = {for (var med in _medications) med["id"]: false};
    notifyListeners();
  }

  void _toggleExpandedState(int medicationId) {
    _isExpanded[medicationId] = !_isExpanded[medicationId]!;
    notifyListeners();
  }

  void _setLoading(bool value) {
    _isLoading = value;
    notifyListeners();
  }
}
