package ch.msf.service;

/**
 * @author cmiville
 * 
 */

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import ch.msf.manager.AggregationDataManager;
import ch.msf.manager.AllCountriesManager;
import ch.msf.manager.ConfigurationManagerImpl;
import ch.msf.manager.DbManager;
import ch.msf.manager.EntryFormConfigurationManagerImpl;
import ch.msf.manager.MessageManager;
import ch.msf.manager.ResourceManagerImpl;
import ch.msf.manager.SectionManager;
import ch.msf.manager.SelectedCountriesManager;
import ch.msf.model.actionrunner.ActionRunnerManager;

public class ServiceHelper implements BeanFactoryAware {

	// protected BeanFactory _BeanFactory;
	protected static BeanFactory _BeanFactory;

	public static synchronized void init() {
		if (_BeanFactory == null) {
			_BeanFactory = new ClassPathXmlApplicationContext("application-context.xml");
			System.out.println("ServiceHelper::init done...");
		}
	}

	public static MessageManager getMessageService() {
		init();
		ConfigurationManagerImpl configurationManager = getConfigurationManagerService();
		MessageManager messageManager =  (MessageManager) _BeanFactory.getBean("messageManager");
		messageManager.setConfigurationManager(configurationManager);
		return messageManager;
	}

	public static ActionRunnerManager getActionRuleService() {
		init();
		return (ActionRunnerManager) _BeanFactory.getBean("actionRunnerManager");
	}

	public static DbManager getDbManagerService() {
		init();
		return (DbManager) _BeanFactory.getBean("dbManager");
	}

	public static AllCountriesManager getAllCountriesManagerService() {
		init();
		return (AllCountriesManager) _BeanFactory.getBean("allCountriesManager");
	}

	public static SelectedCountriesManager getSelectedCountriesManagerService() {
		init();
		return (SelectedCountriesManager) _BeanFactory.getBean("selectedCountriesManager");
	}

	public static ConfigurationManagerImpl getConfigurationManagerService() {

		return (ConfigurationManagerImpl) getEntryFormConfigurationManagerService();
	}

	
	/**
	 * see application-context
	 * @return
	 */
	public static EntryFormConfigurationManagerImpl getEntryFormConfigurationManagerService() {
		init();
		EntryFormConfigurationManagerImpl entryFormConfigurationManagerImpl = (EntryFormConfigurationManagerImpl) _BeanFactory.getBean("entryFormConfigManager");
		
		ResourceManagerImpl resourceManager = (ResourceManagerImpl) _BeanFactory.getBean("resourceManager");
		resourceManager.setConfigurationManager(entryFormConfigurationManagerImpl);
		
		return entryFormConfigurationManagerImpl;
	}

	public static AggregationDataManager getAggregationDataManagerService() {
		init();
		return (AggregationDataManager) _BeanFactory.getBean("aggregationDataManagerServiceManager");
	}

	public static SectionManager getSectionManagerService() {
		init();
		return (SectionManager) _BeanFactory.getBean("sectionManager");
	}

	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {

		_BeanFactory = beanFactory;
	}

}
