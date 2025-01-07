export class PatientListView {
  constructor(patientCodeInput, suggestionsContainer) {
    this.patientCodeInput = patientCodeInput;
    this.suggestionsContainer = suggestionsContainer;
  }

  renderSuggestions(patients) {
    if (!patients || patients.length === 0) {
      this.suggestionsContainer.innerHTML =
        "<li class='error'>No se encontraron resultados.</li>";
      return;
    }

    this.suggestionsContainer.innerHTML = patients
      .map(
        (patient) =>
          `<li class="suggestion-item" data-patient-id="${patient.id}">
              ${patient.name} ${patient.surname} (${patient.code})
            </li>`
      )
      .join("");
  }

  clearSuggestions() {
    this.suggestionsContainer.innerHTML = "";
  }

  showError(message) {
    this.suggestionsContainer.innerHTML = `<li class='error'>${message}</li>`;
  }
}

export class MedicationView {
  constructor(
    container,
    scheduleBody,
    averageTimeElement,
    totalIntakesElement
  ) {
    this.container = container;
    this.scheduleBody = scheduleBody;
    this.averageTimeElement = averageTimeElement;
    this.totalIntakesElement = totalIntakesElement;
  }

  renderMedicationList(medications, patientId) {
    if (!medications || medications.length === 0) {
      this.container.innerHTML = "<li>No hay medicamentos disponibles.</li>";
      return;
    }

    this.container.innerHTML = medications
      .map(
        (med) => `
            <li>
              <article class="medication-item">
                <h3>
                  <a href="ibupro.html?patientId=${patientId}&medicationId=${
          med.id
        }">
                    ${med.name}
                  </a>
                </h3>
                <div>
                  <p>Dosis: ${med.dosage}</p>
                  <p>Fecha de inicio: ${new Date(
                    med.start_date
                  ).toLocaleDateString("es-ES", {
                    year: "numeric",
                    month: "long",
                    day: "numeric",
                  })}</p>
                </div>
              </article>
            </li>`
      )
      .join("");
  }

  renderIntakes(intakes, posologies) {
    if (!intakes || intakes.length === 0) {
      this.scheduleBody.innerHTML = `<tr><td colspan="2">No hay tomas disponibles en el rango seleccionado.</td></tr>`;
      this.averageTimeElement.textContent = "N/A";
      this.totalIntakesElement.textContent = "0";
      return;
    }

    const timeDiffs = [];
    this.scheduleBody.innerHTML = intakes
      .map((intake) => {
        const nearestPosology = this.findNearestPosology(intake, posologies);

        if (!nearestPosology) return '';

        const nearestTime = nearestPosology.posology;

        const nearestTimeFormatted = `${nearestTime.hour
          .toString()
          .padStart(2, "0")}:${(nearestTime.minute || 0)
          .toString()
          .padStart(2, "0")}`;

        const intakeDate = new Date(intake.date);
        const intakeHour = intakeDate.getHours();
        const intakeMinute = intakeDate.getMinutes();
        const nearestPosologyTimeInMinutes = nearestTime.hour * 60 + nearestTime.minute;
        const intakeTimeInMinutes = intakeHour * 60 + intakeMinute;
        timeDiffs.push(Math.abs(intakeTimeInMinutes - nearestPosologyTimeInMinutes));

        return `
            <tr>
              <td>${nearestTimeFormatted}</td>
              <td>${intakeDate.toLocaleString("es-ES", {
                day: "2-digit",
                month: "2-digit",
                year: "numeric",
                hour: "2-digit",
                minute: "2-digit",
              })}</td>
            </tr>`;
      })
      .join("");

    const totalIntakesCount = intakes.length;
    const averageTimeBetweenIntakes = this.calculateAverageTime(timeDiffs);

    this.totalIntakesElement.textContent = totalIntakesCount;
    this.averageTimeElement.textContent = averageTimeBetweenIntakes
      ? `${averageTimeBetweenIntakes} minutos`
      : "N/A";
  }

  findNearestPosology(intake, posologies) {
    const intakeDate = new Date(intake.date);
    const intakeHour = intakeDate.getHours();
    const intakeMinute = intakeDate.getMinutes();

    return posologies.reduce((nearest, posology) => {
      const posologyHour = posology.hour;
      const posologyMinute = posology.minute || 0;
      const currentDifference = Math.abs(intakeHour * 60 + intakeMinute - (posologyHour * 60 + posologyMinute));

      if (!nearest || currentDifference < nearest.difference) {
        return { posology, difference: currentDifference };
      }
      return nearest;
    }, null);
  }

  calculateAverageTime(timeDiffs) {
    if (timeDiffs.length < 1) return null;

    const totalDiff = timeDiffs.reduce((acc, diff) => acc + diff, 0);
    return Math.round(totalDiff / timeDiffs.length);
  }
}
