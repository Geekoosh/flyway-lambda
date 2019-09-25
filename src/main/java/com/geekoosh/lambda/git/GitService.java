package com.geekoosh.lambda.git;

import com.geekoosh.flyway.request.GitRequest;
import com.geekoosh.lambda.MigrationFilesException;
import com.geekoosh.lambda.MigrationFilesService;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.InitCommand;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.ConfigInvalidException;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.storage.file.FileBasedConfig;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.util.FS;
import org.eclipse.jgit.util.SystemReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class GitService implements MigrationFilesService{
    private static final Logger logger = LogManager.getLogger(GitService.class);
    private static final String basePath = "/tmp";
    private static Class<? extends CredentialsProvider> credentialsProviderClass;

    public static void setCredentialsProviderClass(Class<? extends CredentialsProvider> credentialsProviderClass) {
        GitService.credentialsProviderClass = credentialsProviderClass;
    }

    private File gitDirectory;
    private Git repo;
    private GitRequest gitRequest;

    public GitService(String localRepo, GitRequest gitRequest) {
        this.gitRequest = gitRequest;
        this.gitDirectory = new File(basePath + "/" + localRepo);
    }
    public GitService(GitRequest gitRequest) {
        this("gitrepo", gitRequest);
    }

    public CredentialsProvider credentialsProvider() {
        try {
            return GitService.credentialsProviderClass != null ?
                    GitService.credentialsProviderClass.newInstance() :
                    new UsernamePasswordCredentialsProvider(
                        gitRequest.getUsername(), gitRequest.getPassword()
                    );
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return new UsernamePasswordCredentialsProvider(
                gitRequest.getUsername(), gitRequest.getPassword()
        );
    }

    private String branchRef() {
        return gitRequest.getGitBranch() != null ?
                "refs/heads/" + gitRequest.getGitBranch() : "refs/heads/master";
    }

    public void cloneRepo() throws MigrationFilesException {
        if(gitDirectory.exists()) {
            try {
                FileUtils.deleteDirectory(gitDirectory);
            } catch (IOException e) {
                throw new MigrationFilesException("Failed deleting existing git directory", e);
            }
        }
        if(!gitDirectory.mkdir()) {
            throw new MigrationFilesException("Git directory creation failed");
        }
        logger.info("Fetching from " + gitRequest.getGitRepository());
        logger.info("Fetching from branch " + branchRef());

        CloneCommand cloneCmd = Git.cloneRepository();
        cloneCmd
                .setCredentialsProvider(credentialsProvider())
                .setURI(gitRequest.getGitRepository())
                .setBranchesToClone(Collections.singletonList(branchRef()))
                .setBranch(branchRef())
                .setDirectory(gitDirectory)
                .setRemote("origin");
        try {
            repo = cloneCmd.call();
        } catch (GitAPIException e) {
            throw new MigrationFilesException("Failed cloning repo", e);
        }
        checkout();
    }

    private Git getRepo() throws IOException {
        if(repo == null) {
            try {
                repo = Git.open(gitDirectory);
            } catch (IOException e) {
                logger.error("Failed opening git repo", e);
                throw e;
            }
        }
        return repo;
    }

    public void pullRepo() throws MigrationFilesException {
        try {
            PullCommand pullCommand = getRepo().pull();
            pullCommand.setRemoteBranchName(branchRef()).setRemote("origin");
            logger.info("Pulling from branch " + branchRef());

            pullCommand.call();
        } catch (GitAPIException | IOException e) {
            throw new MigrationFilesException("Failed pulling branch " + branchRef(), e);
        }
        checkout();
    }

    public void checkout() throws MigrationFilesException {
        if(gitRequest.getCommit() != null) {
            try {
                getRepo().checkout().setStartPoint(gitRequest.getCommit()).call();
            } catch (GitAPIException | IOException e) {
                throw new MigrationFilesException("Failed checking out commit " + gitRequest.getCommit(), e);
            }
        }
    }

    public void cloneOrPull() throws MigrationFilesException {
        if(gitDirectory.exists()) {
            pullRepo();
        } else {
            cloneRepo();
        }
    }

    public void removeRepo() throws MigrationFilesException {
        try {
            FileUtils.deleteDirectory(gitDirectory);
        } catch (IOException e) {
            logger.error("Failed deleting existing git directory", e);
            throw new MigrationFilesException("Failed deleting existing git directory", e);
        }
    }

    @Override
    public boolean isValid() {
        return this.gitRequest.getGitRepository() != null;
    }

    @Override
    public void prepare() throws MigrationFilesException {
        try {
            if (gitRequest.getReuseRepo()) {
                cloneOrPull();
            } else {
                cloneRepo();
            }
        } catch(Exception e) {
            throw new MigrationFilesException(e);
        }
    }

    @Override
    public List<String> getFolders() {
        return gitRequest.getFolders().size() == 0 ?
                Collections.singletonList(gitDirectory.getPath()) :
                    gitRequest.getFolders()
                    .stream().map(folder -> gitDirectory.getPath() + "/" + folder).collect(Collectors.toList());
    }

    @Override
    public void clean() throws MigrationFilesException {
        try {
            if (!gitRequest.getReuseRepo()) {
                removeRepo();
            }
        } catch(Exception e) {
            throw new MigrationFilesException(e);
        }
    }
}
