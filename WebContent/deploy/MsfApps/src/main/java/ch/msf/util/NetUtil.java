package ch.msf.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import ch.msf.error.ParamException;

/**
 * FTP UPLOAD DOWNLOAD usage: upload("ftp.ocg.msf.org", "ituser", "ftp4it", "webstart/msfAppExports/" + "newDbH2.db",
 * filePath);
 * 
 * download(_Host, _User, _Password, _Dir + _HostFile, _LocalFile);
 */
public class NetUtil {

	/**
	 * Upload a file to a FTP server. A FTP URL is generated with the following syntax:
	 * ftp://user:password@host:port/filePath;type=i.
	 * 
	 * @param ftpServer
	 * @param user
	 * @param password
	 * @param fileName
	 * @param srcFilePath
	 * @throws ParamException
	 */

	public static void upload(String ftpServer, String user, String password, String fileName, String srcFilePath) throws ParamException {
		if (ftpServer != null && fileName != null && srcFilePath != null) {

			FTPClient client = new FTPClient();
			FileInputStream fis = null;

			try {
				client.connect(ftpServer);
				client.login(user, password);

				//
				// The local filename to be uploaded.
				fis = new FileInputStream(srcFilePath);

				client.setFileType(FTP.BINARY_FILE_TYPE);
				client.enterLocalPassiveMode();
				//
				// upload file from FTP server
				boolean ret = client.storeFile(fileName, fis);
				if (!ret)
					System.out.println("PROBLEM FPT upload!!");

			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (fis != null) {
						fis.close();
					}
					client.disconnect();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} else {
			String errMess = "Bad parameters";
			System.out.println(errMess);
			throw new ParamException(errMess);
		}
	}

	// public static void upload(String ftpServer, String user, String password, String fileName, String srcFilePath)
	// throws ParamException {
	// if (ftpServer != null && fileName != null && srcFilePath != null) {
	// StringBuffer sb = new StringBuffer("ftp://");
	// // check for authentication else assume its anonymous access.
	// if (user != null && password != null) {
	// sb.append(user);
	// sb.append(':');
	// sb.append(password);
	// sb.append('@');
	// }
	// sb.append(ftpServer);
	// sb.append('/');
	// sb.append(fileName);
	// /*
	// * type ==&gt; a=ASCII mode, i=image (binary) mode, d= file directory listing
	// */
	// sb.append(";type=i");
	//
	// BufferedInputStream bis = null;
	// BufferedOutputStream bos = null;
	// try {
	// URL url = new URL(sb.toString());
	// URLConnection urlc = url.openConnection();
	// System.out.println("urlc = " + urlc);
	// bos = new BufferedOutputStream(urlc.getOutputStream());
	// bis = new BufferedInputStream(new FileInputStream(srcFilePath));
	//
	// byte[] bytes = new byte[8192];
	// int ret;
	// while ((ret = bis.read(bytes, 0, bytes.length)) != -1) {
	//
	// bos.write(bytes, 0, ret);
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// throw new ParamException(e);
	// } finally {
	// if (bis != null)
	// try {
	// bis.close();
	// } catch (IOException ioe) {
	// ioe.printStackTrace();
	// }
	// if (bos != null)
	// try {
	// bos.flush(); // Export TN80
	// // bos.close(); // cause l'exception suivante!?
	// // Exception in thread "AWT-EventQueue-2" java.lang.OutOfMemoryError: Java heap space
	// // at java.util.Arrays.copyOf(Unknown Source)
	// // at java.util.Arrays.copyOf(Unknown Source)
	// // at java.util.Vector.grow(Unknown Source)
	// // at java.util.Vector.ensureCapacityHelper(Unknown Source)
	// // at java.util.Vector.addElement(Unknown Source)
	// // at sun.net.ftp.impl.FtpClient.readServerResponse(Unknown Source)
	// // at sun.net.ftp.impl.FtpClient.readReply(Unknown Source)
	// // at sun.net.ftp.impl.FtpClient.issueCommand(Unknown Source)
	// // at sun.net.ftp.impl.FtpClient.close(Unknown Source)
	// // at sun.net.www.protocol.ftp.FtpURLConnection$FtpOutputStream.close(Unknown Source)
	// // at java.io.FilterOutputStream.close(Unknown Source)
	// // at ch.msf.util.NetUtil.upload(NetUtil.java:87)
	//
	// } catch (IOException ioe) {
	// ioe.printStackTrace();
	// }
	// }
	// } else {
	// String errMess = "Bad parameters";
	// System.out.println(errMess);
	// throw new ParamException(errMess);
	// }
	// }

	public static void download(String ftpServer, String user, String password, String fileName, String destFilePath) throws ParamException {
		if (ftpServer != null && fileName != null && destFilePath != null) {

			FTPClient client = new FTPClient();
			FileOutputStream fos = null;

			try {
				client.connect(ftpServer);
				client.login(user, password);

				//
				// The remote filename to be downloaded.
				fos = new FileOutputStream(destFilePath);

				client.setFileType(FTP.BINARY_FILE_TYPE);
				client.enterLocalPassiveMode();
				//
				// Download file from FTP server
				boolean ret = client.retrieveFile(fileName, fos);
				if (!ret)
					System.out.println("PROBLEM FPT download!!");

			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (fos != null) {
						fos.close();
					}
					client.disconnect();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} else {
			String errMess = "Bad parameters";
			System.out.println(errMess);
			throw new ParamException(errMess);
		}
	}

	/**
	 * Download a file from a FTP server. A FTP URL is generated with the following syntax:
	 * ftp://user:password@host:port/filePath;type=i.
	 * 
	 * @param ftpServer
	 * @param user
	 * @param password
	 * @param fileName
	 * @param destFilePath
	 * @throws ParamException
	 */
	// public static void downloadQQ(String ftpServer, String user, String password, String fileName, String
	// destFilePath) throws ParamException {
	// if (ftpServer != null && fileName != null && destFilePath != null) {
	// StringBuffer sb = new StringBuffer("ftp://");
	// // check for authentication else assume its anonymous access.
	// if (user != null && password != null) {
	// sb.append(user);
	// sb.append(':');
	// sb.append(password);
	// sb.append('@');
	// }
	// sb.append(ftpServer);
	// sb.append('/');
	// sb.append(fileName);
	// /*
	// * type ==&gt; a=ASCII mode, i=image (binary) mode, d= file directory listing
	// */
	// sb.append(";type=i");
	// // BufferedInputStream bis = null;
	// InputStream is = null;
	// BufferedOutputStream bos = null;
	// FileOutputStream fos = null;
	// try {
	// URL url = new URL(sb.toString());
	// URLConnection urlc = url.openConnection();
	// System.out.println("urlc = " + urlc);
	// // bis = new BufferedInputStream(urlc.getInputStream());
	// is = urlc.getInputStream();
	//
	// // bos = new BufferedOutputStream(new FileOutputStream(destFilePath));
	// fos = new FileOutputStream(destFilePath);
	//
	// byte[] bytes = new byte[512]; // 262144 8192
	// int ret;
	// // ret = bis.read(bytes, 0, 1);
	// int count = 0;
	// while ((ret = is.read(bytes, 0, bytes.length)) != -1) {
	// System.out.println("bytes read:" + bytes.length);
	// fos.write(bytes, 0, ret);
	// System.out.println("bytes written:" + bytes.length + ", count = " + count++);
	// }
	// Thread.sleep(1000);
	// System.out.println("end reading...");
	//
	// } catch (Exception e) {
	// e.printStackTrace();
	// throw new ParamException(e);
	// } finally {
	// if (is != null)
	// try {
	// is.close();
	// } catch (IOException ioe) {
	// ioe.printStackTrace();
	// }
	// if (fos != null)
	// try {
	// fos.close();
	// } catch (IOException ioe) {
	// ioe.printStackTrace();
	// }
	// }
	// } else {
	// String errMess = "Bad parameters";
	// System.out.println(errMess);
	// throw new ParamException(errMess);
	// }
	// }

	/**
	 * move a file on the ftp server
	 * 
	 * @param ftpServer
	 * @param user
	 * @param password
	 * @param fileNameSource
	 * @param fileNameDest
	 * @throws ParamException
	 */
//	public static void move(String ftpServer, String user, String password, String fileNameSource, String fileNameDest) throws ParamException {
//		if (ftpServer != null && fileNameSource != null && fileNameDest != null) {
//			StringBuffer sb = new StringBuffer("ftp://");
//			// check for authentication else assume its anonymous access.
//			if (user != null && password != null) {
//				sb.append(user);
//				sb.append(':');
//				sb.append(password);
//				sb.append('@');
//			}
//			sb.append(ftpServer);
//			sb.append('/');
//			String srcPath = sb.toString() + fileNameSource + ";type=i";
//			// sb.append(fileNameSource);
//			/*
//			 * type ==&gt; a=ASCII mode, i=image (binary) mode, d= file directory listing
//			 */
//			// sb.append(";type=i");
//			String dstPath = sb.toString() + fileNameDest;
//			BufferedInputStream bis = null;
//			BufferedOutputStream bos = null;
//			try {
//				URL urlSrc = new URL(srcPath);
//				URLConnection urlcSrc = urlSrc.openConnection();
//				System.out.println("urlc = " + urlcSrc);
//				bis = new BufferedInputStream(urlcSrc.getInputStream());
//
//				URL urlDst = new URL(dstPath);
//				URLConnection urlcDst = urlDst.openConnection();
//				System.out.println("urlc = " + urlcDst);
//				bos = new BufferedOutputStream(urlcDst.getOutputStream());
//
//				byte[] bytes = new byte[8192];
//				int ret;
//				while ((ret = bis.read(bytes, 0, bytes.length)) != -1) {
//
//					bos.write(bytes, 0, ret);
//				}
//
//			} catch (Exception e) {
//				e.printStackTrace();
//				throw new ParamException(e);
//			} finally {
////				if (bos != null)	//cause java.lang.OutOfMemoryError: Java heap space!
////					try {
////						bos.close();
////					} catch (IOException ioe) {
////						ioe.printStackTrace();
////					}
//				if (bis != null)
//					try {
//						bis.close();
//					} catch (IOException ioe) {
//						ioe.printStackTrace();
//					}
//			}
//		} else {
//			String errMess = "Bad parameters";
//			System.out.println(errMess);
//			throw new ParamException(errMess);
//		}
//	}

	/**
	 * delete fileName on the ftpServer on dir serverDirPath
	 * 
	 * @param ftpServer
	 * @param username
	 * @param password
	 * @param serverDirPath
	 * @param fileName
	 * @return true in case of success, null if problem, false if not found
	 */
	public static Boolean deleteFile(String ftpServer, String username, String password, String serverDirPath, String fileName) throws ParamException {
		Boolean ret = false;
		if (ftpServer != null && serverDirPath != null && fileName != null) {

			final FTPClient ftp = new FTPClient();

			// list hidden files
			boolean hidden = false;
			ftp.setListHiddenFiles(hidden);

			int port = 0;
			try {
				int reply;
				if (port > 0) {
					ftp.connect(ftpServer, port);
				} else {
					ftp.connect(ftpServer);
				}

				// After connection attempt, you should check the reply code to
				// verify
				// success.
				reply = ftp.getReplyCode();

				if (!FTPReply.isPositiveCompletion(reply)) {
					ftp.disconnect();
					System.err.println("FTP server refused connection.");
					return null;
				}

				System.out.println("Connected to " + ftpServer + " on " + (port > 0 ? port : ftp.getDefaultPort()));

				if (!ftp.login(username, password)) {
					ftp.logout();
					System.out.println("Could not connect to " + ftpServer + " on " + (port > 0 ? port : ftp.getDefaultPort()));
					return null;
				}

				System.out.println("Remote system is " + ftp.getSystemType());

				boolean localActive = false;
				// Use passive mode as default because most of us are
				// behind firewalls these days.
				if (localActive) {
					ftp.enterLocalActiveMode();
				} else {
					ftp.enterLocalPassiveMode();
				}

				boolean useEpsvWithIPv4 = false;
				ftp.setUseEPSVwithIPv4(useEpsvWithIPv4);

				for (FTPFile f : ftp.listFiles(serverDirPath)) {

					if (f.getName().equals(fileName)) {
						String fileToDelete = serverDirPath + "/" + f.getName();
						System.out.println(fileToDelete);
						ret = ftp.deleteFile(fileToDelete);
						break;
					}
				}

				ftp.noop(); // check that control connection is working OK
				// ftp.logout(); // workaround, comment otherwise
				// get Exception in thread "AWT-EventQueue-2" java.lang.OutOfMemoryError: Java heap space
			} catch (Exception e) {
				if (ftp.isConnected()) {
					try {
						ftp.disconnect();
					} catch (IOException f) {
						// do nothing
					}
				}
				System.err.println("Could not connect to server.");
				e.printStackTrace();
				return null;
			} finally {
				if (ftp.isConnected()) {
					try {
						ftp.disconnect();
					} catch (IOException f) {
						// do nothing
					}
				}
			}

		} else {
			String errMess = "Bad parameters, ftpServer or serverDirPath or fileName";
			System.out.println(errMess);
			throw new ParamException(errMess);
		}
		return ret;
	}

	/**
	 * // ftp.ocg.msf.org ituser ftp4it webstart/msfAppExports
	 * 
	 * @param ftpServer
	 * @param username
	 * @param password
	 * @param serverDirPath
	 * @return a list of file names that on the ftpServer on dir serverDirPath, null if problem SEE FTPClientExample on
	 *         apache common net
	 */

	public static List<String> getList(String ftpServer, String username, String password, String serverDirPath) throws ParamException {
		ArrayList<String> retList = new ArrayList<String>();
		if (ftpServer != null && serverDirPath != null) {

			final FTPClient ftp = new FTPClient();

			// list hidden files
			boolean hidden = false;
			ftp.setListHiddenFiles(hidden);

			// suppress login details
			// ftp.addProtocolCommandListener(new PrintCommandListener(new
			// PrintWriter(System.out), true));

			int port = 0;
			try {
				int reply;
				if (port > 0) {
					ftp.connect(ftpServer, port);
				} else {
					ftp.connect(ftpServer);
				}

				// After connection attempt, you should check the reply code to
				// verify
				// success.
				reply = ftp.getReplyCode();

				if (!FTPReply.isPositiveCompletion(reply)) {
					ftp.disconnect();
					System.err.println("FTP server refused connection.");
					return null;
				}

				System.out.println("Connected to " + ftpServer + " on " + (port > 0 ? port : ftp.getDefaultPort()));

				if (!ftp.login(username, password)) {
					ftp.logout();
					System.out.println("Could not connect to " + ftpServer + " on " + (port > 0 ? port : ftp.getDefaultPort()));
					return null;
				}

				System.out.println("Remote system is " + ftp.getSystemType());

				// if (binaryTransfer) {
				// ftp.setFileType(FTP.BINARY_FILE_TYPE);
				// }

				boolean localActive = false;
				// Use passive mode as default because most of us are
				// behind firewalls these days.
				if (localActive) {
					ftp.enterLocalActiveMode();
				} else {
					ftp.enterLocalPassiveMode();
				}

				boolean useEpsvWithIPv4 = false;
				ftp.setUseEPSVwithIPv4(useEpsvWithIPv4);

				// if (listFiles) {
				// if (lenient) {
				// FTPClientConfig config = new FTPClientConfig();
				// config.setLenientFutureDates(true);
				// ftp.configure(config);
				// }
				for (FTPFile f : ftp.listFiles(serverDirPath)) {
					retList.add(f.getName());
				}

				ftp.noop(); // check that control connection is working OK
				// ftp.logout(); // workaround, comment otherwise
				// get Exception in thread "AWT-EventQueue-2" java.lang.OutOfMemoryError: Java heap space
			} catch (Exception e) {
				e.printStackTrace();
				if (ftp.isConnected()) {
					try {
						ftp.disconnect();
					} catch (IOException f) {
						// do nothing
					}
				}
				System.err.println("Could not connect to server.");
				
				return retList;
			} finally {
				if (ftp.isConnected()) {
					try {
						ftp.disconnect();
					} catch (IOException f) {
						// do nothing
					}
				}
			}

		} else {
			String errMess = "Bad parameters, ftpServer or serverDirPath";
			System.out.println(errMess);
			throw new ParamException(errMess);
		}
		return retList;
	}

	/**
	 * 
	 * @param ftpServerPath
	 * @param username
	 * @param password
	 * @param oldName
	 * @param newName
	 * @return true if rename was ok
	 * @throws ParamException
	 */
	public static Boolean renameFile(String ftpServerPath, String username, String password, String oldName, String newName) throws ParamException {
		Boolean ret = false;
		if (ftpServerPath != null && oldName != null && newName != null) {

			FTPClient ftpClient = new FTPClient();

			int port = 0;
			try {
				int reply;
				if (port > 0) {
					ftpClient.connect(ftpServerPath, port);
				} else {
					ftpClient.connect(ftpServerPath);
				}
				ret = ftpClient.login(username, password);

				// After connection attempt, you should check the reply code to
				// verify
				// success.
				reply = ftpClient.getReplyCode();

				if (!FTPReply.isPositiveCompletion(reply)) {
					ftpClient.disconnect();
					System.err.println("FTP server refused connection.");
					return null;
				}

				// renaming file

				ret = ftpClient.rename(oldName, newName);
				String msg = "Success in ";
				if (!ret)
					msg = "FAILED in ";
				msg += " renaming from " + oldName + " to " + newName;
				System.out.println(msg);

//				ftpClient.logout();!do not use!
				ftpClient.disconnect();

			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				if (ftpClient.isConnected()) {
					try {
						ftpClient.disconnect();
//						ftpClient.logout();
					} catch (IOException ex) {
						ex.printStackTrace();
					}
				}
			}

		} else {
			String errMess = "Bad parameters, ftpServer or serverDirPath or fileName";
			System.out.println(errMess);
			throw new ParamException(errMess);
		}
		return ret;
	}

	public static String getMachineName() throws UnknownHostException {
		String computername = null;
		// try {
		computername = InetAddress.getLocalHost().getHostName();
		// System.out.println(computername);
		// } catch (Exception e) {
		// System.out.println("Exception caught =" + e.getMessage());
		// }

		return computername;
	}

}
