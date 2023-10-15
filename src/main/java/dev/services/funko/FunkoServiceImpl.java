package dev.services.funko;

import dev.database.models.Funko;
import dev.database.models.Modelo;
import dev.database.models.Notification;
import dev.exceptions.FunkoNoEncontradoException;
import dev.repositories.FunkosReactiveRepo;
import dev.services.cache.FunkosCache;
import dev.services.notification.FunkoNotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class FunkoServiceImpl implements FunkoService {
    private final Logger logger = LoggerFactory.getLogger(FunkoServiceImpl.class);
    private final FunkosReactiveRepo funkosReactiveRepo;
    private final FunkosCache<UUID, Funko> funkosCache;
    private final FunkoAlmacenamientoService funkoAlmacenamientoService;
    private final FunkoNotificationService funkoNotificationService;

    public FunkoServiceImpl(FunkosReactiveRepo funkosReactiveRepo, FunkosCache<UUID, Funko> funkosCache, FunkoAlmacenamientoService funkoAlmacenamientoService, FunkoNotificationService funkoNotificationService) {
        this.funkosReactiveRepo = funkosReactiveRepo;
        this.funkosCache = funkosCache;
        this.funkoAlmacenamientoService = funkoAlmacenamientoService;
        this.funkoNotificationService = funkoNotificationService;
    }

    @Override
    public Flux<Funko> findAll() throws SQLException, IOException {
        return funkosReactiveRepo.findAll();
    }

    @Override
    public Mono<Void> importCsv() {
        return funkoAlmacenamientoService.readCsv().flatMap(funko -> {
            try {
                return save(funko);
            } catch (SQLException | IOException e) {
                logger.error("Error al importar el funko " + funko, e);
                return Flux.error(e);
            }
        }).then();
    }

    @Override
    public Mono<Funko> findById(UUID id) throws SQLException, IOException {
        return funkosCache.get(id)
                .switchIfEmpty(
                        funkosReactiveRepo
                                .findById(id)
                                .flatMap(funko -> funkosCache.put(funko.codigo(), funko)
                                        .then(Mono.just(funko))
                                )
                )
                .switchIfEmpty(Mono.error(new FunkoNoEncontradoException("Funko con id " + id + " no encontrado")));
    }


    @Override
    public Mono<Funko> save(Funko funko) throws SQLException, IOException {
        Mono<Funko> save = funkosReactiveRepo.save(funko);
        funkoNotificationService.notify(new Notification(Notification.Tipo.NEW, funko));
        return save;
    }

    @Override
    public Mono<Boolean> delete(Funko funko) throws SQLException, IOException {
        Mono<Boolean> mono = funkosReactiveRepo.delete(funko.codigo()).doOnSuccess(aBoolean -> funkosCache.remove(funko.codigo()));
        funkoNotificationService.notify(new Notification(Notification.Tipo.DELETE, funko));
        return mono;
    }

    @Override
    public Mono<Funko> update(Funko funko) throws SQLException, IOException {
        Mono<Funko> update = funkosReactiveRepo.update(funko);
        funkoNotificationService.notify(new Notification(Notification.Tipo.UPDATE, funko));
        return update;
    }

    @Override
    public Mono<Funko> mostExpensiveFunko() throws SQLException, IOException {
        return findAll().sort(Comparator.comparingDouble(Funko::precio).reversed()).next();
    }

    @Override
    public Mono<Map<Modelo, List<Funko>>> groupedByModel() throws SQLException, IOException {
        return findAll().collect(Collectors.groupingBy(Funko::modelo));
    }

    @Override
    public Mono<Map<Modelo, Long>> countByModel() throws SQLException, IOException {
        return findAll().collect(Collectors.groupingBy(Funko::modelo, Collectors.counting()));
    }

    @Override
    public Flux<Funko> releasedIn2023() throws SQLException, IOException {
        return findAll().filter(funko -> funko.fechaLanzamiento().getYear() == 2023);
    }

    @Override
    public Mono<Double> averagePrice() throws SQLException, IOException {
        return findAll().collect(Collectors.averagingDouble(Funko::precio));
    }

    @Override
    public Mono<Long> stitchFunkosCount() throws SQLException, IOException {
        return findAll().count();
    }

    @Override
    public Flux<Funko> stitchFunkos() throws SQLException, IOException {
        return findAll().filter(funko -> funko.nombre().contains("Stitch"));
    }

    @Override
    public Mono<Void> backup() {
        return funkoAlmacenamientoService.backup("data" + File.separator + "funkos.json").doOnSubscribe(subscription -> logger.info("Iniciando backup en json")).doOnSuccess(aVoid -> logger.info("Backup en json finalizado en data" + File.separator + "funkos.json"));
    }
}
