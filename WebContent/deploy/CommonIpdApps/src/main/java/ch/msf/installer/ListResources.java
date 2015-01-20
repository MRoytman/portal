package ch.msf.installer;

import java.io.File;
import java.util.ArrayList;

import ch.msf.util.IOUtils;

/**
 * write in a resource file the name of all txt resource files (+ Qlikview file)
 * present in the resource directory
 * 
 * @author cmi
 * 
 */
public class ListResources implements Runnable {

	private String _ResourceDir;
	private String _ResourceListFileName;

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		ListResources prog = new ListResources();
		if (prog.init(args)) {
			prog.run();
		} else {
			System.out.println("Error: ListResources 'resourceDirectory'");
		}

	}

	private boolean init(String[] args) {
		int argCount = 0;
		if (args.length > argCount) {
			_ResourceDir = args[argCount++];
		} else
			return false;
		if (args.length > argCount) {
			_ResourceListFileName = args[argCount++];
		} else
			return false;
		return true;
	}

	@Override
	public void run() {
		String dirName = System.getProperty("user.dir");
		// \\src\\main\\resources\\
		dirName += _ResourceDir;
		System.out.println(getClass().getName() + "dirName = " + dirName);
		File resourceDir = new File(dirName);
		if (resourceDir.isDirectory()) {
			// get all file names
			ArrayList<String> resourceNames = new ArrayList<String>();
			// File[] files = resourceDir.listFiles(new FilenameFilter() {
			// public boolean accept(File dir, String name) {
			// return name.toLowerCase().endsWith(".txt");
			// }
			// });
			for (String fileName : resourceDir.list()) {
				if (fileName.toLowerCase().endsWith(".txt")
						|| fileName.toLowerCase().endsWith(".qvw"))
					resourceNames.add(fileName);
				// System.out.println(getClass().getName() + ":: "+fileName);
			}

			// save into a file
			IOUtils.writeFileString(resourceNames, dirName
					+ _ResourceListFileName);
		} else {
			System.out.println("Error: dirName = " + dirName
					+ " is not a directory!!!");
		}
		System.out.println(getClass().getName() + "FINI");

	}

}
