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
package pro.parseq.ghop.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pro.parseq.ghop.datasources.DataSource;
import pro.parseq.ghop.datasources.MasterDataSource;
import pro.parseq.ghop.entities.Band;
import pro.parseq.ghop.exceptions.UnknownDataSourceException;

@Component
public class DataSourceUtils {

	@Autowired
	private MasterDataSource masterDataSource;

	public DataSource<? extends Band> retrieveDataSourceByUri(String uri) {

		String[] uriTokens = uri.split("/");
		try {
			DataSource<? extends Band> dataSource = masterDataSource
					.getDataSource(Long.parseLong(uriTokens[uriTokens.length - 1]));
			if (dataSource == null) {
				throw new UnknownDataSourceException(uriTokens[uriTokens.length - 1]);
			}
	
			return dataSource;
		} catch (NumberFormatException e) {
			// TODO: mb more sophisticated exception?
			throw new UnknownDataSourceException(uri);
		}
	}
}
