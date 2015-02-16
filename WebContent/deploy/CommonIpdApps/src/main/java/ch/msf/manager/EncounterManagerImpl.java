package ch.msf.manager;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import ch.msf.form.ParamException;
import ch.msf.model.ConceptIdValue;
import ch.msf.model.Encounter;
import ch.msf.model.Patient;
import ch.msf.model.PatientIdValue;
import ch.msf.model.PatientIdentifier;
import ch.msf.service.ServiceHelper;

/**
 * 
 * @author cmi
 * 
 */
public class EncounterManagerImpl implements EncounterManager {

	/**
	 * get all patient encounters ordered by encounter entered date
	 */
	@Override
	public List<Encounter> getAllPatientEncounters(Patient currentPatient, boolean retired) {
		EntityManager em = getDbManager().startTransaction();

		String strQuery = "select e from Encounter e join e._Patient pat where pat.id = :patientId and e._Retired = :retired ";
		strQuery += " order by e._Date "; // TN144

		Query qry = em.createQuery(strQuery);
		qry.setParameter("patientId", currentPatient.getId());
		qry.setParameter("retired", retired);

		List<Encounter> retList = qry.getResultList();

		if (retList != null)
			for (Encounter Encounter : retList) {
				readEncounterInfo(Encounter);
			}

		getDbManager().endTransaction(em);

		return retList;
	}

	//taivd add getAllEncounters
	@Override
	public List<Encounter> getAllEncounters() {
		EntityManager em = getDbManager().startTransaction();
		List<Encounter> retList =em.createQuery("select e from Encounter e").getResultList();

		if (retList != null)
			for (Encounter Encounter : retList) {
				readEncounterInfo(Encounter);
			}

		getDbManager().endTransaction(em);

		return retList;
	}
	
	/**
	 * can also return a deleted Encounter
	 */
	@Override
	public Encounter getEncounter(Long encounterId) {

		if (encounterId == null)
			return null;

		EntityManager em = getDbManager().startTransaction();
		Encounter encounter = em.find(Encounter.class, encounterId);
		readEncounterInfo(encounter);

		getDbManager().endTransaction(em);

		return encounter;
	}

	// @Override
	// /**
	// *
	// */
	// public List<Encounter> getSelectedEncounter(EncounterIdentifier
	// EncounterIdentifier) {
	//
	// EntityManager em = getDbManager().startTransaction();
	//
	// List<Encounter> retList = em
	// .createQuery(
	// //
	// "select pat from EncounterIdentifier pi,  join pi._Encounter pat join pat.id ")
	// //
	// "select pat from EncounterIdentifier pi, Encounter pat, EncounterContext pc, where pi._Encounter = pat.id and pat.id = pc._Encounter ")
	// "select pat from EncounterIdentifier pi, EncounterContext pc, Encounter pat "
	// +
	// "where pi._Encounter = pc._Encounter " +
	// "and pc._Encounter.id = pat.id " +
	// "and pat._Retired = :retired " +
	// "and pi._Identifier = :identifier")
	// .setParameter("retired", false).setParameter("identifier",
	// EncounterIdentifier.getIdentifier()).getResultList();
	//
	// if (retList != null)
	// for (Encounter Encounter : retList) {
	// readEncounterInfo(Encounter);
	// }
	//
	// getDbManager().endTransaction(em);
	//
	// if (retList.isEmpty())
	// throw new NoResultException("No results for EncounterIdentifier="
	// + EncounterIdentifier);
	//
	// return retList;
	//
	// }

	/**

	 */
	@Override
	public Encounter saveEncounter(Encounter encounter) throws ParamException {

		EntityManager em = getDbManager().startTransaction();

		try {
			encounter = em.merge(encounter);
		} catch (Exception e) {
			getDbManager().endTransaction(em, true);// roll back
			throw new ParamException(e);
		}
		getDbManager().endTransaction(em);

		return encounter;
	}

	/**

	 */
	@Override
	public void removeEncounter(Encounter encounter) {

		EntityManager em = getDbManager().startTransaction();

		// do not physically remove the item
		// update Encounter
		encounter.setRetired(true);
		encounter.setRetiredDate(new Date());
		encounter = em.merge(encounter);
		getDbManager().endTransaction(em);

	}

	// /**
	// * return ALL
	// */
	// @Override
	// public List<Encounter> getAllEncounters() {
	// EntityManager em = getDbManager().startTransaction();
	//
	// List<Encounter> allEncounterContext = em.createQuery(
	// "select e from Encounter e").getResultList();
	//
	// getDbManager().endTransaction(em);
	//
	// return allEncounterContext;
	// }

	/**
	 * we are in a transaction and read the max of Encounter info
	 * 
	 * @param Encounter
	 */
	private void readEncounterInfo(Encounter Encounter) {

		// read concepts - values
		List<ConceptIdValue> conceptIdValues = Encounter.getIdValues();
		for (ConceptIdValue conceptIdValue : conceptIdValues) {
		}

	}
	
	@Override
	public List<String> getCurrentRoleEncounterIds() {
		String currentRoleName = ServiceHelper.getPermissionManagerService().getCurrentRole();
		Permissions permissions = ServiceHelper.getPermissionManagerService().getPermissions(currentRoleName);
		if (permissions != null)
			return permissions.getEncounterIds();
		return null;
	}

	// ///////////////////////////
	// spring...

	private DbManager _DbManager;

	public DbManager getDbManager() {
		return _DbManager;
	}

	public void setDbManager(DbManager _DbManager) {
		this._DbManager = _DbManager;
	}

}
