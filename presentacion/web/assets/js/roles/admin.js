document.addEventListener("DOMContentLoaded", async () => {
  const kpiTarget = document.querySelector("[data-admin-kpis]");
  if (!kpiTarget) return;

  kpiTarget.innerHTML = window.SmartCampusUtils.loading("Cargando reportes...");
  try {
    const [turnos, tramites, usuarios] = await Promise.all([
      window.SmartCampusAPI.parseJson(await window.SmartCampusAPI.reporteTurnosAtendidos()),
      window.SmartCampusAPI.parseJson(await window.SmartCampusAPI.reporteTramitesPeriodo("", "")),
      window.SmartCampusAPI.parseJson(await window.SmartCampusAPI.reporteUsuariosPorRol())
    ]);

    const cards = [
      ["Turnos en cola", turnos.total || turnos.cantidad || 0],
      ["Tramites registrados", tramites.total || tramites.cantidad || 0],
      ["Empleados", usuarios.empleados || 0],
      ["Usuarios por rol", usuarios.totalUsuarios || usuarios.total || 0]
    ];

    kpiTarget.innerHTML = cards
      .map(([label, value]) => `<div class="col-md-3"><article class="metric-card"><p class="text-muted mb-1">${label}</p><div class="value">${value}</div></article></div>`)
      .join("");
  } catch (error) {
    window.SmartCampusUtils.showAlert(kpiTarget, error.message, "danger");
  }
});
