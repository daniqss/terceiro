import { Model } from "./model.js";
import { MedicationView } from "./view.js";

(async function init() {
  const model = new Model();

  const medicationScheduleBody = document.getElementById("medication-schedule-body");
  const averageTime = document.getElementById("average-time");
  const totalIntakes = document.getElementById("total-intakes");
  const medicationNameElement = document.getElementById("medication-name");

  const periodOptions = document.getElementById("period-options");
  const dailyInput = document.getElementById("daily-input");
  const daysInput = document.getElementById("days-input");
  const rangeInput = document.getElementById("range-input");
  const startDateInput = document.getElementById("start-date");
  const endDateInput = document.getElementById("end-date");

  const medicationView = new MedicationView(
    null,
    medicationScheduleBody,
    averageTime,
    totalIntakes
  );


  const urlParams = new URLSearchParams(window.location.search);
  const patientId = urlParams.get("patientId");
  const medicationId = urlParams.get("medicationId");

  if (!patientId || !medicationId) {
    console.error("Missing patientId or medicationId in URL.");
    document.title = "Error - Medicamento";
    return;
  }

  try {

    const medication = await model.getMedication(patientId, medicationId);

    document.title = medication.name;
    medicationNameElement.textContent = medication.name;

    const today = new Date();
    const oneMonthAgo = new Date();
    oneMonthAgo.setMonth(today.getMonth() - 1);
    const intakes = await model.getMedicationIntakes(patientId, medicationId, oneMonthAgo, today);
    const posologies = await model.getPosologies(patientId, medicationId);
    medicationView.renderIntakes(intakes, posologies);

    setupPeriodSelection(patientId, medicationId);
  } catch (error) {
    console.error("Error loading medication details:", error);
    document.title = "Error - Medicamento";
  }

  function setupPeriodSelection(patientId, medicationId) {
    periodOptions.addEventListener("change", async () => {
      const selectedOption = periodOptions.value;

      dailyInput.style.display = selectedOption === "daily" ? "block" : "none";
      rangeInput.style.display = selectedOption === "range" ? "block" : "none";

      if (selectedOption === "monthly") {
        await loadMedicationsForLastMonth(patientId, medicationId);
      }
    });

    daysInput.addEventListener("input", async () => {
      const days = parseInt(daysInput.value, 10);

      if (days > 0) {
        await loadMedicationsForLastNDays(patientId, medicationId, days);
      }
    });

    startDateInput.addEventListener("change", handleDateRangeChange);
    endDateInput.addEventListener("change", handleDateRangeChange);

    async function handleDateRangeChange() {
      const startDate = startDateInput.value;
      const endDate = endDateInput.value;

      if (startDate && endDate) {
        await loadMedicationsForDateRange(patientId, medicationId, startDate, endDate);
      }
    }
  }

  async function loadMedicationsForLastMonth(patientId, medicationId) {
    const today = new Date();
    const oneMonthAgo = new Date();
    oneMonthAgo.setMonth(today.getMonth() - 1);

    try {
      const intakes = await model.getMedicationIntakes(patientId, medicationId, oneMonthAgo, today);
      const posologies = await model.getPosologies(patientId, medicationId);
      medicationView.renderIntakes(intakes, posologies);
    } catch (error) {
      console.error("Error loading last month's intakes:", error);
    }
  }

  async function loadMedicationsForLastNDays(patientId, medicationId, days) {
    const today = new Date();
    const nDaysAgo = new Date();
    nDaysAgo.setDate(today.getDate() - days);

    try {
      const intakes = await model.getMedicationIntakes(patientId, medicationId, nDaysAgo, today);
      const posologies = await model.getPosologies(patientId, medicationId);
      medicationView.renderIntakes(intakes, posologies);
    } catch (error) {
      console.error(`Error loading intakes for the last ${days} days:`, error);
    }
  }

  async function loadMedicationsForDateRange(patientId, medicationId, startDate, endDate) {
    try {
      const intakes = await model.getMedicationIntakes(patientId, medicationId, new Date(startDate), new Date(endDate));
      const posologies = await model.getPosologies(patientId, medicationId);
      medicationView.renderIntakes(intakes, posologies);
    } catch (error) {
      console.error("Error loading intakes for the date range:", error);
    }
  }
})();
