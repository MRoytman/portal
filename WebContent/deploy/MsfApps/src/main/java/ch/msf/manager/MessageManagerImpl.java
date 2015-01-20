package ch.msf.manager;

import java.util.ArrayList;
import java.util.HashMap;

import ch.msf.CommonConstants;
import ch.msf.error.ConfigException;
import ch.msf.util.IOUtils;

public class MessageManagerImpl implements MessageManager {

	private HashMap<Integer, HashMap<String, String>> _MessageLocales = new HashMap<Integer, HashMap<String, String>>();
	
	protected ConfigurationManagerBase _ConfigurationManager;



	/**
	 * 
	 * @param errorCode
	 * @param lang
	 * @return an error message linked with errorCode according to lang
	 */
	@Override
	public String getMessage(Integer messageNumber, String lang) {
		try {
			HashMap<String, String> messageLocales = _MessageLocales.get(messageNumber);
			return messageLocales.get(lang);
		} catch (Exception e) {
			// message is not defined!
			HashMap<String, String> messageLocales = _MessageLocales.get(CommonConstants.MESSAGE_NOT_DEFINED);
			return messageLocales.get(lang)+" ("+messageNumber+")";
		}
	}

	/**
	 * 
	 * @param errorCode
	 * @return an error message linked with errorCode according to default
	 *         language
	 */
	@Override
	public String getMessage(Integer errorCode) {
		String defLang = getConfigurationManager().getDefaultLanguage();
		return getMessage(errorCode, defLang);
	}

	@Override
	/**
	 * load all messages from the resource file
	 * @param intputPathName
	 * @return
	 * @throws IOException
	 */
	public boolean loadMessages(String resourcePathName) {
		// read the file resources

		// get all resource file names
		ArrayList<String> errorMessages = getConfigurationManager().getResourceManager().readResourceFile(resourcePathName);

		if (errorMessages == null || errorMessages.size() == 0) {
			return false;
		}

		// determine the separator
		String separator = IOUtils.findSeparator(errorMessages.get(0), ";\t ");
		if (separator == null)
			throw new ConfigException(getClass().getName() + "::loadMessages: No field separator was found in " + resourcePathName);

		// go through all concepts business rules
		int lineCount = 0;
		boolean ret = true;
		try {
			for (String errorMessageLine : errorMessages) {
				lineCount++;
				if (!errorMessageLine.equals("")) {// if not empty
					String[] parts = errorMessageLine.split(separator);
					String messageNumberStr = parts[0].trim();
					boolean b1Ok = messageNumberStr != null && !messageNumberStr.isEmpty();
					String lang = parts[1].trim();
					boolean b2Ok = lang != null && !lang.isEmpty();
					String message = parts[2].trim();
					boolean b3Ok = message != null && !message.isEmpty();
					if (b1Ok && b2Ok && b3Ok) {
						Integer messageNumber = null;
						try {
							messageNumber = Integer.parseInt(messageNumberStr);
						} catch (Exception e) {
							throw new ConfigException("loadConceptResources: bad messageNumber " + messageNumberStr + " on line " + lineCount);
						}
						HashMap<String, String> messageLocales = _MessageLocales.get(messageNumber);
						if (messageLocales == null) {
							messageLocales = new HashMap<String, String>();
							_MessageLocales.put(messageNumber, messageLocales);
						}
						messageLocales.put(lang, message);

					} else {
						System.out.println(getClass().getName() + "::loadMessages skipped on line " + lineCount);
						ret = false;
					}
				}
			}
		} catch (Exception e) {
			throw new ConfigException(getClass().getName() + "::loadMessages: Problem reading " + resourcePathName + " file on line " + lineCount, e);
		}

		return ret;
	}

	
	@Override
	public ConfigurationManagerBase getConfigurationManager() {
		return _ConfigurationManager;
	}

	@Override
	public void setConfigurationManager(ConfigurationManagerBase _ConfigurationManager) {
		this._ConfigurationManager = _ConfigurationManager;
	}

}
