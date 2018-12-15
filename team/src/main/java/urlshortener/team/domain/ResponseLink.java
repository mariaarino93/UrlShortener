package urlshortener.team.domain;


import java.net.URI;

public class ResponseLink {


	private String shortUrl;
	private String originalUrl;
	private String qrImage;
	private String location;


	public ResponseLink() {
	}

	public ResponseLink(String shortUrl, String originalUrl, String qrImage, String location) {
		this.shortUrl = shortUrl;
		this.originalUrl = originalUrl;
		this.qrImage = qrImage;
		this.location = location;
	}

	public String getShortUrl() {
		return shortUrl;
	}

	public void setShortUrl(String shortUrl) {
		this.shortUrl = shortUrl;
	}

	public String getOriginalUrl() {
		return originalUrl;
	}

	public void setOriginalUrl(String originalUrl) {
		this.originalUrl = originalUrl;
	}

	public String getQrImage() {
		return qrImage;
	}

	public void setQrImage(String qrImage) {
		this.qrImage = qrImage;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

}
