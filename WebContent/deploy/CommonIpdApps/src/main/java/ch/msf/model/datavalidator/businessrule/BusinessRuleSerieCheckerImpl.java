package ch.msf.model.datavalidator.businessrule;

import java.util.ArrayList;

/**
 * THIS CLASS ALLOW TO CHECK A valueToCheck AGAINST A SERIE OF BusinessRuleCheckerI
 * @author cmi
 *
 */
public class BusinessRuleSerieCheckerImpl implements BusinessRuleSerieCheckerI {

//	private ConceptIdValue _Concept;
	private String _CheckableId;
	private ArrayList<BusinessRuleCheckerI> _BusinessRules = new ArrayList<BusinessRuleCheckerI>();


	/**
	 * perform all checks on the checkable
	 * @param valueToCheck 
	 * @return null if all check passed successfully on value or the checker that found an error
	 */
	@Override
	public BusinessRuleCheckerI checkAllBR(String valueToCheck, Object[] runtimeParams) {
		for (BusinessRuleCheckerI rule : getBusinessRules()) {
			if (!rule.check(valueToCheck, runtimeParams))
				return rule;
		}
		return null;
	}

	@Override
	public void addBRChecker(BusinessRuleCheckerI businessRuleCheckerI) {
		getBusinessRules().add(businessRuleCheckerI);
	}

	public String getCheckableId() {
		return _CheckableId;
	}


	public ArrayList<BusinessRuleCheckerI> getBusinessRules() {
		return _BusinessRules;
	}

	public void setBusinessRules(ArrayList<BusinessRuleCheckerI> _BusinessRules) {
		this._BusinessRules = _BusinessRules;
	}

//	@Override
//	public void setCheckable(CheckableI checkable) {
//		_Checkable = checkable;
//	}

}
