package com.alura.literatura.Repository;

import com.alura.literatura.Entidades.Autor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AutorRepository extends JpaRepository<Autor, Long> {

    Optional<Autor> findByNombre(String nombre);

    // Autores "vivos" en un año: nacimiento <= año y (muerte es null o muerte >= año)
    @Query("""
           SELECT a FROM Autor a
           WHERE a.anioNacimiento <= :anio
             AND (a.anioMuerte IS NULL OR a.anioMuerte >= :anio)
           """)
    List<Autor> findAutoresVivosEn(Integer anio);
}
