/*
 * Copyright 2007 The JA-SIG Collaborative. All rights reserved. See license
 * distributed with this file and available online at
 * http://www.ja-sig.org/products/cas/overview/license/
 */
package org.jasig.cas.adaptors.generic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.jasig.cas.authentication.handler.support.AbstractUsernamePasswordAuthenticationHandler;
import org.jasig.cas.authentication.principal.UsernamePasswordCredentials;
import org.springframework.core.io.Resource;

import javax.validation.constraints.NotNull;

/**
 * Class designed to read data from a file in the format of USERNAME SEPARATOR
 * PASSWORD that will go line by line and look for the username. If it finds the
 * username it will compare the supplied password (first put through a
 * PasswordTranslator) that is compared to the password provided in the file. If
 * there is a match, the user is authenticated. Note that the default password
 * translator is a plaintext password translator and the default separator is
 * "::" (without quotes).
 * 
 * @author Scott Battaglia
 * @version $Revision: 20191 $ $Date: 2010-03-14 00:05:58 -0500 (Sun, 14 Mar 2010) $
 * @since 3.0
 */
public class FileAuthenticationHandler extends
    AbstractUsernamePasswordAuthenticationHandler {

    /** The default separator in the file. */
    private static final String DEFAULT_SEPARATOR = "::";

    /** The separator to use. */
    @NotNull
    private String separator = DEFAULT_SEPARATOR;

    /** The filename to read the list of usernames from. */
    @NotNull
    private Resource fileName;

    protected final boolean authenticateUsernamePasswordInternal(final UsernamePasswordCredentials credentials) {
        BufferedReader bufferedReader = null;

        try {
            bufferedReader = new BufferedReader(new InputStreamReader(this.fileName.getInputStream()));
            String line = bufferedReader.readLine();
            while (line != null) {
                final String[] lineFields = line.split(this.separator);
                final String userName = lineFields[0];
                final String password = lineFields[1];

                final String transformedUsername = getPrincipalNameTransformer().transform(credentials.getUsername());
                if (transformedUsername.equals(userName)) {
                    if (this.getPasswordEncoder().encode(
                        credentials.getPassword()).equals(password)) {
                        return true;
                    }
                    break;
                }
                line = bufferedReader.readLine();
            }
        } catch (final Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (IOException e) {
                log.error(e.getMessage(),e);
            }
        }

        return false;
    }

    /**
     * @param fileName The fileName to set.
     */
    public final void setFileName(final Resource fileName) {
        this.fileName = fileName;
    }

    /**
     * @param separator The separator to set.
     */
    public final void setSeparator(final String separator) {
        this.separator = separator;
    }
}
