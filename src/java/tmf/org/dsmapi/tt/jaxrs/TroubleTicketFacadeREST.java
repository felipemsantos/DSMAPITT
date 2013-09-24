/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tmf.org.dsmapi.tt.jaxrs;
//changes22222 now look agan too much bbbbb cccc vvvvv last vvv mo

import tmf.org.dsmapi.commons.jaxrs.PATCH;
import tmf.org.dsmapi.tt.model.TroubleTicket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import tmf.org.dsmapi.commons.exceptions.BadUsageException;
import tmf.org.dsmapi.commons.exceptions.UnknownResourceException;
import tmf.org.dsmapi.hub.service.PublisherLocal;
import tmf.org.dsmapi.tt.facade.TroubleTicketFacade;

/**
 *
 * @author pierregauthier
 */
@Stateless
@Path("troubleTicket")
public class TroubleTicketFacadeREST {

    @Context
    UriInfo uriInfo;
    @EJB
    TroubleTicketFacade manager;
    @EJB
    PublisherLocal publisher;

    public TroubleTicketFacadeREST() {
    }

    /*
     * RESOURCE
     * troubleTicket
     */
    @GET
    @Produces({"application/json"})
    public Response list(@Context UriInfo info) {

        MultivaluedMap<String, String> map = info.getQueryParameters();
        List<TroubleTicket> listTT = manager.find(map);

        // Uses GenericEntity for messageBodyWriter list oriented
        // http://christopherhunt-software.blogspot.fr/2010/08/messagebodywriter-iswriteable-method.html
        GenericEntity<List<TroubleTicket>> entity =
                new GenericEntity<List<TroubleTicket>>(listTT) {
        };

        return Response.ok(entity).build();
    }

    @POST
    @Consumes({"application/json"})
    @Produces({"application/json"})
    public Response post(TroubleTicket entity) throws BadUsageException {

        manager.create(entity);

        System.out.println("Calling  Publish");
        publisher.publishTicketCreateNotification(entity);
        System.out.println("After Calling  Publish");

        // 201 OK + location
        UriBuilder uriBuilder = UriBuilder.fromUri(uriInfo.getRequestUri());
        String id = entity.getId();
        uriBuilder.path("{id}");
        return Response.created(uriBuilder.build(id)).
                entity(entity).
                build();

    }

    /*
     * RESOURCE
     * troubleTicket/{id}
     */
    @PUT
    @Path("{id}")
    @Consumes({"application/json"})
    @Produces({"application/json"})
    public Response put(@PathParam("id") String id, TroubleTicket entity) throws UnknownResourceException {

        // Try to merge        
        entity = manager.edit(id, entity);
        publisher.publishTicketChangedNotification(entity);
        publisher.publishTicketStatusChangedNotification(entity);

        // 201 OK + location
        UriBuilder uriBuilder = UriBuilder.fromUri(uriInfo.getRequestUri());
        uriBuilder.path("{id}");
        return Response.created(uriBuilder.build(id)).
                entity(entity).
                build();
    }

    //X-HTTP-Method-Override on POST
    @PATCH
    @Path("{id}")
    @Consumes({"application/json"})
    @Produces({"application/json"})
    public Response patch(@PathParam("id") String id, TroubleTicket partialTT) throws BadUsageException, UnknownResourceException {

        partialTT.setId(id);

        TroubleTicket fullTT;
        fullTT = manager.partialUpdate(partialTT);

        publisher.publishTicketChangedNotification(partialTT);
        publisher.publishTicketStatusChangedNotification(partialTT);

        // 201 OK + location
        UriBuilder uriBuilder = UriBuilder.fromUri(uriInfo.getRequestUri());
        id = fullTT.getId();
        uriBuilder.path("{id}");
        return Response.created(uriBuilder.build(id)).
                entity(fullTT).
                build();


    }

    @GET
    @Path("{id}")
    @Produces({"application/json"})
    public Response get(@PathParam("id") String id) throws UnknownResourceException {

        // Go get
        TroubleTicket responseTT = manager.find(id);

        Response response;
        // if troubleTicket exists
        if (responseTT != null) {
            // 200
            response = Response.ok(responseTT).build();
        } else {
            // 404 not found
            response = Response.status(404).build();
        }

        return response;
    }

    public static Date parse(String input) throws java.text.ParseException {

        //NOTE: SimpleDateFormat uses GMT[-+]hh:mm for the TZ which breaks
        //things a bit.  Before we go on we have to repair this.
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz");

        //this is zero time so we need to add that TZ indicator for 
        if (input.endsWith("Z")) {
            input = input.substring(0, input.length() - 1) + "GMT-00:00";
        } else {
            int inset = 6;

            String s0 = input.substring(0, input.length() - inset);
            String s1 = input.substring(input.length() - inset, input.length());

            input = s0 + "GMT" + s1;
        }

        return df.parse(input);

    }
}
