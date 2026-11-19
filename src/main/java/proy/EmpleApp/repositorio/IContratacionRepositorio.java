package proy.EmpleApp.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import proy.EmpleApp.modelo.Contratacion;

/** Spring Data JPA implementa las operaciones de consulta y persistencia de esta interfaz. */
@Repository
public interface IContratacionRepositorio extends JpaRepository<Contratacion, Integer> {
    boolean existsByServicio_IdServicio(Integer idServicio);
}
