import { Model } from "./model.js";
import { MedicationView } from "./view.js";

document.addEventListener("DOMContentLoaded", async () => {
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

  // Ocultar inputs inicialmente
  dailyInput.style.display = "none";
  rangeInput.style.display = "none";

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

    periodOptions.value = "monthly";  // Selección predeterminada: "monthly"

    // Cargar medicamentos para el último mes al cargar la página
    await loadMedicationsForLastMonth(patientId, medicationId);

    setupPeriodSelection(patientId, medicationId);
  } catch (error) {
    console.error("Error loading medication details:", error);
    document.title = "Error - Medicamento";
  }

  function setupPeriodSelection(patientId, medicationId) {
    periodOptions.addEventListener("change", async () => {
      const selectedOption = periodOptions.value;

      // Mostrar/Ocultar inputs según la opción seleccionada
      dailyInput.style.display = selectedOption === "daily" ? "block" : "none";
      rangeInput.style.display = selectedOption === "range" ? "block" : "none";

      if (selectedOption === "monthly") {
        await loadMedicationsForLastMonth(patientId, medicationId);
      } else if (selectedOption === "range") {
        const startDate = startDateInput.value;
        const endDate = endDateInput.value;

        if (startDate && endDate) {
          await loadMedicationsForDateRange(patientId, medicationId, startDate, endDate);
        }
      }
    });

    daysInput.addEventListener("input", async (e) => {
      e.preventDefault();
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

      const filteredIntakes = intakes.filter((intake) => {
        const intakeDate = new Date(intake.date);
        return intakeDate >= oneMonthAgo && intakeDate <= today;
      });

      const filteredPosologies = posologies.filter((posology) => {
        const posologyDate = new Date();
        posologyDate.setHours(posology.hour, posology.minute, 0, 0);
        return posologyDate >= oneMonthAgo && posologyDate <= today;
      });

      medicationView.renderIntakes(filteredIntakes, filteredPosologies);
    } catch (error) {
      console.error("Error loading last month's intakes:", error);
    }
  }

  async function loadMedicationsForLastNDays(patientId, medicationId, days) {
    const today = new Date();
    const nDaysAgo = new Date();
    nDaysAgo.setDate(today.getDate() - days);

    try {
      const intakes = await model.getMedicationIntakes(
        patientId,
        medicationId,
        nDaysAgo,
        today
      );
      const posologies = await model.getPosologies(patientId, medicationId);

      const filteredIntakes = intakes.filter((intake) => {
        const intakeDate = new Date(intake.date);
        return intakeDate >= nDaysAgo && intakeDate <= today;
      });

      const filteredPosologies = posologies.filter((posology) => {
        const posologyDate = new Date();
        posologyDate.setHours(posology.hour, posology.minute, 0, 0);
        return posologyDate >= nDaysAgo && posologyDate <= today;
      });

      medicationView.renderIntakes(filteredIntakes, filteredPosologies);
    } catch (error) {
      console.error(`Error loading intakes for the last ${days} days:`, error);
    }
  }

  async function loadMedicationsForDateRange(
    patientId,
    medicationId,
    startDate,
    endDate
  ) {
    const start = new Date(startDate);
    const end = new Date(endDate);
  
    if (start > end) {
      console.error("La fecha de inicio no puede ser posterior a la fecha de fin.");
      return;
    }
  
    try {
      const intakes = await model.getMedicationIntakes(
        patientId,
        medicationId,
        start,
        end
      );
  
      const filteredIntakes = intakes.filter((intake) => {
        const intakeDate = new Date(intake.date);
        return intakeDate >= start && intakeDate <= end;
      });
  
      const posologies = await model.getPosologies(patientId, medicationId);
  
      const filteredPosologies = posologies.filter((posology) => {
        const posologyDate = new Date(start);
        posologyDate.setHours(posology.hour);
        posologyDate.setMinutes(posology.minute);
  
        return posologyDate >= start && posologyDate <= end;
      });
  
      if (filteredIntakes.length > 0 || filteredPosologies.length > 0) {
        medicationView.renderIntakes(filteredIntakes, filteredPosologies);
      } else {
        medicationScheduleBody.innerHTML = "<p>No medication data found for this period.</p>";
      }
  
    } catch (error) {
      console.error("Error loading intakes for the date range:", error);
    }
  }
  
});
