package org.idiologue.server.repository.binary.adapter.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.idiologue.server.repository.binary.adapter.WritableBinaryContent;
import org.idiologue.server.repository.binary.adapter.BinaryStorageAdapter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.InputStream;
import java.util.UUID;

@Component
@ConditionalOnProperty(value = "binary.storage.strategy", havingValue = "block")
public class BlockBinaryStorageAdapter implements BinaryStorageAdapter {

    private static final Logger LOG = LogManager.getLogger(BlockBinaryStorageAdapter.class);

    private String storageLocation;

    public BlockBinaryStorageAdapter(@Value("${binary.storage.block.location}") String storageLocation) {
        this.storageLocation = storageLocation;
    }

    @Override
    public WritableBinaryContent prepareContent() {
        File outputFile;
        do {
            String uuid = UUID.randomUUID().toString();
            outputFile = new File(storageLocation, uuid);
        } while (outputFile.exists());

        LOG.debug("Created temp block writable content at {}", outputFile.getAbsolutePath());
        return new BlockWritableBinaryContent(outputFile);
    }

    @Override
    public boolean commitContent(WritableBinaryContent content, String hashPath) {
        LOG.info("Commit binary content to hash path {}", hashPath);
        BlockWritableBinaryContent blockContent = (BlockWritableBinaryContent) content;
        File f = blockContent.getFile();

        File targetFile = new File(storageLocation, hashPath);
        if (targetFile.exists()) {
            LOG.debug("Target file {} exists", targetFile.getAbsolutePath());
            boolean deleted = f.delete();
            LOG.debug("Deleted temp file {}: {}", f.getAbsolutePath(), deleted);
            return false;
        } else {
            File parent = targetFile.getParentFile();
            parent.mkdirs();
            return f.renameTo(targetFile);
        }
    }

    @Override
    public InputStream open(String relativePath) {
        throw new RuntimeException("not implemented");
    }

}
