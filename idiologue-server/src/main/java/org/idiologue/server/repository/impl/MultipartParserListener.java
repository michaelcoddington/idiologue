package org.idiologue.server.repository.impl;

import org.springframework.http.HttpHeaders;

public interface MultipartParserListener {

    void partStarted(HttpHeaders headers);
    void bodyBytesRead(byte[] bytes);

    void partEnded();

    void parseEnded();

}
