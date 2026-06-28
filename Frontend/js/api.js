window.HotelApp = window.HotelApp || {};
(function (App) {
  const API = "http://localhost:8080/api";

  async function pedir(url, opciones) {
    const respuesta = await fetch(url, opciones);
    let cuerpo = null;
    const texto = await respuesta.text();
    if (texto) {
      try {
        cuerpo = JSON.parse(texto);
      } catch (e) {
        cuerpo = texto;
      }
    }
    return { ok: respuesta.ok, status: respuesta.status, data: cuerpo };
  }

  App.api = { API, pedir };
})(window.HotelApp);
