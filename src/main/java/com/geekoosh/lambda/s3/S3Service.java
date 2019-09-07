package com.geekoosh.lambda.s3;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.transfer.Download;
import com.amazonaws.services.s3.transfer.Transfer;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.geekoosh.lambda.MigrationFilesException;
import com.geekoosh.lambda.MigrationFilesService;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class S3Service implements MigrationFilesService {
    private static final Logger logger = LogManager.getLogger(S3Service.class);
    private static final String basePath = "/tmp";

    private String bucket;
    private String folder;
    private File dataDirectory;

    public static void waitForCompletion(Transfer xfer) throws MigrationFilesException {
        try {
            xfer.waitForCompletion();
        } catch (AmazonServiceException e) {
            throw new MigrationFilesException("Amazon service error", e);
        } catch (AmazonClientException e) {
            throw new MigrationFilesException("Amazon client error", e);
        } catch (InterruptedException e) {
            throw new MigrationFilesException("Transfer interrupted", e);
        }
    }

    public S3Service(String localRepo, String bucket, String folder) {
        this.bucket = bucket;
        this.folder = folder;
        this.dataDirectory = new File(basePath + "/" + localRepo);
    }
    public S3Service(String bucket, String folder) {
        this(bucket, bucket, folder);
    }
    public void download() throws MigrationFilesException {
        TransferManager xfer_mgr = TransferManagerBuilder.standard().build();
        Download d = xfer_mgr.download(bucket, folder, dataDirectory);
        S3Service.waitForCompletion(d);
        Transfer.TransferState xfer_state = d.getState();
        logger.info(xfer_state);
        xfer_mgr.shutdownNow();
    }

    @Override
    public boolean isValid() {
        return this.bucket != null;
    }

    @Override
    public void prepare() throws MigrationFilesException {
        download();
    }

    @Override
    public List<String> getFolders() {
        return Collections.singletonList(this.dataDirectory.getPath());
    }

    @Override
    public void clean() throws MigrationFilesException {
        try {
            FileUtils.deleteDirectory(dataDirectory);
        } catch (IOException e) {
            throw new MigrationFilesException("Failed deleting existing s3 directory", e);
        }
    }
}
