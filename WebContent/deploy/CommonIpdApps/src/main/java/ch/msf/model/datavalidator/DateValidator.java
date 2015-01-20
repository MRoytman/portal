package ch.msf.model.datavalidator;

import java.util.Date;

public class DateValidator extends DefaultDataValidator {

	public DateValidator(String checkableId, String dataPattern) {

		super(checkableId, dataPattern);
	}

	public Integer checkBusinessRules(String valueToCheck, Date referenceEntityDate) {
		Object[] runtimeParams = new Object[1];
		runtimeParams[0] = referenceEntityDate;
		return checkBusinessRules(valueToCheck, runtimeParams);
	}

	@Override
	public boolean doFormatChecks(String valueToCheck) {

		// boolean ok = super.doFormatChecks(valueToCheck);
		if (valueToCheck == null || valueToCheck.equals(""))
			return true;
		if (getFormatPattern() == null)
			return true;
			
		return (valueToCheck.length() == getFormatPattern().length());

	}

}
