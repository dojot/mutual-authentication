package br.com.dojot.mutualauthentication.kerberosintegration.restful;

import javax.ejb.EJB;
import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import br.com.dojot.mutualauthentication.kerberosintegration.beans.to.KerberosClientTO;
import br.com.dojot.mutualauthentication.kerberosintegration.beans.to.KerberosEntityTO;
import br.com.dojot.mutualauthentication.kerberosintegration.beans.to.KerberosRegisterReplyTO;
import br.com.dojot.mutualauthentication.kerberosintegration.exception.GenericException;
import br.com.dojot.mutualauthentication.kerberosintegration.facade.api.KerberosIntegrationFacade;

@Named
@Path("/registry")
public class RegistryRESTFul {
	
	@EJB
	private KerberosIntegrationFacade kerberosIntegrationFacade;

	@POST
	@Path("/registerComponent")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response registerComponent(KerberosClientTO to) {
		KerberosRegisterReplyTO result = kerberosIntegrationFacade.registerComponent(to.getId(), to.getKey());
		return Response.status(200).entity(result).build();
	}

	@POST
	@Path("/unregisterComponent")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response unregisterComponent(KerberosClientTO to) {
		KerberosRegisterReplyTO result = kerberosIntegrationFacade.unregisterComponent(to.getId());
		return Response.status(200).entity(result).build();
	}

	@POST
	@Path("/registerSession/")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response registerSession(KerberosEntityTO to) {
		KerberosRegisterReplyTO result = kerberosIntegrationFacade.registerSession(to.getSessionId(), to.getTransactionId());
		return Response.ok(result).build();
	}

	@POST
	@Path("/unregisterSession/")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response unregisterSession(KerberosEntityTO to) throws GenericException {
		KerberosRegisterReplyTO result = kerberosIntegrationFacade.unregisterSession(to.getSessionId(), to.getTransactionId());
		return Response.ok(result).build();
	}
	
	@GET
	@Path("/session/{sessionId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response session(@PathParam("sessionId") String sessionId) {
		String result = kerberosIntegrationFacade.getSessionInfo(sessionId);
		if(result == null) {
			return Response.status(404).build();
		}
		String json = "{\"result\":\"" + result + "\"}";
		return Response.ok(json).build();		
	}
}
