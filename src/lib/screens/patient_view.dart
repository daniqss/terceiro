import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:src/providers/patient_provider.dart';
import 'package:src/providers/login_provider.dart';

class PatientView extends StatefulWidget {
  final int patientId;

  const PatientView({super.key, required this.patientId});

  @override
  PatientViewState createState() => PatientViewState();
}

class PatientViewState extends State<PatientView> with SingleTickerProviderStateMixin {
  late AnimationController _animationController;
  late Animation<Offset> _slideAnimation;
  final Set<int> _expandedMedications = {};
  int _selectedFilter = 24;
  DateTimeRange? _selectedDateRange;
  bool _isLoadingFilter = false;

  @override
  void initState() {
    super.initState();
    _animationController = AnimationController(
      vsync: this,
      duration: const Duration(milliseconds: 300),
    );
    _slideAnimation = Tween<Offset>(
      begin: const Offset(-1.0, 0.0),
      end: Offset.zero,
    ).animate(CurvedAnimation(
      parent: _animationController,
      curve: Curves.easeInOut,
    ));

    WidgetsBinding.instance.addPostFrameCallback((_) {
      Provider.of<PatientProvider>(context, listen: false)
          .loadPatientData(widget.patientId);
    });
  }

  @override
  void dispose() {
    _animationController.dispose();
    super.dispose();
  }

  void toggleMedicationsList() {
    if (_animationController.isDismissed) {
      _animationController.forward();
    } else {
      _animationController.reverse();
    }
  }

  Future<void> _selectDateRange() async {
    final DateTimeRange? picked = await showDateRangePicker(
      context: context,
      firstDate: DateTime.now().subtract(const Duration(days: 30)),
      lastDate: DateTime.now().add(const Duration(days: 30)),
      initialDateRange: _selectedDateRange ??
          DateTimeRange(
            start: DateTime.now(),
            end: DateTime.now().add(const Duration(days: 1)),
          ),
    );

    if (picked != null && picked != _selectedDateRange) {
      setState(() {
        _selectedDateRange = picked;
        _isLoadingFilter = true;
      });
      Future.delayed(const Duration(milliseconds: 500), () {
        setState(() {
          _isLoadingFilter = false;
        });
      });
    }
  }

  void _logout() {
    final loginProvider = Provider.of<LoginProvider>(context, listen: false);
    loginProvider.logout(context);
    Navigator.pushReplacementNamed(context, '/login');
  }

  @override
  Widget build(BuildContext context) {
    final isLandscape = MediaQuery
        .of(context)
        .orientation == Orientation.landscape;

    return Consumer<PatientProvider>(
      builder: (context, patientProvider, child) {
        final patient = patientProvider.patient;
        final allMedications = patientProvider.medications;
        final medications = patientProvider.filterMedications(
          patientId: widget.patientId,
          filterHours: _selectedFilter,
          dateRange: _selectedDateRange,
        );

        if (patient == null) {
          return const Scaffold(
            body: Center(child: CircularProgressIndicator()),
          );
        }

        final patientName = patient["name"];
        final patientSurname = patient["surname"];

        if (isLandscape) {
          return Scaffold(
            appBar: buildAppBar(isLandscape, patientName, patientSurname),
            body: Row(
              children: [
                SizedBox(
                  width: 280,
                  child: Drawer(
                    child: buildSlideMenuContent(allMedications),
                  ),
                ),
                Expanded(
                  child: Column(
                    children: [
                      buildFilterSection(),
                      buildMedicationsList(medications),
                    ],
                  ),
                ),
              ],
            ),
          );
        } else {
          return Scaffold(
            appBar: buildAppBar(isLandscape, patientName, patientSurname),
            body: Stack(
              children: [
                Column(
                  children: [
                    buildFilterSection(),
                    buildMedicationsList(medications),
                  ],
                ),
                buildSlideMenu(allMedications, isLandscape),
              ],
            ),
          );
        }
      },
    );
  }

  AppBar buildAppBar(bool isLandscape, String patientName,
      String patientSurname) {
    return AppBar(
      backgroundColor: Colors.teal,
      title: Text(
        "Paciente: $patientName $patientSurname",
        style: const TextStyle(fontWeight: FontWeight.bold),
      ),
      centerTitle: true,
      leading: isLandscape ? null : IconButton(
        icon: const Icon(Icons.menu),
        onPressed: toggleMedicationsList,
      ),
      actions: [
        IconButton(
          icon: const Icon(Icons.logout),
          onPressed: _logout,
        ),
      ],
    );
  }

  Widget buildFilterSection() {
    return Container(
      padding: const EdgeInsets.all(16.0),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          DropdownButton<int>(
            value: _selectedFilter,
            underline: Container(),
            items: const [
              DropdownMenuItem(
                value: 24,
                child: Text("Próximas 24 horas"),
              ),
              DropdownMenuItem(
                value: 48,
                child: Text("Próximas 48 horas"),
              ),
              DropdownMenuItem(
                value: 72,
                child: Text("Próximas 72 horas"),
              ),
            ],
            onChanged: (value) {
              if (value != null) {
                setState(() {
                  _selectedFilter = value;
                  _isLoadingFilter = true;
                });
                Future.delayed(const Duration(milliseconds: 500), () {
                  setState(() {
                    _isLoadingFilter = false;
                  });
                });
              }
            },
          ),
          ElevatedButton.icon(
            onPressed: _selectDateRange,
            style: ElevatedButton.styleFrom(
              backgroundColor: Colors.teal,
            ),
            icon: const Icon(Icons.calendar_today),
            label: const Text("Intervalo"),
          ),
        ],
      ),
    );
  }

  Widget buildMedicationsList(List medications) {
    return _isLoadingFilter
        ? const Center(
      child: CircularProgressIndicator(),
    )
        : Expanded(
      child: ListView.builder(
        itemCount: medications.length,
        itemBuilder: (context, index) {
          final medication = medications[index];
          return Card(
            shape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(12),
            ),
            margin: const EdgeInsets.symmetric(
              vertical: 8,
              horizontal: 16,
            ),
            child: ListTile(
              title: FittedBox(
                fit: BoxFit.scaleDown,
                alignment: Alignment.centerLeft,
                child: Text(
                  medication["name"],
                  style: const TextStyle(
                    fontWeight: FontWeight.bold,
                  ),
                ),
              ),
              subtitle: Text("Dosis: ${medication["dosage"]}mg"),
              trailing: const Icon(Icons.chevron_right),
              onTap: () {},
            ),
          );
        },
      ),
    );
  }

  Widget buildSlideMenu(List allMedications, bool isLandscape) {
    if (isLandscape) {
      return buildSlideMenuContent(allMedications);
    } else {
      return SlideTransition(
        position: _slideAnimation,
        child: Container(
          width: 250,
          color: Colors.teal.shade100,
          padding: const EdgeInsets.all(16.0),
          child: buildSlideMenuContent(allMedications),
        ),
      );
    }
  }

  Widget buildSlideMenuContent(List allMedications) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        const Text(
          "Medicamentos",
          style: TextStyle(
            fontWeight: FontWeight.bold,
            fontSize: 18,
          ),
        ),
        Expanded(
          child: ListView.builder(
            itemCount: allMedications.length,
            itemBuilder: (context, index) {
              final medication = allMedications[index];
              final medicationId = medication["id"];
              final posologies =
              Provider.of<PatientProvider>(context, listen: false)
                  .getPosologiesForMedication(medicationId);
              final isExpanded = _expandedMedications.contains(medicationId);

              return buildMedicationItem(
                medication,
                medicationId,
                posologies,
                isExpanded,
                index,
              );
            },
          ),
        ),
      ],
    );
  }

  Widget buildMedicationItem(dynamic medication,
      int medicationId,
      List posologies,
      bool isExpanded,
      int index,) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 4.0),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          GestureDetector(
            onTap: () {
              setState(() {
                if (isExpanded) {
                  _expandedMedications.remove(medicationId);
                } else {
                  _expandedMedications.add(medicationId);
                  Provider.of<PatientProvider>(context, listen: false)
                      .loadPosologies(widget.patientId, medicationId, index);
                }
              });
            },
            child: Container(
              padding: const EdgeInsets.all(12.0),
              decoration: BoxDecoration(
                color: Colors.white,
                borderRadius: BorderRadius.circular(8.0),
                boxShadow: const [
                  BoxShadow(
                    color: Colors.black12,
                    blurRadius: 4.0,
                    offset: Offset(0, 2),
                  ),
                ],
              ),
              child: Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  Expanded(
                    child: FittedBox(
                      fit: BoxFit.scaleDown,
                      alignment: Alignment.centerLeft,
                      child: Text(
                        medication["name"],
                        style: const TextStyle(fontWeight: FontWeight.bold),
                      ),
                    ),
                  ),
                  Icon(
                    isExpanded ? Icons.expand_less : Icons.expand_more,
                    color: Colors.teal,
                  ),
                ],
              ),
            ),
          ),
          if (isExpanded)
            posologies.isEmpty
                ? const Padding(
              padding: EdgeInsets.symmetric(horizontal: 8.0, vertical: 4.0),
              child: Text(
                "No hay posologías disponibles.",
                style: TextStyle(
                    fontStyle: FontStyle.italic, color: Colors.grey),
              ),
            )
                : Column(
              children: posologies.map((posology) {
                final hour = posology['hour'].toString().padLeft(2, '0');
                final minute = posology['minute'].toString().padLeft(2, '0');
                return Padding(
                  padding: const EdgeInsets.symmetric(
                      vertical: 4.0, horizontal: 12.0),
                  child: Text("Posología: $hour:$minute"),
                );
              }).toList(),
            ),
        ],
      ),
    );
  }
}