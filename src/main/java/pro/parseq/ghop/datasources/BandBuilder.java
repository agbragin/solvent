package pro.parseq.ghop.datasources;

import pro.parseq.ghop.entities.Band;
import pro.parseq.ghop.utils.BedUtils.Region;

public interface BandBuilder<T extends Band> {
	T build(Region region); 
}
