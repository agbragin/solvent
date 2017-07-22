package pro.parseq.solvent.utils;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.concat;

import java.io.File;
import java.nio.file.AccessMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

import org.apache.commons.lang3.SystemUtils;

import pro.parseq.solvent.entities.Folder;
import pro.parseq.solvent.exceptions.FileAccessDeniedException;
import pro.parseq.solvent.exceptions.FilesystemPathNotFoundException;

public class FilesystemUtils {

	public static final String FILESYSTEM_ROOT = "/";

	public static final String FILESYSTEM_ROOT_REL = "/";
	public static final String PARENT_FOLDER_REL = "..";

	public static final Predicate<String> isNavigationFolder = new Predicate<String>() {

		@Override
		public boolean test(String f) {
			return f.equals(FILESYSTEM_ROOT_REL) || f.equals(PARENT_FOLDER_REL);
		}
	};

	public static final Predicate<File> isHidden = new Predicate<File>() {

		@Override
		public boolean test(File t) {
			return t.isHidden();
		}
	};

	public static final Predicate<File> isFile = new Predicate<File>() {

		@Override
		public boolean test(File t) {
			return t.isFile();
		}
	};

	public static final Predicate<File> isDirectory = new Predicate<File>() {

		@Override
		public boolean test(File t) {
			return t.isDirectory();
		}
	};

	public static final Predicate<File> isNotHiddenFile = isFile.and(isHidden.negate());
	public static final Predicate<File> isNotHiddenDirectory = isDirectory.and(isHidden.negate());

	/**
	 * Retrieves filesystem's content on the path specified
	 * 
	 * @param path Target filesystem part's absolute path
	 * @return {@link Folder}
	 * @throws FilesystemPathNotFoundException when specified path doesn't exist
	 */
	public static final Folder getContent(String path) {

		File content = new File(path);
		if (!content.exists()) {
			throw new FilesystemPathNotFoundException(path);
		}

		if (content.isFile()) {
			return new Folder(content);
		}

		if (SystemUtils.IS_OS_WINDOWS && path.equals(FILESYSTEM_ROOT)) {
			return filesystemRoots();
		} else {
			return content(content);
		}
	}

	/**
	 * Retrieves parent for the path specified
	 * 
	 * @param path Target filesystem part's absolute path
	 * @return Parent folder represented by {@link File} or {@code null} if path doesn't have parent
	 * @throws FilesystemPathNotFoundException when specified path doesn't exist
	 */
	public static final File getParent(String path) {

		File child = new File(path);
		if (!child.exists()) {
			throw new FilesystemPathNotFoundException(path);
		}

		return child.getParentFile();
	}

	/**
	 * Adds specified segment to the existing path
	 * 
	 * @param path
	 * @param segment
	 * @return Resulting path
	 */
	public static final String addSegment(String path, String segment) {
		return String.format("%s/%s", path, segment);
	}

	private static final Folder content(File folder) {

		File[] content = folder.listFiles();
		if (content == null) {

			/*
			 * Unfortunately, we can not use native canXXX File's methods
			 * due to http://bugs.java.com/bugdatabase/view_bug.do?bug_id=6203387
			 * 
			 * So actual try for list folder's content is the most relevant
			 * way to check folder's read permissions (just don't forget to check its existence first)
			 */
			throw new FileAccessDeniedException(folder, AccessMode.READ);
		}

		List<String> files = asList(folder.listFiles()).stream()
				.filter(isNotHiddenFile)
				.map(File::getName)
				.collect(toList());
		List<String> folders = asList(folder.listFiles()).stream()
				.filter(isNotHiddenDirectory)
				.map(File::getName)
				.collect(toList());

		/*
		 * Explicitly sort the lists using case insensitive string comparator,
		 * as File::listFiles method does not guarantee any order
		 */
		Collections.sort(files, String.CASE_INSENSITIVE_ORDER);;
		Collections.sort(folders, String.CASE_INSENSITIVE_ORDER);

		/*
		 * "Navigation" folders
		 * Used to navigate over folder's parent or filesystem's root
		 */
		List<String> navFolders = new ArrayList<>();
		// Filesystem's root link goes first
		navFolders.add(FILESYSTEM_ROOT_REL);
		// Current folder's parent goes after, if present
		if (getParent(folder.getAbsolutePath()) != null) {
			navFolders.add(PARENT_FOLDER_REL);
		}

		return new Folder(folder.getAbsolutePath(), files,
				concat(navFolders.stream(), folders.stream()).collect(toList()));
	}

	private static final Folder filesystemRoots() {

		List<String> roots = asList(File.listRoots()).stream()
				.map(File::getAbsolutePath)
				.collect(toList());
		// Same as in FilesystemUtils::content
		Collections.sort(roots, String.CASE_INSENSITIVE_ORDER);

		return new Folder(FILESYSTEM_ROOT, new ArrayList<>(), roots);
	}
}
