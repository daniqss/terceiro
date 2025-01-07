import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:src/screens/login_view.dart';
import 'package:src/providers/login_provider.dart';
import 'package:src/providers/patient_provider.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MultiProvider(
      providers: [
        ChangeNotifierProvider(create: (context) => LoginProvider()),
        ChangeNotifierProvider(create: (context) => PatientProvider()),
      ],
      child: MaterialApp(
        title: 'SergasProMax',
        initialRoute: '/login',
        routes: {
          '/login': (context) => LoginView(),
        },
        debugShowCheckedModeBanner: false,
      ),
    );
  }
}
