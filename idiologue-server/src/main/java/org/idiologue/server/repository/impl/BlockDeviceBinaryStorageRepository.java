package org.idiologue.server.repository.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.idiologue.server.repository.BinaryStorageRepository;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

@Repository
public class BlockDeviceBinaryStorageRepository implements BinaryStorageRepository {

    private static final Logger LOG = LogManager.getLogger(BlockDeviceBinaryStorageRepository.class);

    private static final String hexFormat = "%02x";

    @Override
    public Mono<Void> store(String boundary, Flux<DataBuffer> bufferFlux) throws NoSuchAlgorithmException {

        MultipartParserListener listener = new MultipartParserListener() {

            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            DigestOutputStream dos;
            private FileOutputStream fos = null;

            @Override
            public void partStarted(HttpHeaders headers) {
                try {
                    File file = new File("/tmp/out-" + UUID.randomUUID().toString() + ".dat");
                    fos = new FileOutputStream(file);
                    dos = new DigestOutputStream(fos, messageDigest);
                    LOG.info("Created temp file {} with headers {}", file.getAbsolutePath(), headers);
                } catch (IOException ioe) {
                    LOG.error(ioe);
                }
            }

            @Override
            public void bodyBytesRead(byte[] bytes) {
                if (dos != null) {
                    try {
                        dos.write(bytes);
                        LOG.info("Wrote {} bytes", bytes.length);
                    } catch (Exception er) {
                        LOG.error(er);
                    }
                }
            }

            @Override
            public void partEnded() {
                if (dos != null) {
                    try {
                        dos.flush();
                        dos.close();
                        LOG.info("File closed");
                    } catch (IOException ioe) {
                        LOG.error(ioe);
                    }
                }
            }

            @Override
            public void parseEnded() {
                var digestBytes = messageDigest.digest();
                var sb = new StringBuffer();
                for (int i = 0; i < digestBytes.length; i++) {
                    sb.append(String.format(hexFormat, digestBytes[i]));
                }
                var shaHash = sb.toString();
                LOG.info("Parse ended with SHA-256 digest " + shaHash);
            }
        };
        MultipartParser parser = new MultipartParser(boundary, listener);
        return parser.parse(bufferFlux);
    }

}
