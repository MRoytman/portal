package ch.msf.model.datavalidator.businessrule.number;

import ch.msf.error.ConfigException;
import ch.msf.model.datavalidator.businessrule.AbstractBusinessRuleCheckerBase;

/**
 * NUMBER CHECKER THAT VERIFY IF ENTERED VALUE IS BETWEEN CONFIG PARAMETERS
 * ex: conceptId	number.LimitInclusiveChecker	500	0	100
 * 0<= number <=101
 * 
 * @author cmi
 * 
 */

public class LimitInclusiveChecker extends AbstractBusinessRuleCheckerBase {

	/**
	 * verifyParam allow to verify the validity of params before runtime
	 * 
	 * @param args
	 * @throws ConfigException
	 *             if args are not valid
	 */
	public void verifyConfigParameters() throws ConfigException {

		String errMess = getClass().getName() + "::verifyParameters: args problem :" + getConfigParameters()
				+ ", there should be one parameter representing a number of days (positive or negative).";
		try {

			if (getConfigParameters() != null && getConfigParameters().length > 0) {
				String nbrLimit = getConfigParameters()[0];
				Float.parseFloat(nbrLimit);
				nbrLimit = getConfigParameters()[1];
				Float.parseFloat(nbrLimit);
			}
		} catch (Exception e) {
			throw new ConfigException(errMess, e);
		}
	}

	/**
	 * business rule implementation
	 * 
	 * @param runtimeParams
	 *            :
	 * @return true if the value comply with the nature of the checker
	 */
	@Override
	public boolean check(Object[] runtimeParams) {
		Float limitInf = 0.0F;
		Float limitSup = 0.0F;

			try {
				// ConfigParameters have already been checked
				if (getConfigParameters() != null && getConfigParameters().length > 0) {
//					System.out.println("checklimitInf:::"+getConfigParameters()[0]);
					limitInf = Float.parseFloat(getConfigParameters()[0]);
//					System.out.println("checklimitSup:::"+getConfigParameters()[1]);
					limitSup = Float.parseFloat(getConfigParameters()[1]);
				}

//				System.out.println("checkgetValueToCheck():::"+getValueToCheck());
				Float newValue = Float.parseFloat(getValueToCheck());

				if (newValue < limitInf)
					return false;
				if (newValue > limitSup)
					return false;
			} catch (NumberFormatException e) {
//				System.out.println("NumberFormatException e");
//				throw new ConfigException(errMess);
				return false; //TN121
			}

		return true;
	}
}
