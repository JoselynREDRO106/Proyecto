document.addEventListener("DOMContentLoaded", async () => {
  const acciones = document.querySelector("[data-pila]");
  if (!acciones) return;

  const tramiteId = window.SmartCampusUtils.getQueryParam("tramiteId") || window.SmartCampusUtils.getQueryParam("id");
  if (!tramiteId) {
    acciones.innerHTML = `<p class="text-muted mb-0">Selecciona un tramite para ver su historial de cambios.</p>`;
    return;
  }

  acciones.innerHTML = window.SmartCampusUtils.loading("Cargando historial de tramite...");
  try {
    const historial = await window.SmartCampusAPI.parseJson(await window.SmartCampusAPI.obtenerHistorialTramites(tramiteId));
    window.SmartCampusPila.render(acciones, Array.isArray(historial) ? historial : [historial].filter(Boolean));
  } catch (error) {
    window.SmartCampusUtils.showAlert(acciones, error.message, "danger");
  }
});
