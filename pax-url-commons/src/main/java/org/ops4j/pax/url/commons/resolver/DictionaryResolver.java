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
package org.ops4j.pax.url.commons.resolver;

import java.util.Dictionary;

/**
 * Resolves properties from a Dictionary.
 * TODO add unit tests
 *
 * @author Alin Dreghiciu
 * @since August 26, 2007
 */
public class DictionaryResolver
    implements Resolver
{

    /**
     * Dictionary to resolve properties from. Can be null.
     */
    private Dictionary m_properties;

    /**
     * Creates a property resolver.
     *
     * @param properties dictionary; optional
     */
    public DictionaryResolver( final Dictionary properties )
    {
        m_properties = properties;
    }

    /**
     * Sets the properties in use.
     *
     * @param properties dictionary of properties
     */
    public void setProperties( final Dictionary properties )
    {
        m_properties = properties;
    }

    /**
     * Resolves a property based on it's name .
     *
     * @param propertyName property name to be resolved
     *
     * @return value of property or null if property is not set or is empty.
     */
    public String get( final String propertyName )
    {
        String value = null;
        if( m_properties != null )
        {
            value = (String) m_properties.get( propertyName );
        }
        if( value != null && value.trim().length() == 0 )
        {
            value = null;
        }
        return value;
    }

}
