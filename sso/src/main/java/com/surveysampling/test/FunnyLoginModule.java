package com.surveysampling.test;


import org.jboss.security.SimpleGroup;
import org.jboss.security.SimplePrincipal;
import org.jboss.security.auth.spi.UsernamePasswordLoginModule;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginException;
import java.security.acl.Group;
import java.util.Map;


public class FunnyLoginModule extends UsernamePasswordLoginModule {

    @Override
    public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState, Map<String, ?> options) {
        System.out.println("login init");
        super.initialize(subject, callbackHandler, sharedState, options);
    }

    @Override
    protected boolean validatePassword(String inputPassword, String expectedPassword) {
        System.out.println("login validate");
        return super.validatePassword(inputPassword, expectedPassword);
    }

    @Override
    protected String getUsersPassword() throws LoginException {
        System.out.println("getting password");
        return "foo";
    }

    @Override
    protected Group[] getRoleSets() throws LoginException {
        final SimpleGroup group = new SimpleGroup("Roles");
        try {
            group.addMember(new SimplePrincipal("user_role"));
        }
        catch (final Throwable e) {
            final LoginException w = new LoginException("Failed to create group member for " + group);
            w.initCause(e);
            throw w;
        }
        return new Group[]{group};
    }
}





/*

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;
import java.security.Principal;
import java.util.Map;
import java.util.Set;


public class FunnyLoginModule implements LoginModule {

    private static class CustomPrincipal implements Principal {

        private String name;

        public CustomPrincipal(final String name) {
            this.name = name;
            if (this.name == null) {
                throw new IllegalStateException("Did not expect username to ever be null.");
            }
        }

        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public int hashCode() {
            return this.name.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof CustomPrincipal)) {
                return false;
            }
            return this.name.equals(((CustomPrincipal) obj).name);
        }
    }

    private Subject subject;
    private boolean loggedIn;
    private Principal principal;

    public FunnyLoginModule() {
    }

    @Override
    public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState, Map<String, ?> options) {
        System.out.println("initializing login module...");
        this.loggedIn = false;
        this.principal = null;
        this.subject = subject;
        System.out.println("callback handler class: "+callbackHandler.getClass());
        System.out.println("subject == \""+this.subject+"\"");
        for (Map.Entry<String, ?> entry : sharedState.entrySet()) {
            System.out.println(entry.getKey() + " == " + entry.getValue());
        }
    }

    @Override
    public boolean login() throws LoginException {
        System.out.println("logging in...");
        this.loggedIn = false;
        this.loggedIn = true;
        return true;
    }

    @Override
    public boolean commit() throws LoginException {
        System.out.println("committing login...");
        if (this.loggedIn) {
            final Set<Principal> principals = this.subject.getPrincipals();
            this.principal = new CustomPrincipal("[some user name]");
            principals.add(this.principal);
        }
        else {
            this.subject = null;
        }
        return this.loggedIn;
    }

    @Override
    public boolean abort() throws LoginException {
        System.out.println("aborting login...");
        this.subject = null;
        return true;
    }

    @Override
    public boolean logout() throws LoginException {
        System.out.println("logging out...");
        if (this.subject != null && this.principal != null) {
            this.subject.getPrincipals().remove(this.principal);
        }
        this.subject = null;
        return true;
    }
}
*/
