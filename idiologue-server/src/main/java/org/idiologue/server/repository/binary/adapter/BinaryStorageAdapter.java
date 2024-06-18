package org.idiologue.server.repository.binary.adapter;

import java.io.InputStream;

public interface BinaryStorageAdapter {

    /**
     * Prepares an object for writing binary data.
     */
    WritableBinaryContent prepareContent();

    /**
     * Commits binary content to a permanent location for that content.
     * If there is already content at that location, the existing content will be preserved.
     * @return true if the content was moved to the given location, false if that location already exists
     */
    boolean commitContent(WritableBinaryContent content, String relativePath);

    InputStream open(String relativePath);

}
