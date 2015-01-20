package ch.msf.model.actionrunner;

public interface ActionRuleSerieRunnerI {

	/**
	 * add a Action rule checker to this serie cheker
	 * @param ActionRuleCheckerI
	 */
	void addActionRunner(ActionRuleRunnerI ActionRuleCheckerI);


	/**
	 * 
	 * @param command
	 * @param runtimeParams
	 * @return null if all check passed successfully on value or the checker that found an error
	 */
	ActionRuleRunnerI runAllAction(String command, Object[] runtimeParams);
	
	
	/**
	 * 
	 * @param runnerName: the class name's runner
	 * @return the runner identifier by runnerName
	 */
	ActionRuleRunnerI getActionRunner(String runnerName);
}
