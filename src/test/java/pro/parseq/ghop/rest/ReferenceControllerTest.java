package pro.parseq.ghop.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
public class ReferenceControllerTest {

	@Autowired
	private MockMvc mvc;

	@Test
	public void testReferenceGenomes() throws Exception {
		mvc.perform(get("/references")).andExpect(status().isOk());
	}

	@Test
	public void testReferenceGenomeContigs() throws Exception {
		mvc.perform(get("/references/TestReference")).andExpect(status().isOk());
	}

	@Test
	public void testUnknownReferenceGenomeContigs() throws Exception {
		mvc.perform(get("/references/unknown")).andExpect(status().isBadRequest());
	}
}
