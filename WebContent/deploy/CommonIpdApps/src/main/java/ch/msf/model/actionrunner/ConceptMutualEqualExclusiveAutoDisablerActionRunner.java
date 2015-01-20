package ch.msf.model.actionrunner;

import java.util.ArrayList;

import ch.msf.CommonIpdConstants;
import ch.msf.error.ConfigException;
import ch.msf.manager.EntryFormConfigurationManagerImpl;
import ch.msf.service.ServiceHelper;

/**
 * THIS CLASS IS AN OBSERVER, AND IS NOTIFIED OF CONCEPT CHANGED FROM ANOTHER
 * ACTION IT DISABLE A CONCEPT GRAPHICAL COMPONENT LINE WHEN ALL CONDITIONS ARE
 * MET
 * 
 * @author cmi
 * 
 */

public class ConceptMutualEqualExclusiveAutoDisablerActionRunner extends AbstractActionRuleRunnerBase {

	private ArrayList<CommandCriteria> _Criterias = new ArrayList<CommandCriteria>();
	private final static int NBR_CONSTPARAMS = 1;
	private final static int NBR_VARIABLEPARAMS = 4;

	private String _GraphicalComponent;

	@Override
	public void verifyConfigParameters() throws ConfigException {

		String errMess = getClass().getName() + "::verifyParameters: args problem :" + getConfigParameters()
				+ ", there should be at least 3 parameters (COMMAND	conceptSource	conceptSourceValueToCheck OPERATION).";

		if (getConfigParameters() != null && getConfigParameters().length < (NBR_VARIABLEPARAMS + NBR_CONSTPARAMS)
				|| (getConfigParameters().length - NBR_CONSTPARAMS) % NBR_VARIABLEPARAMS != 0)
			throw new ConfigException(errMess);

		_GraphicalComponent = getConfigParameters()[0];

		// read the variable part
		// int nbrVaryParams = getConfigParameters().length - NBR_CONSTPARAMS;
		int nbrVaryParams = getConfigParameters().length;
		int index = NBR_CONSTPARAMS;
		// fill the criteria
		do {
			CommandCriteria criteria = new CommandCriteria(getConfigParameters()[index], getConfigParameters()[index + 1], getConfigParameters()[index + 2],
					getConfigParameters()[index + 3]);
			_Criterias.add(criteria);
			index += NBR_VARIABLEPARAMS;
		} while (index < nbrVaryParams);

	}

	@Override
	public boolean runAction(String command, Object[] runtimeParams) {

		boolean ret = true;

		String sourceConceptId = null;
		if (runtimeParams != null && runtimeParams.length>0){
			sourceConceptId = (String) runtimeParams[0];
		}
		String newValue = null;
		if (runtimeParams != null && runtimeParams.length>1){
			newValue = (String) runtimeParams[1];
		}
		

		if (command.equals(CommonIpdConstants.ACTION_RULES_REINIT)) {
			// we should reinit our components
			for (CommandCriteria criteria : _Criterias) {
					criteria.setValue(null);
			}
			return true; //ok

		} else if (command.equals(CommonIpdConstants.ACTION_RULES_NOTIFY)) {
			// we are notified by another mutual exclusive component

			// set the new value for one of the other mutual exclusive component
			for (CommandCriteria criteria : _Criterias) {
				if (criteria._ConceptSourceId.equals(sourceConceptId)) {
					criteria.setValue(newValue);
					// break; can be several from same conceptid
				}
			}

		} else if (command.equals(CommonIpdConstants.ACTION_RULES_VALUECHANGED)) {
			// our component has just changed
			String thisNotifierId = sourceConceptId;

			// make the check to know if all conditions are met to run the
			// command
			// init allCriteriaOk to the first criterium
			boolean allConditionMet = false; // criterias.get(0).valueIdentical();

			for (CommandCriteria criteria : _Criterias) {
				String operation = criteria._Operation;
				if (operation.equals("XOR")) {
					if (criteria._ConceptValue != null && newValue != null) {
						allConditionMet = (criteria._ConceptValue.equals(newValue));
						if (allConditionMet)
							// one is enough to be exclusive
							break;
					}
				}
			}

			// all notifiers are ok
			EntryFormConfigurationManagerImpl _ConfigurationManager = ServiceHelper.getEntryFormConfigurationManagerService();

			// get our component to update
			ComponentEnabler componentEnabler = _ConfigurationManager.getComponentCache().get(_GraphicalComponent);

			// TODO...differently...
			if (componentEnabler == null)
				throw new ConfigException("_GraphicalComponent not found!");
			//
			componentEnabler.disableConcept(thisNotifierId, allConditionMet);
			// return false if disabling
			ret = !allConditionMet;

		}

		return ret;
	}

	class CommandCriteria {
		public String _Command;
		public String _ConceptSourceId;
		public String _ConceptValueToMatch;
		public String _Operation;

		private String _ConceptValue;
		private boolean _Identical;

		public CommandCriteria(String command, String conceptSourceId, String conceptValueToMatch, String operation) {
			_Command = command;
			_ConceptSourceId = conceptSourceId;
			_ConceptValueToMatch = conceptValueToMatch;
			_Operation = operation;
		}

		public void setValue(String newValue) {
			_ConceptValue = newValue;
			_Identical = _ConceptValueToMatch.equals(_ConceptValue);
		}

		public boolean valueIdentical() {
			return _Identical;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "CommandCriteria [_ConceptSourceId=" + _ConceptSourceId + ", _ConceptValueToMatch=" + _ConceptValueToMatch + ", _Operation=" + _Operation + ", _ConceptValue="
					+ _ConceptValue + ", _Identical=" + _Identical + ", hashCode=" + hashCode() + "]";
		}
	}

}
