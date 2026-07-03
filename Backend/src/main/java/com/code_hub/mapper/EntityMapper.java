package com.code_hub.mapper;

import org.springframework.stereotype.Component;

import com.code_hub.dto.ClienteRequest;
import com.code_hub.dto.ClienteResponse;
import com.code_hub.dto.EmpleadoRequest;
import com.code_hub.dto.EmpleadoResponse;
import com.code_hub.dto.EmpleadoResumenResponse;
import com.code_hub.dto.PerfilRequest;
import com.code_hub.dto.PerfilResponse;
import com.code_hub.dto.ProyectoRequest;
import com.code_hub.dto.ProyectoResponse;
import com.code_hub.dto.ProyectoResumenResponse;
import com.code_hub.dto.UsuarioRequest;
import com.code_hub.dto.UsuarioResponse;
import com.code_hub.model.Cliente;
import com.code_hub.model.Empleado;
import com.code_hub.model.Perfil;
import com.code_hub.model.Proyecto;
import com.code_hub.model.Usuario;

@Component
public class EntityMapper {

    // ===== Usuario =====
    // El mapper NO toca el password: eso lo maneja el service con el PasswordEncoder.
    public Usuario toEntity(UsuarioRequest r) {
        Usuario u = new Usuario();
        u.setUsername(r.username());
        u.setNombre(r.nombre());
        u.setEmail(r.email());
        u.setEnabled(r.enabled());
        u.setRol(r.rol());
        return u;
    }

    public void applyToEntity(UsuarioRequest r, Usuario u) {
        u.setUsername(r.username());
        u.setNombre(r.nombre());
        u.setEmail(r.email());
        u.setEnabled(r.enabled());
        u.setRol(r.rol());
    }

    public UsuarioResponse toResponse(Usuario u) {
        return new UsuarioResponse(
            u.getId(),
            u.getUsername(),
            u.getNombre(),
            u.getEmail(),
            u.getEnabled(),
            u.getRol().name()
        );
    }

    // ===== Perfil =====
    public Perfil toEntity(PerfilRequest r) {
        Perfil p = new Perfil();
        p.setCargo(r.cargo());
        p.setEspecialidad(r.especialidad());
        p.setTelefono(r.telefono());
        p.setBio(r.bio());
        p.setSalario(r.salario());
        return p;
    }

    public void applyToEntity(PerfilRequest r, Perfil p) {
        p.setCargo(r.cargo());
        p.setEspecialidad(r.especialidad());
        p.setTelefono(r.telefono());
        p.setBio(r.bio());
        p.setSalario(r.salario());
    }

    public PerfilResponse toResponse(Perfil p) {
        return new PerfilResponse(
            p.getId(),
            p.getCargo(),
            p.getEspecialidad(),
            p.getTelefono(),
            p.getBio(),
            p.getSalario()
        );
    }

    // ===== Empleado =====
    public Empleado toEntity(EmpleadoRequest r) {
        Empleado e = new Empleado();
        e.setNombre(r.nombre());
        e.setApellido(r.apellido());
        e.setEmail(r.email());
        e.setDocumento(r.documento());
        e.setFechaContratacion(r.fechaContratacion());
        e.setPerfil(toEntity(r.perfil()));
        return e;
    }

    public void applyToEntity(EmpleadoRequest r, Empleado e) {
        e.setNombre(r.nombre());
        e.setApellido(r.apellido());
        e.setEmail(r.email());
        e.setDocumento(r.documento());
        e.setFechaContratacion(r.fechaContratacion());
        if (e.getPerfil() != null) {
            applyToEntity(r.perfil(), e.getPerfil());
        } else {
            e.setPerfil(toEntity(r.perfil()));
        }
    }

    public EmpleadoResponse toResponse(Empleado e) {
        PerfilResponse perfil = e.getPerfil() == null ? null : toResponse(e.getPerfil());
        return new EmpleadoResponse(
            e.getId(),
            e.getNombre(),
            e.getApellido(),
            e.getEmail(),
            e.getDocumento(),
            e.getFechaContratacion(),
            perfil,
            e.getProyectos().stream().map(this::toResumen).toList()
        );
    }

    public EmpleadoResumenResponse toResumen(Empleado e) {
        String cargo = e.getPerfil() == null ? null : e.getPerfil().getCargo();
        return new EmpleadoResumenResponse(
            e.getId(),
            e.getNombre() + " " + e.getApellido(),
            cargo
        );
    }

    // ===== Cliente =====
    public Cliente toEntity(ClienteRequest r) {
        Cliente c = new Cliente();
        c.setNombre(r.nombre());
        c.setRuc(r.ruc());
        c.setEmail(r.email());
        c.setTelefono(r.telefono());
        c.setDireccion(r.direccion());
        return c;
    }

    public void applyToEntity(ClienteRequest r, Cliente c) {
        c.setNombre(r.nombre());
        c.setRuc(r.ruc());
        c.setEmail(r.email());
        c.setTelefono(r.telefono());
        c.setDireccion(r.direccion());
    }

    public ClienteResponse toResponse(Cliente c) {
        return new ClienteResponse(
            c.getId(),
            c.getNombre(),
            c.getRuc(),
            c.getEmail(),
            c.getTelefono(),
            c.getDireccion(),
            c.getProyectos().stream().map(this::toResumen).toList()
        );
    }

    // ===== Proyecto =====
    // NO setea cliente ni empleados: eso lo resuelve el service.
    public Proyecto toEntity(ProyectoRequest r) {
        Proyecto p = new Proyecto();
        p.setNombre(r.nombre());
        p.setDescripcion(r.descripcion());
        p.setFechaInicio(r.fechaInicio());
        p.setFechaFin(r.fechaFin());
        p.setEstado(r.estado());
        p.setPresupuesto(r.presupuesto());
        return p;
    }

    public void applyToEntity(ProyectoRequest r, Proyecto p) {
        p.setNombre(r.nombre());
        p.setDescripcion(r.descripcion());
        p.setFechaInicio(r.fechaInicio());
        p.setFechaFin(r.fechaFin());
        p.setEstado(r.estado());
        p.setPresupuesto(r.presupuesto());
    }

    public ProyectoResponse toResponse(Proyecto p) {
        Long clienteId = p.getCliente() == null ? null : p.getCliente().getId();
        String clienteNombre = p.getCliente() == null ? null : p.getCliente().getNombre();
        return new ProyectoResponse(
            p.getId(),
            p.getNombre(),
            p.getDescripcion(),
            p.getFechaInicio(),
            p.getFechaFin(),
            p.getEstado(),
            p.getPresupuesto(),
            clienteId,
            clienteNombre,
            p.getEmpleados().stream().map(this::toResumen).toList()
        );
    }

    public ProyectoResumenResponse toResumen(Proyecto p) {
        return new ProyectoResumenResponse(
            p.getId(),
            p.getNombre(),
            p.getEstado()
        );
    }
}
