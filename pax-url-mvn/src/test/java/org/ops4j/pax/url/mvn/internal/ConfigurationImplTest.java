/*
 * Copyright 2007 Alin Dreghiciu.
 *
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
 * you may not use  this file  except in  compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ops4j.pax.url.mvn.internal;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import org.junit.Test;
import org.ops4j.pax.runner.commons.file.FileUtils;
import org.ops4j.pax.runner.commons.resolver.Resolver;
import org.ops4j.pax.url.mvn.internal.Configuration;
import org.ops4j.pax.url.mvn.internal.ConfigurationImpl;
import org.ops4j.pax.url.mvn.internal.Settings;

public class ConfigurationImplTest
{

    @Test( expected = IllegalArgumentException.class )
    public void constructorWithNullResolver()
    {
        new ConfigurationImpl( null );
    }

    @Test
    public void getCertificateCheck()
    {
        Resolver resolver = createMock( Resolver.class );
        expect( resolver.get( "org.ops4j.pax.runner.handler.mvn.certificateCheck" ) ).andReturn( "true" );
        replay( resolver );
        Configuration config = new ConfigurationImpl( resolver );
        assertEquals( "Certificate check", true, config.getCertificateCheck() );
        verify( resolver );
    }

    @Test
    public void getDefaultCertificateCheck()
    {
        Resolver resolver = createMock( Resolver.class );
        expect( resolver.get( "org.ops4j.pax.runner.handler.mvn.certificateCheck" ) ).andReturn( null );
        replay( resolver );
        Configuration config = new ConfigurationImpl( resolver );
        assertEquals( "Certificate check", false, config.getCertificateCheck() );
        verify( resolver );
    }

    @Test
    public void getSettingsAsURL()
        throws MalformedURLException
    {
        Resolver resolver = createMock( Resolver.class );
        expect( resolver.get( "org.ops4j.pax.runner.handler.mvn.settings" ) ).andReturn(
            "file:somewhere/settings.xml"
        );
        replay( resolver );
        Configuration config = new ConfigurationImpl( resolver );
        assertEquals( "Settings", new URL( "file:somewhere/settings.xml" ), config.getSettings() );
        verify( resolver );
    }

    @Test
    public void getSettingsAsFilePath()
        throws MalformedURLException
    {
        Resolver resolver = createMock( Resolver.class );
        File validSettings = FileUtils.getFileFromClasspath( "configuration/settings.xml" );
        expect( resolver.get( "org.ops4j.pax.runner.handler.mvn.settings" ) ).andReturn(
            validSettings.getAbsolutePath()
        );
        replay( resolver );
        Configuration config = new ConfigurationImpl( resolver );
        assertEquals( "Settings", validSettings.toURL(), config.getSettings() );
        verify( resolver );
    }

    /**
     * Test that a malformed url will not trigger an exception ( will be skipped)
     */
    @Test
    public void getSettingsAsMalformedURL()
        throws MalformedURLException
    {
        Resolver resolver = createMock( Resolver.class );
        expect( resolver.get( "org.ops4j.pax.runner.handler.mvn.settings" ) ).andReturn( "noprotocol:settings.xml" );
        replay( resolver );
        new ConfigurationImpl( resolver ).getSettings();
    }

    @Test
    public void getSettingsWithoutPropertySet()
        throws MalformedURLException
    {
        Resolver resolver = createMock( Resolver.class );
        expect( resolver.get( "org.ops4j.pax.runner.handler.mvn.settings" ) ).andReturn( null );
        replay( resolver );
        Configuration config = new ConfigurationImpl( resolver );
        assertEquals( "Settings", null, config.getSettings() );
        verify( resolver );
    }

    @Test
    public void getRepositoriesWithOneRepository()
        throws MalformedURLException
    {
        Resolver resolver = createMock( Resolver.class );
        expect( resolver.get( "org.ops4j.pax.runner.handler.mvn.repositories" ) ).andReturn( "file:repository1/" );
        expect( resolver.get( "org.ops4j.pax.runner.handler.mvn.localRepository" ) ).andReturn( null );
        replay( resolver );
        Configuration config = new ConfigurationImpl( resolver );
        List<URL> repositories = config.getRepositories();
        assertNotNull( "Repositories is null", repositories );
        assertEquals( "Repositories size", 1, repositories.size() );
        assertEquals( "Repository", new URL( "file:repository1/" ), repositories.get( 0 ) );
        verify( resolver );
    }

    @Test
    public void getRepositoriesWithoutSlash()
        throws MalformedURLException
    {
        Resolver resolver = createMock( Resolver.class );
        expect( resolver.get( "org.ops4j.pax.runner.handler.mvn.repositories" ) ).andReturn( "file:repository1" );
        expect( resolver.get( "org.ops4j.pax.runner.handler.mvn.localRepository" ) ).andReturn( null );
        replay( resolver );
        Configuration config = new ConfigurationImpl( resolver );
        List<URL> repositories = config.getRepositories();
        assertNotNull( "Repositories is null", repositories );
        assertEquals( "Repositories size", 1, repositories.size() );
        assertEquals( "Repository", new URL( "file:repository1/" ), repositories.get( 0 ) );
        verify( resolver );
    }

    @Test
    public void getRepositoriesWithSlash()
        throws MalformedURLException
    {
        Resolver resolver = createMock( Resolver.class );
        expect( resolver.get( "org.ops4j.pax.runner.handler.mvn.repositories" ) ).andReturn( "file:repository1/" );
        expect( resolver.get( "org.ops4j.pax.runner.handler.mvn.localRepository" ) ).andReturn( null );
        replay( resolver );
        Configuration config = new ConfigurationImpl( resolver );
        List<URL> repositories = config.getRepositories();
        assertNotNull( "Repositories is null", repositories );
        assertEquals( "Repositories size", 1, repositories.size() );
        assertEquals( "Repository", new URL( "file:repository1/" ), repositories.get( 0 ) );
        verify( resolver );
    }

    @Test
    public void getRepositoriesWithBackSlash()
        throws MalformedURLException
    {
        Resolver resolver = createMock( Resolver.class );
        expect( resolver.get( "org.ops4j.pax.runner.handler.mvn.repositories" ) ).andReturn( "file:repository1\\" );
        expect( resolver.get( "org.ops4j.pax.runner.handler.mvn.localRepository" ) ).andReturn( null );
        replay( resolver );
        Configuration config = new ConfigurationImpl( resolver );
        List<URL> repositories = config.getRepositories();
        assertNotNull( "Repositories is null", repositories );
        assertEquals( "Repositories size", 1, repositories.size() );
        assertEquals( "Repository", new URL( "file:repository1\\" ), repositories.get( 0 ) );
        verify( resolver );
    }

    @Test
    public void getRepositoriesWithMoreRepositories()
        throws MalformedURLException
    {
        Resolver resolver = createMock( Resolver.class );
        expect( resolver.get( "org.ops4j.pax.runner.handler.mvn.repositories" ) ).andReturn(
            "file:repository1/,file:repository2/"
        );
        expect( resolver.get( "org.ops4j.pax.runner.handler.mvn.localRepository" ) ).andReturn( null );
        replay( resolver );
        Configuration config = new ConfigurationImpl( resolver );
        List<URL> repositories = config.getRepositories();
        assertNotNull( "Repositories is null", repositories );
        assertEquals( "Repositories size", 2, repositories.size() );
        assertEquals( "Repository 1", new URL( "file:repository1/" ), repositories.get( 0 ) );
        assertEquals( "Repository 2", new URL( "file:repository2/" ), repositories.get( 1 ) );
        verify( resolver );
    }

    @Test
    public void getRepositoriesFromSettings()
        throws MalformedURLException
    {
        Resolver resolver = createMock( Resolver.class );
        expect( resolver.get( "org.ops4j.pax.runner.handler.mvn.repositories" ) ).andReturn( null );
        expect( resolver.get( "org.ops4j.pax.runner.handler.mvn.localRepository" ) ).andReturn( null );
        Settings settings = createMock( Settings.class );
        expect( settings.getRepositories() ).andReturn( "file:repository1/" );
        expect( settings.getLocalRepository() ).andReturn( null );
        replay( resolver, settings );
        ConfigurationImpl config = new ConfigurationImpl( resolver );
        config.setSettings( settings );
        List<URL> repositories = config.getRepositories();
        assertNotNull( "Repositories is null", repositories );
        assertEquals( "Repositories size", 1, repositories.size() );
        assertEquals( "Repository", new URL( "file:repository1/" ), repositories.get( 0 ) );
        verify( resolver );
    }

    @Test
    public void getRepositoriesFromConfigAndSettings()
        throws MalformedURLException
    {
        Resolver resolver = createMock( Resolver.class );
        expect( resolver.get( "org.ops4j.pax.runner.handler.mvn.repositories" ) ).andReturn( "+file:repository1/" );
        expect( resolver.get( "org.ops4j.pax.runner.handler.mvn.localRepository" ) ).andReturn( null );
        Settings settings = createMock( Settings.class );
        expect( settings.getRepositories() ).andReturn( "file:repository2/" );
        expect( settings.getLocalRepository() ).andReturn( null );
        replay( resolver, settings );
        ConfigurationImpl config = new ConfigurationImpl( resolver );
        config.setSettings( settings );
        List<URL> repositories = config.getRepositories();
        assertNotNull( "Repositories is null", repositories );
        assertEquals( "Repositories size", 2, repositories.size() );
        assertEquals( "Repository 1", new URL( "file:repository1/" ), repositories.get( 0 ) );
        assertEquals( "Repository 2", new URL( "file:repository2/" ), repositories.get( 1 ) );
        verify( resolver );
    }

    @Test
    public void getRepositoriesWithLocalRepository()
        throws MalformedURLException
    {
        Resolver resolver = createMock( Resolver.class );
        expect( resolver.get( "org.ops4j.pax.runner.handler.mvn.repositories" ) ).andReturn( "file:repository1/" );
        expect( resolver.get( "org.ops4j.pax.runner.handler.mvn.localRepository" ) ).andReturn(
            "file:localRepository/"
        );
        replay( resolver );
        Configuration config = new ConfigurationImpl( resolver );
        List<URL> repositories = config.getRepositories();
        assertNotNull( "Repositories is null", repositories );
        assertEquals( "Repositories size", 2, repositories.size() );
        assertEquals( "Local repository", new URL( "file:localRepository/" ), repositories.get( 0 ) );
        assertEquals( "Repository", new URL( "file:repository1/" ), repositories.get( 1 ) );
        verify( resolver );
    }

    @Test
    public void getLocalRepositoryAsURL()
        throws MalformedURLException
    {
        Resolver resolver = createMock( Resolver.class );
        expect( resolver.get( "org.ops4j.pax.runner.handler.mvn.localRepository" ) ).andReturn(
            "file:somewhere/localrepository/"
        );
        replay( resolver );
        Configuration config = new ConfigurationImpl( resolver );
        assertEquals( "Local repository", new URL( "file:somewhere/localrepository/" ), config.getLocalRepository() );
        verify( resolver );
    }

    @Test
    public void getLocalRepositoryAsURLWithoutSlash()
        throws MalformedURLException
    {
        Resolver resolver = createMock( Resolver.class );
        expect( resolver.get( "org.ops4j.pax.runner.handler.mvn.localRepository" ) ).andReturn(
            "file:somewhere/localrepository"
        );
        replay( resolver );
        Configuration config = new ConfigurationImpl( resolver );
        assertEquals( "Local repository", new URL( "file:somewhere/localrepository/" ), config.getLocalRepository() );
        verify( resolver );
    }
    @Test
    public void getLocalRepositoryAsURLWithBackSlash()
        throws MalformedURLException
    {
        Resolver resolver = createMock( Resolver.class );
        expect( resolver.get( "org.ops4j.pax.runner.handler.mvn.localRepository" ) ).andReturn(
            "file:somewhere/localrepository\\"
        );
        replay( resolver );
        Configuration config = new ConfigurationImpl( resolver );
        assertEquals( "Local repository", new URL( "file:somewhere/localrepository\\" ), config.getLocalRepository() );
        verify( resolver );
    }

    @Test
    public void getLocalRepositoryAsFilePath()
        throws MalformedURLException
    {
        Resolver resolver = createMock( Resolver.class );
        File valid = FileUtils.getFileFromClasspath( "configuration/localrepository" );
        expect( resolver.get( "org.ops4j.pax.runner.handler.mvn.localRepository" ) ).andReturn(
            valid.getAbsolutePath()
        );
        replay( resolver );
        Configuration config = new ConfigurationImpl( resolver );
        assertEquals( "Local repository", valid.toURL(), config.getLocalRepository() );
        verify( resolver );
    }

    @Test
    public void getLocalRepositoryAsFilePathWithoutSlash()
        throws MalformedURLException
    {
        Resolver resolver = createMock( Resolver.class );
        File valid = FileUtils.getFileFromClasspath( "configuration/localrepository" );
        File validWithSlash = FileUtils.getFileFromClasspath( "configuration/localrepository/" );
        expect( resolver.get( "org.ops4j.pax.runner.handler.mvn.localRepository" ) ).andReturn(
            valid.getAbsolutePath()
        );
        replay( resolver );
        Configuration config = new ConfigurationImpl( resolver );
        assertEquals( "Local repository", validWithSlash.toURL(), config.getLocalRepository() );
        verify( resolver );
    }

    /**
     * Test that an url that is malformed will not trigger an execption.
     */
    @Test
    public void getLocalRepositoryAsMalformedURL()
        throws MalformedURLException
    {
        Resolver resolver = createMock( Resolver.class );
        expect( resolver.get( "org.ops4j.pax.runner.handler.mvn.localRepository" ) ).andReturn(
            "noprotocol://localrepository"
        );
        replay( resolver );
        new ConfigurationImpl( resolver ).getLocalRepository();
    }

    /**
     * Test that a path to a file that does not exist will not trigger an exception.
     */
    @Test
    public void getLocalRepositoryToAnInexistentDirectory()
        throws MalformedURLException
    {
        Resolver resolver = createMock( Resolver.class );
        expect( resolver.get( "org.ops4j.pax.runner.handler.mvn.localRepository" ) ).andReturn(
            "c:/this/should/be/an/inexistent/directory"
        );
        replay( resolver );
        new ConfigurationImpl( resolver ).getLocalRepository();
    }

    @Test
    public void getLocalRepositoryWithoutPropertySet()
        throws MalformedURLException
    {
        Resolver resolver = createMock( Resolver.class );
        expect( resolver.get( "org.ops4j.pax.runner.handler.mvn.localRepository" ) ).andReturn( null );
        replay( resolver );
        Configuration config = new ConfigurationImpl( resolver );
        assertEquals( "Local repository", null, config.getLocalRepository() );
        verify( resolver );
    }

    @Test
    public void getLocalRepositoryFromSettings()
        throws MalformedURLException
    {
        Resolver resolver = createMock( Resolver.class );
        expect( resolver.get( "org.ops4j.pax.runner.handler.mvn.localRepository" ) ).andReturn( null );
        Settings settings = createMock( Settings.class );
        expect( settings.getLocalRepository() ).andReturn( "file:somewhere/localrepository/" );
        replay( resolver, settings );
        ConfigurationImpl config = new ConfigurationImpl( resolver );
        config.setSettings( settings );
        assertEquals( "Local repository", new URL( "file:somewhere/localrepository/" ), config.getLocalRepository() );
        verify( resolver, settings );
    }

}
