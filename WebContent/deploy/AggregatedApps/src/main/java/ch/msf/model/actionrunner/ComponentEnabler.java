package ch.msf.model.actionrunner;

public interface ComponentEnabler {
	
	public void enableConcept(String conceptId, boolean allCriteriaOk);
	
	public void disableConcept(String conceptId, boolean allCriteriaOk);

}
