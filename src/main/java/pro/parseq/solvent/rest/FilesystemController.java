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
package pro.parseq.solvent.rest;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.concat;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import static pro.parseq.solvent.utils.UrlUtils.decode;
import static pro.parseq.solvent.utils.UrlUtils.encode;

import java.io.File;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pro.parseq.solvent.entities.Folder;
import pro.parseq.solvent.utils.FilesystemUtils;

@RestController
@RequestMapping("/filesystem")
public class FilesystemController {

	@GetMapping
	public Resource<Folder> getContent(@RequestParam String path) {

		String decodedPath = decode(path);
		Folder content = FilesystemUtils.getContent(decodedPath);

		// Self link
		Link selfLink = linkTo(methodOn(FilesystemController.class).getContent(path)).withSelfRel();
		// Links to path's folders
		List<Link> folderLinks = content.getFolders().stream()
				.filter(FilesystemUtils.isNavigationFolder.negate())
				.map(it -> linkTo(methodOn(FilesystemController.class)
								.getContent(encode(FilesystemUtils.addSegment(content.getPath(), it))))
						.withRel(it))
				.collect(toList());
		// Link to path's parent if present
		File parentFolder = FilesystemUtils.getParent(decodedPath);
		if (parentFolder != null) {
			folderLinks.add(linkTo(methodOn(FilesystemController.class)
							.getContent(encode(parentFolder.getAbsolutePath())))
					.withRel(FilesystemUtils.PARENT_FOLDER_REL));
		}
		// Link to filesystem's root
		folderLinks.add(linkTo(methodOn(FilesystemController.class)
						.getContent(FilesystemUtils.FILESYSTEM_ROOT))
				.withRel(FilesystemUtils.FILESYSTEM_ROOT_REL));

		return new Resource<>(content, concat(Stream.of(selfLink), folderLinks.stream()).collect(toList()));
	}
}
