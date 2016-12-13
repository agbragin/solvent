package pro.parseq.ghop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching(proxyTargetClass = true)
public class GHopCoreApplication {

	public static void main(String[] args) {
		SpringApplication.run(GHopCoreApplication.class, args);
	}
}
