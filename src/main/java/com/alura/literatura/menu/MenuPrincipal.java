package com.alura.literatura.menu;

import com.alura.literatura.Repository.AutorRepository;
import com.alura.literatura.Repository.LibroRepository;
import com.alura.literatura.service.LibroService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class MenuPrincipal implements CommandLineRunner {

    private final LibroService libroService;
    private final LibroRepository libroRepository;
    private final AutorRepository autorRepository;

    public MenuPrincipal(LibroService libroService, LibroRepository libroRepository, AutorRepository autorRepository) {
        this.libroService = libroService;
        this.libroRepository = libroRepository;
        this.autorRepository = autorRepository;
    }

    @Override
    public void run(String... args) {
        Scanner sc = new Scanner(System.in);
        int opcion = -1;

        while (opcion != 0) {
            System.out.println("\n===== Alura =====");
            System.out.println("1. Agregar libro desde Gutendex (API)"); // <- renombrada
            System.out.println("2. Listar libros registrados");
            System.out.println("3. Listar autores registrados");
            System.out.println("4. Listar autores vivos en un año");
            System.out.println("5. Listar libros por idioma");
            System.out.println("0. Salir");
            System.out.print("Elige una opción: ");

            if (!sc.hasNextInt()) {
                System.out.println("Opción inválida");
                sc.nextLine();
                continue;
            }

            opcion = sc.nextInt();
            sc.nextLine(); // limpiar salto de línea

            switch (opcion) {
                case 1 -> {
                    System.out.print("Ingresa un título para buscar en Gutendex: ");
                    String titulo = sc.nextLine();
                    libroService.agregarLibroDesdeApi(titulo);
                }
                case 2 -> libroRepository.findAll()
                        .forEach(l -> System.out.println("Libro: " + l.getTitulo() + " | " + l.getIdioma() + " | " + l.getAutor().getNombre()));
                case 3 -> autorRepository.findAll()
                        .forEach(a -> System.out.println("Autor: " + a.getNombre() + " (" + a.getAnioNacimiento() + " - " + a.getAnioMuerte() + ")"));
                case 4 -> {
                    System.out.print("Ingresa el año: ");
                    if (sc.hasNextInt()) {
                        int anio = sc.nextInt();
                        sc.nextLine();
                        autorRepository.findAutoresVivosEn(anio)
                                .forEach(a -> System.out.println("👤 " + a.getNombre()));
                    } else {
                        System.out.println("Año inválido");
                        sc.nextLine();
                    }
                }
                case 5 -> {
                    System.out.print("Ingresa el idioma (ej: en, es, fr): ");
                    String idioma = sc.nextLine();
                    libroRepository.findByIdiomaIgnoreCase(idioma)
                            .forEach(l -> System.out.println("Libro " + l.getTitulo() + " - " + l.getAutor().getNombre()));
                }
                case 0 -> System.out.println("Saliendo de LiterAlura...");
                default -> System.out.println("Opción no válida");
            }
        }
    }
}
