package com.geekoosh.flyway;
import com.geekoosh.lambda.git.GitService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.dircache.DirCache;
import org.eclipse.jgit.http.server.GitServlet;
import org.eclipse.jgit.junit.TestRepository;
import org.eclipse.jgit.junit.http.AppServer;
import org.eclipse.jgit.junit.http.HttpTestCase;
import org.eclipse.jgit.lib.ConfigConstants;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.pack.PackConfig;
import org.eclipse.jgit.transport.*;
import org.eclipse.jgit.transport.resolver.FileResolver;
import org.eclipse.jgit.transport.resolver.ReceivePackFactory;
import org.eclipse.jgit.transport.resolver.ServiceNotAuthorizedException;
import org.eclipse.jgit.transport.resolver.ServiceNotEnabledException;
import org.eclipse.jgit.util.HttpSupport;
import org.junit.rules.TemporaryFolder;

import javax.net.ssl.*;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.EnumSet;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

public class GitSSLTestCase extends HttpTestCase {

    private TestRepository<Repository> src;

    public TemporaryFolder folder;

    private URIish remoteURI;

    private URIish secureURI;

    public String getRepoUrl() {
        return secureURI.toString();
    }

    public class GitFile {
        private File file;
        private String destPath;

        public GitFile(URL resource, String destPath) {
            this.file = new File(resource.getFile());
            this.destPath = destPath;
        }

        public File getFile() {
            return file;
        }

        public String getDestPath() {
            return destPath;
        }
    }

    private static void disableSslVerification() {
        try
        {
            // Create a trust manager that does not validate certificate chains
            TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }
            };

            // Install the all-trusting trust manager
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            // Create all-trusting host name verifier
            HostnameVerifier allHostsValid = (hostname, session) -> true;

            // Install the all-trusting host verifier
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        disableSslVerification();

        folder = new TemporaryFolder();
        folder.create();

        src = createTestRepository();
        src.getRepository()
                .getConfig()
                .setBoolean(ConfigConstants.CONFIG_CORE_SECTION, null,
                        ConfigConstants.CONFIG_KEY_LOGALLREFUPDATES, true);
        final String srcName = src.getRepository().getDirectory().getName();

        GitServlet gs = new GitServlet();
        /*gs.setReceivePackFactory((req, db) -> {
            ReceivePack pack = new ReceivePack(db);
            return pack;
        });*/
        /*FileResolver<HttpServletRequest> resolver = new FileResolver<>();
        gs.setRepositoryResolver(resolver);*/
        gs.setRepositoryResolver(new TestRepositoryResolver(src, srcName));
        ServletContextHandler app = server.addContext("/git");
        app.addServlet(new ServletHolder(gs), "/*");
        server.authBasic(app);
        server.setUp();

        //ServletContextHandler app = addNormalContext(gs, src, srcName);


        server.setUp();

        remoteURI = toURIish(app, srcName);
        secureURI = new URIish(rewriteUrl(remoteURI.toString(), "https",
                server.getSecurePort()));

        //GitService.setCredentialsProviderClass(GitCredentialsProviderMock.class);

        /*PackConfig packConfig = new PackConfig();
        final org.eclipse.jgit.transport.Daemon d;
        d = new org.eclipse.jgit.transport.Daemon(new InetSocketAddress(org.eclipse.jgit.transport.Daemon.DEFAULT_PORT));
        d.setPackConfig(packConfig);*/
    }

    @Override
    public void tearDown() throws Exception {
        folder.delete();
        super.tearDown();
    }


    @Override
    protected AppServer createServer() {
        return new AppServer(0, 0);
    }

    /*public void addFilesToMaster(List<GitFile> files) throws Exception {
        TestRepository.CommitBuilder commit = src.commit();
        for(GitFile file : files) {
            commit.add(file.getPath(), file.getContent());
        }
        RevCommit revCommit = commit.create();
        src.update(master, revCommit);
    }*/

    public void pushFilesToMaster(List<GitFile> files) throws Exception {
        CredentialsProvider credentialsProvider = new UsernamePasswordCredentialsProvider(AppServer.username, AppServer.password); //new GitCredentialsProviderMock();
        Git repo = Git.cloneRepository()
                .setCredentialsProvider(credentialsProvider)
                .setDirectory(folder.getRoot())
                .setURI(getRepoUrl())
                .setBranch(master)
                .setRemote("origin")
                .call();
        AddCommand addCommand = repo.add();
        for(GitFile f : files) {
            Path destPath = Paths.get(folder.getRoot().getPath(), f.getDestPath());
            new File(destPath.toString()).mkdirs();
            Files.copy(Paths.get(f.getFile().getPath()), destPath, StandardCopyOption.REPLACE_EXISTING);
            addCommand.addFilepattern(f.getDestPath());
        }
        addCommand.call();
        repo.commit().setMessage("committing").call();
        repo.push().setRemote("origin").setCredentialsProvider(credentialsProvider).call();
    }

    private ServletContextHandler addNormalContext(GitServlet gs, TestRepository<Repository> src, String srcName) {
        ServletContextHandler app = server.addContext("/git");
        app.addFilter(new FilterHolder(new Filter() {

            @Override
            public void init(FilterConfig filterConfig)
                    throws ServletException {
                // empty
            }

            // Redirects http to https for requests containing "/https/".
            @Override
            public void doFilter(ServletRequest request,
                                 ServletResponse response, FilterChain chain)
                    throws IOException, ServletException {
                final HttpServletResponse httpServletResponse = (HttpServletResponse) response;
                final HttpServletRequest httpServletRequest = (HttpServletRequest) request;
                final StringBuffer fullUrl = httpServletRequest.getRequestURL();
                if (httpServletRequest.getQueryString() != null) {
                    fullUrl.append("?")
                            .append(httpServletRequest.getQueryString());
                }
                String urlString = rewriteUrl(fullUrl.toString(), "https",
                        server.getSecurePort());
                httpServletResponse
                        .setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
                httpServletResponse.setHeader(HttpSupport.HDR_LOCATION,
                        urlString.replace("/https/", "/"));
            }

            @Override
            public void destroy() {
                // empty
            }
        }), "/https/*", EnumSet.of(DispatcherType.REQUEST));
        app.addFilter(new FilterHolder(new Filter() {

            @Override
            public void init(FilterConfig filterConfig)
                    throws ServletException {
                // empty
            }

            // Redirects https back to http for requests containing "/back/".
            @Override
            public void doFilter(ServletRequest request,
                                 ServletResponse response, FilterChain chain)
                    throws IOException, ServletException {
                final HttpServletResponse httpServletResponse = (HttpServletResponse) response;
                final HttpServletRequest httpServletRequest = (HttpServletRequest) request;
                final StringBuffer fullUrl = httpServletRequest.getRequestURL();
                if (httpServletRequest.getQueryString() != null) {
                    fullUrl.append("?")
                            .append(httpServletRequest.getQueryString());
                }
                String urlString = rewriteUrl(fullUrl.toString(), "http",
                        server.getPort());
                httpServletResponse
                        .setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
                httpServletResponse.setHeader(HttpSupport.HDR_LOCATION,
                        urlString.replace("/back/", "/"));
            }

            @Override
            public void destroy() {
                // empty
            }
        }), "/back/*", EnumSet.of(DispatcherType.REQUEST));
        gs.setRepositoryResolver(new TestRepositoryResolver(src, srcName));
        app.addServlet(new ServletHolder(gs), "/*");
        return app;
    }
}
