const roleRoutes = {
  1: "roles/administrador/dashboard.html",
  2: "roles/empleado/dashboard.html",
  3: "roles/estudiante/dashboard.html",
  administrador: "roles/administrador/dashboard.html",
  empleado: "roles/empleado/dashboard.html",
  estudiante: "roles/estudiante/dashboard.html"
};

const normalizeUser = (usuario = {}) => ({
  id: usuario.id || usuario.idUsuario || 0,
  nombre: usuario.nombre || "Usuario SmartCampus",
  email: usuario.email || usuario.correo || "",
  rol_id: Number(usuario.rol_id || usuario.rolId || usuario.idRol || 3),
  rol_nombre: String(usuario.rol_nombre || usuario.rolNombre || usuario.rol || "estudiante").toLowerCase()
});

window.SmartCampusAuth = {
  getSession() {
    const token = localStorage.getItem("token");
    const rawUser = localStorage.getItem("usuario");
    if (!token && !rawUser) return null;
    try {
      const usuario = normalizeUser(JSON.parse(rawUser || "{}"));
      return { token, usuario };
    } catch {
      return null;
    }
  },
  setSession(data) {
    const usuario = normalizeUser(data.usuario || data.user || data);
    localStorage.setItem("token", data.token || data.jwt || data.sessionToken || "backend-session");
    localStorage.setItem("usuario", JSON.stringify(usuario));
    return { token: localStorage.getItem("token"), usuario };
  },
  clearSession() {
    localStorage.removeItem("token");
    localStorage.removeItem("usuario");
  },
  async logout() {
    try {
      if (window.SmartCampusAPI?.logout) await window.SmartCampusAPI.logout();
    } finally {
      this.clearSession();
    }
  },
  requireSession() {
    const session = this.getSession();
    if (!session) window.location.href = this.pathToRoot() + "login.html";
    return session;
  },
  redirectByRole() {
    const session = this.requireSession();
    const route = roleRoutes[session.usuario.rol_id] || roleRoutes[session.usuario.rol_nombre] || "dashboard.html";
    window.location.replace(this.pathToRoot() + route);
  },
  pathToRoot() {
    const path = window.location.pathname;
    if (path.includes("/roles/administrador/") || path.includes("/roles/empleado/") || path.includes("/roles/estudiante/")) return "../../";
    if (path.includes("/tramites/") || path.includes("/turnos/") || path.includes("/documentos/") || path.includes("/rutas/")) return "../";
    return "";
  }
};

document.addEventListener("submit", async (event) => {
  const loginForm = event.target.closest("[data-login-form]");
  const registerForm = event.target.closest("[data-register-form]");
  if (!loginForm && !registerForm) return;

  event.preventDefault();
  const form = loginForm || registerForm;
  const data = Object.fromEntries(new FormData(form).entries());
  const alertTarget = loginForm ? "#login-alert" : "#register-alert";

  try {
    if (loginForm) {
      const result = await window.SmartCampusAPI.parseJson(await window.SmartCampusAPI.login({ email: data.email, password: data.password }));
      window.SmartCampusAuth.setSession(result);
      window.SmartCampusAuth.redirectByRole();
      return;
    }

    if (data.password !== data.confirmar) throw new Error("Las contrasenas no coinciden.");
    await window.SmartCampusAPI.parseJson(await window.SmartCampusAPI.registrar({
      nombre: data.nombre,
      email: data.email,
      password: data.password,
      rolId: 3,
      telefono: data.telefono,
      carrera: data.carrera
    }));
    window.SmartCampusUtils.showAlert(alertTarget, "Registro exitoso. Ya puedes iniciar sesion.", "success");
    setTimeout(() => { window.location.href = "login.html"; }, 900);
  } catch (error) {
    window.SmartCampusUtils.showAlert(alertTarget, error.message || "No se pudo completar la accion.", "danger");
  }
});

document.addEventListener("DOMContentLoaded", () => {
  const publicTurnQueue = window.location.pathname.endsWith("/turnos/cola_atencion.html");
  const protectedArea = !publicTurnQueue && ["/roles/", "/tramites/", "/turnos/", "/documentos/", "/rutas/"]
    .some((segment) => window.location.pathname.includes(segment));
  if (protectedArea) window.SmartCampusAuth.requireSession();
  const session = window.SmartCampusAuth.getSession();
  const path = window.location.pathname;
  if (session && path.includes("/roles/administrador/") && session.usuario.rol_id !== 1) {
    window.location.href = window.SmartCampusAuth.pathToRoot() + "dashboard.html";
  }
  if (session && path.includes("/roles/empleado/") && session.usuario.rol_id !== 2) {
    window.location.href = window.SmartCampusAuth.pathToRoot() + "dashboard.html";
  }
  if (session && path.includes("/roles/estudiante/") && session.usuario.rol_id !== 3) {
    window.location.href = window.SmartCampusAuth.pathToRoot() + "dashboard.html";
  }
});
