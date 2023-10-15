package dev;

import dev.controllers.FunkoController;
import dev.database.models.Funko;
import dev.repositories.FunkosReactiveRepoImpl;
import dev.services.cache.FunkosCache;
import dev.services.cache.FunkosCacheImpl;
import dev.services.database.DatabaseManager;
import dev.services.funko.FunkoAlmacenamientoService;
import dev.services.funko.FunkoAlmacenamientoServiceImpl;
import dev.services.funko.FunkoService;
import dev.services.funko.FunkoServiceImpl;
import dev.services.generator.IdGenerator;
import dev.services.notification.FunkoNotificationService;
import dev.services.notification.FunkoNotificationServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.util.UUID;

public class Main {
    public static void main(String[] args) throws SQLException, IOException {
        Logger logger = LoggerFactory.getLogger(Main.class);
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        IdGenerator idGenerator = IdGenerator.getInstance();
        FunkoAlmacenamientoService funkoAlmacenamientoService = FunkoAlmacenamientoServiceImpl.getInstance();
        FunkosCache<UUID, Funko> funkosCache = new FunkosCacheImpl();
        FunkosReactiveRepoImpl funkoReactiveRepository = FunkosReactiveRepoImpl.getInstance(databaseManager, idGenerator);
        FunkoNotificationService funkoNotificationService = FunkoNotificationServiceImpl.getInstance();
        funkoNotificationService.getNotifications().subscribe(notification -> logger.info("Notificación: " + notification));
        FunkoService funkoService = new FunkoServiceImpl(funkoReactiveRepository, funkosCache, funkoAlmacenamientoService, funkoNotificationService);
        FunkoController funkoController = new FunkoController(funkoService);

        funkoController.importCsv().block();

        funkoController.findAll().subscribe(funkos -> logger.info("Funkos: " + funkos));

        funkoController.mostExpensiveFunko().subscribe(funko -> logger.info("Funko más caro: " + funko));

        funkoController.groupedByModel().subscribe(funkos -> logger.info("Funkos agrupados por modelo: " + funkos));

        funkoController.countByModel().subscribe(funkos -> logger.info("Funkos contados por modelo: " + funkos));

        funkoController.releasedIn2023().subscribe(funkos -> logger.info("Funkos lanzados en 2023: " + funkos));

        funkoController.averagePrice().subscribe(funkos -> logger.info("Precio medio de los funkos: " + funkos));

        funkoController.stitchFunkosCount().subscribe(funkos -> logger.info("Funkos de Stitch contados: " + funkos));

        funkoController.stitchFunkos().subscribe(funkos -> logger.info("Funkos de Stitch: " + funkos));
        funkoController.backup();
        funkosCache.shutdown();
    }
}