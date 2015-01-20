package ch.msf.model.actionrunner;

import java.util.ArrayList;

import ch.msf.CommonIpdConstants;
import ch.msf.error.ConfigException;
import ch.msf.manager.EntryFormConfigurationManagerImpl;
import ch.msf.service.ServiceHelper;

/**
 * THIS CLASS IS AN OBSERVER, AND IS NOTIFIED OF CONCEPT CHANGED FROM ANOTHER
 * ACTION IT ENABLE A CONCEPT GRAPHICAL COMPONENT LINE WHEN ALL CONDITIONS ARE
 * MET
 * 
 * @author cmi
 * 
 */
// ConceptDisplayerAction
// params ::= <conceptNotifierParams> <OPERATION> {<conceptNotifierParams>
// <OPERATION>...}
// conceptNotifierParams ::= COMMAND conceptSource conceptSourceValueToCheck
// OPERATION ::= "AND" | "OR"

public class ConceptEnablerActionRunner extends AbstractActionRuleRunnerBase {

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
	// note: on the first notify, when panel shows up
	// if the sourceConceptId is at a value that disable or enable
	// thisNotifierId
	// depending on the value of thisNotifierId, this might turn in red the
	// entity panel...
	public boolean runAction(String command, Object[] runtimeParams) {

		boolean ret = false;

		if (command.equals(CommonIpdConstants.ACTION_RULES_REINIT)) {
			// we should reinit our components
			for (CommandCriteria criteria : _Criterias) {
				criteria.setValue(null);
			}
			return true; // ok

		} else if (command.equals(CommonIpdConstants.ACTION_RULES_NOTIFY)) {

			String sourceConceptId = (String) runtimeParams[0];
			String newValue = (String) runtimeParams[1];
			String thisNotifierId = (String) runtimeParams[2];

			for (CommandCriteria criteria : _Criterias) {
				if (criteria._ConceptSourceId.equals(sourceConceptId)) {
					criteria.setValue(newValue);
					// break; can be several from same conceptid
				}
			}

			// make the check to know if all conditions are met to run the
			// command
			// init allCriteriaOk to the first criterium
			boolean allCriteriaOk = _Criterias.get(0).valueIdentical();

			for (CommandCriteria criteria : _Criterias) {
				String operation = criteria._Operation;
				if (operation.equals("OR")) {
					allCriteriaOk |= criteria.valueIdentical();
				} else if (operation.equals("AND")) {
					allCriteriaOk &= criteria.valueIdentical();
				}
			}

			// all notifiers are ok
			EntryFormConfigurationManagerImpl _ConfigurationManager = ServiceHelper.getEntryFormConfigurationManagerService();

			// get our component to update
			ComponentEnabler componentEnabler = _ConfigurationManager.getComponentCache().get(_GraphicalComponent);

			// TODO...differently...
			if (componentEnabler == null)
				throw new ConfigException("_GraphicalComponent not found!");

			componentEnabler.enableConcept(thisNotifierId, allCriteriaOk);
			ret = true;

		} else if (command.equals(CommonIpdConstants.ACTION_RULES_VALUECHANGED)) { // TN110
			// we must check that the value is not changed while the
			// sourceConceptId is null!
			// it happens when a new entity is created...

			String thisNotifierId = (String) runtimeParams[0];
			String newValue = (String) runtimeParams[1];
			if (newValue == null)
				return true; // no problem

			// at creation there should be a notify message that set all criterias to null
			// therefore, if all criteria are null
			// it should be impossible to change the value, so disable the component.
			boolean disableComponent = true;
			for (CommandCriteria criteria : _Criterias) {
				if (criteria.getConceptValue() != null) {
					disableComponent = false;
					break;
				}
			}

			if (disableComponent) {
				// all notifiers are ok
				EntryFormConfigurationManagerImpl _ConfigurationManager = ServiceHelper.getEntryFormConfigurationManagerService();

				// get our component to update
				ComponentEnabler componentEnabler = _ConfigurationManager.getComponentCache().get(_GraphicalComponent);

				// TODO...differently...
				if (componentEnabler == null)
					throw new ConfigException("_GraphicalComponent not found!");

				// disable the component
				componentEnabler.enableConcept(thisNotifierId, false);
			}

			ret = true;
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

		public String getConceptValue() {
			return _ConceptValue;
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
