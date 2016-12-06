package pro.parseq.ghop.rest;

import java.io.IOException;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import pro.parseq.ghop.data.source.BedFileDataSource;
import pro.parseq.ghop.data.source.MasterDataSource;

@RestController
@RequestMapping("/dataSources")
public class DataSourceController {

	@Autowired
	private MasterDataSource masterDataSource;

	@GetMapping
	public Set<String> getSourceLayers() {
		return masterDataSource.getLayers();
	}

	@PostMapping("/bed")
	public void addBedFileDataSource(@RequestParam("layer") String layer,
			@RequestParam("file") MultipartFile file,
			@RequestParam("genome") String referenceGenome) throws ServletRequestBindingException {

		try {
			masterDataSource.addDataSource(new BedFileDataSource(
					layer, file.getInputStream(), referenceGenome));
		} catch (IOException e) {
			throw new RuntimeException(String.format(
					"I/O exception while BED file manipultaions: %s", e.getMessage()));
		} catch (RuntimeException e) {
			throw new ServletRequestBindingException(String
					.format("Bad BED file: %s", e.getMessage()));
		}
	}
}
