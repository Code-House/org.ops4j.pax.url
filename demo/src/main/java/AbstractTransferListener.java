

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.PrintStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import org.apache.maven.repository.TransferEvent;
import org.apache.maven.repository.TransferListener;
import org.apache.maven.repository.TransferResource;

public abstract class AbstractTransferListener
    implements TransferListener
{

    protected PrintStream out;

    protected AbstractTransferListener( PrintStream out )
    {
        this.out = ( out != null ) ? out : System.out;
    }

    public void transferInitiated( TransferEvent event )
    {
        String message = event.getRequestType() == TransferEvent.RequestType.PUT ? "Uploading" : "Downloading";

        out.println( message + ": " + event.getResource().getRepositoryUrl() + event.getResource().getResourceName() );
    }

    public void transferStarted( TransferEvent event )
    {
        // to be overriden by sub classes
    }

    public void transferProgressed( TransferEvent event )
    {
        // to be overriden by sub classes
    }

    public void transferSucceeded( TransferEvent event )
    {
        TransferResource resource = event.getResource();
        long contentLength = event.getTransferredBytes();
        if ( contentLength >= 0 )
        {
            String type = ( event.getRequestType() == TransferEvent.RequestType.PUT ? "Uploaded" : "Downloaded" );
            String len = contentLength >= 1024 ? toKB( contentLength ) + " KB" : contentLength + " B";

            String throughput = "";
            long duration = System.currentTimeMillis() - resource.getTransferStartTime();
            if ( duration > 0 )
            {
                DecimalFormat format = new DecimalFormat( "0.0", new DecimalFormatSymbols( Locale.ENGLISH ) );
                double kbPerSec = ( contentLength / 1024.0 ) / ( duration / 1000.0 );
                throughput = " at " + format.format( kbPerSec ) + " KB/sec";
            }

            out.println( type + ": " + resource.getRepositoryUrl() + resource.getResourceName() + " (" + len
                + throughput + ")" );
        }
    }

    public void transferFailed( TransferEvent event )
    {
        event.getException().printStackTrace( out );
    }

    public void transferCorrupted( TransferEvent event )
    {
        event.getException().printStackTrace( out );
    }

    protected long toKB( long bytes )
    {
        return ( bytes + 1023 ) / 1024;
    }

}
