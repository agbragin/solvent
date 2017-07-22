package pro.parseq.solvent.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@RestController
public class SessionController {
	
	private static Logger logger = LoggerFactory.getLogger(SessionController.class);
	
	private ObjectMapper objectMapper = new ObjectMapper();
	
	@RequestMapping(value="/session", method=RequestMethod.GET)
	public JsonNode getSession() {
		
		logger.debug("Session requested from root controller");
		
		RequestAttributes attributes = RequestContextHolder.currentRequestAttributes();
		
		ObjectNode responseBody = objectMapper.createObjectNode();
		responseBody.put("session", attributes.getSessionId());
		
		return responseBody;
	}
	
}