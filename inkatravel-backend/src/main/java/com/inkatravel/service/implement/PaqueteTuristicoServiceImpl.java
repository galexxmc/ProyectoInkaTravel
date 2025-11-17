package com.inkatravel.service.implement;

import java.math.BigDecimal;
import com.inkatravel.model.PaqueteTuristico;
import com.inkatravel.repository.PaqueteTuristicoRepository;
import com.inkatravel.service.PaqueteTuristicoService;

import com.inkatravel.dto.PaqueteDetalleResponseDTO;
import com.inkatravel.dto.PaqueteTuristicoRequestDTO; // NUEVO IMPORTE
import com.inkatravel.dto.clima.OpenMeteoResponseDTO;
import com.inkatravel.service.ClimaService;

import com.inkatravel.dto.PaqueteTuristicoResponseDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.inkatravel.model.EstadoReserva;
import com.inkatravel.model.Reserva;
import com.inkatravel.model.Usuario;
import com.inkatravel.repository.ReservaRepository;
import com.inkatravel.repository.UsuarioRepository;
import java.security.Principal;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

import java.util.List;

@Service
public class PaqueteTuristicoServiceImpl implements PaqueteTuristicoService {

    private final PaqueteTuristicoRepository paqueteRepository;
    private final ClimaService climaService;
    private final ReservaRepository reservaRepository;
    private final UsuarioRepository usuarioRepository;

    public PaqueteTuristicoServiceImpl(PaqueteTuristicoRepository paqueteRepository,
                                       ClimaService climaService,
                                       ReservaRepository reservaRepository,
                                       UsuarioRepository usuarioRepository) {
        this.paqueteRepository = paqueteRepository;
        this.climaService = climaService;
        this.reservaRepository = reservaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    // --- MÉTODO CREAR PAQUETE (Modificado) ---
    @Override
    public PaqueteTuristicoResponseDTO crearPaquete(PaqueteTuristicoRequestDTO paqueteDTO) {

        // 1. Mapeo del DTO a la Entidad
        PaqueteTuristico paquete = new PaqueteTuristico();

        paquete.setNombre(paqueteDTO.getNombre());
        paquete.setDescripcion(paqueteDTO.getDescripcion());
        paquete.setPrecio(paqueteDTO.getPrecio());
        paquete.setRegion(paqueteDTO.getRegion());
        paquete.setCategoria(paqueteDTO.getCategoria());
        paquete.setItinerario(paqueteDTO.getItinerario());
        paquete.setDisponibilidad(paqueteDTO.isDisponibilidad());
        paquete.setLatitud(paqueteDTO.getLatitud());
        paquete.setLongitud(paqueteDTO.getLongitud());

        // Asignación de la URL de la imagen que ya guardó el Controller
        paquete.setImagenUrl(paqueteDTO.getImagenUrl());

        // 2. Guardar y devolver DTO de Respuesta
        PaqueteTuristico paqueteGuardado = paqueteRepository.save(paquete);
        return new PaqueteTuristicoResponseDTO(paqueteGuardado);
    }

    // --- MÉTODO ACTUALIZAR PAQUETE (Modificado) ---
    @Override
    public PaqueteTuristicoResponseDTO actualizarPaquete(Integer id, PaqueteTuristicoRequestDTO paqueteDetalles) { // 1. ELIMINAMOS 'throws Exception'

        PaqueteTuristico paqueteExistente = paqueteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paquete no encontrado con ID: " + id)); // 2. CAMBIAMOS 'Exception' a 'RuntimeException'

        // 1. Actualizamos todos los campos
        paqueteExistente.setNombre(paqueteDetalles.getNombre());
        paqueteExistente.setDescripcion(paqueteDetalles.getDescripcion());
        paqueteExistente.setPrecio(paqueteDetalles.getPrecio());
        paqueteExistente.setRegion(paqueteDetalles.getRegion());
        paqueteExistente.setCategoria(paqueteDetalles.getCategoria());
        paqueteExistente.setItinerario(paqueteDetalles.getItinerario());
        paqueteExistente.setDisponibilidad(paqueteDetalles.isDisponibilidad());
        paqueteExistente.setLatitud(paqueteDetalles.getLatitud());
        paqueteExistente.setLongitud(paqueteDetalles.getLongitud());

        // 2. Lógica para la URL de la imagen
        if (paqueteDetalles.getImagenUrl() != null) {
            paqueteExistente.setImagenUrl(paqueteDetalles.getImagenUrl());
        }

        PaqueteTuristico paqueteActualizado = paqueteRepository.save(paqueteExistente);

        return new PaqueteTuristicoResponseDTO(paqueteActualizado);
    }

    /**
     * (¡ACTUALIZADO!)
     * Ahora SÓLO devuelve paquetes con disponibilidad = true
     */
    @Override
    public List<PaqueteTuristico> obtenerPaquetesFiltrados(String region, String categoria, BigDecimal precioMin, BigDecimal precioMax) {

        boolean disponibilidad = true;

        if (precioMin != null && precioMax != null) {
            return paqueteRepository.findByPrecioBetweenAndDisponibilidad(precioMin, precioMax, disponibilidad);
        }
        if (precioMin != null) {
            return paqueteRepository.findByPrecioGreaterThanEqualAndDisponibilidad(precioMin, disponibilidad);
        }
        if (precioMax != null) {
            return paqueteRepository.findByPrecioLessThanEqualAndDisponibilidad(precioMax, disponibilidad);
        }
        if (region != null) {
            return paqueteRepository.findByRegionAndDisponibilidad(region, disponibilidad);
        }
        if (categoria != null) {
            return paqueteRepository.findByCategoriaAndDisponibilidad(categoria, disponibilidad);
        }

        // Si no hay filtros, devuelve TODOS los DISPONIBLES
        return paqueteRepository.findAllByDisponibilidad(disponibilidad);
    }

    @Override
    @Transactional(readOnly = true)
    public PaqueteDetalleResponseDTO obtenerPaquetePorId(Integer id) throws Exception {
        // 1. Obtener los datos del paquete de nuestra BD
        PaqueteTuristico paquete = paqueteRepository.findById(id)
                .orElseThrow(() -> new Exception("Paquete no encontrado con ID: " + id));

        // 2. Convertir a DTO (para evitar errores LAZY)
        PaqueteTuristicoResponseDTO paqueteDTO = new PaqueteTuristicoResponseDTO(paquete);

        // 3. Obtener el clima de la API externa
        OpenMeteoResponseDTO clima = null;
        if (paquete.getLatitud() != null && paquete.getLongitud() != null) {
            clima = climaService.obtenerClimaActual(paquete.getLatitud(), paquete.getLongitud());
        }

        // 4. Devolver el DTO contenedor con AMBOS datos
        return new PaqueteDetalleResponseDTO(paqueteDTO, clima);
    }

    @Override
    public void eliminarPaquete(Integer id) throws Exception {

        // --- 1. VERIFICACIÓN DE INTEGRIDAD (¡NUEVO!) ---
        // Verificamos si alguna reserva está usando este paquete
        boolean tieneReservas = reservaRepository.existsByPaqueteTuristicoId(id);

        if (tieneReservas) {
            // Si tiene reservas, lanzamos un error amigable
            throw new Exception("No se puede eliminar: El paquete ya tiene reservas asociadas.");
        }

        // --- 2. VERIFICAR QUE EL PAQUETE EXISTA (Buena práctica) ---
        // (Si no tiene reservas, verificamos que exista antes de borrar)
        PaqueteTuristico paquete = paqueteRepository.findById(id)
                .orElseThrow(() -> new Exception("Paquete no encontrado con ID: " + id));

        // 3. Si pasa ambas verificaciones, lo eliminamos
        paqueteRepository.delete(paquete);
    }

    /**
     * (NUEVO) Implementación de RF-13 (Lógica Simple)
     */
    @Override
    @Transactional(readOnly = true)
    public List<PaqueteTuristicoResponseDTO> obtenerRecomendaciones(Principal principal) throws Exception {

        // 1. Encontrar al usuario logueado
        Usuario usuario = usuarioRepository.findByCorreo(principal.getName())
                .orElseThrow(() -> new Exception("Usuario no encontrado"));

        // 2. Buscar su última reserva CONFIRMADA
        Optional<Reserva> ultimaReservaOpt = reservaRepository
                .findFirstByUsuarioAndEstadoOrderByFechaReservaDesc(usuario, EstadoReserva.CONFIRMADA);

        // 3. Si NO tiene reservas, no podemos recomendar. Devolvemos lista vacía.
        if (ultimaReservaOpt.isEmpty()) {
            return Collections.emptyList();
        }

        // 4. Si SÍ tiene, obtenemos la región y el ID de esa reserva
        Reserva ultimaReserva = ultimaReservaOpt.get();
        String region = ultimaReserva.getPaqueteTuristico().getRegion();
        Integer idExcluir = ultimaReserva.getPaqueteTuristico().getId();

        // 5. Buscar otros paquetes (máx 5) de esa región, excluyendo el que ya compró
        List<PaqueteTuristico> recomendados = paqueteRepository.findTop5ByRegionAndIdNotAndDisponibilidad(region, idExcluir, true);
        // 6. Convertir a DTO (para evitar el error 403) y devolver
        return recomendados.stream()
                .map(PaqueteTuristicoResponseDTO::new)
                .collect(Collectors.toList());
    }
}