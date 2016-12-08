package pro.parseq.ghop.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
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
public class BandControllerTest {

	private static final String GENOME = "testGenome";

	private static final String CHROMOSOMES_TRACK = "chromosomes";
	private static final String REGIONS_TRACK = "regions";

	private static final String CHROMOSOMES_TRACK_BED = "/chromosomes.bed";
	private static final String REGIONS_TRACK_BED = "/regions.bed";

	private static final ObjectMapper mapper = new ObjectMapper();

	@Autowired
	private MockMvc mvc;

	@Before
	public void setUpBedSources() throws Exception {

		MockMultipartFile bed = new MockMultipartFile("bed",
				getClass().getResourceAsStream(CHROMOSOMES_TRACK_BED));
		mvc.perform(fileUpload("/tracks/bed")
						.file(bed)
						.param("track", CHROMOSOMES_TRACK)
						.param("genome", GENOME))
				.andExpect(status().isOk());
		bed = new MockMultipartFile("bed",
				getClass().getResourceAsStream(REGIONS_TRACK_BED));
		mvc.perform(fileUpload("/tracks/bed")
						.file(bed)
						.param("track", REGIONS_TRACK)
						.param("genome", GENOME))
				.andExpect(status().isOk());
	}

	@Test
	public void testBedSources() throws Exception {

		ResultActions actions = mvc.perform(get("/bands")
						.param("genome", GENOME)
						.param("contig", "chr3")
						.param("coord", "100")
						.param("left", "0").param("right", "0")
						.param("tracks", CHROMOSOMES_TRACK, REGIONS_TRACK))
				.andExpect(status().isOk());
		JsonNode responseBody = mapper.readTree(actions.andReturn()
				.getResponse().getContentAsString());
		// No bands
		assertThat(responseBody.has("_links"));
		assertThat(!responseBody.has("_embedded"));

		actions = mvc.perform(get("/bands")
						.param("genome", GENOME)
						.param("contig", "chr3")
						.param("coord", "100")
						.param("left", "0").param("right", "1")
						.param("tracks", CHROMOSOMES_TRACK, REGIONS_TRACK))
				.andExpect(status().isOk());
		responseBody = mapper.readTree(actions.andReturn()
				.getResponse().getContentAsString());
		// Should returns two bands (one for chromosome and one for region with the same start coordinate)
		assertThat(responseBody.has("_embedded"));
		assertThat(responseBody.get("_embedded").has("bands"));
		assertThat(responseBody.get("_embedded").get("bands").isArray());
		assertThat(responseBody.get("_embedded").get("bands").size()).isEqualTo(2);
		assertThat(responseBody.get("_embedded").get("bands").get(0).has("startCoord"));
		assertThat(responseBody.get("_embedded").get("bands").get(0).get("startCoord").has("referenceGenome"));
		assertThat(responseBody.get("_embedded").get("bands").get(0).get("startCoord").has("contig"));
		assertThat(responseBody.get("_embedded").get("bands").get(0).get("startCoord").has("coord"));
		assertThat(responseBody.get("_embedded").get("bands").get(0).get("startCoord").get("referenceGenome").asText()).isEqualTo(GENOME);
		assertThat(responseBody.get("_embedded").get("bands").get(0).get("startCoord").get("contig").asText()).isEqualTo("chr4");
		assertThat(responseBody.get("_embedded").get("bands").get(0).get("startCoord").get("coord").asLong()).isEqualTo(0L);
		assertThat(responseBody.get("_embedded").get("bands").get(1).get("startCoord").get("referenceGenome").asText()).isEqualTo(GENOME);
		assertThat(responseBody.get("_embedded").get("bands").get(1).get("startCoord").get("contig").asText()).isEqualTo("chr4");
		assertThat(responseBody.get("_embedded").get("bands").get(1).get("startCoord").get("coord").asLong()).isEqualTo(0L);

		actions = mvc.perform(get("/bands")
						.param("genome", GENOME)
						.param("contig", "chr3")
						.param("coord", "100")
						.param("left", "0").param("right", "2")
						.param("tracks", CHROMOSOMES_TRACK, REGIONS_TRACK))
				.andExpect(status().isOk());
		assertThat(mapper.readTree(actions.andReturn().getResponse().getContentAsString())
				.get("_embedded")).isEqualTo(responseBody.get("_embedded"));

		actions = mvc.perform(get("/bands")
				.param("genome", GENOME)
				.param("contig", "chr3")
				.param("coord", "100")
				.param("left", "1").param("right", "0")
				.param("tracks", CHROMOSOMES_TRACK, REGIONS_TRACK))
		.andExpect(status().isOk());
		responseBody = mapper.readTree(actions.andReturn()
				.getResponse().getContentAsString());
		// Should returns only one band (for second chromosome)
		assertThat(responseBody.get("_embedded").get("bands").size()).isEqualTo(1);
		assertThat(responseBody.get("_embedded").get("bands").get(0).get("track").asText()).isEqualTo(CHROMOSOMES_TRACK);
		assertThat(responseBody.get("_embedded").get("bands").get(0).get("startCoord").get("contig").asText()).isEqualTo("chr2");
		assertThat(responseBody.get("_embedded").get("bands").get(0).get("startCoord").get("coord").asLong()).isEqualTo(0L);
		assertThat(responseBody.get("_embedded").get("bands").get(0).get("endCoord").get("contig").asText()).isEqualTo("chr2");
		assertThat(responseBody.get("_embedded").get("bands").get(0).get("endCoord").get("coord").asLong()).isEqualTo(150L);
	}
}
