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
package pro.parseq.ghop.datasources;

import java.io.InputStream;
import java.util.Comparator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pro.parseq.ghop.entities.Track;
import pro.parseq.ghop.utils.GenomicCoordinate;

@Component
public class DataSourceFactory {

	@Autowired
	private Comparator<GenomicCoordinate> comparator;

	public BasicBedFileDataSource basicBedFileDataSourceInstance(Track track,
			InputStream bed, String referenceGenomeName) {
		return new BasicBedFileDataSource(track, bed, comparator, referenceGenomeName);
	}

	public VariantsBedFileDataSource variantsBedFileDataSourceInstance(Track track,
			InputStream bed, String referenceGenomeName) {
		return new VariantsBedFileDataSource(track, bed, comparator, referenceGenomeName);
	}

	public VcfFileDataSource vcfFileDataSourceInstance(Track track,
			InputStream vcf, String referenceGenomeName) {
		return new VcfFileDataSource(track, vcf, comparator, referenceGenomeName);
	}
}
