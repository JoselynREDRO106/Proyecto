window.SmartCampusPila = {
  render(target, acciones) {
    const node = typeof target === "string" ? document.querySelector(target) : target;
    if (!node) return;
    const data = Array.isArray(acciones) ? acciones : [];
    if (!data.length) {
      node.innerHTML = `<p class="text-muted mb-0">No hay historial disponible.</p>`;
      return;
    }
    node.innerHTML = data.map((item) => `
      <div class="timeline-item mb-2">
        <strong>${item.accion || item.estadoNuevo || item.estado_nuevo || "Cambio de tramite"}</strong>
        <p class="mb-0 text-muted">${window.SmartCampusUtils.formatDateTime(item.fecha || item.fechaCambio || item.fecha_cambio)}</p>
        ${item.comentario ? `<p class="mb-0">${item.comentario}</p>` : ""}
      </div>
    `).join("");
  }
};
