package pro.parseq.ghop.data.source;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import pro.parseq.ghop.data.Band;
import pro.parseq.ghop.data.Filters;
import pro.parseq.ghop.data.GenomicCoordinate;
import pro.parseq.ghop.data.GenomicCoordinate.CoordinateComparator;
import pro.parseq.ghop.data.Track;
import pro.parseq.ghop.data.source.utils.BedFileEntry;
import pro.parseq.ghop.data.source.utils.BedReader;

public class BedFileDataSource extends InputStreamDataSource {

	private BedReader bedReader;
	private List<GenomicCoordinate> coords;
	private List<Band> bands = new ArrayList<>();

	private CoordinateComparator comparator = new GenomicCoordinate.CoordinateComparator();

	public BedFileDataSource(Track track, InputStream bedFile, String referenceGenome) {

		super(track, bedFile);

		bedReader = new BedReader(bedFile);
		Set<GenomicCoordinate> coords = new HashSet<>();
		for ( ; !bedReader.currentIsDataLine(); bedReader.next());
		for ( ; !bedReader.isEndOfFile(); bedReader.next()) {

			BedFileEntry bedFileEntry = bedReader.parseCurrent();
			GenomicCoordinate startCoord = new GenomicCoordinate(referenceGenome,
					bedFileEntry.getChrom(), bedFileEntry.getChromStart());
			GenomicCoordinate endCoord = new GenomicCoordinate(referenceGenome,
					bedFileEntry.getChrom(), bedFileEntry.getChromEnd());

			coords.add(startCoord);
			coords.add(endCoord);
			bands.add(new Band(track, startCoord, endCoord));
		}

		this.coords = new ArrayList<>(coords);
		Collections.sort(this.coords, comparator);
	}

	@Override
	public List<GenomicCoordinate> leftBorders(int count, GenomicCoordinate coord, Filters filters) {

		// TODO: filters to be taken into account
		int idx = Collections.binarySearch(coords, coord, comparator);
		if (idx < 0) {
			return ((-(idx + 1)) < count) ? coords.subList(0, -(idx + 1)) : coords.subList(-(idx + 1) - count, -(idx + 1));
		} else {
			return (idx < count) ? coords.subList(0, idx + 1) : coords.subList(idx - count, idx + 1);
		}
	}

	@Override
	public List<GenomicCoordinate> rightBorders(int count, GenomicCoordinate coord, Filters filters) {

		// TODO: filters to be taken into account
		int idx = Collections.binarySearch(coords, coord, comparator);
		if (idx < 0) {
			return ((coords.size() - (-(idx + 1))) < count) ? coords.subList(-(idx + 1), coords.size()) : coords.subList(-(idx + 1), -(idx + 1) + count);
		} else {
			return ((coords.size() - idx) < count) ? coords.subList(idx, coords.size()) : coords.subList(idx, idx + count + 1);
		}
	}

	@Override
	public Set<Band> borderGenerants(GenomicCoordinate coord) {

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
