package ch.msf.model.actionrunner;

import ch.msf.CommonIpdConstants;
import ch.msf.error.ConfigException;
import ch.msf.service.ServiceHelper;

/**
 * THIS CLASS DOES A MAPPING OF ACTION BETWEEN TWO CONCEPTS IT ALLOWS THE
 * observer TO BE INFORMED OF DATA CHANGED FROM THE NOTIFIER CONCEPT
 * 
 * @author cmi
 * 
 */
// MapperNotifierActionRunner
// <conceptId> <RuleRunnerModuleName> <errorMessageCode> <params>
// params ::= <observerConceptId> <observer RuleRunnerModuleName> NOTIFY
// aggression_mechanism1 MapperNotifierActionRunner 2000 penetration_site
// ConceptDisablerActionRunner NOTIFY
public class MapperNotifierActionRunner extends AbstractActionRuleRunnerBase {

	// private String _ThisNotifierId;

	private String _ObserverId;
	// observer class name
	private String _ConceptRunnerName;
	// the command to send to the observer
	private String _ObserverCommand;
	
	public MapperNotifierActionRunner(){
//		System.out.println(getClass().getName()+" "+this.hashCode());
	}

	@Override
	public void verifyConfigParameters() throws ConfigException {

		String errMess = getClass().getName() + "::verifyParameters: args problem :" + getConfigParameters()
				+ ", there should be 3 parameters (observer conceptId, observer action, command).";

		if (getConfigParameters() != null && getConfigParameters().length == 3) {
			// _ThisNotifierId = getConfigParameters()[0];
			_ObserverId = getConfigParameters()[0];
			_ConceptRunnerName = getConfigParameters()[1];
			_ObserverCommand = getConfigParameters()[2];
		} else
			throw new ConfigException(errMess);
	}

	@Override
	public boolean runAction(String command, Object[] runtimeParams) {

		boolean ret = false;

		if (command.equals(CommonIpdConstants.ACTION_RULES_REINIT)) {
			// we should reinit our components
			return true; //ok

		} else if (command.equals(CommonIpdConstants.ACTION_RULES_VALUECHANGED)) {
			// get the observer action and run its action
			ActionRuleRunnerI actionRuleRunner = ServiceHelper.getActionRuleService().getAction(_ObserverId, _ConceptRunnerName);
			if (actionRuleRunner == null)
				throw new ConfigException("Action not found for observerId:" + _ObserverId + ", runnerName:" + _ConceptRunnerName);
			// notify the observer
			// should add the observer id
			Object[] runtimeParamsBigger = new Object[runtimeParams.length + 1];
			// System.arraycopy(src, srcPos, dest, destPos, length);
			System.arraycopy(runtimeParams, 0, runtimeParamsBigger, 0, runtimeParams.length);
			runtimeParamsBigger[2] = _ObserverId;
			ret = actionRuleRunner.runAction(_ObserverCommand, runtimeParamsBigger);
		}
		else
			ret = true;
		return ret;
	}

}
