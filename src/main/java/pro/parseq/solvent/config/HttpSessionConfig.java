package pro.parseq.solvent.config;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.hazelcast.config.annotation.web.http.EnableHazelcastHttpSession;
import org.springframework.session.web.http.CookieHttpSessionStrategy;
import org.springframework.session.web.http.HttpSessionStrategy;

/**
 * HttpSession configuration.
 * 
 * NOTE: maxInactiveIntervalInSeconds should be set to -1 for standalone version
 * 	while in case of public service it should be set to some reasonable level.
 * 
 * @author Aonton Bragin
 *
 */
@Configuration
@EnableHazelcastHttpSession(maxInactiveIntervalInSeconds=-1)
public class HttpSessionConfig {
	
	private static Logger logger = LoggerFactory.getLogger(HttpSessionConfig.class);
	
	/**
	 * Send session token in cookies.
	 * 
	 * This is the default behavior but we specify it here to be explicit.
	 * 
	 * For details see: http://docs.spring.io/spring-session/docs/2.0.0.BUILD-SNAPSHOT/reference/html5/#httpsession-rest
	 * 
	 * @return
	 */
	@Bean
	public HttpSessionStrategy httpSessionStrategy() {
		return new CookieHttpSessionStrategy();
	}
    
    /**
     * Note that listeners are registered automatically:
     * 		http://docs.spring.io/spring-session/docs/1.3.1.RELEASE/reference/html5/#httpsession-httpsessionlistener
     * 
     * @return
     */
    @Bean
    public HttpSessionListener httpSessionListener() {
    	return new HttpSessionListener() {
			
			@Override
			public void sessionDestroyed(HttpSessionEvent event) {
				logger.info("Session destroyed event: {}", event.getSession().getId());
			}
			
			@Override
			public void sessionCreated(HttpSessionEvent event) {
				logger.info("Session created event: {}", event.getSession().getId());
			}
		};
    }
	
}
