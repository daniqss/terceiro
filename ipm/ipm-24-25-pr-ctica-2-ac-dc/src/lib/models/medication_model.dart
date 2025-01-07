import 'dart:convert';
import 'package:http/http.dart' as http;
import 'package:src/model.dart';

class MedicationModel {
  Future<List<Map<String, dynamic>>> getMedications(int patientId) async {
    final url = Uri.parse("${Model.path}/patients/$patientId/medications");
    final response = await http.get(url);

    if (response.statusCode != 200) {
      throw Exception("Error al obtener los medicamentos");
    }

    return List<Map<String, dynamic>>.from(jsonDecode(response.body));
  }

  Future<Map<String, dynamic>> getMedication(int patientId, int medicationId) async {
    final url = Uri.parse("${Model.path}/patients/$patientId/medications/$medicationId");
    final response = await http.get(url);

    if (response.statusCode != 200) {
      throw Exception("Error al obtener el medicamento");
    }

    return jsonDecode(response.body);
  }

}
