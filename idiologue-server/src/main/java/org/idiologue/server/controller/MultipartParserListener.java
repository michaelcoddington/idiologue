package org.idiologue.server.controller;

import org.springframework.http.HttpHeaders;

public interface MultipartParserListener {

    void partStarted(HttpHeaders headers);
    void bodyBytesRead(byte[] bytes);

    void partEnded();

}
