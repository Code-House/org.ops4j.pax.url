/*
 *  Copyright 2016 Grzegorz Grzybek
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.ops4j.pax.url.mvn;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.maven.settings.Profile;
import org.apache.maven.settings.Repository;
import org.apache.maven.settings.Settings;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.ops4j.pax.url.mvn.internal.AetherBasedResolver;
import org.ops4j.pax.url.mvn.internal.config.MavenConfigurationImpl;
import org.ops4j.util.property.PropertiesPropertyResolver;

import static org.junit.Assert.*;

/**
 * Test cases for connection and read timeouts
 */
public class AetherTimeoutTest {

    private static Server server;
    private static int port;

    private static ExecutorService pool = Executors.newFixedThreadPool(1);

    @BeforeClass
    public static void startJetty() throws Exception {
        server = new Server(0);
        server.setHandler(new AbstractHandler() {
            @Override
            public void handle(String target, Request baseRequest, HttpServletRequest request,
                               HttpServletResponse response) throws IOException, ServletException {
                try {
                    if (request.getRequestURI().endsWith(".jar")) {
                        // get simulated timeout value from ... version fragment
                        String[] split = request.getRequestURI().split("[-.]");
                        String versionWhichIsTimeout = split[split.length - 2];
                        try {
                            Thread.sleep(Integer.parseInt(versionWhichIsTimeout));
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            return;
                        }
                        response.setStatus(HttpServletResponse.SC_OK);
                        response.getOutputStream().write(0x42);
                    } else {
                        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    }
                } finally {
                    baseRequest.setHandled(true);
                }
            }
        });
        server.start();
        port = server.getConnectors()[0].getLocalPort();
    }

    @Test
    public void readTimeout() throws Exception {
        // case 1: resolution fails because of timeout
        //  - aether uses 500ms read timeout
        //  - server responds with 1000ms delay
        //  - we interrupt resolution after 2000ms in case read timeout in socket.setSoTimeout() is set to 0
        MavenConfigurationImpl mavenConfiguration = basicMavenConfiguration(500);
        mavenConfiguration.setSettings(settingsWithJettyRepository());
        Future<Boolean> task = pool.submit(new ResolveArtifactTask(mavenConfiguration, 1000));
        try {
            boolean resolved = task.get(2000, TimeUnit.MILLISECONDS);
            assertFalse("Should not be resolved due to read timeout", resolved);
        } catch (TimeoutException e) {
            task.cancel(true);
            fail("Should fail due to socket read timeout earlier, not due to future.get() timeout.");
        }

        // case 2: resolution doesn't fail, we're cancelling the task earlier
        //  - aether uses 6s read timeout
        //  - server responds with 3s delay
        //  - we interrupt resolution after 2s ensuring that Aether resolution didn't end on timeout yet
        mavenConfiguration = basicMavenConfiguration(6000);
        mavenConfiguration.setSettings(settingsWithJettyRepository());
        task = pool.submit(new ResolveArtifactTask(mavenConfiguration, 3000));
        boolean timedOut;
        try {
            task.get(2000, TimeUnit.MILLISECONDS);
            timedOut = false;
            fail("Task should not be completed yet");
        } catch (TimeoutException e) {
            timedOut = true;
            task.cancel(true);
        }
        assertTrue("Resolution task should be interrupted", timedOut);
    }

    @AfterClass
    public static void stopJetty() throws Exception {
        server.stop();
        pool.shutdown();
    }

    private MavenConfigurationImpl basicMavenConfiguration(int timeoutInMs) {
        Properties properties = new Properties();
        properties.setProperty("pid.localRepository", "target/" + UUID.randomUUID().toString());
        properties.setProperty("pid.timeout", Integer.toString(timeoutInMs));
        properties.setProperty("pid.globalChecksumPolicy", "ignore");
        return new MavenConfigurationImpl(new PropertiesPropertyResolver(properties), "pid");
    }

    private Settings settingsWithJettyRepository()
    {
        Settings settings = new Settings();
        Profile defaultProfile = new Profile();
        defaultProfile.setId("default");
        Repository repo1 = new Repository();
        repo1.setId("repo1");
        repo1.setUrl("http://localhost:" + port + "/repository");
        defaultProfile.addRepository(repo1);
        settings.addProfile(defaultProfile);
        settings.addActiveProfile("default");
        return settings;
    }

    /**
     * Task that performs Aether resolution with simulated server-side timeout
     */
    private static class ResolveArtifactTask implements Callable<Boolean> {

        private final MavenConfigurationImpl mavenConfiguration;
        private final int expectedTimeout;

        /**
         * @param mavenConfiguration
         * @param expectedTimeout this parameter will be used at server side to simulate read timeout
         */
        public ResolveArtifactTask(MavenConfigurationImpl mavenConfiguration, int expectedTimeout) {
            this.mavenConfiguration = mavenConfiguration;
            this.expectedTimeout = expectedTimeout;
        }

        @Override
        public Boolean call() throws Exception {
            AetherBasedResolver resolver = new AetherBasedResolver(mavenConfiguration);
            try {
                // version value will be used to simulate read timeout at server side
                File resolved = resolver.resolve("org.ops4j.pax.web",
                        "pax-web-api", "", "jar", Integer.toString(expectedTimeout));
                return resolved.isFile();
            } catch (IOException e) {
                assertTrue(e.getMessage().endsWith("Read timed out"));
                return false;
            } finally {
                resolver.close();
            }
        }
    }
}
