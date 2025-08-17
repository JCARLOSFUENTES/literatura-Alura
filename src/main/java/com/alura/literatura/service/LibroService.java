package com.alura.literatura.service;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.alura.literatura.Entidades.Autor;
import com.alura.literatura.Entidades.Libro;
import com.alura.literatura.Repository.AutorRepository;
import com.alura.literatura.Repository.LibroRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Service
public class LibroService {

    private final LibroRepository libroRepository;
    private final AutorRepository autorRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public LibroService(LibroRepository libroRepository, AutorRepository autorRepository) {
        this.libroRepository = libroRepository;
        this.autorRepository = autorRepository;
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    // Opción 1 del menú: Agregar libro desde Gutendex (API)
    public void agregarLibroDesdeApi(String tituloBusqueda) {
        try {
            String q = URLEncoder.encode(tituloBusqueda, StandardCharsets.UTF_8);
            String url = "https://gutendex.com/books/?search=" + q;

            JsonNode respuesta = restTemplate.getForObject(url, JsonNode.class);
            if (respuesta == null || !respuesta.has("results") || respuesta.get("results").isEmpty()) {
                System.out.println("No se encontraron resultados en Gutendex.");
                return;
            }

            JsonNode libroJson = respuesta.get("results").get(0);

            // Autor
            Autor autor;
            if (libroJson.has("authors") && !libroJson.get("authors").isEmpty()) {
                JsonNode autorJson = libroJson.get("authors").get(0);
                String nombre = autorJson.path("name").asText(null);
                Integer birth = autorJson.path("birth_year").isNumber() ? autorJson.get("birth_year").asInt() : null;
                Integer death = autorJson.path("death_year").isNumber() ? autorJson.get("death_year").asInt() : null;

                if (nombre == null) nombre = "Autor desconocido";

                Optional<Autor> existente = autorRepository.findByNombre(nombre);
                if (existente.isPresent()) {
                    autor = existente.get();
                } else {
                    autor = new Autor();
                    autor.setNombre(nombre);
                    autor.setAnioNacimiento(birth);
                    autor.setAnioMuerte(death);
                    autor = autorRepository.save(autor);
                }
            } else {
                // Sin autor
                Optional<Autor> desconocido = autorRepository.findByNombre("Autor desconocido");
                autor = desconocido.orElseGet(() -> autorRepository.save(nuevoAutorDesconocido()));
            }

            // Libro
            String titulo = libroJson.path("title").asText("Titulo desconocido");
            String idioma = (libroJson.has("languages") && !libroJson.get("languages").isEmpty())
                    ? libroJson.get("languages").get(0).asText("xx")
                    : "xx";
            Integer descargas = libroJson.path("download_count").isNumber()
                    ? libroJson.get("download_count").asInt()
                    : 0;

            // Evitar duplicado
            Optional<Libro> yaExiste = libroRepository.findByTituloAndAutor_Nombre(titulo, autor.getNombre());
            if (yaExiste.isPresent()) {
                System.out.println("ℹ El libro ya estaba registrado: " + titulo + " - " + autor.getNombre());
                return;
            }

            Libro libro = new Libro();
            libro.setTitulo(titulo);
            libro.setIdioma(idioma);
            libro.setDescargas(descargas);
            libro.setAutor(autor);

            libroRepository.save(libro);
            System.out.println("Libro guardado: " + titulo + " (" + idioma + "), autor: " + autor.getNombre());

        } catch (Exception e) {
            System.out.println("Error al consultar/guardar libro: " + e.getMessage());
        }
    }

    private Autor nuevoAutorDesconocido() {
        Autor a = new Autor();
        a.setNombre("Autor desconocido");
        a.setAnioNacimiento(null);
        a.setAnioMuerte(null);
        return a;
    }
}
