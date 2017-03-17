package pro.parseq.ghop.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pro.parseq.GenomeExplorer.ReferenceExplorer;
import pro.parseq.ghop.datasources.DataSource;
import pro.parseq.ghop.datasources.MasterDataSource;
import pro.parseq.ghop.datasources.ReferenceDataSource;
import pro.parseq.ghop.entities.NucleotideBand;
import pro.parseq.ghop.entities.Track;
import pro.parseq.ghop.services.BufferedReferenceServiceClient;
import pro.parseq.ghop.services.LocalReferenceService;
import pro.parseq.ghop.services.ReferenceService;
import pro.parseq.ghop.services.RemoteReferenceService;
import pro.parseq.ghop.services.configs.RefserviceConfig;

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
