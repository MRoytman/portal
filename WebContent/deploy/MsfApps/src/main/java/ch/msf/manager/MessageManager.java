package ch.msf.manager;

import java.io.IOException;

public interface MessageManager {

	/**
	 * 
	 * @param errorCode
	 * @param lang
	 * @return an error message linked with errorCode according to lang
	 */
	String getMessage(Integer errorCode, String lang);

	/**
	 * 
	 * @param errorCode
	 * @return an error message linked with errorCode according to default language
	 */
	String getMessage(Integer errorCode);

	/**
	 * load all messages from the resource file
	 * @param intputPathName
	 * @return
	 * @throws IOException
	 */
	boolean loadMessages(String resourcePathName) ;

	ConfigurationManagerBase getConfigurationManager();

	void setConfigurationManager(ConfigurationManagerBase _ConfigurationManager);

}
