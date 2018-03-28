package br.com.dojot.mutualauthentication.kerberosintegration.restful;

import java.util.Map;

import javax.ejb.EJB;
import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import br.com.dojot.mutualauthentication.kerberosintegration.beans.to.KerberosProtocolReplyTO;
import br.com.dojot.mutualauthentication.kerberosintegration.beans.to.KerberosProtocolRequestTO;
import br.com.dojot.mutualauthentication.kerberosintegration.facade.api.KerberosIntegrationFacade;
import br.com.dojot.mutualauthentication.kerberosintegration.service.impl.KerberosProtocolServiceImpl;

@Named
@Path("/protocoltest")
public class ProtocolTestRESTFul {

	@EJB
	private KerberosIntegrationFacade kerberosIntegrationFacade;
	
	@POST
	@Path("/requestAS/")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response processRequestAS(KerberosProtocolRequestTO to) {
		KerberosProtocolReplyTO replyTo = new KerberosProtocolReplyTO();
		Map<String, Object> reply = kerberosIntegrationFacade.processRequestAS(false, to.getSessionId(), to.getTransactionId(),
				to.getRequest());
		replyTo.setKerberosReply((String) reply.get(KerberosProtocolServiceImpl.REPLY_FOR_CLIENT_KERBEROS));
		replyTo.setDescription((String) reply.get(KerberosProtocolServiceImpl.REPLY_FOR_EXTERNAL_APPLICATION));
		return Response.ok(replyTo).build();
	}

	@POST
	@Path("/requestAP/")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response processRequestAP(KerberosProtocolRequestTO to) {
		KerberosProtocolReplyTO replyTo = new KerberosProtocolReplyTO();
		Map<String, Object> reply = kerberosIntegrationFacade.processRequestAP(false, to.getSessionId(), to.getTransactionId(),
				to.getRequest());
		replyTo.setKerberosReply((String) reply.get(KerberosProtocolServiceImpl.REPLY_FOR_CLIENT_KERBEROS));
		replyTo.setDescription((String) reply.get(KerberosProtocolServiceImpl.REPLY_FOR_EXTERNAL_APPLICATION));
		return Response.ok(replyTo).build();
	}

}
