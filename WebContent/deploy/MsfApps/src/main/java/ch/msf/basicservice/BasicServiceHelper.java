package ch.msf.basicservice;

/**
 * @author cmiville
 * 
 */

import ch.msf.manager.ConfigurationManagerBaseImpl;
import ch.msf.manager.MessageManager;
import ch.msf.manager.MessageManagerImpl;
import ch.msf.manager.ResourceManager;
import ch.msf.manager.ResourceManagerImpl;

public class BasicServiceHelper{

	// protected BeanFactory _BeanFactory;
	// protected static BeanFactory _BeanFactory;
	//
	// public static synchronized void init() {
	// if (_BeanFactory == null) {
	// _BeanFactory = new
	// ClassPathXmlApplicationContext("application-context.xml");
	// System.out.println("ServiceHelper::init done...");
	// }
	// }

	private static ConfigurationManagerBaseImpl _ConfigurationManagerImpl;
	private static MessageManagerImpl _MessageManagerImpl;
	private static ResourceManagerImpl _ResourceManagerImpl;

	public static synchronized ConfigurationManagerBaseImpl getConfigurationManagerService() {
		if (_ConfigurationManagerImpl == null) {
			_ConfigurationManagerImpl = new ConfigurationManagerBaseImpl();
			if (_ResourceManagerImpl == null) {
				_ResourceManagerImpl = new ResourceManagerImpl();
			}
			_ConfigurationManagerImpl.setResourceManager(_ResourceManagerImpl);
		}
		return _ConfigurationManagerImpl;
	}

	public static synchronized ResourceManager getResourceService() {
		if (_ResourceManagerImpl == null) {
			_ResourceManagerImpl = new ResourceManagerImpl();
		}
		
		if (_ResourceManagerImpl.getConfigurationManager() == null){
			ConfigurationManagerBaseImpl configurationManager = getConfigurationManagerService();
			_ResourceManagerImpl.setConfigurationManager(configurationManager);
		}
		
		return _ResourceManagerImpl;
	}

	public static synchronized MessageManager getMessageService() {
		if (_MessageManagerImpl == null) {
			_MessageManagerImpl = new MessageManagerImpl();
			ConfigurationManagerBaseImpl configurationManager = getConfigurationManagerService();
			_MessageManagerImpl.setConfigurationManager(configurationManager);
		}
		return _MessageManagerImpl;
	}

}
