package es.vdiaz.patrimonio.finanzas.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {

	public static List<String> readFile(String fileName, boolean leeCabecera) throws Exception {
		FileReader fileReader = new FileReader(fileName);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		List<String> lines = new ArrayList<String>();
		String line = null;
		if (!leeCabecera) {
			bufferedReader.readLine();
		}
		while ((line = bufferedReader.readLine()) != null) {
			lines.add(line);
		}
		bufferedReader.close();

		return lines;
	}

	public static File[] getAllFilesInDirectory(final String folderPath, final String extension) {
		File folder = new File(folderPath);

		if (!folder.isDirectory()) {
			System.err.println("El folderPath no es un directorio ->" + folderPath);
			return null;
		}

		FilenameFilter fileNameFilter = null;
		if (extension != null) {
			// create new filename filter
			fileNameFilter = new FilenameFilter() {

				@Override
				public boolean accept(File dir, String name) {
					String pName = name.toLowerCase();
					if (pName.endsWith(extension)) {
						return true;
					}
					return false;
				}
			};
		}

		File[] res = null;
		if (fileNameFilter != null) {
			res = folder.listFiles(fileNameFilter);
		} else {
			res = folder.listFiles();
		}
		return res;
	}
}
