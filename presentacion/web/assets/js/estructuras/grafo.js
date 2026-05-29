window.SmartCampusGrafo = {
  async render(canvasSelector, camino = null) {
    const canvas = document.querySelector(canvasSelector);
    if (!canvas) return;

    const ctx = canvas.getContext("2d");
    const rect = canvas.getBoundingClientRect();
    const width = Math.max(rect.width, 320);
    const height = 360;
    canvas.width = width * window.devicePixelRatio;
    canvas.height = height * window.devicePixelRatio;
    ctx.setTransform(window.devicePixelRatio, 0, 0, window.devicePixelRatio, 0, 0);

    try {
      const puntosData = await window.SmartCampusAPI.parseJson(await window.SmartCampusAPI.listarNodosRutas());
      const nodos = this.layout(Array.isArray(puntosData) ? puntosData : puntosData.nodos || [], width, height);
      const resaltado = Array.isArray(camino) ? camino.map((p) => p.id ?? p) : [];

      ctx.clearRect(0, 0, width, height);
      this.drawFallbackEdges(ctx, nodos);

      if (resaltado.length > 1) {
        ctx.strokeStyle = "#e87a2a";
        ctx.lineWidth = 5;
        ctx.beginPath();
        resaltado.forEach((id, index) => {
          const punto = nodos.find((n) => n.id === id);
          if (!punto) return;
          index ? ctx.lineTo(punto.x, punto.y) : ctx.moveTo(punto.x, punto.y);
        });
        ctx.stroke();
      }

      nodos.forEach((punto) => {
        ctx.fillStyle = "#0b3b5f";
        ctx.beginPath();
        ctx.arc(punto.x, punto.y, 14, 0, Math.PI * 2);
        ctx.fill();
        ctx.fillStyle = "#17212b";
        ctx.font = "600 13px Inter";
        ctx.fillText(punto.nombre || punto.label || `Punto ${punto.id}`, punto.x + 18, punto.y + 4);
      });
    } catch (error) {
      ctx.clearRect(0, 0, width, height);
      ctx.fillStyle = "#842029";
      ctx.font = "600 14px Inter";
      ctx.fillText(error.message, 20, 40);
    }
  },
  drawFallbackEdges(ctx, nodos) {
    ctx.strokeStyle = "#d9e2ec";
    ctx.lineWidth = 2;
    for (let i = 1; i < nodos.length; i += 1) {
      ctx.beginPath();
      ctx.moveTo(nodos[i - 1].x, nodos[i - 1].y);
      ctx.lineTo(nodos[i].x, nodos[i].y);
      ctx.stroke();
    }
  },
  layout(nodos, width, height) {
    const radius = Math.min(width, height) * 0.36;
    const centerX = width / 2;
    const centerY = height / 2;
    return nodos.map((nodo, index) => {
      const angle = (Math.PI * 2 * index) / Math.max(nodos.length, 1) - Math.PI / 2;
      return {
        ...nodo,
        id: nodo.id,
        x: Number(nodo.x ?? nodo.posX ?? centerX + Math.cos(angle) * radius),
        y: Number(nodo.y ?? nodo.posY ?? centerY + Math.sin(angle) * radius)
      };
    });
  }
};
