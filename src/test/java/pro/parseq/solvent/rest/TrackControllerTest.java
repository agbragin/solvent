package pro.parseq.solvent.rest;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TrackControllerTest {
	
	private static Logger logger = LoggerFactory.getLogger(TrackControllerTest.class);
	
	@Autowired
	private WebApplicationContext webApplicationContext;
	
	
	private MockMvc mockMvc;
	
	
	@Before
	public void setUp() throws Exception {
		
		// Configure MVC mock
		mockMvc = MockMvcBuilders
				.webAppContextSetup(webApplicationContext)
				.build();
	}
	
	@Before
	public void setReferenceService() throws Exception {
		// Set remote reference service
		mockMvc.perform(MockMvcRequestBuilders.
					post("/referenceService")
						.param("type", "REMOTE"))
				.andExpect(status().is2xxSuccessful());
	}
	
	@Test
	public void testGetTracks() {
		//TODO: create tests
	}

}
