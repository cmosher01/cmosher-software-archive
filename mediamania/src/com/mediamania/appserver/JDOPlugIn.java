package com.mediamania.appserver;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManagerFactory;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class JDOPlugIn implements PlugIn {
    private ServletContext ctx;
    private String name;
    private String path;
    private String jndiName;
    public JDOPlugIn() {
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void setPath(String path) {
        this.path = path;
    }
    
    public void setJndiName(String jndiName) {
        this.jndiName = jndiName;
    }
    
    public void init(ActionServlet servlet, ModuleConfig config) 
            throws ServletException {
        ctx = servlet.getServletContext();
        if (name == null || name.length() == 0) {
            throw new ServletException
                ("You must specify name.");
        }
        try {
            PersistenceManagerFactory pmf;
            if (path != null) {
                pmf = getPersistenceManagerFactoryFromPath(path);
            } else if (jndiName != null) {
                pmf = getPersistenceManagerFactoryFromJndi(jndiName);
            } else {
                throw new ServletException
                    ("You must specify either path or jndiName.");
            }
            ctx.setAttribute(name, pmf);
        } catch (Exception ex) {
            throw new ServletException(
                "Unable to load PMF: name:" + name +
                ", path: " + path + 
                ", jndiName: " + jndiName,
                ex);
        }
    }
    
    private PersistenceManagerFactory 
            getPersistenceManagerFactoryFromPath(String path) 
                throws IOException {
        Properties props = new Properties();
        InputStream in = ctx.getResourceAsStream(path);
        props.load(in);
        return JDOHelper.getPersistenceManagerFactory(props);
    }
    
    private PersistenceManagerFactory 
            getPersistenceManagerFactoryFromJndi(String jndiName) 
                throws NamingException {
            Context ic = new InitialContext();
            return (PersistenceManagerFactory) ic.lookup(jndiName);
    }
    
    public void destroy() {}
}