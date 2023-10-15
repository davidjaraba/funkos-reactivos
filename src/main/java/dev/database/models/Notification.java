package dev.database.models;

public record Notification(Tipo tipo, Funko funko) {
    public enum Tipo {
        NEW, UPDATE, DELETE
    }
}
