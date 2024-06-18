package org.idiologue.server.repository.binary.impl;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.idiologue.server.repository.binary.BinaryStorageRepository;
import org.idiologue.server.repository.binary.MultipartParser;
import org.idiologue.server.repository.binary.MultipartParserListener;
import org.idiologue.server.repository.binary.adapter.BinaryStorageAdapter;
import org.idiologue.server.repository.binary.adapter.WritableBinaryContent;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.OutputStream;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class BinaryStorageRepositoryImpl implements BinaryStorageRepository {

    private static final Logger LOG = LogManager.getLogger(BinaryStorageRepositoryImpl.class);

    private static final String hexFormat = "%02x";

    private BinaryStorageAdapter storageAdapter;

    public BinaryStorageRepositoryImpl(BinaryStorageAdapter storageAdapter) {
        this.storageAdapter = storageAdapter;
    }

    @Override
    public Mono<Void> store(String boundary, Flux<DataBuffer> bufferFlux) throws NoSuchAlgorithmException {

        MultipartParserListener listener = new MultipartParserListener() {

            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            DigestOutputStream dos;

            private WritableBinaryContent binaryContent;

            private OutputStream outputStream;

            @Override
            public void partStarted(HttpHeaders headers) {
                try {
                    binaryContent = storageAdapter.prepareContent();
                    outputStream = binaryContent.getOutputStream();
                    dos = new DigestOutputStream(outputStream, messageDigest);
                    LOG.info("Created temp file with headers {}", headers);
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

                        var digestBytes = messageDigest.digest();
                        var sb = new StringBuilder();
                        for (byte digestByte : digestBytes) {
                            sb.append(String.format(hexFormat, digestByte));
                        }
                        var shaHash = sb.toString();
                        var shaSplit = Splitter.fixedLength(2).split(shaHash);
                        List<String> shaParts = new ArrayList<>();
                        shaSplit.forEach(shaParts::add);
                        shaParts.add(shaHash);
                        var shaPath = Joiner.on("/").join(shaParts);
                        LOG.info("Parse ended with SHA-256 path {}", shaPath);

                        storageAdapter.commitContent(binaryContent, shaPath);
                        LOG.info("File closed");
                    } catch (IOException ioe) {
                        LOG.error(ioe);
                    }
                }
            }

            @Override
            public void parseEnded() {
               LOG.info("Parse ended");
            }

        };
        MultipartParser parser = new MultipartParser(boundary, listener);
        return parser.parse(bufferFlux);
    }

}
