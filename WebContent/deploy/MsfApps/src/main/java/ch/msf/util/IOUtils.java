package ch.msf.util;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

/**
 * @author cmiville
 * 
 */
public class IOUtils {

	public static final String LS = System.getProperty("line.separator");

	/**
	 * write to fileName the content of str
	 * 
	 * @param fileName
	 * @param str
	 * @param charSet
	 * @throws IOException
	 */
	public static void writeFile(String fileName, String str, String charSet) throws IOException {
		OutputStream out = null;
		try {
			out = new FileOutputStream(fileName);
			if (charSet != null)
				out.write(str.getBytes(charSet));
			else
				out.write(str.getBytes());

		} finally {
			if (out != null) {
				out.close();
			}
		}
	}

	/**
	 * write the lineList in an outputFileName
	 * 
	 * @param lineList
	 * @param outputFileName
	 */
	public static void writeFileString(ArrayList<String> lineList, String outputFileName) {

		try {
			Writer out = new OutputStreamWriter(new FileOutputStream(outputFileName), "UTF8");

			StringBuilder sb = new StringBuilder();

			for (String line : lineList) {
				try {
					sb.append(line);
					sb.append(LS);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			out.write(sb.toString());
			out.close();

			System.out.println("Written Process Completed.");

		} catch (UnsupportedEncodingException ue) {
			System.out.println("Not supported : ");
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * read a file, line by line
	 * 
	 * @param strList
	 *            : the collection which is filled with file contents.
	 * @param fileFullName
	 * @param charSet
	 *            : can be null, in this case, the default charset is applied
	 */
	public static boolean loadFile(ArrayList<String> strList, String fileFullName, String charSet) {

		try {

			FileInputStream fis = new FileInputStream(fileFullName);
			DataInputStream in = new DataInputStream(fis);
			BufferedReader br = null;
			if (charSet != null)
				br = new BufferedReader(new InputStreamReader(in, charSet));
			else
				br = new BufferedReader(new InputStreamReader(in));

			String line = "";
			while ((line = br.readLine()) != null) {
				strList.add(line);
			}
			in.close();
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static String readKeyboard() {
		String line = ""; // Line read from standard in
		InputStreamReader converter = new InputStreamReader(System.in);
		BufferedReader in = new BufferedReader(converter);

		// while (!(CurLine.equals("quit"))) {
		System.out.println("readKeyboard");
		try {
			line = in.readLine();
			in.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

		// if (!(CurLine.equals("quit"))) {
		// // System.out.println("You typed: " + CurLine);
		// // printConnectParams();
		// _SerialOut.write(CurLine.getBytes());
		// run();
		// }

		// }
		return line;

	}

	public static java.net.URL getResource(String name, Class c) {

		// InputStream IS =
		// Thread.currentThread().getContextClassLoader().getResourceAsStream(name);
		ClassLoader cl = c.getClassLoader();
		if (cl == null) {
			return ClassLoader.getSystemResource(name); // A system class.
		}
		java.net.URL url = cl.getResource(name);
		if (url == null)
			url = c.getResource(name);
		return url;
	}

	public static java.net.URL getResource(String name, ArrayList<Class> classList) {

		// Thread.currentThread().getContextClassLoader().getResourceAsStream(name);

		java.net.URL url = null;
		for (Class theClass : classList) {

			ClassLoader cl = theClass.getClassLoader();
			url = cl.getResource(name);
			if (url == null)
				url = theClass.getResource(name);
			if (url != null)
				System.out.println("getResource()1::: url non nul with class " + theClass + " on " + name);
			else
				System.out.println("getResource()2::: url nul with class " + theClass + " on " + name);

			if (url != null)
				return url;
		}

		// if (url == null) {
		url = ClassLoader.getSystemResource(name); // A system class.
		if (url != null)
			System.out.println("getResource()3::: url non nul with class ");
		for (Class theClass : classList) {
			InputStream obj = theClass.getResourceAsStream(name);
			if (obj != null) {
				System.out.println("PROGRAMING ERROR! Use InputStream instead for " + theClass + " on " + name);
			}
		}
		// }

		return url;
	}

	public static InputStream getResourceAsStream(String name, ArrayList<Class> classList) {

		// Thread.currentThread().getContextClassLoader().getResourceAsStream(name);

		InputStream inputStream = null;

		// if (url == null) {
		for (Class theClass : classList) {
			inputStream = theClass.getResourceAsStream(name);
			if (inputStream != null) {
				return inputStream;
			}
		}
		// }

		return inputStream;
	}

	/**
	 * guess what is the field separator in stringToParse from the separatorList
	 * 
	 * @param stringToParse
	 * @param separatorList
	 * @return the separator or null if not found
	 */
	public static String findSeparator(String stringToParse, String separatorList) {

		if (stringToParse != null && stringToParse.length() != 0 && separatorList != null && separatorList.length() != 0) {

			for (int i = 0; i < separatorList.length(); i++) {
				char sep = separatorList.charAt(i);
				if (stringToParse.indexOf(sep) != -1)
					// found it
					return "" + sep;
			}
		}
		return null;
	}

	/**
	 * create a directory
	 * 
	 * @param directoryName
	 * @return true if dir exists or created
	 */
	public static boolean createDirIfNotExists(String directoryName) {

		File indexDir = new File(directoryName);

		boolean exists = indexDir.exists();
		if (exists && !indexDir.isDirectory())
			return false;

		if (!exists) {
			// if ( !indexDir.canWrite() ) {
			// throw new FatalException( "Cannot write into directory: " +
			// directoryName );
			// }

			// create dir
			return indexDir.mkdir();
		} else
			return true; //
	}

	/**
	 * 
	 * @param directoryName
	 * @return true if dir exists or created
	 */
	public static boolean checkIfDirNotExists(String directoryName) {

		File indexDir = new File(directoryName);

		return indexDir.exists();

	}

	/**
	 * 
	 * @param directoryName
	 * @return true if dir is empty
	 */
	public static boolean checkIfDirEmpty(File indexDir) {

		return indexDir.exists() && indexDir.list().length == 0;
	}

	/**
	 * 
	 * @param directoryName
	 * @return true if ok
	 */

	public static boolean deleteDirectoryContents(File indexDir) {

		boolean exists = indexDir.exists();
		if (!exists || !indexDir.isDirectory())
			return false;

		for (File file : indexDir.listFiles()) {
			System.out.println(file.toString());
			if (file.isDirectory())
				deleteDirectoryContents(file);
			boolean ret = file.delete();
			System.out.println("file deleted...");
			if (!ret)
				return false;
		}
		return true;
	}

	public ArrayList<String> readFileUrl(URL inputFileUrl) throws IOException {
		System.out.println("reading file from url : " + inputFileUrl);
		ArrayList<String> stringList = new ArrayList<String>();

		InputStream is = inputFileUrl.openStream();
		BufferedReader in = new BufferedReader(new InputStreamReader(is, "UTF8"));

		String thisLine = null;
		while ((thisLine = in.readLine()) != null) {
			stringList.add(thisLine);
		}

		in.close();

		return stringList;
	}

	// ///////////// copying start ////////////

	public static final void copyAnyFile(File source, File destination) throws IOException {
		if (source.isDirectory()) {
			copyDirectory(source, destination);
		} else {
			copyFile(source, destination);
		}
	}

	public static final void copyDirectory(File source, File destination) throws IOException {
		if (!source.isDirectory()) {
			throw new IllegalArgumentException("Source (" + source.getPath() + ") must be a directory.");
		}

		if (!source.exists()) {
			throw new IllegalArgumentException("Source directory (" + source.getPath() + ") doesn't exist.");
		}
		// if (destination.exists() && !checkIfDirEmpty(destination)) {
		// throw new IllegalArgumentException("Destination ("
		// + destination.getPath() + ") exists.");
		// }

		destination.mkdirs();
		File[] sourceFiles = source.listFiles();

		for (File file : sourceFiles) {
			if (!file.equals(destination)) {
				System.out.println("copying file " + file);
				if (file.isDirectory()) {
					copyDirectory(file, new File(destination, file.getName()));
				} else {
					copyFile(file, new File(destination, file.getName()));
				}
			} else {
				System.out.println("Skiping to avoid infinite looping...");
			}
		}
	}

	public static final boolean renameTo(File source, File destination) {
		boolean success = source.renameTo(destination);
		return success;
	}

	// public static final void copyFile(File source, File destination){
	//
	// }

	public static final void copyFile(File source, File destination) throws IOException {
		FileChannel sourceChannel = null;
		FileChannel targetChannel = null;
		try {
			sourceChannel = new FileInputStream(source).getChannel();
			targetChannel = new FileOutputStream(destination).getChannel();
			sourceChannel.transferTo(0, sourceChannel.size(), targetChannel);
			sourceChannel.force(true);
			targetChannel.force(true);
		} 
		catch(FileNotFoundException e){
			// file  does not exist
//			System.out.println(e);
		}
		finally {
			if (sourceChannel != null)
				sourceChannel.close();
			if (targetChannel != null)
				targetChannel.close();
		}

	}

	// ///////////// copying end ////////////

}
