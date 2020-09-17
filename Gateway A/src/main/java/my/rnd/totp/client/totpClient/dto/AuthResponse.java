package my.rnd.totp.client.totpClient.dto;


/**
 * 
 * @author charith
 *
 */

public class AuthResponse {

	private long id;
	private String appID;
	private String secret = "";
	
	public AuthResponse() {
	}

	/**
	 * 
	 * @param id
	 * @param appID
	 * @param secret
	 */
	public AuthResponse(long id, String appID, String secret) {
		super();
		this.id = id;
		this.appID = appID;
		this.secret = secret;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getAppID() {
		return appID;
	}

	public void setAppID(String appID) {
		this.appID = appID;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

}
