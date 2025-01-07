import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:integration_test/integration_test.dart';
import 'package:provider/provider.dart';
import 'package:src/main.dart';
import 'package:src/model.dart';
import 'package:src/providers/login_provider.dart';

Future<String> getTestCode() async {
  final PatientModel model = PatientModel();

  final List<Map<String, dynamic>> patients = await model.getPatients();
  return patients[0]["code"];
}

void main() {

  IntegrationTestWidgetsFlutterBinding.ensureInitialized();
  TestWidgetsFlutterBinding.ensureInitialized();

  group('Login Integration Tests', ()
  {

    testWidgets('Login with invalid codes', (WidgetTester tester) async {

      await tester.pumpWidget(MyApp());

      expect(find.text('Bienvenido a SergasProMax'), findsOneWidget);

      final patientCodeInput = find.byType(TextField);
      expect(patientCodeInput, findsOneWidget);

      final loginButton = find.text('Entrar');
      expect(loginButton, findsOneWidget);

      await tester.enterText(patientCodeInput, 'invalid_code');
      await tester.pumpAndSettle();
      await tester.enterText(patientCodeInput, 'invalid_code');
      await tester.pumpAndSettle();


      await tester.tap(loginButton);
      await tester.pumpAndSettle();

      expect(find.text('Paciente no encontrado'), findsOneWidget);
    });

    testWidgets('Login with valid code', (WidgetTester tester) async {

      await tester.pumpWidget(MyApp());

      final patientCodeInput = find.byType(TextField);
      expect(patientCodeInput, findsOneWidget);

      final loginButton = find.text('Entrar');
      expect(loginButton, findsOneWidget);

      final code = await getTestCode();


      await tester.enterText(patientCodeInput, code);
      await tester.pumpAndSettle();
      await tester.enterText(patientCodeInput, code);
      await tester.pumpAndSettle();


      await tester.tap(loginButton);
      await tester.pumpAndSettle();

      expect(find.text('Paciente no encontrado'), findsNothing);

      // tests calendarios
      expect(find.text('Seleccionar Intervalo'), findsOneWidget);
      await tester.tap(find.text('Seleccionar Intervalo'));
      await tester.pumpAndSettle();
      expect(find.byType(DateRangePickerDialog), findsOneWidget);
    });



    testWidgets('Logout test', (WidgetTester tester) async {

      await tester.pumpWidget(MyApp());

      final patientCodeInput = find.byType(TextField);
      expect(patientCodeInput, findsOneWidget);

      final loginButton = find.text('Entrar');
      expect(loginButton, findsOneWidget);
      
      final code = await getTestCode();

      await tester.enterText(patientCodeInput, code);
      await tester.pumpAndSettle();
      await tester.enterText(patientCodeInput, code);
      await tester.pumpAndSettle();

      await tester.tap(loginButton);
      await tester.pumpAndSettle();

      expect(find.text('Paciente no encontrado'), findsNothing);

      final logoutButton = find.byIcon(Icons.logout);
      expect(logoutButton, findsOneWidget);

      await tester.tap(logoutButton);
      await tester.pumpAndSettle();

      expect(find.text('Bienvenido a SergasProMax'), findsOneWidget);
      expect(find.byType(TextField), findsOneWidget);

    });
  });
}