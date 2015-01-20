package ch.msf.model.datavalidator.businessrule.date;

import java.text.ParseException;
import java.util.Date;

import ch.msf.CommonConstants;
import ch.msf.CommonIpdConstants;
import ch.msf.error.ConfigException;
import ch.msf.model.datavalidator.businessrule.AbstractBusinessRuleCheckerBase;
import ch.msf.service.ServiceHelper;
import ch.msf.util.DateUtil;

/**
 * DATE CHECKER THAT VERIFY IF VALUE DATE IS AFTER THE DATE RUNTIME
 * PARAMETER(entity creation date) ADDED TO A GIVEN NUMBER OF DAYS (POSITIVE OR
 * NEGATIVE) ex: conceptDateId date.DateJourMustAfterChecker 1 -30 entityDate-30
 * < newValueDate
 * 
 * @author cmi
 * 
 */
public class DateJourMustAfterChecker extends AbstractBusinessRuleCheckerBase {

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
				// the number of days in future compare to today
				String nbrOfDayStr = getConfigParameters()[0];
				Integer.parseInt(nbrOfDayStr);
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
		Integer nbrOfDayInPast = 0;
		// ConfigParameters have already been checked
		if (getConfigParameters() != null && getConfigParameters().length > 0) {
			nbrOfDayInPast = Integer.parseInt(getConfigParameters()[0]);
		}

		// take first argument as the reference date
		Date dateValueToCheck;
		try {
			// Date todayMidnight = DateUtil.midnight(new Date());
			if (runtimeParams == null || runtimeParams.length == 0 || !(runtimeParams[0] instanceof Date)) {
				// fetch error message
				int errorCode = CommonConstants.MESSAGE_BAD_PARAM;
				String errMess = ServiceHelper.getMessageService().getMessage(errorCode);
				throw new ConfigException(errMess);
			}
			Date entityCreationDateMidnight = DateUtil.midnight((Date) runtimeParams[0]);
			Date limitDate = DateUtil.addDays(entityCreationDateMidnight, nbrOfDayInPast);

			dateValueToCheck = CommonConstants._tsSdf.parse(getValueToCheck());

			if (!dateValueToCheck.after(limitDate))
				return false;
		} catch (ParseException e) {
			return false;
		}
		return true;
	}

}
