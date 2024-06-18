package org.idiologue.server.repository.binary;

import org.springframework.http.HttpHeaders;

public interface MultipartParserListener {

    void partStarted(HttpHeaders headers);
    void bodyBytesRead(byte[] bytes);

    void partEnded();

    void parseEnded();

}
