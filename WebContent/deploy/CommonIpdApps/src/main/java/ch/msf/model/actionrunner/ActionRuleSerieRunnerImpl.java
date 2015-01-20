package ch.msf.model.actionrunner;

import java.util.ArrayList;

/**
 * THIS CLASS ALLOW TO CHECK A valueToCheck AGAINST A SERIE OF ActionRuleRunnerI
 * @author cmi
 *
 */
public class ActionRuleSerieRunnerImpl implements ActionRuleSerieRunnerI {

	private String _CheckableId;
	private ArrayList<ActionRuleRunnerI> _ActionRules = new ArrayList<ActionRuleRunnerI>();


	/**
	 * 
	 * @param command
	 * @param runtimeParams
	 * @return null if all check passed successfully on value or the checker that found an error
	 */
	@Override
	public ActionRuleRunnerI runAllAction(String command, Object[] runtimeParams) {
		for (ActionRuleRunnerI rule : getActionRules()) {
			// if rule not abided, return it
			if (!rule.runAction(command, runtimeParams))
				return rule;
		}
		return null;
	}

	@Override
	public void addActionRunner(ActionRuleRunnerI ActionRuleRunnerI) {
		getActionRules().add(ActionRuleRunnerI);
	}

	public String getCheckableId() {
		return _CheckableId;
	}


	public ArrayList<ActionRuleRunnerI> getActionRules() {
		return _ActionRules;
	}

	public void setActionRules(ArrayList<ActionRuleRunnerI> _ActionRules) {
		this._ActionRules = _ActionRules;
	}

	/**
	 * 
	 * @param runnerName: the class name's runner
	 * @return the runner identifier by runnerName
	 */
	@Override
	public ActionRuleRunnerI getActionRunner(String runnerName) {
		for (ActionRuleRunnerI rule : getActionRules()) {
			String className = rule.getClass().getName();
			className = className.substring(className.lastIndexOf('.')+1);
			if (className.equals(runnerName))
				return rule;
		}
		return null;
	}

}
