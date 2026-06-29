window.HotelApp = window.HotelApp || {};
(function (App) {
  const NA = "—";

  function esc(valor) {
    if (valor === null || valor === undefined) return NA;
    return String(valor)
      .replace(/&/g, "&amp;")
      .replace(/</g, "&lt;")
      .replace(/>/g, "&gt;")
      .replace(/"/g, "&quot;")
      .replace(/'/g, "&#39;");
  }

  function dinero(v) {
    if (v === null || v === undefined || v === "") return NA;
    return "$" + Number(v).toFixed(2);
  }

  function booleano(v) {
    return v ? "Sí" : "No";
  }

  function estrellas(n) {
    const num = Number(n);
    if (!num || num < 1) return NA;
    return `<span class="estrellas">${"★".repeat(Math.min(num, 5))}</span>`;
  }

  function chipsServicios(servicios) {
    if (!Array.isArray(servicios) || servicios.length === 0) return NA;
    const chips = servicios
      .map((s) => `<span class="chip">${esc(s.nombre)}</span>`)
      .join("");
    return `<div class="chips">${chips}</div>`;
  }

  function miniatura(url) {
    if (!url) return NA;
    return `<img src="${esc(url)}" alt="" class="miniatura" />`;
  }

  App.format = { NA, esc, dinero, booleano, estrellas, chipsServicios, miniatura };
})(window.HotelApp);
