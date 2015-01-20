package ch.msf.model.actionrunner;

import java.util.ArrayList;

import ch.msf.CommonIpdConstants;
import ch.msf.error.ConfigException;
import ch.msf.manager.EntryFormConfigurationManagerImpl;
import ch.msf.service.ServiceHelper;

/**
 * THIS CLASS IS AN OBSERVER, AND IS NOTIFIED OF CONCEPT CHANGED FROM ANOTHER ACTION
 * 
 * It compute a total from other cells. This total can never be negative
 * 
 * @author cmi
 * 
 */

public class PositiveCalculatorActionRunner extends AbstractActionRuleRunnerBase {

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
		if (runtimeParams != null && runtimeParams.length > 0) {
			sourceConceptId = (String) runtimeParams[0];
		}
		String newValue = null;
		if (runtimeParams != null && runtimeParams.length > 1) {
			newValue = (String) runtimeParams[1];
		}

		if (command.equals(CommonIpdConstants.ACTION_RULES_REINIT)) {
			// we should reinit our components
			for (CommandCriteria criteria : _Criterias) {
				criteria.setValue(null);
			}
			return true; // ok

		} else if (command.equals(CommonIpdConstants.ACTION_RULES_NOTIFY)) {
			// we are notified by another mutual exclusive component
			String thisNotifierId = (String) runtimeParams[2];

			// set the new value for one of the other mutual exclusive component
			for (CommandCriteria criteria : _Criterias) {
				if (criteria._ConceptSourceId.equals(sourceConceptId)) {
					criteria.setValue(newValue);
					// break; can be several from same conceptid
				}
			}
			int total = doOperation();
			ret = doUpdate(thisNotifierId, total);

		}

		return ret;
	}

	/**
	 * do the computation
	 */
	private int doOperation() {

		int nbrParams = _Criterias.size();
		int indexParam = 0;

		int total = _Criterias.get(indexParam++).getIntValue();
		try {
			do {
				CommandCriteria currentCriteria = _Criterias.get(indexParam);
				int currentNumber = currentCriteria.getIntValue();
				switch (currentCriteria._OperationType) {
				case ADD:
					total += currentNumber;
					break;
				case SUB:
					total -= currentNumber;
					break;
				}

			} while (++indexParam < nbrParams);
		} catch (IndexOutOfBoundsException e) {
			// probably only one argument
			assert _Criterias.size() == 1;
			// return value of uniq cell
		}
		//TN10
		if (total < 0)
			total = 0;
		return total;
	}

	private boolean doUpdate(String sourceConceptId, int total) {
		String thisNotifierId = sourceConceptId;

		// all notifiers are ok
		EntryFormConfigurationManagerImpl _ConfigurationManager = ServiceHelper.getEntryFormConfigurationManagerService();

		// get our component to update
		ComponentCellUpdator componentCellUpdator = _ConfigurationManager.getComponentUpdatorCache().get(_GraphicalComponent);

		//
		if (componentCellUpdator == null) {
			System.out.println("_GraphicalComponent not found!:" + _GraphicalComponent);
			// throw new ConfigException("_GraphicalComponent not found!:" + _GraphicalComponent);
			return false; // TN9 do throw exception because the cache might not be completely filled with GraphicalComponents
		}
		//
		if (componentCellUpdator != null) {
			componentCellUpdator.updateCell(thisNotifierId, total);
		}
		//
		return true;
	}

	public enum OperationType {
		ADD, SUB, MUL, DIV
	};

	class CommandCriteria {
		public String _Command;
		public String _ConceptSourceId;
		public String _ConceptValueToMatch;
		// public String _Operation;
		public OperationType _OperationType;

		private String _ConceptValue;
		private boolean _Identical;

		public CommandCriteria(String command, String conceptSourceId, String conceptValueToMatch, String operation) {
			_Command = command;
			_ConceptSourceId = conceptSourceId;
			_ConceptValueToMatch = conceptValueToMatch;
			// _Operation = operation;
			if (operation.equals("ADD")) {
				_OperationType = OperationType.ADD;
			} else if (operation.equals("MUL")) {
				_OperationType = OperationType.MUL;
			} else if (operation.equals("SUB")) {
				_OperationType = OperationType.SUB;
			} else if (operation.equals("DIV")) {
				_OperationType = OperationType.DIV;
			}
		}

		public void setValue(String newValue) {
			_ConceptValue = newValue;
			_Identical = _ConceptValueToMatch.equals(_ConceptValue);
		}

		// public boolean valueIdentical() {
		// return _Identical;
		// }

		public int getIntValue() {
			if (_ConceptValue == null)
				return 0;
			return Integer.parseInt(_ConceptValue);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "CommandCriteria [_ConceptSourceId=" + _ConceptSourceId + ", _ConceptValueToMatch=" + _ConceptValueToMatch + ", _Operation=" + _OperationType
					+ ", _ConceptValue=" + _ConceptValue + ", _Identical=" + _Identical + ", hashCode=" + hashCode() + "]";
		}
	}

}
