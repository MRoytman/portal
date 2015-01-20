package ch.msf.model.datavalidator;

import ch.msf.CommonIpdConstants;
import ch.msf.model.actionrunner.ActionRuleRunnerI;
import ch.msf.model.datavalidator.businessrule.BusinessRuleCheckerI;
import ch.msf.service.ServiceHelper;

public class DefaultDataValidator implements DataValidator {

	protected String _CheckableId;

	protected String _FormatPattern;

	public DefaultDataValidator(String checkableId) {

		setCheckableId(checkableId);
	}

	public DefaultDataValidator(String checkableId, String dataPattern) {

		setCheckableId(checkableId);
		setFormatPattern(dataPattern);
	}

	@Override
	public boolean doFormatChecks(String valueToCheck) {
		return true;
	}

	@Override
	public Integer checkBusinessRules(String valueToCheck, Object[] runtimeParams) {

		if (getCheckableId() == null) { // ok case
			return null;
		}

		BusinessRuleCheckerI businessRuleChecker = null;
		if (valueToCheck != null && !valueToCheck.equals("")) {
			// load the business rules
			businessRuleChecker = ServiceHelper.getActionRuleService().check(getCheckableId(), valueToCheck, runtimeParams);
		}

		if (businessRuleChecker == null) { // ok case
			return null;
		}
		Integer errCode = businessRuleChecker.getErrorCode();
		return errCode;
	}

	@Override
	public Integer runAction(String command, Object[] runtimeParams) {

		if (getCheckableId() == null) { // ok case
			return null;
		}

		ActionRuleRunnerI actionRuleRunner = null;
		if (command != null && !command.equals("")) {
			// load the business rules

			actionRuleRunner = ServiceHelper.getActionRuleService().runAction(getCheckableId(), command, runtimeParams);
		}

		if (actionRuleRunner == null) { // ok case
			return null;
		}
		Integer errCode = actionRuleRunner.getErrorCode();
		return errCode;
	}
	

	@Override
	public void initParamAction() {

		if (getCheckableId() == null) { // ok case
			return ;
		}

		ServiceHelper.getActionRuleService().runAction(getCheckableId(), CommonIpdConstants.ACTION_RULES_REINIT, null);

	}

	/**
	 * @return the _CheckableId
	 */
	public String getCheckableId() {
		return _CheckableId;
	}

	/**
	 * @param _CheckableId
	 *            the _CheckableId to set
	 */
	public void setCheckableId(String _CheckableId) {
		this._CheckableId = _CheckableId;
	}

	/**
	 * @return the _FormatPattern
	 */
	public String getFormatPattern() {
		return _FormatPattern;
	}

	/**
	 * @param _FormatPattern
	 *            the _FormatPattern to set
	 */
	public void setFormatPattern(String _FormatPattern) {
		this._FormatPattern = _FormatPattern;
	}


}
