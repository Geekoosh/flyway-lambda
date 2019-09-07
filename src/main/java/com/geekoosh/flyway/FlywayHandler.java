package com.geekoosh.flyway;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.geekoosh.flyway.request.*;
import com.geekoosh.lambda.MigrationFilesException;
import com.geekoosh.lambda.MigrationFilesService;
import com.geekoosh.lambda.git.GitService;
import com.geekoosh.lambda.s3.S3Service;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FlywayHandler implements RequestHandler<Request, Response> {
    private static final Logger logger = LogManager.getLogger(FlywayHandler.class);

    private List<String> getMigrationFiles(List<String> folders) {
        ArrayList<String> files = new ArrayList<>();
        for(String folder : folders) {
            FileUtils.listFiles(new File(folder), new String[]{"sql"}, false)
                    .forEach(f -> files.add(f.getPath()));
        }
        return files;
    }

    @Override
    public Response handleRequest(Request input, Context context) {
        // TODO: S3 bucket support
        // TODO: Config support
        // TODO: MySQL support
        // TODO: Postgres support
        // TODO: Env vars support
        // TODO: SSM secrets support
        // TODO: migrate support
        // TODO: baseline support
        // TODO: Git clone or reuse with pull
        // TODO: log files based on suffix and prefix
        // TODO: dump configuration to log

        MigrationFilesService migrationFilesService = null;

        try {

            GitRequest gitRequest = GitRequest.build(input.getGitRequest());
            S3Request s3Request = S3Request.build(input.getS3Request());
            DBRequest dbRequest = DBRequest.build(input.getDbRequest());
            FlywayRequest flywayRequest = FlywayRequest.build(input.getFlywayRequest());


            GitService gitService = new GitService(
                    gitRequest.getGitRepository(),
                    gitRequest.getGitBranch(),
                    gitRequest.getUsername(),
                    gitRequest.getPassword(),
                    gitRequest.getReuseRepo(),
                    gitRequest.getFolders()
            );

            S3Service s3Service = new S3Service(
                    s3Request.getBucket(),
                    s3Request.getFolder()
            );

            if (s3Service.isValid()) {
                migrationFilesService = s3Service;
            } else if (gitService.isValid()) {
                migrationFilesService = gitService;
            } else {
                throw new RuntimeException("Both S3 and Git repositories missing configuration");
            }

            migrationFilesService.prepare();

            List<String> folders = migrationFilesService.getFolders();
            logger.info("Migration scripts from folders: " + String.join(",", folders));
            logger.info("Migration scripts: " + String.join(",", getMigrationFiles(folders)));

            FlywayService flywayService = new FlywayService(flywayRequest, dbRequest, folders);

            return new Response(flywayService.call());
        } catch(Exception e) {
            throw new RuntimeException(e);
        } finally {
            if(migrationFilesService != null) try {
                migrationFilesService.clean();
            } catch (MigrationFilesException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
