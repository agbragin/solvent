package pro.parseq.solvent.rest;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class FilesystemControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	public void testResourcesRoot() throws Exception {

		ResultActions result = mockMvc.perform(get("/filesystem?path=/")).andDo(print())
				.andExpect(status().isOk());
		result.andExpect(content().string(containsString("files")));
		result.andExpect(content().string(containsString("folders")));
		result.andExpect(content().string(containsString("_links")));
	}
}
