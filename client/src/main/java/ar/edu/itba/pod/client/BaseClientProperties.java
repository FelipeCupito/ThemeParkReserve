package ar.edu.itba.pod.client;

import java.util.Properties;

public class BaseClientProperties {
	private String serverAddress;

	public BaseClientProperties(String serverAddress) {
		this.serverAddress = serverAddress;
	}

	public BaseClientProperties(Properties properties) {
		serverAddress = properties.getProperty("serverAddress");
	}

	public String getServerAddress() {
		return serverAddress;
	}
}
