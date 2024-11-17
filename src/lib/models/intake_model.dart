import 'dart:convert';
import 'package:http/http.dart' as http;
import 'package:src/model.dart';

class IntakeModel {
  Future<void> addIntake(int patientId, int medicationId, Map<String, dynamic> intakeData) async {
    print("Intake data: $intakeData");
    final url = Uri.parse("${Model.path}/patients/$patientId/medications/$medicationId/intakes");
    final response = await http.post(
      url,
      headers: {"Content-Type": "application/json"},
      body: jsonEncode(intakeData),
    );

    if (response.statusCode != 201) {
      print("Response status: ${response.statusCode}");
      print("Response body: ${response.body}");
      throw Exception("Error al añadir el intake");
    }
  }

  Future<List<Map<String, dynamic>>> getIntakesByPatientAndMedication(int patientId, int medicationId) async {
    final url = Uri.parse("${Model.path}/patients/$patientId/medications/$medicationId/intakes");
    final response = await http.get(url);

    if (response.statusCode != 200) {
      throw Exception("Error al obtener los intakes para el paciente y medicamento");
    }

    return List<Map<String, dynamic>>.from(jsonDecode(response.body));
  }

  Future<List<Map<String, dynamic>>> getIntakesByPatient(int patientId) async {
    final url = Uri.parse("${Model.path}/patients/$patientId/intakes");
    final response = await http.get(url);

    if (response.statusCode != 200) {
      throw Exception("Error al obtener los intakes para el paciente");
    }

    return List<Map<String, dynamic>>.from(jsonDecode(response.body));
  }

  Future<void> deleteIntake(int patientId, int medicationId, int intakeId) async {
    final url = Uri.parse("${Model.path}/patients/$patientId/medications/$medicationId/intakes/$intakeId");
    final response = await http.delete(url);

    if (response.statusCode != 204) {
      throw Exception("Error al eliminar el intake");
    }
  }
}
