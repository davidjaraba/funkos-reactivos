package dev.database.models;

import dev.locale.EsLocale;

import java.time.LocalDate;
import java.util.UUID;

public record Funko(UUID codigo, long myid,String nombre, Modelo modelo, double precio, LocalDate fechaLanzamiento) {

    public Funko withNombre(String nombre) {
        return new Funko(codigo, myid, nombre, modelo, precio, fechaLanzamiento);
    }

    @Override
    public String toString() {

        return String.format("Código: %s\n MyId: %s\n Nombre: %s\nModelo: %s\nPrecio: %s\nFecha de lanzamiento: %s\n",
                codigo, myid, nombre, modelo, EsLocale.toLocalMoney(precio), EsLocale.toLocalDate(fechaLanzamiento));

    }

}
