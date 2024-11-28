import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import 'package:provider/provider.dart';
import 'package:src/providers/patient_provider.dart';
import 'package:src/providers/login_provider.dart';

class PatientView extends StatefulWidget {
  final int patientId;

  const PatientView({super.key, required this.patientId});

  @override
  PatientViewState createState() => PatientViewState();
}

class PatientViewState extends State<PatientView>
    with SingleTickerProviderStateMixin {
  late AnimationController animationController;
  late Animation<Offset> slideAnimation;
  late bool isLoadingFilter;
  late List<Map<String, dynamic>> filteredMedications;
  final Set<int> expandedMedications = {};
  DateTimeRange? selectedDateRange;

  @override
  void initState() {
    super.initState();
    isLoadingFilter = false;
    filteredMedications = [];
    final today = DateTime.now();
    selectedDateRange = DateTimeRange(start: today, end: today);

    animationController = AnimationController(
      vsync: this,
      duration: const Duration(milliseconds: 300),
    );
    slideAnimation = Tween<Offset>(
      begin: const Offset(-1.0, 0.0),
      end: Offset.zero,
    ).animate(CurvedAnimation(
      parent: animationController,
      curve: Curves.easeInOut,
    ));

    WidgetsBinding.instance.addPostFrameCallback((_) {
      final patientProvider =
          Provider.of<PatientProvider>(context, listen: false);
      patientProvider.loadPatientData(widget.patientId).then((_) {
        patientProvider.loadAllPosologies(widget.patientId).then((_) {
          applyDateRangeFilter(patientProvider);
        });
      });
    });
  }

  void toggleMedicationsList() {
    if (animationController.isDismissed) {
      animationController.forward();
    } else {
      animationController.reverse();
    }
  }

  Future<void> applyDateRangeFilter(PatientProvider provider) async {
    if (selectedDateRange != null) {
      setState(() => isLoadingFilter = true);

      await provider.filterMedicationsByDateRange(
        selectedDateRange!.start,
        selectedDateRange!.end,
      );

      setState(() {
        filteredMedications = provider.filteredMedications;
        isLoadingFilter = false;
      });
    }
  }

  Future<void> selectDateRange() async {
    final DateTimeRange? picked = await showDateRangePicker(
      context: context,
      firstDate: DateTime.now().subtract(const Duration(days: 30)),
      lastDate: DateTime.now().add(const Duration(days: 30)),
      initialDateRange: selectedDateRange,
      builder: (context, child) {
        return Theme(
          data: Theme.of(context).copyWith(
            colorScheme: ColorScheme.light(
              primary: Colors.teal,
              onPrimary: Colors.white,
              onSurface: Colors.teal.shade700,
            ),
          ),
          child: child!,
        );
      },
    );

    if (picked != null) {
      setState(() {
        selectedDateRange = picked;
      });

      final patientProvider =
          Provider.of<PatientProvider>(context, listen: false);
      await applyDateRangeFilter(patientProvider);
    }
  }

  Future<void> addIntake(BuildContext context, int medicationId) async {
    DateTime selectedDateTime = DateTime.now();

    final intakeConfirmed = await showDialog<bool>(
      context: context,
      builder: (context) {
        return AlertDialog(
          title: const Text('Añadir Toma'),
          content: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              ElevatedButton.icon(
                onPressed: () async {
                  final time = await showTimePicker(
                    context: context,
                    initialTime: TimeOfDay.fromDateTime(selectedDateTime),
                  );

                  if (time != null) {
                    setState(() {
                      selectedDateTime = DateTime(
                        selectedDateTime.year,
                        selectedDateTime.month,
                        selectedDateTime.day,
                        time.hour,
                        time.minute,
                      );
                    });
                  }
                },
                icon: const Icon(Icons.access_time),
                label: Text(
                  'Hora: ${TimeOfDay.fromDateTime(selectedDateTime).format(context)}',
                ),
              ),
            ],
          ),
          actions: [
            TextButton(
              onPressed: () => Navigator.pop(context, false),
              child: const Text('Cancelar'),
            ),
            ElevatedButton(
              onPressed: () => Navigator.pop(context, true),
              child: const Text('Confirmar'),
            ),
          ],
        );
      },
    );

    if (intakeConfirmed == true) {
      final formattedDate =
          DateFormat("yyyy-MM-ddTHH:mm").format(selectedDateTime);

      final intakeData = {'date': formattedDate};

      final patientProvider =
          Provider.of<PatientProvider>(context, listen: false);
      await patientProvider.addMedicationIntake(
          widget.patientId, medicationId, intakeData);
      await applyDateRangeFilter(patientProvider);
    }
  }

  @override
  void dispose() {
    animationController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Consumer<PatientProvider>(
      builder: (context, patientProvider, child) {
        final patient = patientProvider.patient;
        final allMedications = patientProvider.medications;

        if (patient == null) {
          return const Scaffold(
            body: Center(child: CircularProgressIndicator()),
          );
        }

        final isLandscape =
            MediaQuery.of(context).orientation == Orientation.landscape;
        final isWatch = MediaQuery.of(context).size.width < 300;
        return Scaffold(
          appBar: AppBar(
            backgroundColor: Colors.teal,
            foregroundColor: Colors.black,
            centerTitle: true,
            leading: isLandscape || isWatch
                ? null
                : IconButton(
                    icon: const Icon(Icons.menu),
                    onPressed: toggleMedicationsList,
                  ),
            title: isWatch
                ? Center(
                    child: Icon(
                      Icons.logout,
                      size: 16,
                    ),
                  )
                : Text("Paciente: ${patient["name"]} ${patient["surname"]}"),
            actions: isWatch
                ? null
                : [
                    IconButton(
                      icon: Icon(
                        Icons.logout,
                        size: 24,
                      ),
                      onPressed: () {
                        final loginProvider =
                            Provider.of<LoginProvider>(context, listen: false);
                        loginProvider.logout(context);
                        Navigator.pushReplacementNamed(context, '/login');
                      },
                    ),
                  ],
          ),
          body: Stack(
            children: [
              Column(
                children: [
                  if (!isWatch)
                    Container(
                      padding: const EdgeInsets.all(16.0),
                      child: ElevatedButton.icon(
                        onPressed: selectDateRange,
                        style: ElevatedButton.styleFrom(
                          backgroundColor: Colors.teal,
                          foregroundColor: Colors.black,
                          padding: isWatch
                              ? const EdgeInsets.symmetric(
                                  horizontal: 8, vertical: 4)
                              : null,
                        ),
                        icon: Icon(
                          Icons.calendar_today,
                          size: isWatch ? 12 : 24,
                        ),
                        label: Text(
                          "Seleccionar Intervalo",
                          style: TextStyle(fontSize: 16),
                        ),
                      ),
                    ),
                  Expanded(
                    child: isLoadingFilter
                        ? const Center(child: CircularProgressIndicator())
                        : buildMedicationsList(
                            filteredMedications, patientProvider, isWatch),
                  ),
                ],
              ),
              buildSlideMenu(allMedications, isLandscape),
            ],
          ),
        );
      },
    );
  }

  Widget buildSlideMenu(List allMedications, bool isLandscape) {
    if (isLandscape) {
      return Drawer(
        child: buildSlideMenuContent(allMedications),
      );
    } else {
      return SlideTransition(
        position: slideAnimation,
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
          "Medicaciones",
          style: TextStyle(fontWeight: FontWeight.bold, fontSize: 18),
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
              final isExpanded = expandedMedications.contains(medicationId);

              return buildMedicationItem(
                  medication, medicationId, posologies, isExpanded);
            },
          ),
        ),
      ],
    );
  }

  Widget buildMedicationItem(
      dynamic medication, int medicationId, List posologies, bool isExpanded) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 4.0),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          GestureDetector(
            onTap: () {
              setState(() {
                if (isExpanded) {
                  expandedMedications.remove(medicationId);
                } else {
                  expandedMedications.add(medicationId);
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
                      offset: Offset(0, 2)),
                ],
              ),
              child: Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  Expanded(
                    child: Text(
                      medication["name"],
                      style: const TextStyle(fontWeight: FontWeight.bold),
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
                    padding: EdgeInsets.all(8.0),
                    child: Text(
                      "No hay posologías disponibles.",
                      style: TextStyle(
                          fontStyle: FontStyle.italic, color: Colors.grey),
                    ),
                  )
                : Column(
                    children: posologies.map((posology) {
                      final hour = posology['hour'].toString().padLeft(2, '0');
                      final minute =
                          posology['minute'].toString().padLeft(2, '0');
                      return Padding(
                        padding: const EdgeInsets.all(8.0),
                        child: Text("Posología: $hour:$minute"),
                      );
                    }).toList(),
                  ),
        ],
      ),
    );
  }

  Widget buildMedicationsList(
      List filteredMedications, PatientProvider patientProvider, bool isWatch) {
    return ListView.builder(
      itemCount: filteredMedications.length,
      itemBuilder: (context, index) {
        final medication = filteredMedications[index];
        final medicationId = medication["id"];
        return Card(
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(12),
          ),
          margin: const EdgeInsets.symmetric(vertical: 8, horizontal: 16),
          child: ListTile(
            titleTextStyle: TextStyle(
                fontSize: isWatch ? 11 : 16,
                backgroundColor: Colors.white60,
                color: Colors.black,
            ),
            title: Text(medication["name"]),
            trailing: const Icon(Icons.add),
            onTap: () => addIntake(context, medicationId),
          ),
        );
      },
    );
  }
}
