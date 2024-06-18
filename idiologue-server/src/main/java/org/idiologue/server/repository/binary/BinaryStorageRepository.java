package org.idiologue.server.repository.binary;

import org.springframework.core.io.buffer.DataBuffer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.security.NoSuchAlgorithmException;

public interface BinaryStorageRepository {

    Mono<Void> store(String boundary, Flux<DataBuffer> bufferFlux) throws NoSuchAlgorithmException;

}
