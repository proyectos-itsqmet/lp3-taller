const API = "http://localhost:8080/api";

const recursos = {
  hoteles: "Hoteles",
  habitaciones: "Habitaciones",
  reservas: "Reservas",
  pagos: "Pagos",
  usuarios: "Usuarios",
};

const menu = document.getElementById("menu");
const titulo = document.getElementById("titulo");
const contenido = document.getElementById("contenido");

Object.keys(recursos).forEach((recurso) => {
  const boton = document.createElement("button");
  boton.textContent = recursos[recurso];
  boton.dataset.recurso = recurso;
  boton.className = "boton-menu";
  boton.addEventListener("click", () => cargar(recurso));
  menu.appendChild(boton);
});

async function cargar(recurso) {
  titulo.textContent = recursos[recurso];
  contenido.innerHTML = `<p class="info">Cargando...</p>`;
  marcarActivo(recurso);

  try {
    const respuesta = await fetch(`${API}/${recurso}`);
    if (!respuesta.ok) throw new Error("HTTP " + respuesta.status);
    let datos = await respuesta.json();

    if (recurso === "habitaciones") {
      datos = await reemplazarHotel(datos);
    }

    mostrarTabla(datos);
  } catch (error) {
    contenido.innerHTML = `
      <div class="mensaje-error">
        No se pudo conectar con la API. ¿Está corriendo el backend?
      </div>`;
  }
}

async function reemplazarHotel(habitaciones) {
  const hoteles = await (await fetch(`${API}/hoteles`)).json();

  const nombrePorId = {};
  hoteles.forEach((hotel) => {
    nombrePorId[hotel.id] = hotel.nombre;
  });

  return habitaciones.map((hab) => {
    const fila = {};
    Object.keys(hab).forEach((campo) => {
      if (campo === "hotelId") {
        fila.hotel = nombrePorId[hab.hotelId] ?? hab.hotelId;
      } else {
        fila[campo] = hab[campo];
      }
    });
    return fila;
  });
}

function mostrarTabla(datos) {
  if (!datos || datos.length === 0) {
    contenido.innerHTML = `<p class="info">No hay registros.</p>`;
    return;
  }

  const columnas = Object.keys(datos[0]);

  const encabezado = columnas.map((col) => `<th>${col}</th>`).join("");

  const filas = datos
    .map((item) => {
      const celdas = columnas
        .map((col) => {
          const valor = item[col];
          if (col === "imagen" && valor) {
            return `<td><img src="${valor}" alt="" class="miniatura" /></td>`;
          }
          return `<td>${valor}</td>`;
        })
        .join("");
      return `<tr>${celdas}</tr>`;
    })
    .join("");

  contenido.innerHTML = `
    <p class="info" style="margin-bottom: 0.5rem;">${datos.length} registro(s)</p>
    <table class="tabla">
      <thead><tr>${encabezado}</tr></thead>
      <tbody>${filas}</tbody>
    </table>`;
}

function marcarActivo(recurso) {
  document.querySelectorAll(".boton-menu").forEach((boton) => {
    boton.classList.toggle("activo", boton.dataset.recurso === recurso);
  });
}

cargar("hoteles");
