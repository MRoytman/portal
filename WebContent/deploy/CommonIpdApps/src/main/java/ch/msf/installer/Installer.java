package ch.msf.installer;

import java.io.IOException;
import java.util.Properties;

import javax.swing.JOptionPane;

import ch.msf.CommonConstants;
import ch.msf.form.FatalException;
import ch.msf.manager.ConfigurationManagerImpl;
import ch.msf.manager.ResourceManagerImpl;
import ch.msf.service.ServiceHelper;
import ch.msf.util.StackTraceUtil;

public class Installer {

	@SuppressWarnings("unused")
	private static final long serialVersionUID = 1L;

	protected ConfigurationManagerImpl _ConfigurationManager;

//	private String _MsfApplicationDir;
//	private String _MsfRessourceDir;
//	private String _MsfDriverDir;


	public Installer(String[] args) {

		_ConfigurationManager = ServiceHelper.getConfigurationManagerService();

		if (args == null)
			return;

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		try {

			Installer prog = new Installer(args);

			if (!prog.doRun()) {
				JOptionPane.showMessageDialog(null,
						"The installation didnot work!!", "Fatal error",
						JOptionPane.ERROR_MESSAGE, null);
			} else {
				JOptionPane.showMessageDialog(null,
						"The installation was successful", "Success",
						JOptionPane.INFORMATION_MESSAGE, null);
			}

		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, CommonConstants.ERR_MESS
					+ StackTraceUtil.getCustomStackTrace(e), "Fatal error",
					JOptionPane.ERROR_MESSAGE, null);
		}

	}

	/**
	 * application init - read the configuration for this program - build the
	 * config file
	 * 
	 * @return
	 */
	public boolean doRun() {
		System.out.println(getClass().getName()
				+ "::init............................");
		boolean ret = false;

		// create msf structure directory if needed
//		boolean dirOk = createMsfStructureDirectory();
		boolean dirOk = getConfigurationManager().createMsfStructureDirectory();
		
		if (!dirOk) {
			throw new FatalException(
					"Fatal: problem writing application structure directory: ");
		}

		String resourceFileListFileName = System
				.getProperty("resourceFileListFileName");
		if (resourceFileListFileName == null) {
			String errMess = "msfApplicationDir is not defined!!";
			JOptionPane.showMessageDialog(null, errMess, "Fatal error",
					JOptionPane.ERROR_MESSAGE, null);
			throw new FatalException(errMess);
		}
		System.out.println("resourceFileListFileName = "
				+ resourceFileListFileName);

		try {

			// copy all resource files in msf resource directory
			ret = ((ResourceManagerImpl) ServiceHelper
					.getConfigurationManagerService().getResourceManager())
					.exportAllResources(resourceFileListFileName,
							/*_MsfRessourceDir*/getConfigurationManager().getMsfRessourceDir());

			// copy driver
			if (ret)
				ret = ((ResourceManagerImpl) ServiceHelper
						.getConfigurationManagerService().getResourceManager())
						.exportResource("Driver" + "/" + "h2-1.3.166.jar",
								/*_MsfApplicationDir*/ getConfigurationManager().getMsfApplicationDir()+ "/" + "Driver" + "/"
										+ "h2-1.3.166.jar");

			// Runtime.getRuntime().exec(command);

		} catch (IOException e1) {
			e1.printStackTrace();
		}

		return ret;
	}

//	// create msf structure directory if needed
//	private boolean createMsfStructureDirectory() {
//
//		String dirPath = null;
//
//		// /////// create msf root dir
//		_MsfApplicationDir = System.getProperty("msfApplicationDir");
//		if (_MsfApplicationDir == null) {
//			String errMess = "msfApplicationDir is not defined!!";
//			JOptionPane.showMessageDialog(null, errMess, "Fatal error",
//					JOptionPane.ERROR_MESSAGE, null);
//			throw new FatalException(errMess);
//		} else
//			dirPath = _MsfApplicationDir;
//
//		boolean dirOk = IOUtils.createDirIfNotExists(_MsfApplicationDir);
//		if (!dirOk) {
//			String errMess = "Could not create application directory! "
//					+ dirPath;
//			JOptionPane.showMessageDialog(null, errMess, "Fatal error",
//					JOptionPane.ERROR_MESSAGE, null);
//			throw new FatalException(errMess);
//		}
//
//		// /////// create msf driver dir
//		String driverDir = "Driver";
//
//		dirPath = _MsfApplicationDir + CommonConstants.FILE_SEPARATOR + driverDir;
//
//		dirOk = IOUtils.createDirIfNotExists(dirPath);
//		if (!dirOk) {
//			String errMess = "Could not create application directory! "
//					+ dirPath;
//			JOptionPane.showMessageDialog(null, errMess, "Fatal error",
//					JOptionPane.ERROR_MESSAGE, null);
//			throw new FatalException(errMess);
//		}
//		_MsfDriverDir = dirPath;
//		System.out.println("MsfDriverDir = " + _MsfDriverDir);
//
//		// /////// create msf application dir
//		String applicationShortName = System
//				.getProperty("applicationShortName");
//		if (applicationShortName == null) {
//			String errMess = "applicationShortName is not defined!!";
//			JOptionPane.showMessageDialog(null, errMess, "Fatal error",
//					JOptionPane.ERROR_MESSAGE, null);
//			throw new FatalException(errMess);
//		} else {
//			dirPath = _MsfApplicationDir + CommonConstants.FILE_SEPARATOR
//					+ applicationShortName;
//		}
//		dirOk = IOUtils.createDirIfNotExists(dirPath);
//		if (!dirOk) {
//			String errMess = "Could not create application directory! "
//					+ dirPath;
//			JOptionPane.showMessageDialog(null, errMess, "Fatal error",
//					JOptionPane.ERROR_MESSAGE, null);
//			throw new FatalException(errMess);
//		}
//
//		// /////// create msf application subdir for versioned elements
//		String applicationVersion = System.getProperty("applicationVersion");
//		if (applicationVersion == null) {
//			String errMess = "applicationVersion is not defined!!";
//			JOptionPane.showMessageDialog(null, errMess, "Fatal error",
//					JOptionPane.ERROR_MESSAGE, null);
//			throw new FatalException(errMess);
//		} else {
//			dirPath = dirPath + CommonConstants.FILE_SEPARATOR + applicationShortName + "_"
//					+ applicationVersion;
//		}
//		dirOk = IOUtils.createDirIfNotExists(dirPath);
//		if (!dirOk) {
//			String errMess = "Could not create application directory! "
//					+ dirPath;
//			JOptionPane.showMessageDialog(null, errMess, "Fatal error",
//					JOptionPane.ERROR_MESSAGE, null);
//			throw new FatalException(errMess);
//		}
//		
//		// check backup dir exists
//		String backupDir = dirPath + CommonConstants.FILE_SEPARATOR + "backup";
//		System.out.println("Backup dir="+backupDir);
//		boolean exists = IOUtils.checkIfDirNotExists(backupDir);
//		if (exists){
//			// if yes create backup
//			System.out.println("Backup dir already exists...");
//			
//			String backupReplaceAll = System.getProperty("backupReplaceAll");
//			System.out.println("backupReplaceAll = "+backupReplaceAll);
//			if (backupReplaceAll != null && backupReplaceAll.equals("true")){
//				System.out.println("Replacing all contents from backup directory...");
//				// delete dir contents
//				IOUtils.deleteDirectoryContents(new File(backupDir));
//			}		
//		}
//		else{
//			dirOk = IOUtils.createDirIfNotExists(backupDir);
//			if (!dirOk) {
//				String errMess = "Could not create backup directory! "
//						+ dirPath;
//				JOptionPane.showMessageDialog(null, errMess, "Fatal error",
//						JOptionPane.ERROR_MESSAGE, null);
//				throw new FatalException(errMess);
//			}
//		}
//		
//		//move actual contents to backup dir
//		try {
//			IOUtils.copyDirectory(new File(dirPath), new File(backupDir));
//		} catch (IOException e) {
//			e.printStackTrace();
//			String errMess = "Problem copying backup directory!!";
//			JOptionPane.showMessageDialog(null, errMess, "Fatal error",
//					JOptionPane.ERROR_MESSAGE, null);
//			throw new FatalException(errMess);
//		}
//		
//		
//		dirOk = IOUtils.createDirIfNotExists(dirPath);
//		if (!dirOk) {
//			String errMess = "Could not create application directory! "
//					+ dirPath;
//			JOptionPane.showMessageDialog(null, errMess, "Fatal error",
//					JOptionPane.ERROR_MESSAGE, null);
//			throw new FatalException(errMess);
//		}
//
//		_MsfRessourceDir = dirPath;
//
//		return dirOk;
//	}

	public Properties getProperties() {
		// return _Properties;
		return _ConfigurationManager.getGlobalProperties();
	}
	
	public ConfigurationManagerImpl getConfigurationManager() {
		return _ConfigurationManager;
	}

}
