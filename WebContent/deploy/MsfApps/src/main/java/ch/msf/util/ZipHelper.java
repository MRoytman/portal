package ch.msf.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipHelper
{

	public static byte[] compress(byte[] source) throws IOException
	{
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream);
		zipOutputStream.putNextEntry(new ZipEntry("0"));
		zipOutputStream.write(source);
		zipOutputStream.closeEntry();
		byte[] compressed = byteArrayOutputStream.toByteArray();
		zipOutputStream.close();
		return compressed;
	}

	public static byte[] uncompress(byte[] source) throws IOException
	{
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(source);
		ZipInputStream zipInputStream = new ZipInputStream(byteArrayInputStream);
		zipInputStream.getNextEntry();
		byte[] buffer = new byte[1024];
		int nbBytes;
		while ((nbBytes = zipInputStream.read(buffer)) != -1)
			byteArrayOutputStream.write(buffer, 0, nbBytes);
		byte[] decompressed = byteArrayOutputStream.toByteArray();
		byteArrayOutputStream.close();
		zipInputStream.close();
		return decompressed;
	}
	
	
	/**
	 * 
	 * @param inputFilePathName: the source path
	 * @param outputFilePathName: 
	 * @return true if zip created ok
	 */
	public static boolean encodeFileToZip(String inputFilePathName, String outputFilePathName) {
		int level = 9;
		FileOutputStream fout;
		try {
			fout = new FileOutputStream(outputFilePathName);
			ZipOutputStream zout = new ZipOutputStream(fout);
			zout.setLevel(level);

//			String fileNameEntry = inputFilePathName.substring(inputFilePathName.lastIndexOf('\\') + 1);
			// skip extension
			String fileNameEntry = outputFilePathName.substring(0, outputFilePathName.lastIndexOf('.'));
			ZipEntry ze = new ZipEntry(fileNameEntry);
			FileInputStream fin = new FileInputStream(inputFilePathName);
			zout.putNextEntry(ze);

			byte[] buffer = new byte[1024];
			int nbBytes;
			while ((nbBytes = fin.read(buffer)) != -1)
				zout.write(buffer, 0, nbBytes);
			
			fin.close();
			zout.close();
			return true;
		} catch (Exception e) {

			e.printStackTrace();
		}
		return false;
	}

}
