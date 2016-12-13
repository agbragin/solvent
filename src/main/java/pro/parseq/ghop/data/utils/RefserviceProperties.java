package pro.parseq.ghop.data.utils;

import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RefserviceProperties {

	@NotNull
	@Value("${refservice.connection.scheme}")
	private String scheme;

	@NotNull
	@Value("${refservice.connection.host}")
	private String host;

	@NotNull
	@Value("${refservice.connection.port}")
	private int port;

	@NotNull
	@Value("${refservice.api.root}")
	private String root;

	@NotNull
	@Value("${refservice.api.version}")
	private String version;

	protected RefserviceProperties() {}

	public String getScheme() {
		return scheme;
	}

	public void setScheme(String scheme) {
		this.scheme = scheme;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getRoot() {
		return root;
	}

	public void setRoot(String root) {
		this.root = root;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
}
