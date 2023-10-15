package dev.services.funko;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.adapters.LocalDateAdapter;
import dev.database.models.Funko;
import dev.database.models.Modelo;
import dev.services.generator.IdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class FunkoAlmacenamientoServiceImpl implements FunkoAlmacenamientoService {
    private final Logger logger = LoggerFactory.getLogger(FunkoAlmacenamientoServiceImpl.class);
    private static FunkoAlmacenamientoService instance;
    private final IdGenerator idGenerator;

    public synchronized static FunkoAlmacenamientoService getInstance() {
        if (instance == null) {
            instance = new FunkoAlmacenamientoServiceImpl(IdGenerator.getInstance());
        }
        return instance;
    }

    private FunkoAlmacenamientoServiceImpl(IdGenerator idGenerator) {

        this.idGenerator = idGenerator;
    }

    @Override
    public Flux<Funko> readCsv() {
        final String filePath = "data" + File.separator + "funkos.csv";
        if (!Files.exists(Path.of(filePath))) {
            logger.error("El fichero " + filePath + " no existe");
            return Flux.empty();
        }
        final String delimiter = ",";
        return Flux.using(
                () -> new BufferedReader(new FileReader(filePath)),
                reader -> Flux.fromStream(reader.lines().skip(1).map(line -> {
                    String[] values = line.split(delimiter);
                    UUID uuid = UUID.fromString(values[0].substring(0, 35));
                    return new Funko(uuid, idGenerator.getAndIncrement(), values[1], Modelo.valueOf(values[2]), Double.parseDouble(values[3]), LocalDate.parse(values[4]));
                })),
                reader -> {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        logger.error("Error al cerrar el reader", e);
                    }
                });
    }

    @Override
    public Mono<Void> backup(String filePath) {
        Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).setPrettyPrinting().create();
        return readCsv().collectList().flatMap(funkos -> {
            try {
                Files.writeString(Path.of(filePath), gson.toJson(funkos));
                return Mono.empty();
            } catch (IOException e) {
                logger.error("Error al escribir el fichero " + filePath, e);
                return Mono.error(e);
            }
        });
    }
}
