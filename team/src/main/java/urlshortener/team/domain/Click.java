package urlshortener.team.domain;

import java.sql.Date;

public class Click {

	private Long id;
	private String customUrl;
	private String browser;
	private String platform;
	private String ip;
	private String country;
	private String city;
	private Date time;



	public Click(Long id, String customUrl,
				 String browser, String platform, String ip, String country, String city, Date time) {
		this.id = id;
		this.customUrl = customUrl;
		this.browser = browser;
		this.platform = platform;
		this.ip = ip;
		this.country = country;
		this.city = city;
		this.time = time;
	}

	public Long getId() {
		return id;
	}

	public String getCustomUrl() {
		return customUrl;
	}

	public String getBrowser() {
		return browser;
	}

	public String getPlatform() {
		return platform;
	}

	public String getIp() {
		return ip;
	}

	public String getCountry() {
		return country;
	}

	public String getCity() {
		return city;
	}

	public Date getTime() {
		return time;
	}
}
