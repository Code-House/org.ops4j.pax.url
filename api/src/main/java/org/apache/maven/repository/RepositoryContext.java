package org.apache.maven.repository;

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

import java.util.Properties;

/**
 * @author Benjamin Bentmann
 */
public interface RepositoryContext
{

    boolean isOffline();

    String getChecksumPolicy();

    String getUpdatePolicy();

    LocalRepository getLocalRepository();

    WorkspaceReader getWorkspaceReader();

    TransferListener getTransferListener();

    Properties getSystemProperties();

    MirrorSelector getMirrorSelector();

    ProxySelector getProxySelector();

    AuthenticationSelector getAuthenticationSelector();

    // TODO: cache?

    // TODO: retry/error policy, i.e. handling of cached not-found/transfer errors 

}
