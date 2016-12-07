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

import pro.parseq.ghop.data.Track;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
public class TrackControllerTest {

	private static final String GENOME = "testGenome";

	private static final String UNKNOWN_TRACK = "totallyUnknownTrack";
	private static final Track unknown = new Track(UNKNOWN_TRACK);

	private static final String TRACK = "testTrack";
	private static final Track track = new Track(TRACK);

	@Autowired
	private MockMvc mvc;

	@Test
	public void testUnknownTrackFetching() throws Exception {
		mvc.perform(get(String.format("/tracks/%s", unknown)))
				.andExpect(status().isNotFound());
	}

	@Test
	public void testUnknownTrackRemoving() throws Exception {
		mvc.perform(delete(String.format("/tracks/%s", unknown)))
				.andExpect(status().isNotFound());
	}

	@Test
	public void testUploadTrackFromBed() throws Exception {

		MockMultipartFile bed = new MockMultipartFile("bed",
				getClass().getResourceAsStream("/contigs.bed"));
		ResultActions actions = mvc.perform(fileUpload("/tracks/bed")
						.file(bed)
						.param("track", track.getName())
						.param("genome", GENOME))
				.andExpect(status().isOk());
		JsonNode responseBody = new ObjectMapper()
				.readTree(actions.andReturn().getResponse().getContentAsByteArray());

		assertThat(responseBody.get("track").asText()).isEqualTo(TRACK);

		actions = mvc.perform(delete(String.format("/tracks/%s", track)))
				.andExpect(status().isOk());
		responseBody = new ObjectMapper()
				.readTree(actions.andReturn().getResponse().getContentAsByteArray());

		assertThat(responseBody.get("track").asText()).isEqualTo(TRACK);
	}
}
