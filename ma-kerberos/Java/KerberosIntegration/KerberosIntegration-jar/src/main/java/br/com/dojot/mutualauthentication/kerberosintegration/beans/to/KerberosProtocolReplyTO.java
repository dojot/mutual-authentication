package br.com.dojot.mutualauthentication.kerberosintegration.beans.to;

public class KerberosProtocolReplyTO {
	private String kerberosReply;

	private String description;

	public String getKerberosReply() {
		return kerberosReply;
	}

	public void setKerberosReply(String reply) {
		this.kerberosReply = reply;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
