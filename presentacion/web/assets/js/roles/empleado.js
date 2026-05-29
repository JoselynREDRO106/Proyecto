document.addEventListener("DOMContentLoaded", async () => {
  const cola = document.querySelector("[data-cola]");
  if (!cola) return;

  cola.innerHTML = window.SmartCampusUtils.loading("Cargando turnos del usuario...");
  try {
    const turnos = await window.SmartCampusAPI.parseJson(await window.SmartCampusAPI.obtenerTurnosUsuario());
    window.SmartCampusCola.render(cola, Array.isArray(turnos) ? turnos : [turnos].filter(Boolean));
  } catch (error) {
    window.SmartCampusUtils.showAlert(cola, error.message, "danger");
  }
});
