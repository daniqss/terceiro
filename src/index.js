import { Model } from "./model.js";
import { PatientListView, MedicationView } from "./view.js";

(async function init() {
  const model = new Model();

  const patientCodeInput = document.getElementById("patient-code-input");
  const suggestionsContainer = document.getElementById("suggestions-list");
  const medicationsListContainer = document.getElementById("patient-medication-list-container");
  const medicationsList = document.getElementById("patient-medication-list");

  if (!patientCodeInput || !suggestionsContainer || !medicationsListContainer || !medicationsList) {
    return;
  }

  const patientListView = new PatientListView(patientCodeInput, suggestionsContainer);
  const medicationView = new MedicationView(medicationsList);

  patientCodeInput.addEventListener("input", async (event) => {
    const query = event.target.value.trim();
    if (query === "") {
      patientListView.clearSuggestions();
      return;
    }

    try {
      const patients = await model.getPatients();
      const filteredPatients = patients
        .filter((p) =>
          p.name.toLowerCase().includes(query.toLowerCase()) ||
          p.surname.toLowerCase().includes(query.toLowerCase()) ||
          p.code.toLowerCase().includes(query.toLowerCase())
        )
        .slice(0, 5);

      patientListView.renderSuggestions(filteredPatients);
    } catch (error) {
      patientListView.showError("Error al buscar pacientes.");
    }
  });

  suggestionsContainer.addEventListener("click", async (event) => {
    const item = event.target.closest(".suggestion-item");
    if (!item) return;

    const patientId = item.dataset.patientId;
    patientListView.clearSuggestions();
    patientCodeInput.value = "";

    try {
      const medications = await model.getMedications(patientId);
      medicationView.renderMedicationList(medications, patientId);
      medicationsListContainer.style.display = "block";
    } catch (error) {
      medicationsListContainer.style.display = "none";
    }
  });

  document.addEventListener("DOMContentLoaded", () => {
    medicationsListContainer.style.display = "none";
  });
})();
