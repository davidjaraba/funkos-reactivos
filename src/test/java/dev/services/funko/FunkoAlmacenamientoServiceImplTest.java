package dev.services.funko;

import dev.database.models.Funko;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FunkoAlmacenamientoServiceImplTest {

    private FunkoAlmacenamientoService funkoAlmacenamientoService;

    @BeforeEach
    void setUp() {
        funkoAlmacenamientoService = FunkoAlmacenamientoServiceImpl.getInstance();
    }

    @Test
    void readCsv() {
        List<Funko> funkoFlux = funkoAlmacenamientoService.readCsv().collectList().block();
        assertTrue(!funkoFlux.isEmpty());
    }

    @Test
    void backup() {
        String filePath = "data" + File.separator + "funkos.test.json";
        funkoAlmacenamientoService.backup(filePath).block();
        assertTrue(Files.exists(Path.of(filePath)));
    }
}