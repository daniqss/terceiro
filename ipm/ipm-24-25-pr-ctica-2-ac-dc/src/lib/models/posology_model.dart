import 'dart:convert';
import 'package:http/http.dart' as http;
import 'package:src/model.dart';

class PosologyModel {
  Future<List<Map<String, dynamic>>> getPosologies(int patientId, int medicationId) async {
    final url = Uri.parse("${Model.path}/patients/$patientId/medications/$medicationId/posologies");
    final response = await http.get(url);

    if (response.statusCode != 200) {
      throw Exception("Error al obtener las posologías");
    }

    // Verifica qué datos estás recibiendo
    List<Map<String, dynamic>> posologies = List<Map<String, dynamic>>.from(jsonDecode(response.body));

    return posologies;
  }

  Future<Map<String, dynamic>> addPosology(int patientId, int medicationId, int minute, int hour) async {
    final url = Uri.parse("${Model.path}/patients/$patientId/medications/$medicationId/posologies");
    final response = await http.post(
      url,
      headers: {"Content-Type": "application/json"},
      body: jsonEncode({
        "medication_id": medicationId,
        "minute": minute,
        "hour": hour,
      }),
    );

    if (response.statusCode != 201) {
      throw Exception("Error al agregar posología");
    }

    return jsonDecode(response.body);
  }

  Future<void> deletePosology(int patientId, int medicationId, int posologyId) async {
    final url = Uri.parse("${Model.path}/patients/$patientId/medications/$medicationId/posologies/$posologyId");
    final response = await http.delete(url);

    if (response.statusCode != 204) {
      throw Exception("Error al eliminar la posología");
    }
  }
}
