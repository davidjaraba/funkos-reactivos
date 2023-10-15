package dev.services.funko;

import dev.database.models.Funko;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FunkoAlmacenamientoService {
    Flux<Funko> readCsv();

    Mono<Void> backup(String filePath);
}
