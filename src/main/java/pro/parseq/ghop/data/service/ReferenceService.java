package pro.parseq.ghop.data.service;

import java.util.List;
import java.util.Set;

public interface ReferenceService {

	Set<ReferenceGenome> getReferenceGenomes();
	List<Contig> getContigs(ReferenceGenome referenceGenome);
}
