package ch.msf.manager;

import java.util.ArrayList;
import java.util.Properties;
import java.util.TreeSet;

import javax.swing.ImageIcon;

import org.apache.log4j.Logger;

import ch.msf.manager.ConfigurationManagerBaseImpl.ExportOptions;


 public interface ConfigurationManagerBase {
	

	
	 String getDefaultLanguage();
	
	 TreeSet<String> getLanguages();
	
	 void setDefaultLanguage(String defaultLanguage);

	
	 Properties getGlobalProperties();

	 ArrayList<String> getConfigFields(String keyPattern);

	 String getConfigField(String key);
	
	 String getApplicationDirectory();
	
	 boolean createMsfStructureBaseDirectory();
	
	 boolean createMsfStructureDirectory();
	
	 boolean isConfigurationSaved();

	String getMsfApplicationDir();

	String getMsfRessourceDir();

	String getMsfDriverDir();

	ResourceManager getResourceManager();

	void setResourceManager(ResourceManager resourceManager);

	void loadResourceLanguages(String className);

	String getBaseDirectory();
	
	ImageIcon getApplicationIcon();
	void setApplicationIcon(ImageIcon applicationIcon);
	
	void setExportOptions(ExportOptions _ExportOptions);
	ExportOptions getExportOptions();
	
	public String getApplicationTitle();
	void setApplicationTitle(String applicationTitle);
	
	public String getDevProjectPath();
	void setDevProjectPath(String _DevProjectPath);
	
	Logger getLogger();

}
