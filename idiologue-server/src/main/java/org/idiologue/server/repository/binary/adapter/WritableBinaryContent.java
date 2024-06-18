package org.idiologue.server.repository.binary.adapter;

import java.io.IOException;
import java.io.OutputStream;

public interface WritableBinaryContent {

    OutputStream getOutputStream() throws IOException;

}
