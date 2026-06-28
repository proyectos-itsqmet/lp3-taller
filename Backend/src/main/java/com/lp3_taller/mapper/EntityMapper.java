package com.lp3_taller.mapper;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.lp3_taller.dto.HabitacionRequest;
import com.lp3_taller.dto.HabitacionResponse;
import com.lp3_taller.dto.HotelRequest;
import com.lp3_taller.dto.HotelResponse;
import com.lp3_taller.dto.PagoRequest;
import com.lp3_taller.dto.PagoResponse;
import com.lp3_taller.dto.ReservaRequest;
import com.lp3_taller.dto.ReservaResponse;
import com.lp3_taller.dto.ServicioRequest;
import com.lp3_taller.dto.ServicioResponse;
import com.lp3_taller.dto.UsuarioRequest;
import com.lp3_taller.dto.UsuarioResponse;
import com.lp3_taller.model.Habitacion;
import com.lp3_taller.model.Hotel;
import com.lp3_taller.model.Pago;
import com.lp3_taller.model.Reserva;
import com.lp3_taller.model.Servicio;
import com.lp3_taller.model.Usuario;

@Component
public class EntityMapper {

    public Hotel toEntity(HotelRequest request) {
        Hotel hotel = new Hotel();
        applyToEntity(request, hotel);
        return hotel;
    }

    public void applyToEntity(HotelRequest request, Hotel hotel) {
        hotel.setNombre(request.nombre());
        hotel.setCiudad(request.ciudad());
        hotel.setCategoria(request.categoria());
        hotel.setTelefono(request.telefono());
        hotel.setImagen(request.imagen());
    }

    public HotelResponse toResponse(Hotel hotel) {
        Set<Servicio> servicios = hotel.getServicios();
        List<ServicioResponse> servicioResponses = servicios == null
                ? List.of()
                : servicios.stream().map(this::toResponse).toList();
        return new HotelResponse(
                hotel.getId(),
                hotel.getNombre(),
                hotel.getCiudad(),
                hotel.getCategoria(),
                hotel.getTelefono(),
                hotel.getImagen(),
                servicioResponses
        );
    }

    public Servicio toEntity(ServicioRequest request) {
        Servicio servicio = new Servicio();
        applyToEntity(request, servicio);
        return servicio;
    }

    public void applyToEntity(ServicioRequest request, Servicio servicio) {
        servicio.setNombre(request.nombre());
        servicio.setDescripcion(request.descripcion());
    }

    public ServicioResponse toResponse(Servicio servicio) {
        return new ServicioResponse(
                servicio.getId(),
                servicio.getNombre(),
                servicio.getDescripcion()
        );
    }

    public Habitacion toEntity(HabitacionRequest request) {
        Habitacion habitacion = new Habitacion();
        applyToEntity(request, habitacion);
        return habitacion;
    }

    public void applyToEntity(HabitacionRequest request, Habitacion habitacion) {
        habitacion.setNumero(request.numero());
        habitacion.setTipo(request.tipo());
        habitacion.setPrecioPorNoche(request.precioPorNoche());
        habitacion.setCapacidad(request.capacidad());
        habitacion.setDisponible(request.disponible());
        habitacion.setImagen(request.imagen());
    }

    public HabitacionResponse toResponse(Habitacion habitacion) {
        Hotel hotel = habitacion.getHotel();
        return new HabitacionResponse(
                habitacion.getId(),
                hotel == null ? null : hotel.getId(),
                hotel == null ? null : hotel.getNombre(),
                habitacion.getNumero(),
                habitacion.getTipo(),
                habitacion.getPrecioPorNoche(),
                habitacion.getCapacidad(),
                habitacion.getDisponible(),
                habitacion.getImagen()
        );
    }

    public Usuario toEntity(UsuarioRequest request) {
        Usuario usuario = new Usuario();
        applyToEntity(request, usuario);
        return usuario;
    }

    public void applyToEntity(UsuarioRequest request, Usuario usuario) {
        usuario.setNombre(request.nombre());
        usuario.setApellido(request.apellido());
        usuario.setEmail(request.email());
        usuario.setTelefono(request.telefono());
        usuario.setDocumento(request.documento());
    }

    public UsuarioResponse toResponse(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getEmail(),
                usuario.getTelefono(),
                usuario.getDocumento()
        );
    }

    public Reserva toEntity(ReservaRequest request) {
        Reserva reserva = new Reserva();
        applyToEntity(request, reserva);
        return reserva;
    }

    public void applyToEntity(ReservaRequest request, Reserva reserva) {
        reserva.setFechaEntrada(request.fechaEntrada());
        reserva.setFechaSalida(request.fechaSalida());
        reserva.setEstado(request.estado());
        reserva.setTotal(request.total());
    }

    public ReservaResponse toResponse(Reserva reserva) {
        Usuario usuario = reserva.getUsuario();
        Habitacion habitacion = reserva.getHabitacion();
        return new ReservaResponse(
                reserva.getId(),
                usuario == null ? null : usuario.getId(),
                usuario == null ? null : (usuario.getNombre() + " " + usuario.getApellido()),
                habitacion == null ? null : habitacion.getId(),
                habitacion == null ? null : habitacion.getNumero(),
                reserva.getFechaEntrada(),
                reserva.getFechaSalida(),
                reserva.getEstado(),
                reserva.getTotal()
        );
    }

    public Pago toEntity(PagoRequest request) {
        Pago pago = new Pago();
        applyToEntity(request, pago);
        return pago;
    }

    public void applyToEntity(PagoRequest request, Pago pago) {
        pago.setMonto(request.monto());
        pago.setFecha(request.fecha());
        pago.setMetodo(request.metodo());
    }

    public PagoResponse toResponse(Pago pago) {
        Reserva reserva = pago.getReserva();
        return new PagoResponse(
                pago.getId(),
                reserva == null ? null : reserva.getId(),
                pago.getMonto(),
                pago.getFecha(),
                pago.getMetodo()
        );
    }
}
