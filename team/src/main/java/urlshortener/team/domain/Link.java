package urlshortener.team.domain;


import java.net.URI;

public class Link {

	private String customUrl;
	private String originalUrl;
	private Boolean createQr;
	private Boolean checkSafe;
	private Boolean isSafe;

	private URI uri;



	public Link() {
	}

	public Link(String customUrl, String originalUrl, URI uri, Boolean createQr, Boolean checkSafe, Boolean isSafe) {
		this.customUrl = customUrl;
		this.originalUrl = originalUrl;
		this.createQr = createQr;
		this.checkSafe = checkSafe;
		this.isSafe = isSafe;
		this.uri = uri;
	}

	public String getCustomUrl() {
		return customUrl;
	}

	public String getOriginalUrl() {
		return originalUrl;
	}

	public Boolean getCreateQr() {
		return createQr;
	}

	public Boolean getCheckSafe() {
		return checkSafe;
	}

	public Boolean getIsSafe() {
		return isSafe;
	}

	public URI getUri() {
		return uri;
	}
}
