import 'package:flutter/material.dart';
import 'package:src/models/patient_model.dart';

class LoginProvider extends ChangeNotifier {
  bool _isLoading = false;
  String _errorMessage = '';
  Map<String, dynamic>? _patient;

  bool get isLoading => _isLoading;
  String get errorMessage => _errorMessage;
  Map<String, dynamic>? get patient => _patient;

  Future<Map<String, dynamic>?> login(String patientCode) async {
    final PatientModel _patientModel = PatientModel();
    _isLoading = true;
    notifyListeners();

    try {
      final patient = await _patientModel.getPatientByCode(patientCode);

      if (patient != null) {
        _isLoading = false;
        _patient = patient;
        notifyListeners();
        return patient;
      } else {
        _isLoading = false;
        _errorMessage = 'Paciente no encontrado';
        notifyListeners();
        return null;
      }
    } catch (e) {
      _isLoading = false;
      _errorMessage = 'Error al realizar la búsqueda';
      notifyListeners();
      return null;
    }
  }

  void logout(BuildContext context) {
    _patient = null;  // Limpiar los datos del paciente
    notifyListeners(); // Notificar a los listeners que el estado ha cambiado

    // Redirigir al login después de cerrar sesión
    Navigator.pushReplacementNamed(context, '/login');
  }
}
