package dev.repositories;

import dev.database.models.Funko;
import dev.database.models.Modelo;
import dev.services.database.DatabaseManager;
import dev.services.generator.IdGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class FunkosReactiveRepoImplTest {

    private FunkosReactiveRepoImpl funkosReactiveRepo;

    @BeforeEach
    void setUp() {
        funkosReactiveRepo = FunkosReactiveRepoImpl.getInstance(DatabaseManager.getInstance(), IdGenerator.getInstance());
        DatabaseManager.getInstance().initTables().block();
    }


    @Test
    void findAll() {
        Funko funko = new Funko(UUID.randomUUID(), 1, "Funko", Modelo.OTROS, 10, LocalDate.now());
        Funko funko2 = new Funko(UUID.randomUUID(), 2, "Funko 2", Modelo.OTROS, 10, LocalDate.now());
        funkosReactiveRepo.save(funko).block();
        funkosReactiveRepo.save(funko2).block();
        assertEquals(2, funkosReactiveRepo.findAll().collectList().block().size());
    }

    @Test
    void findById() {
        Funko funko = new Funko(UUID.randomUUID(), 1, "Funko", Modelo.OTROS, 10, LocalDate.now());
        funkosReactiveRepo.save(funko).block();
        Funko dbFunko = funkosReactiveRepo.findById(funko.codigo()).block();
        assertEquals(funko.codigo(), dbFunko.codigo());
    }

    @Test
    void findByIdNoExists() {
        assertNull(funkosReactiveRepo.findById(UUID.randomUUID()).block());
    }

    @Test
    void save() {
        Funko funko = new Funko(UUID.randomUUID(), 1, "Funko", Modelo.OTROS, 10, LocalDate.now());
        funkosReactiveRepo.save(funko).block();
        assertEquals(funko.codigo(), funkosReactiveRepo.findById(funko.codigo()).block().codigo());
    }

    @Test
    void update() throws SQLException, IOException {
        Funko funko = new Funko(UUID.randomUUID(), 1, "Funko", Modelo.OTROS, 10, LocalDate.now());
        funkosReactiveRepo.save(funko).block();
        Funko updatedFunko = funko.withNombre("Funko 2");
        Optional<Funko> funkoOptional = funkosReactiveRepo.update(updatedFunko).blockOptional();
        assertAll(() -> {
            assertTrue(funkoOptional.isPresent());
            assertEquals(updatedFunko.nombre(), funkoOptional.get().nombre());
        });
    }

    @Test
    void delete() throws SQLException, IOException {
        Funko funko = new Funko(UUID.randomUUID(), 1, "Funko", Modelo.OTROS, 10, LocalDate.now());
        funkosReactiveRepo.save(funko).block();
        funkosReactiveRepo.delete(funko.codigo()).block();
        assertEquals(0, funkosReactiveRepo.findAll().collectList().block().size());
    }

    @Test
    void deleteNoExists() throws SQLException, IOException {
        assertFalse(funkosReactiveRepo.delete(UUID.randomUUID()).block());
    }

    @Test
    void deleteAll() {
        Funko funko = new Funko(UUID.randomUUID(), 1, "Funko", Modelo.OTROS, 10, LocalDate.now());
        Funko funko2 = new Funko(UUID.randomUUID(), 2, "Funko 2", Modelo.OTROS, 10, LocalDate.now());
        funkosReactiveRepo.save(funko).block();
        funkosReactiveRepo.save(funko2).block();
        funkosReactiveRepo.deleteAll().block();
        assertEquals(0, funkosReactiveRepo.findAll().collectList().block().size());
    }

    @Test
    void findByName() {
        Funko funko = new Funko(UUID.randomUUID(), 1, "Funko", Modelo.OTROS, 10, LocalDate.now());
        Funko funko2 = new Funko(UUID.randomUUID(), 2, "Funko 2", Modelo.OTROS, 10, LocalDate.now());
        funkosReactiveRepo.save(funko).block();
        funkosReactiveRepo.save(funko2).block();
        assertEquals(funko.codigo(), funkosReactiveRepo.findByName(funko.nombre()).block().codigo());
    }

    @Test
    void findByNameNoExists() {
        assertNull(funkosReactiveRepo.findByName("Funko").block());
    }
}