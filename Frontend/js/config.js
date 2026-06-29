window.HotelApp = window.HotelApp || {};
(function (App) {
  const { API } = App.api;
  const { dinero, booleano, estrellas, chipsServicios, miniatura } = App.format;

  const config = {
    hoteles: {
      etiqueta: "Hoteles",
      plural: "hoteles",
      columnas: [
        { campo: "id", titulo: "ID" },
        { campo: "nombre", titulo: "Nombre" },
        { campo: "ciudad", titulo: "Ciudad" },
        { campo: "categoria", titulo: "Categoría", render: (v) => estrellas(v) },
        { campo: "telefono", titulo: "Teléfono" },
        { campo: "imagen", titulo: "Imagen", render: (v) => miniatura(v) },
        {
          campo: "servicios",
          titulo: "Servicios",
          render: (v) => chipsServicios(v),
        },
      ],
      campos: [
        { campo: "nombre", titulo: "Nombre", tipo: "text", requerido: true },
        { campo: "ciudad", titulo: "Ciudad", tipo: "text", requerido: true },
        {
          campo: "categoria",
          titulo: "Categoría (1-5)",
          tipo: "number",
          requerido: true,
          min: 1,
          max: 5,
        },
        { campo: "telefono", titulo: "Teléfono", tipo: "text" },
        { campo: "imagen", titulo: "Imagen (URL)", tipo: "url", ancho: true },
        {
          campo: "servicioIds",
          titulo: "Servicios",
          tipo: "multicheck",
          ancho: true,
          fuente: "servicios",
          opcionValor: "id",
          opcionTexto: (o) => o.nombre,
        },
      ],
      aFormulario: (row) => ({
        ...row,
        servicioIds: Array.isArray(row.servicios)
          ? row.servicios.map((s) => s.id)
          : [],
      }),
      navegacion: {
        etiqueta: "Ver habitaciones",
        url: (row) => `${API}/habitaciones/hotel/${row.id}`,
        destino: "habitaciones",
        caption: (row) => `Habitaciones del hotel: ${row.nombre}`,
      },
    },

    habitaciones: {
      etiqueta: "Habitaciones",
      plural: "habitaciones",
      columnas: [
        { campo: "id", titulo: "ID" },
        { campo: "hotelNombre", titulo: "Hotel" },
        { campo: "numero", titulo: "Número" },
        { campo: "tipo", titulo: "Tipo" },
        {
          campo: "precioPorNoche",
          titulo: "Precio/Noche",
          render: (v) => dinero(v),
        },
        { campo: "capacidad", titulo: "Capacidad" },
        { campo: "disponible", titulo: "Disponible", render: (v) => booleano(v) },
        { campo: "imagen", titulo: "Imagen", render: (v) => miniatura(v) },
      ],
      campos: [
        {
          campo: "hotelId",
          titulo: "Hotel",
          tipo: "select",
          requerido: true,
          fuente: "hoteles",
          opcionValor: "id",
          opcionTexto: (o) => o.nombre,
        },
        { campo: "numero", titulo: "Número", tipo: "text", requerido: true },
        {
          campo: "tipo",
          titulo: "Tipo",
          tipo: "select",
          requerido: true,
          opciones: ["Simple", "Doble", "Suite"],
        },
        {
          campo: "precioPorNoche",
          titulo: "Precio por noche",
          tipo: "number",
          requerido: true,
          step: "0.01",
        },
        {
          campo: "capacidad",
          titulo: "Capacidad",
          tipo: "number",
          requerido: true,
        },
        {
          campo: "disponible",
          titulo: "Disponible",
          tipo: "select",
          booleano: true,
          opciones: [
            { valor: "true", texto: "Sí" },
            { valor: "false", texto: "No" },
          ],
        },
        { campo: "imagen", titulo: "Imagen (URL)", tipo: "url", ancho: true },
      ],
    },

    usuarios: {
      etiqueta: "Usuarios",
      plural: "usuarios",
      columnas: [
        { campo: "id", titulo: "ID" },
        { campo: "nombre", titulo: "Nombre" },
        { campo: "apellido", titulo: "Apellido" },
        { campo: "email", titulo: "Email" },
        { campo: "telefono", titulo: "Teléfono" },
        { campo: "documento", titulo: "Documento" },
      ],
      campos: [
        { campo: "nombre", titulo: "Nombre", tipo: "text", requerido: true },
        { campo: "apellido", titulo: "Apellido", tipo: "text", requerido: true },
        { campo: "email", titulo: "Email", tipo: "email", requerido: true },
        { campo: "telefono", titulo: "Teléfono", tipo: "text", requerido: true },
        {
          campo: "documento",
          titulo: "Documento",
          tipo: "text",
          requerido: true,
        },
      ],
      navegacion: {
        etiqueta: "Ver reservas",
        url: (row) => `${API}/reservas/usuario/${row.id}`,
        destino: "reservas",
        caption: (row) => `Reservas del usuario: ${row.nombre} ${row.apellido}`,
      },
    },

    reservas: {
      etiqueta: "Reservas",
      plural: "reservas",
      columnas: [
        { campo: "id", titulo: "ID" },
        { campo: "usuarioNombre", titulo: "Usuario" },
        { campo: "habitacionNumero", titulo: "Habitación" },
        { campo: "fechaEntrada", titulo: "Entrada" },
        { campo: "fechaSalida", titulo: "Salida" },
        { campo: "estado", titulo: "Estado" },
        { campo: "total", titulo: "Total", render: (v) => dinero(v) },
      ],
      campos: [
        {
          campo: "usuarioId",
          titulo: "Usuario",
          tipo: "select",
          requerido: true,
          fuente: "usuarios",
          opcionValor: "id",
          opcionTexto: (o) => `${o.nombre} ${o.apellido}`,
        },
        {
          campo: "habitacionId",
          titulo: "Habitación",
          tipo: "select",
          requerido: true,
          fuente: "habitaciones",
          opcionValor: "id",
          opcionTexto: (o) => `${o.numero} — ${o.hotelNombre ?? ""}`.trim(),
        },
        {
          campo: "fechaEntrada",
          titulo: "Fecha de entrada",
          tipo: "date",
          requerido: true,
        },
        {
          campo: "fechaSalida",
          titulo: "Fecha de salida",
          tipo: "date",
          requerido: true,
        },
        {
          campo: "estado",
          titulo: "Estado",
          tipo: "select",
          requerido: true,
          opciones: ["PENDIENTE", "CONFIRMADA", "CANCELADA"],
        },
        { campo: "total", titulo: "Total", tipo: "number", step: "0.01" },
      ],
      navegacion: {
        etiqueta: "Ver pagos",
        url: (row) => `${API}/pagos/reserva/${row.id}`,
        destino: "pagos",
        caption: (row) => `Pagos de la reserva #${row.id}`,
      },
    },

    pagos: {
      etiqueta: "Pagos",
      plural: "pagos",
      columnas: [
        { campo: "id", titulo: "ID" },
        { campo: "reservaId", titulo: "Reserva" },
        { campo: "monto", titulo: "Monto", render: (v) => dinero(v) },
        { campo: "fecha", titulo: "Fecha" },
        { campo: "metodo", titulo: "Método" },
      ],
      campos: [
        {
          campo: "reservaId",
          titulo: "Reserva",
          tipo: "select",
          requerido: true,
          fuente: "reservas",
          opcionValor: "id",
          opcionTexto: (o) => `#${o.id} — ${o.usuarioNombre ?? ""}`.trim(),
        },
        {
          campo: "monto",
          titulo: "Monto",
          tipo: "number",
          requerido: true,
          step: "0.01",
        },
        { campo: "fecha", titulo: "Fecha", tipo: "date", requerido: true },
        {
          campo: "metodo",
          titulo: "Método",
          tipo: "select",
          requerido: true,
          opciones: ["TARJETA", "EFECTIVO", "TRANSFERENCIA"],
        },
      ],
    },

    servicios: {
      etiqueta: "Servicios",
      plural: "servicios",
      columnas: [
        { campo: "id", titulo: "ID" },
        { campo: "nombre", titulo: "Nombre" },
        { campo: "descripcion", titulo: "Descripción" },
      ],
      campos: [
        { campo: "nombre", titulo: "Nombre", tipo: "text", requerido: true },
        {
          campo: "descripcion",
          titulo: "Descripción",
          tipo: "textarea",
          ancho: true,
        },
      ],
    },
  };

  App.config = config;
})(window.HotelApp);
