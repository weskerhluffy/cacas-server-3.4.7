/*
 * Copyright 2007 The JA-SIG Collaborative. All rights reserved. See license
 * distributed with this file and available online at
 * http://www.ja-sig.org/products/cas/overview/license/
 */
package org.jasig.cas.adaptors.radius;

import org.jasig.cas.authentication.principal.UsernamePasswordCredentials;

/**
 * Interface representing a Radius Server.
 * 
 * @author Scott Battaglia
 * @version $Revision: 14064 $ $Date: 2007-06-10 09:17:55 -0400 (Sun, 10 Jun 2007) $
 * @since 3.1
 */
public interface RadiusServer {

    /**
     * Method to authenticate a set of credentials.
     * 
     * @param credentials the credentials to authenticate.
     * @return true if authenticated, false otherwise.
     */
    boolean authenticate(UsernamePasswordCredentials credentials);

}
