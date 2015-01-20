package ch.msf.model.actionrunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import ch.msf.CommonIpdConstants;
import ch.msf.error.ConfigException;
import ch.msf.error.FatalException;
import ch.msf.model.datavalidator.businessrule.BusinessRuleCheckerI;
import ch.msf.model.datavalidator.businessrule.BusinessRuleSerieCheckerI;
import ch.msf.model.datavalidator.businessrule.BusinessRuleSerieCheckerImpl;
import ch.msf.service.ServiceHelper;
import ch.msf.util.Reflex;

public class ActionRunnerManagerImpl implements ActionRunnerManager {

	// map HashMap<ActionRuleRunner ClassName, ActionRuleRunnerI>
//	protected HashMap<String, ActionRuleRunnerI> _ActionRuleRunners = new HashMap<String, ActionRuleRunnerI>();

	// map HashMap<conceptId, ActionRuleSerieRunnerI>
	// map of rules per concept
	private HashMap<String, ActionRuleSerieRunnerI> _ConceptRuleSerieRunners = new HashMap<String, ActionRuleSerieRunnerI>();

	// map HashMap<BusinessRuleChecker ClassName, BusinessRuleCheckerI>
//	protected HashMap<String, BusinessRuleCheckerI> _BusinessRuleCheckers = new HashMap<String, BusinessRuleCheckerI>();

	// map HashMap<conceptId, BusinessRuleSerieCheckerI>
	// map of rules per concept
	private HashMap<String, BusinessRuleSerieCheckerI> _ConceptRuleSerieCheckers = new HashMap<String, BusinessRuleSerieCheckerI>();

	
	private <T extends ActionRuleRunnerI> T instanciateActionRuleRunner(String className, Integer errorCode, String[] args) throws ConfigException {
		ActionRuleRunnerI ActionRuleRunner = null;
		String errMess = "ActionRuleManager::ActionRuleRunner: instanciateActionRuleRunner problem on " + className;

		className = CommonIpdConstants.ACTION_RULES_RUNNER_PREFIX + className;
		Class<? extends Object> cls = Reflex.getClassInstance(className);
		if (cls != null) {
			try {
				ActionRuleRunner = (ActionRuleRunnerI) cls.newInstance();
				if (ActionRuleRunner == null)
					throw new FatalException(errMess);
				ActionRuleRunner.setErrorCode(errorCode);
				ActionRuleRunner.verifyConfigParameters(args);
			} catch (ConfigException e) {
				throw e;
			} catch (Exception e) {
				throw new ConfigException(errMess, e);
			}
		} else
			throw new ConfigException(errMess);

		return (T) ActionRuleRunner;
	}
	
	private <T extends BusinessRuleCheckerI> T instanciateBusinessRuleChecker(String className, Integer errorCode, String[] args) throws ConfigException {
		BusinessRuleCheckerI businessRuleChecker = null;
		String errMess = "BusinessRuleManager::BusinessRuleChecker: instanciateBusinessRuleChecker problem on "+className;

		className = CommonIpdConstants.BUSINESS_RULES_CHECKER_PREFIX+ className;
		Class<? extends Object> cls = Reflex.getClassInstance(className);
		if (cls != null) {
			try {
				businessRuleChecker = (BusinessRuleCheckerI) cls.newInstance();
				if (businessRuleChecker == null)
					throw new FatalException(errMess);
				businessRuleChecker.setErrorCode(errorCode);
				businessRuleChecker.verifyConfigParameters(args);
			}
			catch (ConfigException e) {
				throw e;
			}
			catch (Exception e) {
				throw new ConfigException(errMess, e);
			}
		} else
			throw new ConfigException(errMess);

		return (T) businessRuleChecker;
	}

	/**
	 * 
	 * @param checkableId
	 * @return the Runner for the given conceptId build it if it dows not exist
	 */
	protected ActionRuleSerieRunnerI getActionRuleSerieRunner(String checkableId, boolean createIfNotExist) {
		ActionRuleSerieRunnerI cbrc = getConceptRuleSerieRunners().get(checkableId);
		if (cbrc == null && createIfNotExist) {
			cbrc = new ActionRuleSerieRunnerImpl();
			getConceptRuleSerieRunners().put(checkableId, cbrc);
		}
//		System.out.println("checkableId="+checkableId+", cbrc="+cbrc.hashCode());
		return cbrc;
	}
	
	
	/**
	 * 
	 * @param checkableId
	 * @return the checker for the given conceptId build it if it dows not exist
	 */
	protected BusinessRuleSerieCheckerI getBusinessRuleSerieChecker(String checkableId) {
		BusinessRuleSerieCheckerI cbrc = getConceptRuleSerieCheckers().get(checkableId);
		if (cbrc == null) {
			cbrc = new BusinessRuleSerieCheckerImpl();
			getConceptRuleSerieCheckers().put(checkableId, cbrc);
		}
		return cbrc;
	}

	/**
	 * 
	 * @param ActionRuleRunnerName
	 * @param args
	 * @param errorCode
	 * @return the ActionRuleRunner identified by ActionRuleRunnerName, build it
	 *         if it dows not exist
	 */
	protected ActionRuleRunnerI getActionRuleRunner(String ActionRuleRunnerName, Integer errorCode, String[] args) {
		ActionRuleRunnerI brc = null;	//getActionRuleRunners().get(ActionRuleRunnerName);
//		if (brc == null) {
			brc = instanciateActionRuleRunner(ActionRuleRunnerName, errorCode, args);
//			getActionRuleRunners().put(ActionRuleRunnerName, brc);
//		}
		return brc;
	}
	
	/**
	 * 
	 * @param businessRuleCheckerName
	 * @param args 
	 * @param errorCode 
	 * @return the BusinessRuleChecker identified by businessRuleCheckerName,
	 *         build it if it dows not exist
	 */
	protected BusinessRuleCheckerI getBusinessRuleChecker(String businessRuleCheckerName, Integer errorCode, String[] args) {
		BusinessRuleCheckerI brc = null; //getBusinessRuleCheckers().get(businessRuleCheckerName);
//		if (brc == null) { // PB! must take care of the parameters
			brc = instanciateBusinessRuleChecker(businessRuleCheckerName, errorCode, args);
//			getBusinessRuleCheckers().put(businessRuleCheckerName, brc);
//		}
		return brc;
	}

	/**
	 * load all Action rules configuration
	 * 
	 * @param resourcePathName
	 * @return true if concept resources have been loaded correctly
	 * @throws IOException
	 */
	@Override
	public boolean loadActionRulesResources(String resourcePathName) {
		// read the file resources

		// get all resource file names
		ArrayList<String> conceptsRules = ServiceHelper.getConfigurationManagerService().getResourceManager().readResourceFile(resourcePathName);

		if (conceptsRules == null || conceptsRules.size() == 0) {
			return false;
		}

		// determine the separator
		String separator = CommonIpdConstants.RESOURCE_FIELD_SEPARATOR;
		if (separator == null)
			throw new ConfigException(getClass().getName() + "::loadActionRulesResources: No field separator was found in " + resourcePathName);

		// go through all concepts Action rules
		int lineCount = 0;
		boolean ret = true;
		try {
			for (String conceptsRule : conceptsRules) {
				lineCount++;
				if (!conceptsRule.equals("") && !conceptsRule.startsWith("//")) {
//					 if  not  empty  and  not  a  comment
					String[] parts = conceptsRule.split(separator);
					String conceptId = parts[0].trim();
					boolean b1Ok = conceptId != null && !conceptId.isEmpty();
					String conceptRunnerName = parts[1].trim();
					boolean b2Ok = conceptRunnerName != null && !conceptRunnerName.isEmpty();
					String errorMessageCodeStr = parts[2].trim();
					boolean b3Ok = errorMessageCodeStr != null && !errorMessageCodeStr.isEmpty();
					boolean createIfNotExist = true;
					if (b1Ok && b2Ok && b3Ok) {
						if (conceptRunnerName.endsWith("Runner")) {
							// get the ConceptActionRuleRunner
							ActionRuleSerieRunnerI conceptActionRuleRunner = getActionRuleSerieRunner(conceptId, createIfNotExist);
							// get/create the ActionRuleRunner
							// copy the remaining array into new array
							String[] args = Arrays.copyOfRange(parts, 3, parts.length);
							Integer errorCode = Integer.parseInt(errorMessageCodeStr);
							ActionRuleRunnerI actionRuleRunner = getActionRuleRunner(conceptRunnerName, errorCode, args);
							// add the rule to the concept Runner
							conceptActionRuleRunner.addActionRunner(actionRuleRunner);
						} else if (conceptRunnerName.endsWith("Checker")) {
							// get the ConceptBusinessRuleChecker
							BusinessRuleSerieCheckerI conceptBusinessRuleChecker = getBusinessRuleSerieChecker(conceptId);
							// get/create the BusinessRuleChecker
							// copy the remaining array into new array
							String[] args = Arrays.copyOfRange(parts, 3, parts.length);
							Integer errorCode = Integer.parseInt(errorMessageCodeStr);
							BusinessRuleCheckerI businessRuleChecker = getBusinessRuleChecker(conceptRunnerName, errorCode, args);
							// add the rule to the concept checker
							conceptBusinessRuleChecker.addBRChecker(businessRuleChecker);
						}
						else{
							throw new ConfigException(getClass().getName() + "::Action should be Runner or Checker...");
						}

					} else {
						System.out.println(getClass().getName() + "::loadActionRulesResources skipped on line " + lineCount);
						ret = false;
					}
				}
			}
		} catch (Exception e) {

			throw new ConfigException(getClass().getName() + "::loadActionRulesResources: Problem reading " + resourcePathName + " file on line " + lineCount + ".\n"
					+ e.getMessage(), e);
		}

		return ret;
	}

	/**
	 * 
	 * @param ActionRuleRunnerId: the concept identifier to which the action is applied
	 * @param command
	 * @param runtimeParams
	 * @return null or the first runner that fail
	 */
	@Override
	public ActionRuleRunnerI runAction(String ActionRuleRunnerId, String command, Object[] runtimeParams) {
		// get the serie Runner
		boolean createIfNotExist = false;

		ActionRuleSerieRunnerI serieRunner = getActionRuleSerieRunner(ActionRuleRunnerId, createIfNotExist);
		// perform all checks and return null or the first Runner that fail
		if (serieRunner == null)
			return null;
//	if (ActionRuleRunnerId.equals("diagnosis_final_main"))
//		System.out.println("breakpoint");
		return serieRunner.runAllAction(command, runtimeParams);
	}
	
	
	
	/**
	 * 
	 * @param ActionRuleRunnerId
	 * @param runnerName
	 * @return the action identified by both parameters
	 */
	@Override
	public ActionRuleRunnerI getAction(String ActionRuleRunnerId, String runnerName) {
		// get the serie Runner
		boolean createIfNotExist = false;
		ActionRuleSerieRunnerI serieRunner = getActionRuleSerieRunner(ActionRuleRunnerId, createIfNotExist);
		//
		if (serieRunner == null)
			return null;
//			throw new ConfigException("Action not found:"+ActionRuleRunnerId+", runnerName:"+runnerName);
		return serieRunner.getActionRunner(runnerName);
	}

	
	
	/**
	 * perform a check on valueToCheck using a checker identified by businessRuleCheckerId
	 * @param businessRuleCheckerId
	 * @param valueToCheck
	 * @param runtimeParams: params for checker
	 * @return null or the first checker that fail
	 */
	@Override
	public BusinessRuleCheckerI check(/*CheckableI checkable*/ String businessRuleCheckerId, String valueToCheck, Object[] runtimeParams){
//		String key = checkable.getKey();
		// get the serie checker
		BusinessRuleSerieCheckerI serieChecker = getBusinessRuleSerieChecker(businessRuleCheckerId);
		// perform all checks and return null or the first checker that fail
		return serieChecker.checkAllBR(valueToCheck, runtimeParams);
	}
	

//	protected HashMap<String, ActionRuleRunnerI> getActionRuleRunners() {
//		return _ActionRuleRunners;
//	}

	protected HashMap<String, ActionRuleSerieRunnerI> getConceptRuleSerieRunners() {
		return _ConceptRuleSerieRunners;
	}
	
//	protected HashMap<String, BusinessRuleCheckerI> getBusinessRuleCheckers() {
//		return _BusinessRuleCheckers;
//	}

	protected HashMap<String, BusinessRuleSerieCheckerI> getConceptRuleSerieCheckers() {
		return _ConceptRuleSerieCheckers;
	}

}
