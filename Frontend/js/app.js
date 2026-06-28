window.HotelApp = window.HotelApp || {};
(function (App) {
  const { API, pedir } = App.api;
  const { esc } = App.format;
  const config = App.config;

  const menu = document.getElementById("menu");
  const titulo = document.getElementById("titulo");
  const contenido = document.getElementById("contenido");
  const panel = document.getElementById("panel");
  const alerta = document.getElementById("alerta");
  const btnNuevo = document.getElementById("btnNuevo");

  let recursoActual = "hoteles";
  const cacheOpciones = {};

  function mostrarExito(mensaje) {
    alerta.innerHTML = `<div class="alerta alerta-exito">${esc(mensaje)}</div>`;
  }

  function mostrarError(mensaje) {
    alerta.innerHTML = `<div class="alerta alerta-error">${esc(mensaje)}</div>`;
  }

  function limpiarAlerta() {
    alerta.innerHTML = "";
  }

  Object.keys(config).forEach((recurso) => {
    const boton = document.createElement("button");
    boton.textContent = config[recurso].etiqueta;
    boton.dataset.recurso = recurso;
    boton.className = "boton-menu";
    boton.addEventListener("click", () => cargar(recurso));
    menu.appendChild(boton);
  });

  function marcarActivo(recurso) {
    document.querySelectorAll(".boton-menu").forEach((boton) => {
      boton.classList.toggle("activo", boton.dataset.recurso === recurso);
    });
  }

  btnNuevo.addEventListener("click", () =>
    abrirFormulario(recursoActual, null),
  );

  async function cargar(recurso) {
    recursoActual = recurso;
    const cfg = config[recurso];
    titulo.textContent = cfg.etiqueta;
    marcarActivo(recurso);
    ocultarPanel();
    limpiarAlerta();
    btnNuevo.classList.remove("oculto");
    contenido.innerHTML = `<p class="info">Cargando...</p>`;

    try {
      const { ok, status, data } = await pedir(`${API}/${cfg.plural}`);
      if (!ok) throw new Error("HTTP " + status);
      renderTabla(recurso, data, null);
    } catch (error) {
      contenido.innerHTML = `
        <div class="mensaje-error">
          No se pudo conectar con la API. 
        </div>`;
    }
  }

  async function cargarNavegacion(recurso, url, caption, volverA) {
    recursoActual = recurso;
    const cfg = config[recurso];
    titulo.textContent = cfg.etiqueta;
    marcarActivo(recurso);
    ocultarPanel();
    limpiarAlerta();
    btnNuevo.classList.add("oculto");
    contenido.innerHTML = `<p class="info">Cargando...</p>`;

    try {
      const { ok, status, data } = await pedir(url);
      if (!ok) throw new Error("HTTP " + status);
      renderTabla(recurso, data, { caption, volverA });
    } catch (error) {
      contenido.innerHTML = `
        <div class="mensaje-error">
          No se pudo conectar con la API. 
        </div>`;
    }
  }

  function renderTabla(recurso, datos, vista) {
    const cfg = config[recurso];
    const partes = [];

    if (vista) {
      partes.push(
        `<button class="volver" data-volver="${esc(vista.volverA)}">← Volver a ${esc(
          config[vista.volverA].etiqueta,
        )}</button>`,
      );
      partes.push(`<p class="caption-nav">${esc(vista.caption)}</p>`);
    }

    if (!datos || datos.length === 0) {
      partes.push(`<p class="info">No hay registros.</p>`);
      contenido.innerHTML = partes.join("");
      cablearAcciones(recurso);
      return;
    }

    const encabezado =
      cfg.columnas.map((c) => `<th>${esc(c.titulo)}</th>`).join("") +
      `<th>Acciones</th>`;

    const filas = datos
      .map((item) => {
        const celdas = cfg.columnas
          .map((c) => {
            const valor = item[c.campo];
            const contenidoCelda = c.render
              ? c.render(valor, item)
              : esc(valor);
            return `<td>${contenidoCelda}</td>`;
          })
          .join("");

        const acciones = construirAcciones(recurso, item);
        return `<tr>${celdas}<td>${acciones}</td></tr>`;
      })
      .join("");

    partes.push(
      `<p class="info" style="margin-bottom: 0.5rem;">${datos.length} registro(s)</p>`,
    );
    partes.push(`
      <table class="tabla">
        <thead><tr>${encabezado}</tr></thead>
        <tbody>${filas}</tbody>
      </table>`);

    contenido.innerHTML = partes.join("");
    cablearAcciones(recurso);
  }

  function construirAcciones(recurso, item) {
    const cfg = config[recurso];
    const botones = [
      `<button class="btn-accion btn-editar" data-accion="editar" data-id="${esc(item.id)}">Editar</button>`,
      `<button class="btn-accion btn-borrar" data-accion="borrar" data-id="${esc(item.id)}">Borrar</button>`,
    ];
    if (cfg.navegacion) {
      botones.push(
        `<button class="btn-accion btn-nav" data-accion="navegar" data-id="${esc(item.id)}">${esc(cfg.navegacion.etiqueta)}</button>`,
      );
    }
    return `<div class="acciones-celda">${botones.join("")}</div>`;
  }

  function cablearAcciones(recurso) {
    contenido.querySelectorAll("[data-accion]").forEach((boton) => {
      boton.addEventListener("click", () => {
        const accion = boton.dataset.accion;
        const id = boton.dataset.id;
        if (accion === "editar") abrirFormulario(recurso, id);
        else if (accion === "borrar") borrar(recurso, id);
        else if (accion === "navegar") navegar(recurso, id);
      });
    });

    const volver = contenido.querySelector("[data-volver]");
    if (volver) {
      volver.addEventListener("click", () => cargar(volver.dataset.volver));
    }
  }

  async function navegar(recurso, id) {
    const cfg = config[recurso];
    const nav = cfg.navegacion;
    try {
      const { ok, status, data } = await pedir(`${API}/${cfg.plural}/${id}`);
      if (!ok) throw new Error("HTTP " + status);
      await cargarNavegacion(
        nav.destino,
        nav.url(data),
        nav.caption(data),
        recurso,
      );
    } catch (error) {
      mostrarError("No se pudo conectar con la API. ");
    }
  }

  async function obtenerOpciones(plural) {
    if (cacheOpciones[plural]) return cacheOpciones[plural];
    const { ok, status, data } = await pedir(`${API}/${plural}`);
    if (!ok) throw new Error("HTTP " + status);
    cacheOpciones[plural] = Array.isArray(data) ? data : [];
    return cacheOpciones[plural];
  }

  async function abrirFormulario(recurso, id) {
    const cfg = config[recurso];
    limpiarAlerta();
    panel.innerHTML = `<p class="info">Cargando formulario...</p>`;
    panel.classList.remove("oculto");

    let valores = {};
    if (id) {
      try {
        const { ok, status, data } = await pedir(`${API}/${cfg.plural}/${id}`);
        if (!ok) throw new Error("HTTP " + status);
        valores = cfg.aFormulario ? cfg.aFormulario(data) : data;
      } catch (error) {
        ocultarPanel();
        mostrarError("No se pudo conectar con la API. ");
        return;
      }
    }

    try {
      await Promise.all(
        cfg.campos
          .filter((c) => c.fuente)
          .map((c) => obtenerOpciones(c.fuente)),
      );
    } catch (error) {
      ocultarPanel();
      mostrarError("No se pudo conectar con la API. ");
      return;
    }

    panel.innerHTML = construirFormulario(recurso, id, valores);
    panel
      .querySelector("form")
      .addEventListener("submit", (e) => enviarFormulario(e, recurso, id));
    panel
      .querySelector("[data-cancelar]")
      .addEventListener("click", ocultarPanel);
  }

  function construirFormulario(recurso, id, valores) {
    const cfg = config[recurso];
    const tituloForm = id
      ? `Editar ${cfg.etiqueta}`
      : `Nuevo registro: ${cfg.etiqueta}`;

    const campos = cfg.campos
      .map((campo) => construirCampo(campo, valores))
      .join("");

    return `
      <p class="form-titulo">${esc(tituloForm)}</p>
      <form novalidate>
        <div class="form-grid">${campos}</div>
        <div class="form-acciones">
          <button type="submit" class="btn btn-primario">Guardar</button>
          <button type="button" class="btn btn-secundario" data-cancelar>Cancelar</button>
        </div>
      </form>`;
  }

  function construirCampo(campo, valores) {
    const valor = valores[campo.campo];
    const claseAncho = campo.ancho ? "campo campo-ancho" : "campo";
    const req = campo.requerido ? "required" : "";
    let control = "";

    if (campo.tipo === "textarea") {
      control = `<textarea name="${campo.campo}" ${req}>${esc(valor !== undefined && valor !== null ? valor : "")}</textarea>`;
    } else if (campo.tipo === "select") {
      control = construirSelect(campo, valor);
    } else if (campo.tipo === "multicheck") {
      control = construirMulticheck(campo, valor);
    } else {
      const v = valor !== undefined && valor !== null ? esc(valor) : "";
      const extras = [];
      if (campo.step) extras.push(`step="${campo.step}"`);
      if (campo.min !== undefined) extras.push(`min="${campo.min}"`);
      if (campo.max !== undefined) extras.push(`max="${campo.max}"`);
      control = `<input type="${campo.tipo}" name="${campo.campo}" value="${v}" ${req} ${extras.join(" ")} />`;
    }

    return `
      <div class="${claseAncho}">
        <label for="${campo.campo}">${esc(campo.titulo)}${campo.requerido ? " *" : ""}</label>
        ${control}
        <span class="error-campo" data-error="${campo.campo}"></span>
      </div>`;
  }

  function construirSelect(campo, valor) {
    let opciones = `<option value="">-- Seleccionar --</option>`;

    if (campo.fuente) {
      const lista = cacheOpciones[campo.fuente] || [];
      opciones += lista
        .map((o) => {
          const ov = o[campo.opcionValor];
          const ot = campo.opcionTexto(o);
          const sel = String(ov) === String(valor) ? "selected" : "";
          return `<option value="${esc(ov)}" ${sel}>${esc(ot)}</option>`;
        })
        .join("");
    } else if (campo.opciones) {
      opciones += campo.opciones
        .map((o) => {
          const ov = typeof o === "object" ? o.valor : o;
          const ot = typeof o === "object" ? o.texto : o;
          const sel = String(ov) === String(valor) ? "selected" : "";
          return `<option value="${esc(ov)}" ${sel}>${esc(ot)}</option>`;
        })
        .join("");
    }

    const req = campo.requerido ? "required" : "";
    return `<select name="${campo.campo}" ${req}>${opciones}</select>`;
  }

  function construirMulticheck(campo, valor) {
    const seleccionados = Array.isArray(valor) ? valor.map(String) : [];
    const lista = cacheOpciones[campo.fuente] || [];
    if (lista.length === 0) {
      return `<div class="lista-check"><span class="info">No hay opciones disponibles.</span></div>`;
    }
    const items = lista
      .map((o) => {
        const ov = o[campo.opcionValor];
        const ot = campo.opcionTexto(o);
        const checked = seleccionados.includes(String(ov)) ? "checked" : "";
        return `
          <label>
            <input type="checkbox" name="${campo.campo}" value="${esc(ov)}" ${checked} />
            ${esc(ot)}
          </label>`;
      })
      .join("");
    return `<div class="lista-check">${items}</div>`;
  }

  function leerFormulario(recurso, form) {
    const cfg = config[recurso];
    const cuerpo = {};

    cfg.campos.forEach((campo) => {
      if (campo.tipo === "multicheck") {
        const marcados = form.querySelectorAll(
          `input[name="${campo.campo}"]:checked`,
        );
        cuerpo[campo.campo] = Array.from(marcados).map((c) => Number(c.value));
        return;
      }

      const control = form.elements[campo.campo];
      let valor = control ? control.value : "";

      if (campo.booleano) {
        cuerpo[campo.campo] = valor === "true";
        return;
      }

      if (campo.tipo === "number") {
        cuerpo[campo.campo] = valor === "" ? null : Number(valor);
        return;
      }

      cuerpo[campo.campo] = valor === "" ? null : valor;
    });

    return cuerpo;
  }

  function limpiarErroresCampo(form) {
    form.querySelectorAll(".error-campo").forEach((e) => (e.textContent = ""));
    form
      .querySelectorAll(".invalido")
      .forEach((e) => e.classList.remove("invalido"));
  }

  function mostrarErroresCampo(form, fieldErrors) {
    Object.keys(fieldErrors).forEach((campo) => {
      const span = form.querySelector(`[data-error="${campo}"]`);
      if (span) span.textContent = fieldErrors[campo];
      const control = form.elements[campo];
      if (control && control.classList) control.classList.add("invalido");
    });
  }

  async function enviarFormulario(evento, recurso, id) {
    evento.preventDefault();
    const cfg = config[recurso];
    const form = evento.target;
    const botonGuardar = form.querySelector('button[type="submit"]');

    limpiarErroresCampo(form);
    limpiarAlerta();

    const cuerpo = leerFormulario(recurso, form);
    const url = id ? `${API}/${cfg.plural}/${id}` : `${API}/${cfg.plural}`;
    const metodo = id ? "PUT" : "POST";

    botonGuardar.disabled = true;
    try {
      const { ok, status, data } = await pedir(url, {
        method: metodo,
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(cuerpo),
      });

      if (ok) {
        ocultarPanel();
        mostrarExito("Guardado correctamente");
        await cargar(recurso);
        return;
      }

      if (status === 400 && data && data.fieldErrors) {
        mostrarErroresCampo(form, data.fieldErrors);
        mostrarError("Revisá los campos marcados.");
        return;
      }

      const mensaje =
        data && data.message
          ? data.message
          : `Ocurrió un error (HTTP ${status}).`;
      mostrarError(mensaje);
    } catch (error) {
      mostrarError("No se pudo conectar con la API. ");
    } finally {
      botonGuardar.disabled = false;
    }
  }

  async function borrar(recurso, id) {
    const cfg = config[recurso];
    if (!confirm(`¿Seguro que querés borrar este registro (#${id})?`)) return;

    limpiarAlerta();
    try {
      const { ok, status, data } = await pedir(`${API}/${cfg.plural}/${id}`, {
        method: "DELETE",
      });

      if (ok) {
        mostrarExito("Eliminado correctamente");
        await cargar(recurso);
        return;
      }

      const mensaje =
        data && data.message
          ? data.message
          : `No se pudo eliminar (HTTP ${status}).`;
      mostrarError(mensaje);
    } catch (error) {
      mostrarError("No se pudo conectar con la API. ");
    }
  }

  function ocultarPanel() {
    panel.classList.add("oculto");
    panel.innerHTML = "";
  }

  cargar("hoteles");
})(window.HotelApp);
