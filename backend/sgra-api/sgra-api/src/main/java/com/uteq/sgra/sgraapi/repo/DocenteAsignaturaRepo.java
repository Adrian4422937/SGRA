package com.uteq.sgra.sgraapi.repo;

import com.uteq.sgra.sgraapi.entity.DocenteAsignatura;
import com.uteq.sgra.sgraapi.entity.DocenteAsignaturaId;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface DocenteAsignaturaRepo extends JpaRepository<DocenteAsignatura, DocenteAsignaturaId> {

    @Query("""
    select da
    from DocenteAsignatura da
    join fetch da.asignatura a
    join fetch da.periodo p
    where da.docente.idUsuario = :docenteId
""")
    List<DocenteAsignatura> findAllByDocenteId(@Param("docenteId") Long docenteId);
}

