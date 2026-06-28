package com.lp3_taller.config;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.lp3_taller.model.Habitacion;
import com.lp3_taller.model.Hotel;
import com.lp3_taller.model.Pago;
import com.lp3_taller.model.Reserva;
import com.lp3_taller.model.Servicio;
import com.lp3_taller.model.Usuario;
import com.lp3_taller.repository.HabitacionRepository;
import com.lp3_taller.repository.HotelRepository;
import com.lp3_taller.repository.PagoRepository;
import com.lp3_taller.repository.ReservaRepository;
import com.lp3_taller.repository.ServicioRepository;
import com.lp3_taller.repository.UsuarioRepository;

@Component
public class DataSeeder implements CommandLineRunner {

    private final HotelRepository hotelRepository;
    private final ServicioRepository servicioRepository;
    private final HabitacionRepository habitacionRepository;
    private final UsuarioRepository usuarioRepository;
    private final ReservaRepository reservaRepository;
    private final PagoRepository pagoRepository;

    public DataSeeder(HotelRepository hotelRepository,
                      ServicioRepository servicioRepository,
                      HabitacionRepository habitacionRepository,
                      UsuarioRepository usuarioRepository,
                      ReservaRepository reservaRepository,
                      PagoRepository pagoRepository) {
        this.hotelRepository = hotelRepository;
        this.servicioRepository = servicioRepository;
        this.habitacionRepository = habitacionRepository;
        this.usuarioRepository = usuarioRepository;
        this.reservaRepository = reservaRepository;
        this.pagoRepository = pagoRepository;
    }

    @Override
    public void run(String... args) {
        if (hotelRepository.count() > 0) {
            return;
        }

        //! Servicios
        Servicio wifi = nuevoServicio("WiFi", "Conexion a internet de alta velocidad en todo el hotel");
        Servicio piscina = nuevoServicio("Piscina", "Piscina climatizada disponible todo el ano");
        Servicio estacionamiento = nuevoServicio("Estacionamiento", "Parqueadero privado con vigilancia 24 horas");
        Servicio desayuno = nuevoServicio("Desayuno", "Desayuno buffet incluido");
        servicioRepository.saveAll(List.of(wifi, piscina, estacionamiento, desayuno));

        //! Hoteles
        Hotel granVista = nuevoHotel("Hotel Gran Vista", "Quito", 5, "022500100",
                "https://images.pexels.com/photos/29570237/pexels-photo-29570237.jpeg");
        granVista.setServicios(Set.of(wifi, piscina, desayuno));

        Hotel costaAzul = nuevoHotel("Hotel Costa Azul", "Guayaquil", 4, "042600200",
                "https://images.pexels.com/photos/27153434/pexels-photo-27153434.jpeg");
        costaAzul.setServicios(Set.of(wifi, estacionamiento, piscina));

        Hotel sierraReal = nuevoHotel("Hotel Sierra Real", "Cuenca", 3, "072700300",
                "https://images.pexels.com/photos/28243824/pexels-photo-28243824.jpeg");
        sierraReal.setServicios(Set.of(wifi, desayuno));

        hotelRepository.saveAll(List.of(granVista, costaAzul, sierraReal));

        //! Habitaciones
        Habitacion habGranVista101 = nuevaHabitacion(granVista, "101", "Suite",
                new BigDecimal("180.00"), 2, true, "https://images.pexels.com/photos/28174031/pexels-photo-28174031.jpeg");
        Habitacion habGranVista102 = nuevaHabitacion(granVista, "102", "Doble",
                new BigDecimal("120.50"), 3, true, "https://images.pexels.com/photos/34016639/pexels-photo-34016639.jpeg");
        Habitacion habCostaAzul201 = nuevaHabitacion(costaAzul, "201", "Familiar",
                new BigDecimal("150.00"), 4, true, "https://images.pexels.com/photos/28909289/pexels-photo-28909289.jpeg");
        Habitacion habSierraReal301 = nuevaHabitacion(sierraReal, "301", "Individual",
                new BigDecimal("75.00"), 1, false, "https://images.pexels.com/photos/37658584/pexels-photo-37658584.jpeg");
        habitacionRepository.saveAll(
                List.of(habGranVista101, habGranVista102, habCostaAzul201, habSierraReal301));

        //! Usuarios
        Usuario ana = nuevoUsuario("Ana", "Martinez", "ana.martinez@example.com", "0991112233", "1712345678");
        Usuario carlos = nuevoUsuario("Carlos", "Lopez", "carlos.lopez@example.com", "0992223344", "0923456789");
        Usuario lucia = nuevoUsuario("Lucia", "Fernandez", "lucia.fernandez@example.com", null, "1103456789");
        usuarioRepository.saveAll(List.of(ana, carlos, lucia));

        //! Reservas
        Reserva reservaAna = nuevaReserva(ana, habGranVista101,
                LocalDate.of(2026, 7, 10), LocalDate.of(2026, 7, 13),
                "CONFIRMADA", new BigDecimal("540.00"));
        Reserva reservaCarlos = nuevaReserva(carlos, habCostaAzul201,
                LocalDate.of(2026, 8, 1), LocalDate.of(2026, 8, 5),
                "CONFIRMADA", new BigDecimal("600.00"));
        Reserva reservaLucia = nuevaReserva(lucia, habGranVista102,
                LocalDate.of(2026, 9, 15), LocalDate.of(2026, 9, 17),
                "PENDIENTE", new BigDecimal("241.00"));
        reservaRepository.saveAll(List.of(reservaAna, reservaCarlos, reservaLucia));

        //! Pagos
        Pago pagoAna = nuevoPago(reservaAna, new BigDecimal("540.00"),
                LocalDate.of(2026, 7, 10), "TARJETA");
        Pago pagoCarlosAnticipo = nuevoPago(reservaCarlos, new BigDecimal("300.00"),
                LocalDate.of(2026, 7, 20), "TRANSFERENCIA");
        Pago pagoCarlosSaldo = nuevoPago(reservaCarlos, new BigDecimal("300.00"),
                LocalDate.of(2026, 8, 1), "EFECTIVO");
        pagoRepository.saveAll(List.of(pagoAna, pagoCarlosAnticipo, pagoCarlosSaldo));
    }

    private Servicio nuevoServicio(String nombre, String descripcion) {
        Servicio servicio = new Servicio();
        servicio.setNombre(nombre);
        servicio.setDescripcion(descripcion);
        return servicio;
    }

    private Hotel nuevoHotel(String nombre, String ciudad, Integer categoria, String telefono, String imagen) {
        Hotel hotel = new Hotel();
        hotel.setNombre(nombre);
        hotel.setCiudad(ciudad);
        hotel.setCategoria(categoria);
        hotel.setTelefono(telefono);
        hotel.setImagen(imagen);
        return hotel;
    }

    private Habitacion nuevaHabitacion(Hotel hotel, String numero, String tipo, BigDecimal precioPorNoche,
                                       Integer capacidad, Boolean disponible, String imagen) {
        Habitacion habitacion = new Habitacion();
        habitacion.setHotel(hotel);
        habitacion.setNumero(numero);
        habitacion.setTipo(tipo);
        habitacion.setPrecioPorNoche(precioPorNoche);
        habitacion.setCapacidad(capacidad);
        habitacion.setDisponible(disponible);
        habitacion.setImagen(imagen);
        return habitacion;
    }

    private Usuario nuevoUsuario(String nombre, String apellido, String email, String telefono, String documento) {
        Usuario usuario = new Usuario();
        usuario.setNombre(nombre);
        usuario.setApellido(apellido);
        usuario.setEmail(email);
        usuario.setTelefono(telefono);
        usuario.setDocumento(documento);
        return usuario;
    }

    private Reserva nuevaReserva(Usuario usuario, Habitacion habitacion, LocalDate fechaEntrada,
                                 LocalDate fechaSalida, String estado, BigDecimal total) {
        Reserva reserva = new Reserva();
        reserva.setUsuario(usuario);
        reserva.setHabitacion(habitacion);
        reserva.setFechaEntrada(fechaEntrada);
        reserva.setFechaSalida(fechaSalida);
        reserva.setEstado(estado);
        reserva.setTotal(total);
        return reserva;
    }

    private Pago nuevoPago(Reserva reserva, BigDecimal monto, LocalDate fecha, String metodo) {
        Pago pago = new Pago();
        pago.setReserva(reserva);
        pago.setMonto(monto);
        pago.setFecha(fecha);
        pago.setMetodo(metodo);
        return pago;
    }
}
