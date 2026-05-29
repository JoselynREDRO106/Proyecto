window.SmartCampusNavbar = {
  linksByRole(roleId, root) {
    const common = [{ href: `${root}dashboard.html`, label: "Dashboard" }];
    if (roleId === 1) {
      return [
        { href: `${root}roles/administrador/dashboard.html`, label: "Panel" },
        { href: `${root}roles/administrador/gestion_usuarios.html`, label: "Usuarios" },
        { href: `${root}roles/administrador/gestion_tramites.html`, label: "Tramites" },
        { href: `${root}roles/administrador/reportes.html`, label: "Reportes" }
      ];
    }
    if (roleId === 2) {
      return [
        { href: `${root}roles/empleado/dashboard.html`, label: "Panel" },
        { href: `${root}roles/empleado/atender_turno.html`, label: "Atender" },
        { href: `${root}roles/empleado/mis_tramites_asignados.html`, label: "Tramites" },
        { href: `${root}roles/empleado/mapa_campus.html`, label: "Mapa" }
      ];
    }
    if (roleId === 3) {
      return [
        { href: `${root}roles/estudiante/dashboard.html`, label: "Panel" },
        { href: `${root}roles/estudiante/solicitar_tramite.html`, label: "Solicitar" },
        { href: `${root}roles/estudiante/mis_turnos.html`, label: "Turnos" },
        { href: `${root}roles/estudiante/mapa_campus.html`, label: "Mapa" }
      ];
    }
    return common;
  },
  render() {
    const target = document.querySelector("[data-navbar]");
    if (!target) return;
    const root = window.SmartCampusAuth.pathToRoot();
    const session = window.SmartCampusAuth.getSession();
    const links = session ? this.linksByRole(session.usuario.rol_id, root) : [
      { href: `${root}index.html`, label: "Inicio" },
      { href: `${root}login.html`, label: "Iniciar sesion" },
      { href: `${root}registro.html`, label: "Registro" }
    ];

    target.innerHTML = `
      <nav class="navbar navbar-expand-lg bg-white">
        <div class="container">
          <a class="navbar-brand d-flex align-items-center gap-2" href="${root}index.html">
            <span class="brand-mark">SC</span><span>SmartCampusUTA</span>
          </a>
          <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#mainNavbar" aria-controls="mainNavbar" aria-expanded="false" aria-label="Abrir menu">
            <span class="navbar-toggler-icon"></span>
          </button>
          <div class="collapse navbar-collapse" id="mainNavbar">
            <ul class="navbar-nav ms-auto align-items-lg-center gap-lg-2">
              ${links.map((link) => `<li class="nav-item"><a class="nav-link" href="${link.href}">${link.label}</a></li>`).join("")}
              ${session ? `<li class="nav-item ms-lg-2"><span class="nav-link text-muted">${session.usuario.nombre}</span></li><li class="nav-item"><button class="btn btn-outline-danger btn-sm" data-logout>Cerrar sesion</button></li>` : ""}
            </ul>
          </div>
        </div>
      </nav>`;
  }
};

document.addEventListener("DOMContentLoaded", () => window.SmartCampusNavbar.render());
document.addEventListener("click", (event) => {
  if (!event.target.matches("[data-logout]")) return;
  window.SmartCampusAuth.logout();
  window.location.href = window.SmartCampusAuth.pathToRoot() + "login.html";
});
