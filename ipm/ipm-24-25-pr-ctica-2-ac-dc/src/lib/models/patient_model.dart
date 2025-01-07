import 'dart:convert';
import 'package:http/http.dart' as http;
import 'package:src/model.dart';

class PatientModel {
  Future<Map<String, dynamic>> getPatient(int patientId) async {
    final url = Uri.parse("${Model.path}/patients/$patientId");
    final response = await http.get(url);

    if (response.statusCode != 200) {
      throw Exception("Error al obtener el paciente");
    }

    return jsonDecode(response.body);
  }

  Future<Map<String, dynamic>?> getPatientByCode(String code) async {
    try {
      // Obtener la lista de pacientes
      final List<Map<String, dynamic>> patients = await getPatients();

      // Buscar el paciente cuyo código coincide
      for (var patient in patients) {
        print("Buscando paciente: $patient");
        print("Código: ${patient["code"]}");
        print("Code: $code");
        if (patient["code"] == code) {
          print("Paciente encontrado: $patient");
          return patient;
        }
      }

      // Si no se encuentra el paciente, retornar null
      return null;
    } catch (e) {
      // En caso de error, retornar null
      print("Error en la búsqueda del paciente: $e");
      return null;
    }
  }


  Future<List<Map<String, dynamic>>> getPatients() async {
    final url = Uri.parse("${Model.path}/patients");
    final response = await http.get(url);

    if (response.statusCode != 200) {
      throw Exception("Error al obtener la lista de pacientes");
    }

    return List<Map<String, dynamic>>.from(jsonDecode(response.body));
  }
}
