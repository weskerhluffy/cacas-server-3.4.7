/*
 * Copyright 2007 The JA-SIG Collaborative. All rights reserved. See license
 * distributed with this file and available online at
 * http://www.ja-sig.org/products/cas/overview/license/
 */
package org.jasig.cas;

import org.jasig.cas.ticket.ExpirationPolicy;
import org.jasig.cas.ticket.TicketException;
import org.jasig.cas.ticket.TicketState;
import org.jasig.cas.ticket.support.MultiTimeUseOrTimeoutExpirationPolicy;
import org.jasig.cas.ticket.support.NeverExpiresExpirationPolicy;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Scott Battaglia
 * @version $Revision: 19808 $ $Date: 2010-01-21 09:37:10 -0500 (Thu, 21 Jan 2010) $
 * @since 3.0
 */
public class CentralAuthenticationServiceImplTests extends AbstractCentralAuthenticationServiceTest {

    @Test
    public void testBadCredentialsOnTicketGrantingTicketCreation() {
        try {
            getCentralAuthenticationService().createTicketGrantingTicket(
                TestUtils.getCredentialsWithDifferentUsernameAndPassword());
            fail(TestUtils.CONST_EXCEPTION_EXPECTED);
        } catch (TicketException e) {
            // nothing to do here, exception is expected.
        }
    }

    @Test
    public void testGoodCredentialsOnTicketGrantingTicketCreation() {
        try {
            assertNotNull(getCentralAuthenticationService()
                .createTicketGrantingTicket(
                    TestUtils.getCredentialsWithSameUsernameAndPassword()));
        } catch (TicketException e) {
            fail(TestUtils.CONST_EXCEPTION_NON_EXPECTED);
        }
    }

    @Test
    public void testDestroyTicketGrantingTicketWithNonExistantTicket() {
        getCentralAuthenticationService().destroyTicketGrantingTicket("test");
    }

    @Test
    public void testDestroyTicketGrantingTicketWithValidTicket()
        throws TicketException {
        final String ticketId = getCentralAuthenticationService()
            .createTicketGrantingTicket(
                TestUtils.getCredentialsWithSameUsernameAndPassword());
        getCentralAuthenticationService().destroyTicketGrantingTicket(ticketId);
    }

    @Test
    public void testDestroyTicketGrantingTicketWithInvalidTicket()
        throws TicketException {
        final String ticketId = getCentralAuthenticationService()
            .createTicketGrantingTicket(
                TestUtils.getCredentialsWithSameUsernameAndPassword());
        final String serviceTicketId = getCentralAuthenticationService()
            .grantServiceTicket(ticketId, TestUtils.getService());
        try {
            getCentralAuthenticationService().destroyTicketGrantingTicket(
                serviceTicketId);
            fail(TestUtils.CONST_EXCEPTION_EXPECTED);
        } catch (ClassCastException e) {
            // nothing to do here, exception is expected.
        }
    }

    @Test
    public void testGrantServiceTicketWithValidTicketGrantingTicket()
        throws TicketException {
        final String ticketId = getCentralAuthenticationService()
            .createTicketGrantingTicket(
                TestUtils.getCredentialsWithSameUsernameAndPassword());
        getCentralAuthenticationService().grantServiceTicket(ticketId,
            TestUtils.getService());
    }

    @Test
    public void testGrantServiceTicketWithInvalidTicketGrantingTicket()
        throws TicketException {
        final String ticketId = getCentralAuthenticationService()
            .createTicketGrantingTicket(
                TestUtils.getCredentialsWithSameUsernameAndPassword());
        getCentralAuthenticationService().destroyTicketGrantingTicket(ticketId);
        try {
            getCentralAuthenticationService().grantServiceTicket(ticketId,
                TestUtils.getService());
            fail(TestUtils.CONST_EXCEPTION_EXPECTED);
        } catch (TicketException e) {
            // nothing to do here, exception is expected.
        }
    }

    @Test
    public void testGrantServiceTicketWithExpiredTicketGrantingTicket() throws TicketException {
        ((CentralAuthenticationServiceImpl) getCentralAuthenticationService()).setTicketGrantingTicketExpirationPolicy(new ExpirationPolicy() {
            private static final long serialVersionUID = 1L;

            public boolean isExpired(final TicketState ticket) {
                return true;
            }});
    final String ticketId = getCentralAuthenticationService()
        .createTicketGrantingTicket(
            TestUtils.getCredentialsWithSameUsernameAndPassword());
    try {
        getCentralAuthenticationService().grantServiceTicket(ticketId,
            TestUtils.getService());
        fail(TestUtils.CONST_EXCEPTION_EXPECTED);
    } catch (TicketException e) {
        // nothing to do here, exception is expected.
    } finally {
        ((CentralAuthenticationServiceImpl) getCentralAuthenticationService()).setTicketGrantingTicketExpirationPolicy(new NeverExpiresExpirationPolicy());
    }
}

    @Test
    public void testDelegateTicketGrantingTicketWithProperParams() throws TicketException {
        final String ticketId = getCentralAuthenticationService()
            .createTicketGrantingTicket(
                TestUtils.getCredentialsWithSameUsernameAndPassword());
        final String serviceTicketId = getCentralAuthenticationService()
            .grantServiceTicket(ticketId, TestUtils.getService());
        getCentralAuthenticationService().delegateTicketGrantingTicket(
            serviceTicketId, TestUtils.getHttpBasedServiceCredentials());
    }

    @Test
    public void testDelegateTicketGrantingTicketWithBadCredentials()
        throws TicketException {
        final String ticketId = getCentralAuthenticationService()
            .createTicketGrantingTicket(
                TestUtils.getCredentialsWithSameUsernameAndPassword());
        final String serviceTicketId = getCentralAuthenticationService()
            .grantServiceTicket(ticketId, TestUtils.getService());
        try {
            getCentralAuthenticationService().delegateTicketGrantingTicket(
                serviceTicketId, TestUtils.getBadHttpBasedServiceCredentials());
            fail(TestUtils.CONST_EXCEPTION_EXPECTED);
        } catch (TicketException e) {
            // nothing to do here, exception is expected.
        }
    }

    @Test
    public void testDelegateTicketGrantingTicketWithBadServiceTicket()
        throws TicketException {
        final String ticketId = getCentralAuthenticationService()
            .createTicketGrantingTicket(
                TestUtils.getCredentialsWithSameUsernameAndPassword());
        final String serviceTicketId = getCentralAuthenticationService()
            .grantServiceTicket(ticketId, TestUtils.getService());
        getCentralAuthenticationService().destroyTicketGrantingTicket(ticketId);
        try {
            getCentralAuthenticationService().delegateTicketGrantingTicket(
                serviceTicketId, TestUtils.getHttpBasedServiceCredentials());
            fail(TestUtils.CONST_EXCEPTION_EXPECTED);
        } catch (TicketException e) {
            // nothing to do here, exception is expected.
        }
    }

    @Test
    public void testGrantServiceTicketWithValidCredentials()
        throws TicketException {
        final String ticketGrantingTicket = getCentralAuthenticationService()
            .createTicketGrantingTicket(
                TestUtils.getCredentialsWithSameUsernameAndPassword());
        getCentralAuthenticationService().grantServiceTicket(
            ticketGrantingTicket, TestUtils.getService(),
            TestUtils.getCredentialsWithSameUsernameAndPassword());
    }

    @Test
    public void testGrantServiceTicketWithInvalidCredentials()
        throws TicketException {
        final String ticketGrantingTicket = getCentralAuthenticationService()
            .createTicketGrantingTicket(
                TestUtils.getCredentialsWithSameUsernameAndPassword());
        try {
            getCentralAuthenticationService().grantServiceTicket(
                ticketGrantingTicket, TestUtils.getService(),
                TestUtils.getBadHttpBasedServiceCredentials());
            fail(TestUtils.CONST_EXCEPTION_EXPECTED);
        } catch (TicketException e) {
            // nothing to do here, exception is expected.
        }
    }

    @Test
    public void testGrantServiceTicketWithDifferentCredentials()
        throws TicketException {
        final String ticketGrantingTicket = getCentralAuthenticationService()
            .createTicketGrantingTicket(
                TestUtils.getCredentialsWithSameUsernameAndPassword());
        try {
            getCentralAuthenticationService().grantServiceTicket(
                ticketGrantingTicket, TestUtils.getService(),
                TestUtils.getCredentialsWithSameUsernameAndPassword("test1"));
            fail(TestUtils.CONST_EXCEPTION_EXPECTED);
        } catch (TicketException e) {
            // nothing to do here, exception is expected.
        }
    }

    @Test
    public void testValidateServiceTicketWithExpires() throws TicketException {
        ((CentralAuthenticationServiceImpl) getCentralAuthenticationService())
            .setServiceTicketExpirationPolicy(new MultiTimeUseOrTimeoutExpirationPolicy(
                1, 1100));
        final String ticketGrantingTicket = getCentralAuthenticationService()
            .createTicketGrantingTicket(
                TestUtils.getCredentialsWithSameUsernameAndPassword());
        final String serviceTicket = getCentralAuthenticationService()
            .grantServiceTicket(ticketGrantingTicket, TestUtils.getService());

        getCentralAuthenticationService().validateServiceTicket(serviceTicket,
            TestUtils.getService());

        assertFalse(getTicketRegistry().deleteTicket(serviceTicket));
        ((CentralAuthenticationServiceImpl) getCentralAuthenticationService())
            .setServiceTicketExpirationPolicy(new NeverExpiresExpirationPolicy());
    }

    @Test
    public void testValidateServiceTicketWithValidService()
        throws TicketException {
        final String ticketGrantingTicket = getCentralAuthenticationService()
            .createTicketGrantingTicket(
                TestUtils.getCredentialsWithSameUsernameAndPassword());
        final String serviceTicket = getCentralAuthenticationService()
            .grantServiceTicket(ticketGrantingTicket, TestUtils.getService());

        getCentralAuthenticationService().validateServiceTicket(serviceTicket,
            TestUtils.getService());
    }

    @Test
    public void testValidateServiceTicketWithInvalidService()
        throws TicketException {
        final String ticketGrantingTicket = getCentralAuthenticationService()
            .createTicketGrantingTicket(
                TestUtils.getCredentialsWithSameUsernameAndPassword());
        final String serviceTicket = getCentralAuthenticationService()
            .grantServiceTicket(ticketGrantingTicket, TestUtils.getService());

        try {
            getCentralAuthenticationService().validateServiceTicket(
                serviceTicket, TestUtils.getService("test2"));
            fail(TestUtils.CONST_EXCEPTION_EXPECTED);
        } catch (TicketException e) {
            // nothing to do here, exception is expected.
        }
    }

    @Test
    public void testValidateServiceTicketWithInvalidServiceTicket()
        throws TicketException {
        final String ticketGrantingTicket = getCentralAuthenticationService()
            .createTicketGrantingTicket(
                TestUtils.getCredentialsWithSameUsernameAndPassword());
        final String serviceTicket = getCentralAuthenticationService()
            .grantServiceTicket(ticketGrantingTicket, TestUtils.getService());
        getCentralAuthenticationService().destroyTicketGrantingTicket(
            ticketGrantingTicket);

        try {
            getCentralAuthenticationService().validateServiceTicket(
                serviceTicket, TestUtils.getService());
            fail(TestUtils.CONST_EXCEPTION_EXPECTED);
        } catch (TicketException e) {
            // nothing to do here, exception is expected.
        }
    }

    @Test
    public void testValidateServiceTicketNonExistantTicket() {
        try {
            getCentralAuthenticationService().validateServiceTicket("test",
                TestUtils.getService());
            fail(TestUtils.CONST_EXCEPTION_EXPECTED);
        } catch (TicketException e) {
            // nothing to do here, exception is expected.
        }
    }
}
