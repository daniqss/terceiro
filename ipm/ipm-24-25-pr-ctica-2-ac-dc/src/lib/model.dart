export 'models/patient_model.dart';
export 'models/medication_model.dart';
export 'models/posology_model.dart';

class Model {
  static const String host = '10.0.2.2'; // Localhost for Android emulator
  static const int port = 8000;
  static const String path = "http://$host:$port";
}
