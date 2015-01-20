package ch.msf.model.datavalidator;

import java.math.BigDecimal;


public class NumericValidator  extends DefaultDataValidator {

	public NumericValidator(String checkableId /*, String dataPattern*/) {

		super(checkableId/*, dataPattern*/);
	}

	public Integer checkBusinessRules(String valueToCheck) {
		Object[] runtimeParams = new Object[1];
		runtimeParams[0] = null;
		return checkBusinessRules(valueToCheck, runtimeParams);
	}

	public Integer checkBusinessRules(String valueToCheck, BigDecimal runtimeArg) {
		Object[] runtimeParams = new Object[1];
		runtimeParams[0] = runtimeArg;
		return checkBusinessRules(valueToCheck, runtimeParams);
	}

	@Override
	public boolean doFormatChecks(String valueToCheck) {
		// do not accept ',' //TN121
		if (valueToCheck != null)
			if (valueToCheck.indexOf(',') != -1)//TN121
				return false;
			return true;
	}

}
