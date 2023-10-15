package dev.services.funko;

import dev.database.models.Funko;
import reactor.core.publisher.Flux;

public interface FunkoAlmacenamientoService {
    Flux<Funko> readCsv();
}
