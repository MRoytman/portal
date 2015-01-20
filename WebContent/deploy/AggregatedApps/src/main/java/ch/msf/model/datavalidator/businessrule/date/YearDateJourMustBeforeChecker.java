package ch.msf.model.datavalidator.businessrule.date;

import java.math.BigDecimal;

import ch.msf.CommonConstants;
import ch.msf.error.ConfigException;
import ch.msf.model.datavalidator.businessrule.AbstractBusinessRuleCheckerBase;
import ch.msf.service.ServiceHelper;

/**
 * DATE CHECKER THAT VERIFY IF YEAR VALUE DATE IS AFTER THE DATE RUNTIME
 * PARAMETER ADDED TO A GIVEN NUMBER OF DAYS (POSITIVE OR
 * NEGATIVE)
 * 
 * @author cmi
 * TN12
 */
public class YearDateJourMustBeforeChecker extends AbstractBusinessRuleCheckerBase {

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
				// if parameter exists, it should be a positive integer equal to
				// the number of years in future compare to today's year
				String offsetNumber = getConfigParameters()[0];
				Integer.parseInt(offsetNumber);
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
		Long offsetNumberYearInPast = null;
		// ConfigParameters have already been checked
		if (getConfigParameters() != null && getConfigParameters().length > 0) {
			offsetNumberYearInPast = Long.parseLong(getConfigParameters()[0]);
		}
		else
			return false;

		// take first argument as the reference date
		Long dateValueToCheck;
		try {
			// Date todayMidnight = DateUtil.midnight(new Date());
			if (runtimeParams == null || runtimeParams.length == 0 || !(runtimeParams[0] instanceof Number)) {
				// fetch error message
				int errorCode = CommonConstants.MESSAGE_BAD_PARAM;
				String errMess = ServiceHelper.getMessageService().getMessage(errorCode);
				throw new ConfigException(errMess);
			}

			Long limitYear = ((BigDecimal)runtimeParams[0]).longValue() + offsetNumberYearInPast;

			dateValueToCheck = Long.parseLong(getValueToCheck());

			if (!(dateValueToCheck < limitYear))
				return false;
		} catch (Exception e) {
			return false;
		}
		return true;
	}

}
