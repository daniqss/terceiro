const API_BASE_URL = "http://localhost:8000";

export class Model {
  async getPatients() {
    const response = await fetch(`${API_BASE_URL}/patients`, { method: "GET" });
    if (!response.ok) throw new Error(`Error fetching patients: ${response.status}`);
    return await response.json();
  }

  async getPatientByCode(code) {
    const patients = await this.getPatients();
    return patients.find((patient) => patient.code === code);
  }

  async getPatient(patientId) {
    const response = await fetch(`${API_BASE_URL}/patients/${patientId}`, { method: "GET" });
    if (!response.ok) throw new Error(`Error fetching patient: ${response.status}`);
    return await response.json();
  }

  async getMedications(patientId) {
    const response = await fetch(`${API_BASE_URL}/patients/${patientId}/medications`, { method: "GET" });
    if (!response.ok) throw new Error(`Error fetching medications: ${response.status}`);
    return await response.json();
  }

  async getMedication(patientId, medicationId) {
    const response = await fetch(`${API_BASE_URL}/patients/${patientId}/medications/${medicationId}`, { method: "GET" });
    if (!response.ok) throw new Error(`Error fetching medication: ${response.status}`);
    return await response.json();
  }

  async getPosologies(patientId, medicationId) {
    const response = await fetch(
      `${API_BASE_URL}/patients/${patientId}/medications/${medicationId}/posologies`,
      { method: "GET" }
    );
    if (!response.ok) throw new Error(`Error fetching posologies: ${response.status}`);
    return await response.json();
  }

  async getMedicationIntakes(patientId, medicationId, startDate, endDate) {
    let url = `${API_BASE_URL}/patients/${patientId}/medications/${medicationId}/intakes`;

    // Si se proporciona un rango de fechas, se añade como query string
    if (startDate && endDate) {
      const start = startDate.toISOString();
      const end = endDate.toISOString();
      url += `?start=${encodeURIComponent(start)}&end=${encodeURIComponent(end)}`;
    }

    const response = await fetch(url, { method: "GET" });
    if (!response.ok) throw new Error(`Error fetching intakes: ${response.status}`);
    return await response.json();
  }
}
