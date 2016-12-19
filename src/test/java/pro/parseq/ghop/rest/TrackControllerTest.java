package pro.parseq.ghop.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
public class TrackControllerTest {

	@Autowired
	private MockMvc mvc;

	@Test
	public void testUnknownTrackFetching() throws Exception {
		mvc.perform(get("/tracks/unknown"))
				.andExpect(status().isNotFound());
	}

	@Test
	public void testUnknownTrackRemoving() throws Exception {
		mvc.perform(delete("/tracks/unknown"))
				.andExpect(status().isNotFound());
	}

	@Test
	public void testUploadTrackFromBed() throws Exception {

		MockMultipartFile bed = new MockMultipartFile("bed",
				getClass().getResourceAsStream("/contigs.bed"));
		ResultActions actions = mvc.perform(fileUpload("/tracks/bed")
						.file(bed)
						.param("track", "testTrack")
						.param("genome", "GRCh37.p13"))
				.andExpect(status().isOk());
		JsonNode responseBody = new ObjectMapper()
				.readTree(actions.andReturn().getResponse().getContentAsByteArray());

		assertThat(responseBody.get("track").asText()).isEqualTo("testTrack");

		actions = mvc.perform(delete("/tracks/testTrack"))
				.andExpect(status().isOk());
		responseBody = new ObjectMapper()
				.readTree(actions.andReturn().getResponse().getContentAsByteArray());

		assertThat(responseBody.get("track").asText()).isEqualTo("testTrack");
	}

	@Test
	public void testUploadTrackFromBedWithUnknownReference() throws Exception {

		MockMultipartFile bed = new MockMultipartFile("bed",
				getClass().getResourceAsStream("/contigs.bed"));
		mvc.perform(fileUpload("/tracks/bed")
						.file(bed)
						.param("track", "testTrack")
						.param("genome", "unknown"))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testUploadTrackFromBedWithUnknownContig() throws Exception {

		MockMultipartFile bed = new MockMultipartFile("bed",
				getClass().getResourceAsStream("/contigs.bed"));
		mvc.perform(fileUpload("/tracks/bed")
						.file(bed)
						.param("track", "testTrack")
						.param("genome", "TestReference"))
				.andExpect(status().isBadRequest());
	}
}
