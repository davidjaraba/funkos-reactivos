package dev.repositories;

import dev.database.models.Funko;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface FunkosReactiveRepo extends CRUDRepo<Funko, UUID>{

    Mono<Funko> findByName(String name);

}
