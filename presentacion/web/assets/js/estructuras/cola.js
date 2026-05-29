window.SmartCampusCola = {
  render(target, turnos) {
    const node = typeof target === "string" ? document.querySelector(target) : target;
    if (!node) return;
    const data = Array.isArray(turnos) ? turnos : [];
    if (!data.length) {
      node.innerHTML = `<p class="text-muted mb-0">No hay turnos para mostrar.</p>`;
      return;
    }
    node.innerHTML = data.map((turno, index) => `
      <div class="queue-item mb-2">
        <div class="d-flex justify-content-between gap-3">
          <strong>${index === 0 ? "Siguiente: " : ""}${turno.numero || turno.id}</strong>
          <span>${window.SmartCampusUtils.stateBadge(turno.estado || "pendiente")}</span>
        </div>
        <p class="mb-0 text-muted">Posicion FIFO ${turno.posicion || index + 1}${turno.ventanillaId ? ` - Ventanilla ${turno.ventanillaId}` : ""}</p>
      </div>
    `).join("");
  }
};
