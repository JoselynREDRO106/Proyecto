document.addEventListener("DOMContentLoaded", () => {
  const footer = document.querySelector("[data-footer]");
  if (!footer) return;
  footer.innerHTML = `<footer class="footer"><div class="container d-flex flex-column flex-md-row justify-content-between gap-2"><span>SmartCampusUTA Web - FISEI</span><span>2026 Universidad Tecnica de Ambato</span></div></footer>`;
});
