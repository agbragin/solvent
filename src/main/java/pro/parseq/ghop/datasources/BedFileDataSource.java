package pro.parseq.ghop.datasources;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import pro.parseq.ghop.entities.Band;
import pro.parseq.ghop.entities.Contig;
import pro.parseq.ghop.entities.ReferenceGenome;
import pro.parseq.ghop.entities.Track;
import pro.parseq.ghop.utils.BedFileEntry;
import pro.parseq.ghop.utils.BedReader;
import pro.parseq.ghop.utils.Filters;
import pro.parseq.ghop.utils.GenomicCoordinate;

/**
 * BED-file {@link DataSource} straightforward implementation
 * 
 * @author Alexander Afanasyev <a href="mailto:aafanasyev@parseq.pro">aafanasyev@parseq.pro</a>
 */
public class BedFileDataSource extends InputStreamDataSource {

	// BED-file reader
	private BedReader bedReader;
	// Ordered list of regions' coordinates (starts and stops)
	private List<GenomicCoordinate> coords;
	// Bands representing a regions from file (no special order in general)
	private List<Band> bands = new ArrayList<>();

	private Comparator<GenomicCoordinate> comparator;

	protected BedFileDataSource(Track track, InputStream bedFile, ReferenceGenome referenceGenome,
			Comparator<GenomicCoordinate> comparator) {

		super(track, bedFile);

		this.comparator = comparator;
		bedReader = new BedReader(bedFile);
		Set<GenomicCoordinate> coords = new HashSet<>();

		// Skip header lines
		for ( ; !bedReader.currentIsDataLine(); bedReader.next());
		// Read data lines
		for (int i = 0; !bedReader.isEndOfFile(); bedReader.next(), ++i) {

			BedFileEntry bedFileEntry = bedReader.parseCurrent();
			GenomicCoordinate startCoord = new GenomicCoordinate(
					new Contig(referenceGenome, bedFileEntry.getChrom()),
					bedFileEntry.getChromStart());
			GenomicCoordinate endCoord = new GenomicCoordinate(
					new Contig(referenceGenome, bedFileEntry.getChrom()),
					bedFileEntry.getChromEnd());
			String name = bedFileEntry.getName();
			// TODO: access and process any other information (e.g. entry attributes)

			coords.add(startCoord);
			coords.add(endCoord);
			/**
			 * Generate surrogate band id to make it possible to differentiate
			 * two region of the same geometry from different BED-entries
			 * as ${TRACK_NAME}_${BED_ENTRY_NUMBER}
			 */
			bands.add(new Band.BandBuilder(String.format("%s_%d", track, i),
					track, startCoord, endCoord).name(name).build());
		}

		this.coords = new ArrayList<>(coords);
		/**
		 * Critical part of BED data source implementation
		 * Due to this we have a natural BED-file size limitation
		 * as we keep all it's content in memory
		 */
		Collections.sort(this.coords, comparator);
	}

	@Override
	public List<GenomicCoordinate> leftBorders(int count, GenomicCoordinate coord, Filters filters) {

		// TODO: filters to be taken into account
		int idx = Collections.binarySearch(coords, coord, comparator);
		if (idx < 0) {
			return ((-(idx + 1)) <= count) ? coords.subList(0, -(idx + 1)) : coords.subList(-(idx + 1) - count, -(idx + 1));
		} else {
			return (idx <= count) ? coords.subList(0, idx + 1) : coords.subList(idx - count, idx + 1);
		}
	}

	@Override
	public List<GenomicCoordinate> rightBorders(int count, GenomicCoordinate coord, Filters filters) {

		// TODO: filters to be taken into account
		int idx = Collections.binarySearch(coords, coord, comparator);
		if (idx < 0) {
			return ((coords.size() - (-(idx + 1))) <= count) ? coords.subList(-(idx + 1), coords.size()) : coords.subList(-(idx + 1), -(idx + 1) + count);
		} else {
			return ((coords.size() - idx) <= count) ? coords.subList(idx, coords.size()) : coords.subList(idx, idx + count + 1);
		}
	}

	@Override
	public Set<Band> borderGenerants(GenomicCoordinate coord, Filters filters) {

		// TODO: optimize this request
		return bands.stream()
				.filter(band -> band.getStartCoord().equals(coord)
						|| band.getEndCoord().equals(coord))
				.collect(Collectors.toSet());
	}

	@Override
	public Set<Band> coverage(GenomicCoordinate coord, Filters filters) {

		// TODO: optimize this request
		return bands.stream()
				.filter(band -> comparator.compare(band.getStartCoord(), coord) != 1
						&& comparator.compare(band.getEndCoord(), coord) != -1)
				.collect(Collectors.toSet());
	}
}
