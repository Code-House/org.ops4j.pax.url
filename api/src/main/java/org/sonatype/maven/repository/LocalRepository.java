package org.sonatype.maven.repository;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.File;

/**
 * A repository on the local file system used to cache contents of remote repositories and to store locally built
 * artifacts.
 * 
 * @author Benjamin Bentmann
 */
public class LocalRepository
    implements ArtifactRepository
{

    private File basedir;

    private String type = "";

    /**
     * Creates a new local repository with the specified base directory and unknown type.
     * 
     * @param basedir The base directory of the repository, may be {@code null}.
     */
    public LocalRepository( String basedir )
    {
        this( ( basedir != null ) ? new File( basedir ) : null, "" );
    }

    /**
     * Creates a new local repository with the specified base directory and unknown type.
     * 
     * @param basedir The base directory of the repository, may be {@code null}.
     */
    public LocalRepository( File basedir )
    {
        this( basedir, "" );
    }

    /**
     * Creates a new local repository with the specified properties.
     * 
     * @param basedir The base directory of the repository, may be {@code null}.
     * @param type The type of the repository, may be {@code null}.
     */
    public LocalRepository( File basedir, String type )
    {
        setBasedir( basedir );
        setType( type );
    }

    public String getContentType()
    {
        return type;
    }

    private LocalRepository setType( String type )
    {
        this.type = ( type != null ) ? type : "";
        return this;
    }

    public String getId()
    {
        return "local";
    }

    /**
     * Gets the base directory of the repository.
     * 
     * @return The base directory or {@code null} if none.
     */
    public File getBasedir()
    {
        return basedir;
    }

    private LocalRepository setBasedir( File basedir )
    {
        this.basedir = basedir;
        return this;
    }

    @Override
    public String toString()
    {
        return getBasedir().getAbsolutePath() + " (" + getContentType() + ")";
    }

    @Override
    public boolean equals( Object obj )
    {
        if ( this == obj )
        {
            return true;
        }
        if ( obj == null || !getClass().equals( obj.getClass() ) )
        {
            return false;
        }

        LocalRepository that = (LocalRepository) obj;

        return eq( basedir, that.basedir ) && eq( type, that.type );
    }

    private static <T> boolean eq( T s1, T s2 )
    {
        return s1 != null ? s1.equals( s2 ) : s2 == null;
    }

    @Override
    public int hashCode()
    {
        int hash = 17;
        hash = hash * 31 + hash( basedir );
        hash = hash * 31 + hash( type );
        return hash;
    }

    private static int hash( Object obj )
    {
        return obj != null ? obj.hashCode() : 0;
    }

}
