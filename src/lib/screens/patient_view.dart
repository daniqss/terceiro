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

class PatientViewState extends State<PatientView> with SingleTickerProviderStateMixin {
  late AnimationController _animationController;
  late Animation<Offset> _slideAnimation;
  late bool _isLoadingFilter;
  late List<Map<String, dynamic>> _filteredMedications;
  final Set<int> _expandedMedications = {};
  DateTimeRange? _selectedDateRange;

  @override
  void initState() {
    super.initState();
    _isLoadingFilter = false;
    _filteredMedications = [];
    final today = DateTime.now();
    _selectedDateRange = DateTimeRange(start: today, end: today);

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
      final patientProvider = Provider.of<PatientProvider>(context, listen: false);
      patientProvider.loadPatientData(widget.patientId).then((_) {
        patientProvider.loadAllPosologies(widget.patientId).then((_) {
          _applyDateRangeFilter(patientProvider);
        });
      });
    });
  }

  void toggleMedicationsList() {
    if (_animationController.isDismissed) {
      _animationController.forward();
    } else {
      _animationController.reverse();
    }
  }

  Future<void> _applyDateRangeFilter(PatientProvider provider) async {
    if (_selectedDateRange != null) {
      setState(() => _isLoadingFilter = true);

      await provider.filterMedicationsByDateRange(
        _selectedDateRange!.start,
        _selectedDateRange!.end,
      );

      setState(() {
        _filteredMedications = provider.filteredMedications;
        _isLoadingFilter = false;
      });
    }
  }

  Future<void> _selectDateRange() async {
    final DateTimeRange? picked = await showDateRangePicker(
      context: context,
      firstDate: DateTime.now().subtract(const Duration(days: 30)),
      lastDate: DateTime.now().add(const Duration(days: 30)),
      initialDateRange: _selectedDateRange,
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
        _selectedDateRange = picked;
      });

      final patientProvider = Provider.of<PatientProvider>(context, listen: false);
      await _applyDateRangeFilter(patientProvider);
    }
  }

  Future<void> _addIntake(BuildContext context, int medicationId) async {
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
      final formattedDate = DateFormat("yyyy-MM-ddTHH:mm").format(selectedDateTime);

      final intakeData = {'date': formattedDate};

      final patientProvider = Provider.of<PatientProvider>(context, listen: false);
      await patientProvider.addMedicationIntake(widget.patientId, medicationId, intakeData);
      await _applyDateRangeFilter(patientProvider);
    }
  }

  @override
  void dispose() {
    _animationController.dispose();
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

        final isLandscape = MediaQuery.of(context).orientation == Orientation.landscape;

        return Scaffold(
          appBar: AppBar(
            backgroundColor: Colors.teal,
            foregroundColor: Colors.black,
            title: Text("Paciente: ${patient["name"]} ${patient["surname"]}"),
            centerTitle: true,
            leading: isLandscape
                ? null
                : IconButton(
              icon: const Icon(Icons.menu),
              onPressed: toggleMedicationsList,
            ),
            actions: [
              IconButton(
                icon: const Icon(Icons.logout),
                onPressed: () {
                  final loginProvider = Provider.of<LoginProvider>(context, listen: false);
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
                  Container(
                    padding: const EdgeInsets.all(16.0),
                    child: ElevatedButton.icon(
                      onPressed: _selectDateRange,
                      style: ElevatedButton.styleFrom(
                        backgroundColor: Colors.teal,
                        foregroundColor: Colors.black
                      ),
                      icon: const Icon(Icons.calendar_today),
                      label: const Text("Seleccionar Intervalo"),
                    ),
                  ),
                  Expanded(
                    child: _isLoadingFilter
                        ? const Center(child: CircularProgressIndicator())
                        : _buildMedicationsList(_filteredMedications, patientProvider),
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
          "Medicaciones",
          style: TextStyle(fontWeight: FontWeight.bold, fontSize: 18),
        ),
        Expanded(
          child: ListView.builder(
            itemCount: allMedications.length,
            itemBuilder: (context, index) {
              final medication = allMedications[index];
              final medicationId = medication["id"];
              final posologies = Provider.of<PatientProvider>(context, listen: false)
                  .getPosologiesForMedication(medicationId);
              final isExpanded = _expandedMedications.contains(medicationId);

              return buildMedicationItem(medication, medicationId, posologies, isExpanded);
            },
          ),
        ),
      ],
    );
  }

  Widget buildMedicationItem(dynamic medication, int medicationId, List posologies, bool isExpanded) {
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
                }
              });
            },
            child: Container(
              padding: const EdgeInsets.all(12.0),
              decoration: BoxDecoration(
                color: Colors.white,
                borderRadius: BorderRadius.circular(8.0),
                boxShadow: const [
                  BoxShadow(color: Colors.black12, blurRadius: 4.0, offset: Offset(0, 2)),
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
                style: TextStyle(fontStyle: FontStyle.italic, color: Colors.grey),
              ),
            )
                : Column(
              children: posologies.map((posology) {
                final hour = posology['hour'].toString().padLeft(2, '0');
                final minute = posology['minute'].toString().padLeft(2, '0');
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

  Widget _buildMedicationsList(List filteredMedications, PatientProvider patientProvider) {
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
            title: Text(medication["name"]),
            trailing: const Icon(Icons.add),
            onTap: () => _addIntake(context, medicationId),
          ),
        );
      },
    );
  }
}