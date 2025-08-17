package com.alura.literatura.Repository;

import com.alura.literatura.Entidades.Libro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LibroRepository extends JpaRepository<Libro, Long> {

    List<Libro> findByIdiomaIgnoreCase(String idioma);

    Optional<Libro> findByTituloAndAutor_Nombre(String titulo, String autorNombre);
}
