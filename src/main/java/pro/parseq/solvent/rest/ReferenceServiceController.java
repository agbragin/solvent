package pro.parseq.solvent.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pro.parseq.GenomeExplorer.ReferenceExplorer;
import pro.parseq.solvent.datasources.DataSource;
import pro.parseq.solvent.datasources.MasterDataSource;
import pro.parseq.solvent.datasources.ReferenceDataSource;
import pro.parseq.solvent.entities.NucleotideBand;
import pro.parseq.solvent.entities.Track;
import pro.parseq.solvent.services.BufferedReferenceServiceClient;
import pro.parseq.solvent.services.LocalReferenceService;
import pro.parseq.solvent.services.ReferenceService;
import pro.parseq.solvent.services.RemoteReferenceService;
import pro.parseq.solvent.services.configs.RefserviceConfig;

@RestController
@RequestMapping("/referenceService")
public class ReferenceServiceController {

	@Autowired
	private MasterDataSource masterDataSource;

	@Autowired
	private RefserviceConfig config;

	@PostMapping
	public void setReferenceService(ReferenceServiceType type) {

		Track referenceTrack = new Track("Reference");
		ReferenceService refservice;

		switch (type) {
		case LOCAL:
			ReferenceExplorer referenceExplorer = new ReferenceExplorer(config.getReferencesPath());
			refservice = new LocalReferenceService(referenceExplorer);
			break;

		case REMOTE:
		default:
			refservice = new BufferedReferenceServiceClient(new RemoteReferenceService(config));
			break;
		}

		masterDataSource.setReferenceService(refservice);

		DataSource<NucleotideBand> referenceDataSource = new ReferenceDataSource(referenceTrack, refservice);
		referenceTrack.setDataSource(referenceDataSource);

		masterDataSource.addTrack(referenceTrack);
	}

	private enum ReferenceServiceType {
		LOCAL,
		REMOTE
	}
}
