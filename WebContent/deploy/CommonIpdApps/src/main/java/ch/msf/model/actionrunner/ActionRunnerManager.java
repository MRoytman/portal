package ch.msf.model.actionrunner;

import java.io.IOException;

import ch.msf.model.datavalidator.businessrule.BusinessRuleCheckerI;

public interface ActionRunnerManager {


	/**
	 * load all Action rules configuration
	 * @param resourcePathName
	 * @return true if concept resources have been loaded correctly
	 * @throws IOException
	 */
	public boolean loadActionRulesResources(String resourcePathName);


	/**
	 * 
	 * @param ActionRuleRunnerId: the concept identifier to which the action is applied
	 * @param command
	 * @param runtimeParams
	 * @return null or the first runner that fail
	 */
	ActionRuleRunnerI runAction(String ActionRuleRunnerId, String command, Object[] runtimeParams);
	
	
	/**
	 * 
	 * @param ActionRuleRunnerId
	 * @param runnerName
	 * @return the action identified by both parameters
	 */
	ActionRuleRunnerI getAction(String ActionRuleRunnerId, String runnerName);
	
	
	
	/**
	 * perform a check on valueToCheck using a checker identified by businessRuleCheckerId
	 * @param businessRuleCheckerId
	 * @param valueToCheck
	 * @param runtimeParams: params for checker
	 * @return null or the first checker that fail
	 */
	BusinessRuleCheckerI check(String businessRuleCheckerId, String valueToCheck, Object[] runtimeParams);

}
