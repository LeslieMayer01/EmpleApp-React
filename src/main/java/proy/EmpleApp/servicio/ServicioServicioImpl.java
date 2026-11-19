package proy.EmpleApp.servicio;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import proy.EmpleApp.modelo.Servicio;
import proy.EmpleApp.repositorio.IServicioRepositorio;
import proy.EmpleApp.repositorio.*;
import proy.EmpleApp.modelo.*;
import proy.EmpleApp.dto.*;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

/** Implementa las operaciones de la entidad usando su repositorio de Spring Data JPA. */
@Service
public class ServicioServicioImpl implements IServicioServicio {
    // Spring inyecta el repositorio; no es necesario crearlo con new.
    @Autowired
    private IServicioRepositorio repositorio;

    @Autowired private proy.EmpleApp.seguridad.UsuarioActual actual;
    @Autowired private IEmpleadorRepositorio empleadores;
    @Autowired private ITipoServicioRepositorio tipos;
    @Autowired private IEstadoServicioRepositorio estados;
    @Autowired private IPostulacionRepositorio postulaciones;
    @Autowired private IContratacionRepositorio contrataciones;
    @Autowired private jakarta.validation.Validator validador;

    @Override
    public List<Servicio> listarServicio() { return repositorio.findAll(); }

    @Override
    public Servicio guardarServicio(Servicio entidad) {
        // Si tiene ID, primero comprueba que el registro que se quiere actualizar exista.
        if (entidad.getIdServicio() != null) {
            buscarServicio(entidad.getIdServicio());
        }
        return repositorio.save(entidad);
    }

    @Override
    public Servicio buscarServicio(Integer id) {
        // Convierte la ausencia del registro en una respuesta HTTP 404.
        return repositorio.findById(id).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "Servicio no encontrado"));
    }

    @Override
    public void eliminarServicio(Integer id) {
        repositorio.delete(buscarServicio(id));
    }

    @Override
    @Transactional(readOnly = true)
    public PaginaServicios buscarPublicaciones(FiltroServicios filtro, Authentication autenticacion,
            boolean soloMias) {
        validar(filtro);
        Usuario usuario = soloMias ? actual.exigirRol(autenticacion, "Empleador") : actual.obtener(autenticacion);
        int pagina = filtro.pagina() == null ? 0 : filtro.pagina();
        int tamano = filtro.tamano() == null ? 8 : filtro.tamano();
        Specification<Servicio> condiciones = (raiz, consulta, cb) -> {
            var predicados = new java.util.ArrayList<jakarta.persistence.criteria.Predicate>();
            if (soloMias) predicados.add(cb.equal(raiz.get("empleador").get("usuario").get("idUsuario"), usuario.getIdUsuario()));
            if (filtro.tipoId() != null) predicados.add(cb.equal(raiz.get("tipoServicio").get("idTipoServicio"), filtro.tipoId()));
            if (filtro.estadoId() != null) predicados.add(cb.equal(raiz.get("estado").get("idEstado"), filtro.estadoId()));
            if (filtro.fechaDesde() != null) predicados.add(cb.greaterThanOrEqualTo(raiz.get("fecha"), filtro.fechaDesde()));
            if (filtro.fechaHasta() != null) predicados.add(cb.lessThanOrEqualTo(raiz.get("fecha"), filtro.fechaHasta()));
            if (filtro.ubicacion() != null && !filtro.ubicacion().isBlank())
                predicados.add(cb.like(cb.lower(raiz.get("ubicacion")), patron(filtro.ubicacion()), '\\'));
            if (filtro.busqueda() != null && !filtro.busqueda().isBlank()) {
                String patron = patron(filtro.busqueda());
                predicados.add(cb.or(
                    cb.like(cb.lower(raiz.get("ubicacion")), patron, '\\'),
                    cb.like(cb.lower(raiz.get("tipoServicio").get("nombreTipo")), patron, '\\'),
                    cb.like(cb.lower(raiz.get("empleador").get("usuario").get("nombre")), patron, '\\')));
            }
            return cb.and(predicados.toArray(jakarta.persistence.criteria.Predicate[]::new));
        };
        var resultado = repositorio.findAll(condiciones,
            PageRequest.of(pagina, tamano, Sort.by(Sort.Direction.DESC, "idServicio")));
        return new PaginaServicios(resultado.getContent().stream().map(ServicioResumen::de).toList(),
            pagina, tamano, resultado.getTotalElements(), resultado.getTotalPages());
    }

    @Override
    @Transactional
    public ServicioResumen publicar(DatosPublicacion datos, Authentication autenticacion) {
        validar(datos);
        Usuario usuario = actual.exigirRol(autenticacion, "Empleador");
        Empleador propietario = empleadores.findFirstByUsuario_IdUsuarioOrderByIdEmpleadorAsc(usuario.getIdUsuario())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT, "Completa tu perfil de empleador antes de publicar"));
        Servicio servicio = new Servicio();
        servicio.setEmpleador(propietario);
        servicio.setEstado(estado("Publicado"));
        aplicarDatos(servicio, datos);
        return ServicioResumen.de(repositorio.save(servicio));
    }

    @Override
    @Transactional
    public ServicioResumen actualizarPublicacion(Integer id, DatosPublicacion datos, Authentication autenticacion) {
        validar(datos);
        Servicio servicio = publicacionPropia(id, autenticacion);
        comprobarEditable(servicio);
        aplicarDatos(servicio, datos);
        return ServicioResumen.de(repositorio.save(servicio));
    }

    @Override
    @Transactional
    public ServicioResumen cancelarPublicacion(Integer id, Authentication autenticacion) {
        Servicio servicio = publicacionPropia(id, autenticacion);
        comprobarEditable(servicio);
        servicio.setEstado(estado("Cancelado"));
        return ServicioResumen.de(repositorio.save(servicio));
    }

    private Servicio publicacionPropia(Integer id, Authentication autenticacion) {
        Usuario usuario = actual.exigirRol(autenticacion, "Empleador");
        // Se bloquea la fila durante la edición o cancelación para serializar estas operaciones.
        Servicio servicio = repositorio.buscarParaEditar(id).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "Publicación no encontrada"));
        if (!servicio.getEmpleador().getUsuario().getIdUsuario().equals(usuario.getIdUsuario()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "La publicación no te pertenece");
        return servicio;
    }

    private void comprobarEditable(Servicio servicio) {
        // Los flujos de postulación y contratación se implementarán después; no se alteran sus datos.
        if (!servicio.getEstado().getNombreEstado().equalsIgnoreCase("Publicado"))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Solo puedes modificar ofertas en estado Publicado");
        if (postulaciones.existsByServicio_IdServicio(servicio.getIdServicio())
                || contrataciones.existsByServicio_IdServicio(servicio.getIdServicio()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "La oferta ya tiene postulaciones o contrataciones vinculadas");
    }

    private void aplicarDatos(Servicio servicio, DatosPublicacion datos) {
        servicio.setTipoServicio(tipos.findById(datos.idTipoServicio()).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "Tipo de servicio no encontrado")));
        servicio.setFecha(datos.fecha());
        servicio.setHoraInicio(datos.horaInicio());
        servicio.setHoraFin(datos.horaFin());
        servicio.setUbicacion(datos.ubicacion().strip());
        servicio.setValorOfertado(datos.valorOfertado());
    }

    private EstadoServicio estado(String nombre) {
        // Crea solo la etiqueta necesaria si el catálogo aún no la contiene; no cambia estados existentes.
        return estados.findFirstByNombreEstadoIgnoreCaseOrderByIdEstadoAsc(nombre).orElseGet(() -> {
            EstadoServicio nuevo = new EstadoServicio(); nuevo.setNombreEstado(nombre);
            return estados.save(nuevo);
        });
    }

    private void validar(Object datos) {
        if (datos == null || !validador.validate(datos).isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Revisa los campos, fechas y horarios de la publicación");
    }

    private String patron(String valor) {
        // Escapa los comodines de LIKE: % y _ se buscan como texto, no amplían la consulta.
        return "%" + valor.strip().toLowerCase(java.util.Locale.ROOT)
            .replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_") + "%";
    }
}
