package com.inkatravel.dto;

import com.inkatravel.model.TipoUsuario;
import com.inkatravel.model.Usuario;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioResponseDTO {

    private Integer id;
    private String nombre;
    private String correo;
    private TipoUsuario tipo;
    private int puntosAcumulados;
    private boolean suscripcionActiva;
    private boolean activo;

    public UsuarioResponseDTO(Usuario usuario) {
        this.id = usuario.getId();
        this.nombre = usuario.getNombre();
        this.correo = usuario.getCorreo();
        this.tipo = usuario.getTipo();
        this.puntosAcumulados = usuario.getPuntosAcumulados();
        this.suscripcionActiva = usuario.isSuscripcionActiva();
        this.activo = usuario.isActivo();
    }
}