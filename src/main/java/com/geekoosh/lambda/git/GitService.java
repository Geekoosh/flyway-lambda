package com.geekoosh.lambda.git;

import com.geekoosh.flyway.request.GitRequest;
import com.geekoosh.lambda.MigrationFilesException;
import com.geekoosh.lambda.MigrationFilesService;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class GitService implements MigrationFilesService{
    private static final Logger logger = LogManager.getLogger(GitService.class);
    private static final String basePath = "/tmp";

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
                .setCredentialsProvider(new UsernamePasswordCredentialsProvider(
                        gitRequest.getUsername(), gitRequest.getPassword()
                ))
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
