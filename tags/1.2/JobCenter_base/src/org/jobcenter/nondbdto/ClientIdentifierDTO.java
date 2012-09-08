package org.jobcenter.nondbdto;

/**
 * The identifier assigned to the client by the server when the client starts up.
 *
 * Not persisted on the database
 */
public class ClientIdentifierDTO {

	private long clientIdentifier;

	public long getClientIdentifier() {
		return clientIdentifier;
	}

	public void setClientIdentifier(long clientIdentifier) {
		this.clientIdentifier = clientIdentifier;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ (int) (clientIdentifier ^ (clientIdentifier >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ClientIdentifierDTO other = (ClientIdentifierDTO) obj;
		if (clientIdentifier != other.clientIdentifier)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ClientIdentifierDTO [clientIdentifier=" + clientIdentifier
				+ "]";
	}

}
