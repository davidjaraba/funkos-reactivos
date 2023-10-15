package dev.services.funko;

import dev.database.models.Funko;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.sql.SQLException;
import java.util.UUID;

public interface FunkoService {
    Flux<Funko> findAll() throws SQLException, IOException;

    Mono<Funko> findById(UUID id) throws SQLException, IOException;

    Mono<Funko> save(Funko funko) throws SQLException, IOException;

    Mono<Boolean> delete(Funko funko) throws SQLException, IOException;

    Mono<Funko> update(Funko funko) throws SQLException, IOException;

    Mono<Void> backup();

}
