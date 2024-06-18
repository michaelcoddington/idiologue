package org.idiologue.server.repository.binary.adapter.impl;

import org.idiologue.server.repository.binary.adapter.WritableBinaryContent;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class BlockWritableBinaryContent implements WritableBinaryContent {

    private File file;

    public BlockWritableBinaryContent(File file) {
        this.file = file;
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        return new FileOutputStream(file);
    }

    File getFile() {
        return file;
    }

}
