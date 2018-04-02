package br.com.dojot.mutualauthentication.cryptointegration.restful;

import java.util.Map;

import javax.ejb.EJB;
import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import br.com.dojot.mutualauthentication.cryptointegration.beans.to.CryptoChannelResultTO;
import br.com.dojot.mutualauthentication.cryptointegration.beans.to.CryptoChannelTO;
import br.com.dojot.mutualauthentication.cryptointegration.beans.to.CryptoReplyTO;
import br.com.dojot.mutualauthentication.cryptointegration.beans.to.CryptoRequestTO;
import br.com.dojot.mutualauthentication.cryptointegration.beans.to.StandaloneCryptoRequestTO;
import br.com.dojot.mutualauthentication.cryptointegration.facade.api.CryptoIntegrationFacade;
import br.com.dojot.mutualauthentication.cryptointegration.util.Constants;

@Named
@Path("/crypto")
public class CryptoRESTFul {

	@EJB
	private CryptoIntegrationFacade cryptoIntegrationFacade;

	@POST
	@Path("/encrypt/")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response encrypt(StandaloneCryptoRequestTO to) {
		Map<String, String> reply = cryptoIntegrationFacade.encrypt(to.getKey(), to.getIv(), to.getTagSize(),
				to.getData());
		CryptoReplyTO replyTO = new CryptoReplyTO();
		replyTO.setData(reply.get(Constants.CIPHERTEXT));
		replyTO.setResult(reply.get(Constants.OPERATION_RESULT));
		return Response.ok(replyTO).build();
	}

	@POST
	@Path("/decrypt/")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response decrypt(StandaloneCryptoRequestTO to) {
		Map<String, String> reply = cryptoIntegrationFacade.decrypt(to.getKey(), to.getIv(), to.getTagSize(),
				to.getData());
		CryptoReplyTO replyTO = new CryptoReplyTO();
		replyTO.setData(reply.get(Constants.PLAINTEXT));
		replyTO.setResult(reply.get(Constants.OPERATION_RESULT));
		return Response.ok(replyTO).build();
	}

	@POST
	@Path("/encryptWithCC/")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response encryptWithCC(CryptoRequestTO to) {
		Map<String, String> reply = cryptoIntegrationFacade.encryptWithCC(to.getSessionId(), to.getData());
		CryptoReplyTO replyTO = new CryptoReplyTO();
		replyTO.setData(reply.get(Constants.CIPHERTEXT));
		replyTO.setResult(reply.get(Constants.OPERATION_RESULT));
		return Response.ok(replyTO).build();
	}

	@POST
	@Path("/decryptWithCC/")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response decryptWithCC(CryptoRequestTO to) {
		Map<String, String> reply = cryptoIntegrationFacade.decryptWithCC(to.getSessionId(), to.getData());
		CryptoReplyTO replyTO = new CryptoReplyTO();
		replyTO.setData(reply.get(Constants.PLAINTEXT));
		replyTO.setResult(reply.get(Constants.OPERATION_RESULT));
		return Response.ok(replyTO).build();
	}

	@POST
	@Path("/registerCC/")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response registerCC(CryptoChannelTO to) {
		CryptoChannelResultTO result = new CryptoChannelResultTO();
		boolean res = cryptoIntegrationFacade.registerCC(to.getSessionId(), to.getTransactionId(), to.getProvider(), to.getTagLen(),
				to.getKeyServerToComponent(), to.getKeyComponentToServer(), to.getIvServerToComponent(),
				to.getIvComponentToServer(), to.getLifespan());
		result.setResult(res);
		return Response.ok(result).build();
	}
	
	@POST
	@Path("/unregisterCC/")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response unregisterCC(CryptoChannelTO to) {
		CryptoChannelResultTO result = new CryptoChannelResultTO();
		boolean res = cryptoIntegrationFacade.unregisterCC(to.getSessionId());
		result.setResult(res);
		return Response.ok(result).build();
	}

}
