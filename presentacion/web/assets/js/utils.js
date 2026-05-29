window.SmartCampusUtils = {
  qs(selector, root = document) {
    return root.querySelector(selector);
  },
  qsa(selector, root = document) {
    return Array.from(root.querySelectorAll(selector));
  },
  formatDate(value) {
    if (!value) return "Sin fecha";
    return new Intl.DateTimeFormat("es-EC", { dateStyle: "medium" }).format(new Date(value));
  },
  formatDateTime(value) {
    if (!value) return "Sin fecha";
    return new Intl.DateTimeFormat("es-EC", { dateStyle: "medium", timeStyle: "short" }).format(new Date(value));
  },
  stateBadge(estado) {
    const label = String(estado || "pendiente");
    const safe = label.toLowerCase().replace(/\s+/g, "-").replace(/_/g, "-");
    return `<span class="badge-state state-${safe}">${label}</span>`;
  },
  showAlert(target, message, type = "info") {
    const node = typeof target === "string" ? document.querySelector(target) : target;
    if (!node) return;
    node.innerHTML = `<div class="alert alert-${type}" role="alert">${message}</div>`;
  },
  loading(label = "Cargando datos...") {
    return `<div class="spinner-block"><div class="text-center"><div class="spinner-border text-primary" role="status"></div><p class="mt-3 mb-0 text-muted">${label}</p></div></div>`;
  },
  getQueryParam(name) {
    return new URLSearchParams(window.location.search).get(name);
  },
  formData(form) {
    return Object.fromEntries(new FormData(form).entries());
  }
};
