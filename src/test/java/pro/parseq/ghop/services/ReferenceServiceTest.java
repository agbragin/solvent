/*******************************************************************************
 *     Copyright 2016-2017 the original author or authors.
 *
 *     This file is part of CONC.
 *
 *     CONC. is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     CONC. is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with CONC. If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package pro.parseq.ghop.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import pro.parseq.ghop.entities.Contig;
import pro.parseq.ghop.entities.ReferenceGenome;
import pro.parseq.ghop.exceptions.ReferenceGenomeNotFoundException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ReferenceServiceTest {

	private static final String TEST_REFERENCE = "TestReference";
	private static final String UNKNOWN_REFERENCE = "TotallyUnknown";
	private static final String SOMEBIGCONTIG = "somebigcontig";

	@Autowired
	private ReferenceService referenceService;

	@Test
	public void testAvailableReferenceGenomes() throws Exception {
		assertThat(referenceService.getReferenceGenomes()).contains(new ReferenceGenome(TEST_REFERENCE));
	}

	@Test
	public void testAvailableTestReferenceGenomeContigs() throws Exception {
		assertThat(referenceService.getContigs(TEST_REFERENCE)).containsExactly(new Contig(TEST_REFERENCE, SOMEBIGCONTIG));
	}

	@Test
	public void testAvailableUnknownReferenceGenomeContigs() throws Exception {

		try {
			referenceService.getContigs(UNKNOWN_REFERENCE);
			fail("Requesting contig list for unknown reference genome should cause an exception!");
		} catch (ReferenceGenomeNotFoundException e) {}
	}
}
