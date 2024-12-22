import { Model } from "./model.js";
import { MedicationView } from "./view.js";

document.addEventListener("DOMContentLoaded", async () => {
  const model = new Model();

  const medicationScheduleBody = document.getElementById(
    "medication-schedule-body"
  );
  const averageTime = document.getElementById("average-time");
  const totalIntakes = document.getElementById("total-intakes");
  const medicationNameElement = document.getElementById("medication-name");

  const periodOptions = document.getElementById("period-options");
  const dailyInput = document.getElementById("daily-input");
  const daysInput = document.getElementById("days-input");
  dailyInput.onkeydown = preventFormSubmit;
  const rangeInput = document.getElementById("range-input");
  const startDateInput = document.getElementById("start-date");
  const endDateInput = document.getElementById("end-date");

  const medicationView = new MedicationView(
    null,
    medicationScheduleBody,
    averageTime,
    totalIntakes
  );

  function preventFormSubmit(event) {
    if (event.key === "Enter" || event.keyCode === 13) {
      // Previene el comportamiento por defecto (envío del formulario/recarga)
      event.preventDefault();

      // Aquí puedes agregar el código que quieras ejecutar cuando se presione Enter
      console.log("Valor ingresado:", event.target.value);
    }
  }

  const urlParams = new URLSearchParams(window.location.search);
  const patientId = urlParams.get("patientId");
  const medicationId = urlParams.get("medicationId");

  if (!patientId || !medicationId) {
    console.error("Missing patientId or medicationId in URL.");
    document.title = "Error - Medicamento";
    return;
  }

  model
    .getMedication(patientId, medicationId)
    .then(async (medication) => {
      document.title = medication.name;
      medicationNameElement.textContent = medication.name;

      const today = new Date();
      const oneMonthAgo = new Date();
      oneMonthAgo.setMonth(today.getMonth() - 1);

      const [intakes, posologies] = await Promise.all([
        model.getMedicationIntakes(patientId, medicationId, oneMonthAgo, today),
        model.getPosologies(patientId, medicationId),
      ]);
      medicationView.renderIntakes(intakes, posologies);
      setupPeriodSelection(patientId, medicationId);
    })
    .catch((error) => {
      console.error("Error loading medication details:", error);
      document.title = "Error - Medicamento";
    });

  function setupPeriodSelection(patientId, medicationId) {
    periodOptions.addEventListener("change", async () => {
      const selectedOption = periodOptions.value;

      dailyInput.style.display = selectedOption === "daily" ? "block" : "none";
      rangeInput.style.display = selectedOption === "range" ? "block" : "none";

      if (selectedOption === "monthly") {
        await loadMedicationsForLastMonth(patientId, medicationId);
      }
    });

    daysInput.addEventListener("input", async (e) => {
      e.preventDefault();
      const days = parseInt(daysInput.value, 10);
      console.log(days);

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
        await loadMedicationsForDateRange(
          patientId,
          medicationId,
          startDate,
          endDate
        );
      }
    }
  }

  async function loadMedicationsForLastMonth(patientId, medicationId) {
    const today = new Date();
    const oneMonthAgo = new Date();
    oneMonthAgo.setMonth(today.getMonth() - 1);

    try {
      const intakes = await model.getMedicationIntakes(
        patientId,
        medicationId,
        oneMonthAgo,
        today
      );
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

    model
      .getMedicationIntakes(patientId, medicationId, nDaysAgo, today)
      .then(async (intakes) => {
        return model
          .getPosologies(patientId, medicationId)
          .then((posologies) => {
            medicationView.renderIntakes(intakes, posologies);
          });
      })
      .catch((error) => {
        console.error(
          `Error loading intakes for the last ${days} days:`,
          error
        );
      });
  }

  async function loadMedicationsForDateRange(
    patientId,
    medicationId,
    startDate,
    endDate
  ) {
    try {
      const intakes = await model.getMedicationIntakes(
        patientId,
        medicationId,
        new Date(startDate),
        new Date(endDate)
      );
      const posologies = await model.getPosologies(patientId, medicationId);
      medicationView.renderIntakes(intakes, posologies);
    } catch (error) {
      console.error("Error loading intakes for the date range:", error);
    }
  }
});
