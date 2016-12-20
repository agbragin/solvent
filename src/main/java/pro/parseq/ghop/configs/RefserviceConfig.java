package pro.parseq.ghop.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RefserviceConfig {

	@Value("${refservice.connection.scheme}")
	private String connectionScheme;

	@Value("${refservice.connection.host}")
	private String connectionHost;

	@Value("${refservice.connection.port}")
	private int connectionPort;

	@Value("${refservice.api.root}")
	private String apiRoot;

	@Value("${refservice.api.version}")
	private String apiVersion;

	@Value("${refservice.api.endpoints.references}")
	private String referencesEndpoint;

	protected RefserviceConfig() {}

	public String getConnectionScheme() {
		return connectionScheme;
	}

	public void setConnectionScheme(String connectionScheme) {
		this.connectionScheme = connectionScheme;
	}

	public String getConnectionHost() {
		return connectionHost;
	}

	public void setConnectionHost(String connectionHost) {
		this.connectionHost = connectionHost;
	}

	public int getConnectionPort() {
		return connectionPort;
	}

	public void setConnectionPort(int connectionPort) {
		this.connectionPort = connectionPort;
	}

	public String getApiRoot() {
		return apiRoot;
	}

	public void setApiRoot(String apiRoot) {
		this.apiRoot = apiRoot;
	}

	public String getApiVersion() {
		return apiVersion;
	}

	public void setApiVersion(String apiVersion) {
		this.apiVersion = apiVersion;
	}

	public String getReferencesEndpoint() {
		return referencesEndpoint;
	}

	public void setReferencesEndpoint(String referencesEndpoint) {
		this.referencesEndpoint = referencesEndpoint;
	}
}