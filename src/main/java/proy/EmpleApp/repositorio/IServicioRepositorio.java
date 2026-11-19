package proy.EmpleApp.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import proy.EmpleApp.modelo.Servicio;

/** Spring Data JPA implementa las operaciones de consulta y persistencia de esta interfaz. */
@Repository
public interface IServicioRepositorio extends JpaRepository<Servicio, Integer>,
        org.springframework.data.jpa.repository.JpaSpecificationExecutor<Servicio> {
    @Override
    @org.springframework.data.jpa.repository.EntityGraph(attributePaths = {"empleador.usuario", "tipoServicio", "estado"})
    org.springframework.data.domain.Page<Servicio> findAll(
        org.springframework.data.jpa.domain.Specification<Servicio> filtro,
        org.springframework.data.domain.Pageable pagina);

    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @org.springframework.data.jpa.repository.Query("select s from Servicio s where s.idServicio = :id")
    java.util.Optional<Servicio> buscarParaEditar(@org.springframework.data.repository.query.Param("id") Integer id);
}
