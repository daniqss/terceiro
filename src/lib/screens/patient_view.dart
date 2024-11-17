import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:src/providers/patient_provider.dart';
import 'package:src/providers/login_provider.dart';

class PatientView extends StatefulWidget {
  final int patientId;

  const PatientView({Key? key, required this.patientId}) : super(key: key);

  @override
  PatientViewState createState() => PatientViewState();
}

class PatientViewState extends State<PatientView> with SingleTickerProviderStateMixin {
  late AnimationController _animationController;
  late Animation<Offset> _slideAnimation;
  final Set<int> _expandedMedications = {};
  int? _selectedFilter = 24;
  DateTimeRange? _selectedDateRange;
  bool _isLoadingFilter = false;
  List<Map<String, dynamic>> _filteredMedications = [];
  final Map<int, bool> _checkedMedications = {};

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
      final patientProvider = Provider.of<PatientProvider>(context, listen: false);

      // Primero cargar datos de medicamentos y posologías.
      patientProvider.loadPatientData(widget.patientId).then((_) {
        // Luego, cargar las posologías de todos los medicamentos.
        patientProvider.loadAllPosologies(widget.patientId).then((_) {
          // Filtrar los medicamentos una vez que todos los datos están cargados.
          _filterMedications(patientProvider);
        });
      });
    });
  }

  @override
  void dispose() {
    _animationController.dispose();
    super.dispose();
  }

  void _filterMedications(PatientProvider provider) {
    setState(() => _isLoadingFilter = true);

    setState(() {
      if (_selectedFilter != null) {
        _filteredMedications = provider.filterMedications(
          patientId: widget.patientId,
          filterHours: _selectedFilter,
        );
      } else if (_selectedDateRange != null) {
        _filteredMedications = provider.filterMedicationsByDateRange(
          patientId: widget.patientId,
          dateRange: _selectedDateRange!,
        );
      }
      _isLoadingFilter = false;

      // Inicializa el estado de cada checkbox de los medicamentos filtrados
      for (var medication in _filteredMedications) {
        _checkedMedications[medication["id"]] = false;
      }
    });
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
      builder: (context, child) {
        return Theme(
          data: Theme.of(context).copyWith(
            colorScheme: const ColorScheme.light(
              primary: Colors.teal, // Color principal del calendario
              onPrimary: Colors.white, // Color del texto en botones
              onSurface: Colors.teal, // Color de texto en el calendario
            ),
            textButtonTheme: TextButtonThemeData(
              style: TextButton.styleFrom(
                foregroundColor: Colors.teal, // Color del botón de selección
              ),
            ),
          ),
          child: child!,
        );
      },
    );

    if (picked != null && picked != _selectedDateRange) {
      setState(() {
        _selectedDateRange = picked;
        _selectedFilter = null; // Cambiamos a rango personalizado
      });
      final patientProvider = Provider.of<PatientProvider>(context, listen: false);
      _filterMedications(patientProvider);
    }
  }

  void _updateFilter(int? filterHours) {
    setState(() {
      _selectedFilter = filterHours;
      if (filterHours != null) {
        _selectedDateRange = null; // Descartar rango personalizado si seleccionamos horas
      }
    });
    final patientProvider = Provider.of<PatientProvider>(context, listen: false);
    _filterMedications(patientProvider);
  }

  void toggleMedicationsList() {
    if (_animationController.isDismissed) {
      _animationController.forward();
    } else {
      _animationController.reverse();
    }
  }

  void _logout() {
    final loginProvider = Provider.of<LoginProvider>(context, listen: false);
    loginProvider.logout(context);
    Navigator.pushReplacementNamed(context, '/login');
  }

  @override
  Widget build(BuildContext context) {
    final isLandscape = MediaQuery.of(context).orientation == Orientation.landscape;

    return Consumer<PatientProvider>(
      builder: (context, patientProvider, child) {
        final patient = patientProvider.patient;
        if (patient == null) {
          return const Scaffold(
            body: Center(child: CircularProgressIndicator()),
          );
        }

        final patientName = patient["name"];
        final patientSurname = patient["surname"];

        return Scaffold(
          appBar: buildAppBar(isLandscape, patientName, patientSurname),
          body: Column(
            children: [
              buildFilterSection(),
              Expanded(
                child: _isLoadingFilter
                    ? const Center(child: CircularProgressIndicator())
                    : buildMedicationsList(_filteredMedications),
              ),
            ],
          ),
        );
      },
    );
  }

  AppBar buildAppBar(bool isLandscape, String patientName, String patientSurname) {
    return AppBar(
      backgroundColor: Colors.teal,
      title: Text(
        "Paciente: $patientName $patientSurname",
        style: const TextStyle(fontWeight: FontWeight.bold),
      ),
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
          DropdownButton<int?>(
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
              DropdownMenuItem(
                value: null,
                child: Text("Rango personalizado"),
              ),
            ],
            onChanged: (value) {
              if (value == null) {
                _selectDateRange();
              } else {
                _updateFilter(value);
              }
            },
          ),
        ],
      ),
    );
  }

  Widget buildMedicationsList(List medications) {
    return ListView.builder(
      itemCount: medications.length,
      itemBuilder: (context, index) {
        final medication = medications[index];
        final medicationId = medication["id"];
        final nextPosologyTime = _getNextPosologyTime(medication);
        final timeUntilNext = _calculateTimeUntil(nextPosologyTime);

        return Card(
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(12),
          ),
          margin: const EdgeInsets.symmetric(vertical: 8, horizontal: 16),
          child: ListTile(
            leading: Checkbox(
              value: _checkedMedications[medicationId] ?? false,
              onChanged: (bool? value) {
                setState(() {
                  _checkedMedications[medicationId] = value ?? false;
                });
              },
            ),
            title: Text(
              medication["name"],
              style: const TextStyle(fontWeight: FontWeight.bold),
            ),
            subtitle: Text(
              timeUntilNext.isNotEmpty ? "Próxima dosis en: $timeUntilNext" : "Sin próximas dosis",
            ),
            trailing: const Icon(Icons.chevron_right),
            onTap: () {},
          ),
        );
      },
    );
  }

  DateTime? _getNextPosologyTime(Map<String, dynamic> medication) {
    final validPosologies = medication["valid_posologies"] as List<DateTime>;
    return validPosologies.isNotEmpty ? validPosologies.first : null;
  }

  String _calculateTimeUntil(DateTime? time) {
    if (time == null) return "";
    final now = DateTime.now();
    final difference = time.difference(now);

    if (difference.isNegative) {
      return "";
    } else if (difference.inHours > 0) {
      return "${difference.inHours} horas y ${difference.inMinutes % 60} minutos";
    } else {
      return "${difference.inMinutes} minutos";
    }
  }
}
