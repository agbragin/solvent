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
