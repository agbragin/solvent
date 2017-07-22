package pro.parseq.solvent;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.http.Cookie;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.session.web.http.SessionRepositoryFilter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.ResourceUtils;
import org.springframework.web.context.WebApplicationContext;

import pro.parseq.solvent.datasources.DataSourceType;


@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringSessionApplicationTests {
	
	private static Logger logger = LoggerFactory.getLogger(SpringSessionApplicationTests.class);
	
	private static String SESSION_COOKIE = "SESSION";
	private static String REFERENCE_GENOME = "GRCh37.p13";

	@Autowired
	private WebApplicationContext webApplicationContext;
	
	// Autowire filters to get access to springSessionRepositoryFilter. For details see:
	//		https://github.com/spring-projects/spring-boot/issues/2650
	@Autowired
	private List<Filter> filters;
	
	@Rule
	public final ExpectedException exception = ExpectedException.none();
	
	private MockMvc mockMvc;
	
	
	@Before
	public void setUp() throws Exception {
		
		// Get session filter to add it to MockMvc filter chain
		Filter springSessionRepositoryFilter = filters.stream()
			.filter(SessionRepositoryFilter.class::isInstance)
			.findAny().get();
		
		// Configure MVC mock
		mockMvc = MockMvcBuilders
				.webAppContextSetup(webApplicationContext)
				.addFilters(springSessionRepositoryFilter)
				.apply(SecurityMockMvcConfigurers.springSecurity())
				.build();
	}
	
	/**
	 * Check that anonymous user receives session cookie.
	 * 
	 * @throws Exception 
	 */
	@Test
	public void testUnauthorized() throws Exception {

		MockHttpServletResponse response = mockMvc.perform(
				MockMvcRequestBuilders
						.get("/session"))
					.andExpect(status().is2xxSuccessful())
			.andReturn().getResponse();
		
		assertTrue("Cookies returned", response.getCookies().length > 0);
		
		Cookie sessionCookie = response.getCookie(SESSION_COOKIE);
		assertNotNull("Session cookie exists", sessionCookie);
		
		MockHttpServletResponse secondResponse = mockMvc.perform(
				MockMvcRequestBuilders
						.get("/session"))
					.andExpect(status().is2xxSuccessful())
			.andReturn().getResponse();
		
		assertNotEquals("Different cookie is returned for unrelated request", 
				sessionCookie.getValue(), 
				secondResponse.getCookie(SESSION_COOKIE).getValue());
		
	}
	
	/**
	 * Check that the same cookie is used for authorized user.
	 * 
	 * @throws Exception 
	 */
	@Test
	public void testAuthorized() throws Exception {
		
		MockHttpServletResponse firstResponse = mockMvc.perform(
				MockMvcRequestBuilders
						.get("/session"))
					.andExpect(status().is2xxSuccessful())
			.andReturn().getResponse();
		
		MockHttpServletResponse secondResponse = mockMvc.perform(
				MockMvcRequestBuilders
						.get("/session")
						// Set cookie
						.cookie(firstResponse.getCookie(SESSION_COOKIE)))
					.andExpect(status().is2xxSuccessful())
			.andReturn().getResponse();
		
		assertNull("Session cookie is not returned if already provided",
				secondResponse.getCookie(SESSION_COOKIE));
	}
	
	@Test
	public void testLogout() throws Exception {
		
		MockHttpServletResponse response = mockMvc.perform(
				MockMvcRequestBuilders
						.get("/session"))
					.andExpect(status().is2xxSuccessful())
			.andReturn().getResponse();
		
		Cookie sessionCookie = response.getCookie(SESSION_COOKIE);
		assertNotNull("Session cookie exists", sessionCookie);
		
		// Logout
		response = mockMvc.perform(
				MockMvcRequestBuilders
					.post("/logout")
					.cookie(sessionCookie))
			.andExpect(status().isOk())
			.andReturn().getResponse();
		
		assertEquals("Empty session cookie on logout",
				"", response.getCookie(SESSION_COOKIE).getValue());
		
		assertEquals("Session cookie expired on logout",
				0, response.getCookie(SESSION_COOKIE).getMaxAge());
		
		// Check that new session is created
		response = mockMvc.perform(
				MockMvcRequestBuilders
						.get("/session"))
					.andExpect(status().is2xxSuccessful())
			.andReturn().getResponse();
		
		Cookie newSessionCookie = response.getCookie(SESSION_COOKIE);
		assertNotNull("Session cookie exists", newSessionCookie);
		
		assertNotEquals("New session cookie after logout", sessionCookie, newSessionCookie);
		
	}
	
	/**
	 * Test session-scoped objects.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSessionScopedBeans() throws Exception {
		
		Cookie sessionCookie = retrieveSessionCookie();
		
		// Set reference service
		mockMvc.perform(
				MockMvcRequestBuilders
					.post("/referenceService")
					.param("type", "REMOTE")
					.cookie(sessionCookie))
			.andExpect(status().is2xxSuccessful());
		
		// Set reference genome
		mockMvc.perform(
				MockMvcRequestBuilders
					.post("/tracks")
					.param("genome", REFERENCE_GENOME)
					.cookie(sessionCookie))
			.andExpect(status().is2xxSuccessful());
		
		// Create track
		String trackId = "test";
		DataSourceType type = DataSourceType.VCF;
		File vcfFile = ResourceUtils.getFile("classpath:tvc.vcf");
		
		mockMvc.perform(
				MockMvcRequestBuilders
					.post("/tracks")
					.param("track", trackId)
					.param("type", type.name())
					.param("path", vcfFile.getAbsolutePath())
					.cookie(sessionCookie))
			.andExpect(status().is2xxSuccessful());
		
		// Retrieve track
		mockMvc.perform(
				MockMvcRequestBuilders
					.get("/tracks/{trackId}", trackId)
					.cookie(sessionCookie))
			.andExpect(status().isOk())
			.andReturn().getResponse();
		
		// Check that track can't be retrieved from other session
		exception.expect(Exception.class);
		mockMvc.perform(
				MockMvcRequestBuilders
					.get("/tracks/{trackId}", trackId)
					.cookie(retrieveSessionCookie()))
			.andExpect(status().isNotFound());
		
	}
	

	/**
	 * Create new session and get its cookie.
	 * 
	 * @return Cookie session cookie
	 * @throws Exception
	 */
	private Cookie retrieveSessionCookie() throws Exception {

		MockHttpServletResponse response = mockMvc.perform(
				MockMvcRequestBuilders
						.get("/session"))
					.andExpect(status().is2xxSuccessful())
			.andReturn().getResponse();
		
		Cookie sessionCookie = response.getCookie(SESSION_COOKIE);
		assertNotNull("Session cookie exists", sessionCookie);
		
		return sessionCookie;
	}

}
