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
    let timeDifferenceInMinutes = (h1, m1, h2, m2) => {
      return Math.abs(h1 * 60 + m1 - (h2 * 60 + m2));
    };
    console.log(posologies.map((p) => p.hour));
    this.scheduleBody.innerHTML = intakes
      .map((intake) => {
        // Extract the hour and minute from the intake date
        const intakeDate = new Date(intake.date);
        const intakeHour = intakeDate.getHours();
        const intakeMinute = intakeDate.getMinutes();

        // Find the posology with the nearest time to the intake time
        const nearestPosology = posologies.reduce((nearest, posology) => {
          const posologyHour = posology.hour; // Assuming posology.hour is the hour
          const posologyMinute = posology.minute || 0; // Assuming posology has an optional minute property
          const currentDifference = timeDifferenceInMinutes(
            intakeHour,
            intakeMinute,
            posologyHour,
            posologyMinute
          );

          // Compare with the nearest found so far
          if (!nearest || currentDifference < nearest.difference) {
            return {
              posology,
              difference: currentDifference,
            };
          }
          return nearest;
        }, null);

        const nearestTime = nearestPosology.posology;

        // Format the nearest posology time as HH:mm
        const nearestTimeFormatted = `${nearestTime.hour
          .toString()
          .padStart(2, "0")}:${(nearestTime.minute || 0)
          .toString()
          .padStart(2, "0")}`;

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
    const averageTimeBetweenIntakes = this.calculateAverageTime(intakes);

    this.totalIntakesElement.textContent = totalIntakesCount;
    this.averageTimeElement.textContent = averageTimeBetweenIntakes
      ? `${averageTimeBetweenIntakes} minutos`
      : "N/A";
  }

  calculateAverageTime(intakes) {
    if (intakes.length < 2) return null;

    const timeDiffs = [];
    for (let i = 1; i < intakes.length; i++) {
      const diff = new Date(intakes[i].date) - new Date(intakes[i - 1].date);
      timeDiffs.push(diff / (1000 * 60)); // Convertir a minutos
    }

    const totalDiff = timeDiffs.reduce((acc, diff) => acc + diff, 0);
    return Math.round(totalDiff / timeDiffs.length);
  }
}
