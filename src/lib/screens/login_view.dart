import 'package:flutter/material.dart';
import 'package:flutter_device_type/flutter_device_type.dart';
import 'package:provider/provider.dart';
import 'package:src/providers/login_provider.dart';
import 'patient_view.dart';

class LoginView extends StatelessWidget {
  const LoginView({super.key});

  @override
  Widget build(BuildContext context) {
    TextEditingController patientCodeController = TextEditingController();
    final bool isWatch = MediaQuery.of(context).size.width < 300;

    if (isWatch) {
      return Scaffold(
        backgroundColor: Colors.teal,
        body: Stack(
          children: [
            Center(
              child: Column(
                mainAxisSize: MainAxisSize.min,
                children: [
                  SizedBox(
                    width: 200,
                    child: Padding(
                      padding: EdgeInsets.symmetric(horizontal: 8.0, vertical: 4.0),
                      child: PatientCodeInput(patientCodeController, isWatch),
                    ),
                  ),
                  SizedBox(height: 12),
                  Consumer<LoginProvider>(
                    builder: (context, loginProvider, child) {
                      return LoginButton(
                        isWatch: isWatch,
                        onPressed: () async { logInto(loginProvider, patientCodeController, context); },
                      );
                    },
                  ),
                ],
              ),
            ),
          ],
        ),
      );
    }

    return Scaffold(
      body: Stack(
        children: [
          Container(
            decoration: const BoxDecoration(
              gradient: LinearGradient(
                begin: Alignment.topCenter,
                end: Alignment.bottomCenter,
                colors: [Colors.teal, Colors.tealAccent],
              ),
            ),
          ),
          Padding(
            padding: const EdgeInsets.symmetric(horizontal: 24.0),
            child: Consumer<LoginProvider>(
              builder: (context, loginProvider, child) {
                return Column(
                  mainAxisAlignment: MainAxisAlignment.center,
                  crossAxisAlignment: CrossAxisAlignment.stretch,
                  children: [
                    const Spacer(flex: 2),
                    CircleAvatar(
                      radius: 50,
                      backgroundColor: Colors.white.withOpacity(0.2),
                      child: const Icon(
                        Icons.medical_services_outlined,
                        size: 60,
                        color: Colors.white,
                      ),
                    ),
                    const SizedBox(height: 16),
                    Text(
                      'Bienvenido a SergasProMax',
                      style: const TextStyle(
                        fontSize: 26,
                        fontWeight: FontWeight.w600,
                        color: Colors.white,
                      ),
                      textAlign: TextAlign.center,
                    ),
                    const Spacer(flex: 1),
                    PatientCodeInput(patientCodeController, isWatch),
                    const Spacer(flex: 1),
                    loginProvider.isLoading
                        ? const Center(
                            child:
                                CircularProgressIndicator(color: Colors.white),
                          )
                        : ElevatedButton(
                            onPressed: () async { logInto(loginProvider, patientCodeController, context); },
                            style: ElevatedButton.styleFrom(
                              backgroundColor: Colors.teal,
                              foregroundColor: Colors.white,
                              minimumSize: const Size(double.infinity, 50),
                              shape: RoundedRectangleBorder(
                                borderRadius: BorderRadius.circular(16),
                              ),
                              shadowColor: Colors.black26,
                              elevation: 5,
                            ),
                            child: const Text(
                              'Entrar',
                              style: TextStyle(
                                fontSize: 18,
                                fontWeight: FontWeight.w600,
                              ),
                            ),
                          ),
                    const Spacer(flex: 2),
                    Text(
                      'Powered by SergasProMax',
                      style: TextStyle(
                        color: Colors.white70,
                        fontSize: 14,
                        fontStyle: FontStyle.italic,
                      ),
                      textAlign: TextAlign.center,
                    ),
                  ],
                );
              },
            ),
          ),
        ],
      ),
    );
  }
}

Widget PatientCodeInput(TextEditingController patientCodeController, bool isWatch) {
  return TextField(
    controller: patientCodeController,
    style: TextStyle(
      color: Colors.black87,
      fontSize: isWatch ? 12 : 18,
    ),
    decoration: InputDecoration(
      hintText: 'Código de paciente',
      hintStyle: TextStyle(
        color: Colors.teal.shade200,
        fontSize: isWatch ? 12 : 16,
      ),
      filled: true,
      fillColor: Colors.white,
      contentPadding: EdgeInsets.symmetric(
        vertical: isWatch ? 8.0 : 18.0,
        horizontal: isWatch ? 8.0 : 16.0,
      ),
      border: OutlineInputBorder(
        borderRadius: BorderRadius.circular(isWatch ? 12 : 20),
        borderSide: BorderSide.none,
      ),
      focusedBorder: OutlineInputBorder(
        borderRadius: BorderRadius.circular(isWatch ? 12 : 20),
        borderSide: const BorderSide(color: Colors.teal),
      ),
      prefixIcon: Icon(
        Icons.person_outline,
        color: Colors.teal,
        size: isWatch ? 16 : 24,
      ),
    ),
    keyboardType: TextInputType.text,
    textAlignVertical: TextAlignVertical.center,
  );
}

Widget LoginButton({
  required bool isWatch,
  required VoidCallback onPressed,
}) {
  return Container(
    margin: EdgeInsets.symmetric(
      horizontal: isWatch ? 4.0 : 16.0,
      vertical: isWatch ? 2.0 : 8.0,
    ),
    child: ElevatedButton(
      onPressed: onPressed,
      style: ElevatedButton.styleFrom(
        backgroundColor: Colors.teal,
        foregroundColor: Colors.white,
        minimumSize: Size(
          double.infinity,
          isWatch ? 30 : 50,
        ),
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(isWatch ? 10 : 16),
        ),
        shadowColor: Colors.black26,
        elevation: 3,
      ),
      child: Text(
        'Entrar',
        style: TextStyle(
          fontSize: isWatch ? 12 : 18,
          fontWeight: FontWeight.w500,
        ),
      ),
    ),
  );
}

void showErrorMessage(BuildContext context, String message, bool isWatch) {
  ScaffoldMessenger.of(context).showSnackBar(
    SnackBar(
      content: Text(
        message,
        textAlign: TextAlign.center,
        style: TextStyle(
          fontSize: isWatch ? 9 : 16,
        ),
      ),
      backgroundColor: Colors.redAccent,
      duration: const Duration(seconds: 2),
    ),
  );
}

void logInto(LoginProvider loginProvider, TextEditingController patientCodeController, BuildContext context) async {
  final patientCode = patientCodeController.text;

  if (patientCode.isNotEmpty) {
    final patient =
    await loginProvider.login(patientCode);

    if (patient != null) {
      Navigator.pushReplacement(
        context,
        MaterialPageRoute(
          builder: (context) => PatientView(
            patientId: patient['id'],
          ),
        ),
      );
    } else {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text(loginProvider.errorMessage),
          backgroundColor: Colors.redAccent,
        ),
      );
    }
  } else {
    ScaffoldMessenger.of(context).showSnackBar(
      const SnackBar(
        content: Text(
            'Por favor ingrese un código de paciente'),
      ),
    );
  }
}