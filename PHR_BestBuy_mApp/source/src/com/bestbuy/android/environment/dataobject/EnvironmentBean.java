package com.bestbuy.android.environment.dataobject;

public class EnvironmentBean {
	private String name, title, host;
	private String path;
	private String domain;
	private String secureHost;
	private String apiKey;
	private String secureApiKey;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getSecureHost() {
		return secureHost;
	}

	public void setSecureHost(String secureHost) {
		this.secureHost = secureHost;
	}

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public String getSecureApiKey() {
		return secureApiKey;
	}

	public void setSecureApiKey(String secureApiKey) {
		this.secureApiKey = secureApiKey;
	}

}
