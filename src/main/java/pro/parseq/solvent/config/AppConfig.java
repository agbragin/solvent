package pro.parseq.solvent.config;

import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ch.qos.logback.classic.LoggerContext;

@Configuration
public class AppConfig {
	
	@Bean(destroyMethod="terminate")
	LogbackTerminator logbackTerminator() {
		return new LogbackTerminator();
	}
	
}

class LogbackTerminator {
	
	/**
	 * Terminate logback to prevent memory leaks. 
	 * 
	 * Reference: 
	 * 	http://logback.qos.ch/manual/configuration.html#stopContext
	 * 
	 */
	public void terminate() {
		System.out.println("Stopping logback");
		LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
		loggerContext.stop();
	}
	
}
