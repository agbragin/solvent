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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pro.parseq.ghop.entities.Track;

@Component
public class DataSourceFactory {

	@Autowired
	private MasterDataSource masterDataSource;

	public BasicBedFileDataSource basicBedFileDataSourceInstance(Track track, InputStream bed) {

		return new BasicBedFileDataSource(track, bed,
				masterDataSource.getComparator(),
				masterDataSource.getReferenceGenome().getId());
	}

	public VariantsBedFileDataSource variantsBedFileDataSourceInstance(Track track, InputStream bed) {

		return new VariantsBedFileDataSource(track, bed,
				masterDataSource.getComparator(),
				masterDataSource.getReferenceGenome().getId());
	}

	public VcfFileDataSource vcfFileDataSourceInstance(Track track, InputStream vcf) {

		return new VcfFileDataSource(track, vcf,
				masterDataSource.getComparator(),
				masterDataSource.getReferenceGenome().getId());
	}
}
