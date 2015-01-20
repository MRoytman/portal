package ch.msf.util;

import java.io.File;
import java.util.ArrayList;

public class FileUtil {
	
	/**
	 * 
	 * @param inputDir
	 * @param extension
	 * @return a list of files in inputDir with given extension
	 */

	public static ArrayList<String> findFiles(String inputDir, String extension) {

		System.out.println("FileUtil:: dirName = " + inputDir
				+ "extension = " + extension);
		ArrayList<String> resourceNames = new ArrayList<String>();
		File resourceDir = new File(inputDir);
		if (resourceDir.isDirectory()) {
			// get all file names

			for (String fileName : resourceDir.list()) {
				if (fileName.toLowerCase().endsWith(extension))
					resourceNames.add(fileName);
				// System.out.println(getClass().getName() + ":: "+fileName);
			}

		} else {
			System.out.println("Error: dirName = " + inputDir
					+ " is not a directory!!!");
		}
		return resourceNames;

	}

}
