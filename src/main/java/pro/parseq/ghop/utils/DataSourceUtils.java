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
