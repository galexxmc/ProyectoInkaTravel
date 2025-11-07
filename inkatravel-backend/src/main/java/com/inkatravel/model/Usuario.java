package com.inkatravel.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails; // <-- IMPORTANTE

import java.time.LocalDateTime;
import java.util.Collection; // <-- IMPORTANTE
import java.util.List; // <-- IMPORTANTE
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "usuario")
public class Usuario implements UserDetails { // <-- ¡LA CLAVE ESTÁ AQUÍ!

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false, unique = true)
    private String correo;

    @Column(nullable = false)
    private String contrasena;

    private String telefono;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoUsuario tipo = TipoUsuario.GRATIS;

    @Column(name = "puntos_acumulados")
    private int puntosAcumulados = 0;

    @Column(name = "fecha_registro", updatable = false)
    private LocalDateTime fechaRegistro;

    @Column(name = "suscripcion_activa")
    private boolean suscripcionActiva = false;

    @Column(nullable = false)
    private boolean activo = true;

    @OneToMany(mappedBy = "usuario")
    private Set<Reserva> reservas;

    @OneToMany(mappedBy = "usuario")
    private Set<PuntoFidelidad> puntos;

    @PrePersist
    protected void onCreate() {
        fechaRegistro = LocalDateTime.now();
    }

    // --- MÉTODOS REQUERIDOS POR "UserDetails" ---
    // (Estos métodos conectan tu entidad con Spring Security)

    /**
     * Devuelve los "permisos" o "roles" del usuario.
     * Le decimos a Spring que el "rol" es el nombre de su TipoUsuario (ej: "GRATIS" o "PREMIUM")
     * (Más adelante podemos añadir "ROLE_ADMIN" aquí)
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Creamos una lista de permisos y añadimos el TIPO de usuario como un permiso
        return List.of(new SimpleGrantedAuthority("ROLE_" + tipo.name()));
    }

    /**
     * Spring Security llama a este método para obtener la contraseña.
     * Nosotros le devolvemos la contraseña que ya está encriptada en la BD.
     */
    @Override
    public String getPassword() {
        return contrasena;
    }

    /**
     * Spring Security llama a este método para obtener el "username".
     * Para nosotros, el username es el correo.
     */
    @Override
    public String getUsername() {
        return correo;
    }

    // --- Métodos de estado de la cuenta ---
    // (Como tu app no maneja cuentas bloqueadas o expiradas,
    // simplemente devolvemos 'true' en todos)

    @Override
    public boolean isAccountNonExpired() {
        return true; // La cuenta nunca expira
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // La cuenta nunca se bloquea
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Las credenciales (contraseña) nunca expiran
    }

    @Override
    public boolean isEnabled() {
        return this.activo; // Antes devolvía 'true'
    }
}