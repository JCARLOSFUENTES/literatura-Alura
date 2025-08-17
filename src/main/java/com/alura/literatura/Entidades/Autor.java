package com.alura.literatura.Entidades;
import jakarta.persistence.*;

@Entity
public class Autor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true) // Evitar autores duplicados por nombre
    private String nombre;

    private Integer anioNacimiento; // puede ser null
    private Integer anioMuerte;     // puede ser null

    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Integer getAnioNacimiento() { return anioNacimiento; }
    public void setAnioNacimiento(Integer anioNacimiento) { this.anioNacimiento = anioNacimiento; }

    public Integer getAnioMuerte() { return anioMuerte; }
    public void setAnioMuerte(Integer anioMuerte) { this.anioMuerte = anioMuerte; }
}
