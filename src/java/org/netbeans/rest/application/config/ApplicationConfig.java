/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.rest.application.config;

import java.util.Set;
import javax.ws.rs.core.Application;

/**
 *
 * @author pierregauthier
 */
@javax.ws.rs.ApplicationPath("api")
public class ApplicationConfig extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        return getRestResourceClasses();
    }
 
    /**
     * Do not modify this method. It is automatically generated by NetBeans REST support.
     */
    private Set<Class<?>> getRestResourceClasses() {
        Set<Class<?>> resources = new java.util.HashSet<Class<?>>();
        resources.add(tmf.org.dsmapi.hub.service.HubFacadeREST.class);
        resources.add(tmf.org.dsmapi.tt.service.AdminFacadeREST.class);
        resources.add(tmf.org.dsmapi.tt.service.TroubleTicketFacadeREST.class);
        resources.add(tmf.org.dsmapi.tt.service.mapper.BadUsageExceptionMapper.class);
        resources.add(tmf.org.dsmapi.tt.service.mapper.JacksonConfigurator.class);
        resources.add(tmf.org.dsmapi.tt.service.mapper.JsonMappingExceptionMapper.class);
        resources.add(tmf.org.dsmapi.tt.service.mapper.TroubleTicketReader.class);
        resources.add(tmf.org.dsmapi.tt.service.mapper.UnknowResourceExceptionMapper.class);
        // following code can be used to customize Jersey 1.x JSON provider:
        try {
            Class jacksonProvider = Class.forName("org.codehaus.jackson.jaxrs.JacksonJsonProvider");
            resources.add(jacksonProvider);
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(getClass().getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        return resources;
    }
    
}
