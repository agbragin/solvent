package pro.parseq.ghop.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class RootController {

	@GetMapping
	public String index() {
		return "Hello, let's hop the genomes through!";
	}
}
