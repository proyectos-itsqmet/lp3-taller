package com.code_hub.config;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.code_hub.model.Cliente;
import com.code_hub.model.Empleado;
import com.code_hub.model.Perfil;
import com.code_hub.model.Proyecto;
import com.code_hub.model.Rol;
import com.code_hub.model.Usuario;
import com.code_hub.repository.ClienteRepository;
import com.code_hub.repository.EmpleadoRepository;
import com.code_hub.repository.PerfilRepository;
import com.code_hub.repository.ProyectoRepository;
import com.code_hub.repository.UsuarioRepository;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PerfilRepository perfilRepository;
    private final EmpleadoRepository empleadoRepository;
    private final ClienteRepository clienteRepository;
    private final ProyectoRepository proyectoRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UsuarioRepository usuarioRepository,
                      PerfilRepository perfilRepository,
                      EmpleadoRepository empleadoRepository,
                      ClienteRepository clienteRepository,
                      ProyectoRepository proyectoRepository,
                      PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.perfilRepository = perfilRepository;
        this.empleadoRepository = empleadoRepository;
        this.clienteRepository = clienteRepository;
        this.proyectoRepository = proyectoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        //! Guard: si ya hay datos, no sembrar de nuevo
        if (usuarioRepository.count() > 0) {
            return;
        }

        //! Usuarios (password encodeada con BCrypt)
        usuarioRepository.save(nuevoUsuario("admin", "admin123", "Administrador",
                "admin@codehub.com", Rol.ADMIN));
        usuarioRepository.save(nuevoUsuario("gerente", "gerente123", "Gabriela Torres",
                "gerente@codehub.com", Rol.GERENTE));
        usuarioRepository.save(nuevoUsuario("empleado", "empleado123", "Bruno Diaz",
                "empleado@codehub.com", Rol.EMPLEADO));

        //! Clientes
        Cliente clienteUno = clienteRepository.save(nuevoCliente("Comercial Andina S.A.",
                "1790012345001", "contacto@andina.com", "022345678", "Av. Amazonas N34-120, Quito"));
        Cliente clienteDos = clienteRepository.save(nuevoCliente("Distribuidora Pacifico",
                "0992345678001", "ventas@pacifico.com", "042987654", "Malecon 100, Guayaquil"));
        Cliente clienteTres = clienteRepository.save(nuevoCliente("TecnoSoluciones Cia. Ltda.",
                "1791234567001", "info@tecnosoluciones.com", "023456789", "Av. Republica E7-50, Quito"));

        //! Empleados (cada uno con su perfil, guardado por cascade)
        Empleado empleadoUno = empleadoRepository.save(nuevoEmpleado("Carla", "Mendez",
                "carla.mendez@codehub.com", "1712345678", LocalDate.of(2022, 3, 15),
                nuevoPerfil("Desarrollador Backend", "Java y Spring Boot", "0991112233",
                        "Especialista en APIs REST y bases de datos relacionales.",
                        new BigDecimal("2200.00"))));
        Empleado empleadoDos = empleadoRepository.save(nuevoEmpleado("Diego", "Salazar",
                "diego.salazar@codehub.com", "1723456789", LocalDate.of(2021, 7, 1),
                nuevoPerfil("Desarrollador Frontend", "Angular y React", "0992223344",
                        "Enfocado en interfaces accesibles y rendimiento.",
                        new BigDecimal("2100.00"))));
        Empleado empleadoTres = empleadoRepository.save(nuevoEmpleado("Valeria", "Ponce",
                "valeria.ponce@codehub.com", "1734567890", LocalDate.of(2023, 1, 10),
                nuevoPerfil("Lider Tecnico", "Arquitectura de software", "0993334455",
                        "Coordina equipos y define estandares tecnicos.",
                        new BigDecimal("3000.00"))));
        Empleado empleadoCuatro = empleadoRepository.save(nuevoEmpleado("Martin", "Reyes",
                "martin.reyes@codehub.com", "1745678901", LocalDate.of(2022, 11, 20),
                nuevoPerfil("Analista QA", "Pruebas automatizadas", "0994445566",
                        "Responsable de la calidad y cobertura de pruebas.",
                        new BigDecimal("1900.00"))));

        //! Proyectos (cliente 1:N, empleados N:M)
        proyectoRepository.save(nuevoProyecto("Portal de Ventas", "Plataforma web para gestion de pedidos.",
                LocalDate.of(2024, 1, 15), LocalDate.of(2024, 6, 30), "FINALIZADO",
                new BigDecimal("35000.00"), clienteUno,
                Set.of(empleadoUno, empleadoDos)));
        proyectoRepository.save(nuevoProyecto("App Movil Logistica", "Aplicacion para seguimiento de envios.",
                LocalDate.of(2024, 3, 1), null, "EN_PROGRESO",
                new BigDecimal("48000.00"), clienteDos,
                Set.of(empleadoDos, empleadoTres, empleadoCuatro)));
        proyectoRepository.save(nuevoProyecto("Sistema de Inventario", "Control de stock en tiempo real.",
                LocalDate.of(2024, 5, 10), null, "PLANIFICADO",
                new BigDecimal("27000.00"), clienteTres,
                Set.of(empleadoUno, empleadoTres)));
        proyectoRepository.save(nuevoProyecto("Migracion de Datos", "Migracion del sistema legado a la nube.",
                LocalDate.of(2024, 2, 1), LocalDate.of(2024, 4, 15), "CANCELADO",
                new BigDecimal("15000.00"), clienteUno,
                Set.of(empleadoCuatro)));
    }

    //! Helpers de construccion
    private Usuario nuevoUsuario(String username, String password, String nombre, String email, Rol rol) {
        Usuario u = new Usuario();
        u.setUsername(username);
        u.setPassword(passwordEncoder.encode(password));
        u.setNombre(nombre);
        u.setEmail(email);
        u.setEnabled(true);
        u.setRol(rol);
        return u;
    }

    private Cliente nuevoCliente(String nombre, String ruc, String email, String telefono, String direccion) {
        Cliente c = new Cliente();
        c.setNombre(nombre);
        c.setRuc(ruc);
        c.setEmail(email);
        c.setTelefono(telefono);
        c.setDireccion(direccion);
        return c;
    }

    private Perfil nuevoPerfil(String cargo, String especialidad, String telefono, String bio, BigDecimal salario) {
        Perfil p = new Perfil();
        p.setCargo(cargo);
        p.setEspecialidad(especialidad);
        p.setTelefono(telefono);
        p.setBio(bio);
        p.setSalario(salario);
        return p;
    }

    private Empleado nuevoEmpleado(String nombre, String apellido, String email, String documento,
                                   LocalDate fechaContratacion, Perfil perfil) {
        Empleado e = new Empleado();
        e.setNombre(nombre);
        e.setApellido(apellido);
        e.setEmail(email);
        e.setDocumento(documento);
        e.setFechaContratacion(fechaContratacion);
        e.setPerfil(perfil);
        return e;
    }

    private Proyecto nuevoProyecto(String nombre, String descripcion, LocalDate fechaInicio, LocalDate fechaFin,
                                   String estado, BigDecimal presupuesto, Cliente cliente, Set<Empleado> empleados) {
        Proyecto p = new Proyecto();
        p.setNombre(nombre);
        p.setDescripcion(descripcion);
        p.setFechaInicio(fechaInicio);
        p.setFechaFin(fechaFin);
        p.setEstado(estado);
        p.setPresupuesto(presupuesto);
        p.setCliente(cliente);
        p.setEmpleados(new HashSet<>(empleados));
        return p;
    }
}
