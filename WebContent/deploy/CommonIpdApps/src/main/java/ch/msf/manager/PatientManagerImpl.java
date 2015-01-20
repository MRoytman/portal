package ch.msf.manager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import ch.msf.CommonConstants;
import ch.msf.CommonIpdConstants;
import ch.msf.form.ParamException;
import ch.msf.model.Patient;
import ch.msf.model.PatientContext;
import ch.msf.model.PatientIdValue;
import ch.msf.model.PatientIdentifier;
import ch.msf.model.SelectionContext;

/**
 * 
 * @author cmi
 * 
 */

public class PatientManagerImpl implements PatientManager {

	// min lenght search text size for finders
	public static int SEARCH_MIN_SIZE = 1;
	public static int SEARCH_ITEMS_MAX_SIZE = 30;

	/**
	 * can also return a deleted patient
	 */
	@Override
	public Patient getSelectedPatient(Long patientId) {

		if (patientId == null)
			return null;
		EntityManager em = getDbManager().startTransaction();
		Patient patient = em.find(Patient.class, patientId);

		getDbManager().endTransaction(em);

		return patient;
	}

	//
	//
	// @Override
	// public Patient getSelectedPatient(String patientIdentifier) {
	//
	// EntityManager em = getDbManager().startTransaction();
	//
	// List<Patient> patient = em
	// .createQuery(
	// "select p from Patient p where p._Identifier = :identifier and p._Retired = :retired")
	// .setParameter("identifier", patientIdentifier).setParameter("retired",
	// false).getResultList();
	//
	// if (patient == null || patient.size() !=1)
	// throw new
	// NoResultException("Several or no results for patientIdentifier="+patientIdentifier);
	//
	// getDbManager().endTransaction(em);
	//
	// return patient.get(0);
	//
	// }

	@Override
	/**
	 * 
	 */
	public List<PatientContext> getSelectedPatient(PatientIdentifier patientIdentifier, Boolean patientRetired) {

		EntityManager em = getDbManager().startTransaction();

		@SuppressWarnings("unchecked")
		// String strQuery =
		// "select pat from PatientIdentifier pi, PatientContext pc, Patient pat "
		// + "where pi._Patient = pc._Patient " + "and pc._Patient.id = pat.id "
		// + "and pi._Identifier = :identifier";
		String strQuery = "select pc from PatientIdentifier pi, PatientContext pc, Patient pat " + "where pi._Patient = pc._Patient " + "and pc._Patient.id = pat.id "
				+ "and pi._Identifier = :identifier";
		if (patientRetired != null) {
			strQuery += " and pat._Retired = :retired ";
		}

		Query qry = em.createQuery(strQuery);
		qry.setParameter("identifier", patientIdentifier.getIdentifier());

		if (patientRetired != null) {
			qry.setParameter("retired", patientRetired);
		}

		// List<Patient> retList = qry.getResultList();
		List<PatientContext> retList = qry.getResultList();
		boolean all = false;
		// boolean all = true;
		if (retList != null)
			for (PatientContext pc : retList) {
				readPatientInfo(pc.getPatient(), all); // TN83 entryform:
			}

		getDbManager().endTransaction(em);

		if (retList.isEmpty())
			throw new NoResultException("No results for patientIdentifier=" + patientIdentifier);

		return retList;
	}

	// /**
	// * The creation of the PatientContext comes before the Patient creation
	// * Therefore it is the responsibility of the PatientContext to create a
	// new patient or not
	// *
	// * -> do not start a new transaction, it has to be done
	// */
	// // @Override
	// public Patient saveSelectedPatient(Patient patient) {
	//
	// // EntityManager em = getDbManager().startTransaction();
	// Patient sc = em.merge(patient);
	// // getDbManager().endTransaction(em);
	//
	// return sc;
	// }
	//

	// /**
	// * // new PatientContext is not returned // but it's assumed the reference
	// * won't be used
	// */
	// @Override
	// public void removeSelectedPatient(Patient patient) {
	//
	//
	// EntityManager em = getDbManager().startTransaction();
	// patient.setRetired(true);
	// patient.setRetiredDate(new Date());
	// Patient sc = em.merge(patient);
	//
	// List<PatientContext> allPatientPatientContexts =
	// getAllSelectedPatientContext(sc);
	// for(PatientContext patientPatientContext: allPatientPatientContexts){
	// // System.out.println(patientPatientContext.toString());
	// removeSelectedPatientContext(patientPatientContext);
	// }
	//
	// getDbManager().endTransaction(em);
	//
	// }

	// /**
	// * normally not used as we get the patient from the PatientContext
	// */
	// @Override
	// public PatientContext getSelectedPatientContext(Long patientId) {
	//
	// EntityManager em = getDbManager().startTransaction();
	// PatientContext patientContext = em
	// .find(PatientContext.class, patientId);
	// getDbManager().endTransaction(em);
	//
	// return patientContext;
	// }
	//
	//

	/**
	 * The creation of the PatientContext comes before the Patient creation Therefore it is the responsibility of the
	 * PatientContext to create a new patient or not
	 */
	@Override
	public synchronized PatientContext saveSelectedPatientContext(PatientContext patientContext) throws ParamException {

		EntityManager em = getDbManager().startTransaction();

		// life of PatientContext is linked with Patient
		// save patient before if needed
		Patient patient = patientContext.getPatient();

		// detect if it is a new patient...
		if (patient.getId() == null) {
			// ...if so, set an unique index number
			Long index = getPatientCount(em);
			patient.setIndex(++index);
		}
		// patient = saveSelectedPatient(patient);

		// TN73 check surname name
		if (patient.getFirstName() == null)
			patient.setFirstName("/");

		if (patient.getFamilyName() == null)
			patient.setFamilyName("/");

		PatientContext sc;
		try {
			patient = em.merge(patient);
			patientContext.setPatient(patient);

			sc = em.merge(patientContext);
		} catch (Exception e) {
			getDbManager().endTransaction(em, true);// roll back
			throw new ParamException(e);
		}
		getDbManager().endTransaction(em);

		return sc;
	}

	/**
	 * // new PatientContext is not returned // but it's assumed the reference won't be used
	 */
	@Override
	public void removeSelectedPatientContext(PatientContext patientContext) {

		EntityManager em = getDbManager().startTransaction();

		// do not physically remove the item
		// patientContext.setRetired(true);
		// patientContext.setRetiredDate(new Date());

		// update patient
		Patient patient = patientContext.getPatient();
		patient.setRetired(true);
		patient.setRetiredDate(new Date());
		patient = em.merge(patient);
		patientContext.setPatient(patient);

		PatientContext sc = em.merge(patientContext);
		getDbManager().endTransaction(em);

	}

	@Override
	public PatientContext getPatientContext(Long patientContextId) {
		if (patientContextId == null)
			return null;
		EntityManager em = getDbManager().startTransaction();
		PatientContext patientContext = em.find(PatientContext.class, patientContextId);

		// TN83 entryform: load dependencies
		boolean all = false;
		readPatientInfo(patientContext.getPatient(), all);

		getDbManager().endTransaction(em);

		return patientContext;
	}

	/**
	 * return ALL
	 */
	@Override
	public List<PatientContext> getAllSelectedPatientContext() {
		EntityManager em = getDbManager().startTransaction();

		List<PatientContext> allPatientContext = em.createQuery("select pc from PatientContext pc").getResultList();

		// String strQuery =
		// "select pc, pat from PatientContext pc join pc._Patient pat ";
		// String strQuery =
		// "select pc._Patient from PatientContext pc join pc._Patient pat ";

		// Query qry = em.createQuery(strQuery);
		//
		// List<Object> objs = qry.getResultList();

		getDbManager().endTransaction(em);

		return allPatientContext;
	}

	@Override
	public List<PatientContext> getAllSelectedPatientContext(int yearDate) throws ParamException {
		EntityManager em = getDbManager().startTransaction();

		SimpleDateFormat sdf = new SimpleDateFormat(CommonConstants.DATEMINUTE_FORMAT);
		Date startDate = null;
		Date endDate = null;
		try {
			startDate = sdf.parse("01.01." + yearDate + " 00:00:00");
			endDate = sdf.parse("01.01." + (yearDate + 1) + " 00:00:00");
		} catch (ParseException e) {
			throw new ParamException(e);
		}

		String strQuery = "select pc from PatientContext pc join pc._Patient pat where pat._Retired = :retired and pat._CreationDate >= :startdate and  pat._CreationDate < :enddate";

		Query qry = em.createQuery(strQuery).setParameter("retired", false).setParameter("startdate", startDate).setParameter("enddate", endDate);
		// TN83 entryform:
		boolean all = false;
		List<PatientContext> allPatientContext = qry.getResultList();
		for (PatientContext patientContext : allPatientContext) {
			readPatientInfo(patientContext.getPatient(), all);
		}

		getDbManager().endTransaction(em);

		return allPatientContext;
	}

	/**
	 * 
	 */
	@Override
	public List<PatientContext> getAllSelectedPatientContext(Patient patient) {
		EntityManager em = getDbManager().startTransaction();

		List<PatientContext> allPatientContexts = em.createQuery("select pc from PatientContext pc where pc._Patient = :patient and pc._Patient._Retired = :retired")
				.setParameter("patient", patient).setParameter("retired", false).getResultList();
		// System.out.println("");

		if (allPatientContexts != null) {
			boolean all = false;// TN83 entryform:
			for (PatientContext patientContext : allPatientContexts) {
				readPatientInfo(patientContext.getPatient(), all);
				// Patient patient2 = patientContext.getPatient();
				// List<PatientIdValue> patientIdValues =
				// patient2.getIdValues();
				// for (PatientIdValue patientIdValue : patientIdValues) {
				// }
			}
		}

		getDbManager().endTransaction(em);

		return allPatientContexts;
	}

	/**
	 * // TN141 search on both id and names
	 * 
	 * @param selectionContext
	 * @param yearDate
	 *            : can be null or should represent a single number as a year
	 * @param idOrName
	 *            : select on id or patient name, can be null
	 */
	@Override
	public List<PatientContext> getAllSelectedPatientContext(SelectionContext selectionContext, String yearDate, String idOrName) throws ParamException {
		EntityManager em = getDbManager().startTransaction();

		if (idOrName != null && idOrName.length() < SEARCH_MIN_SIZE) {
			throw new ParamException("" + CommonIpdConstants.MESSAGE_TEXT_TOO_SHORT); // TN9 search text too short
		}

		String strQuery = "";
		String strContextQuery = "";
		if (selectionContext.getSelectedCountry() != null) {
			strContextQuery += " pc._SelectedCountry = :selectedCountry ";
		}

		if (selectionContext.getSelectedProject() != null) {
			if (strContextQuery.length() > 0)
				strContextQuery += " AND ";
			strContextQuery += " pc._SelectedProject = :selectedProject ";
		}

		if (selectionContext.getSelectedCareCenter() != null) {
			if (strContextQuery.length() > 0)
				strContextQuery += " AND ";
			strContextQuery += " pc._SelectedCareCenter = :selectedCareCenter ";
		}

		if (strContextQuery.length() > 0) {

			// strQuery = "select  pc from PatientContext pc, Patient pat ";
			strQuery = "select pc from PatientIdentifier pi, PatientContext pc, Patient pat ";

			strQuery += "where pi._Patient = pc._Patient " + "and pc._Patient.id = pat.id ";
			strQuery += "and pat._Retired = :retired  AND ";
			strQuery += strContextQuery;
		} else {
			// equivallent to select all
			// do not authorise!
			return null;
		}

		// date param
		Date startDate = null;
		Date endDate = null;
		if (yearDate != null) {
			SimpleDateFormat sdf = new SimpleDateFormat(CommonConstants.DATEMINUTE_FORMAT);

			try {
				int intYearDate = Integer.parseInt(yearDate);
				startDate = sdf.parse("01.01." + intYearDate + " 00:00:00");
				endDate = sdf.parse("01.01." + (intYearDate + 1) + " 00:00:00");
			} catch (Exception e) {
				throw new ParamException(e);
			}
			strQuery += " and pat._CreationDate >= :startdate and  pat._CreationDate < :enddate";
		}

		if (idOrName != null && !idOrName.isEmpty()) {
			strQuery += " and (";
			strQuery += " ( upper(pat._FamilyName) like '%" + idOrName.toUpperCase() + "%' or upper(pat._FirstName) like '%" + idOrName.toUpperCase() + "%' ) OR";

			strQuery += "  (upper(pi._Identifier) like '%" + CommonConstants.PATIENTID_STRING_SEPARATOR + "%" + idOrName.toUpperCase() + "%')";
			strQuery += " )";
		}

		// order by clause
		strQuery += " order by pat._CreationDate DESC";

		Query qry = em.createQuery(strQuery);

		if (selectionContext.getSelectedCountry() != null) {
			qry.setParameter("selectedCountry", selectionContext.getSelectedCountry());
		}

		if (selectionContext.getSelectedProject() != null) {
			qry.setParameter("selectedProject", selectionContext.getSelectedProject());
		}

		if (selectionContext.getSelectedCareCenter() != null) {
			qry.setParameter("selectedCareCenter", selectionContext.getSelectedCareCenter());
		}

		qry.setParameter("retired", false);

		if (yearDate != null) {
			qry.setParameter("startdate", startDate).setParameter("enddate", endDate);
		}

		List<PatientContext> allPatientContext = null;
		boolean transactionProblem = false;
		try {
			allPatientContext = qry.getResultList();
		} catch (Exception e) {
			transactionProblem = true;// TN132
		}
		// TN83 entryform: PERF ISSUE: NE LIRE LES INFOS PATIENT QUE SI
		// NECESSAIRE
		boolean all = false;
		for (PatientContext patientContext : allPatientContext) {
			readPatientInfo(patientContext.getPatient(), all);
		}

		getDbManager().endTransaction(em, transactionProblem);

		return allPatientContext;
	}

	// /**
	// * TN9 // get all patient contexts that belong to a context // patientContext must be filled with relevant
	// * information // return null if no selecting info has been setup
	// *
	// * @param selectionContext
	// * @param yearDate
	// * : can be null or should represent a single number as a year
	// * @param idOrName
	// * : select on id or patient name, can be null
	// * @param testOnId
	// * : if param idOrName true, select on patientId - if false, select on patient name
	// */
	// @Override
	// public List<PatientContext> getAllSelectedPatientContext(SelectionContext selectionContext, String yearDate,
	// String idOrName, boolean testOnId) throws ParamException {
	// EntityManager em = getDbManager().startTransaction();
	//
	// if (idOrName != null && idOrName.length() < SEARCH_MIN_SIZE) {
	// throw new ParamException("" + CommonIpdConstants.MESSAGE_TEXT_TOO_SHORT); // TN9 search text too short
	// }
	//
	// String strQuery = "";
	// String strContextQuery = "";
	// if (selectionContext.getSelectedCountry() != null) {
	// strContextQuery += " pc._SelectedCountry = :selectedCountry ";
	// }
	//
	// if (selectionContext.getSelectedProject() != null) {
	// if (strContextQuery.length() > 0)
	// strContextQuery += " AND ";
	// strContextQuery += " pc._SelectedProject = :selectedProject ";
	// }
	//
	// if (selectionContext.getSelectedCareCenter() != null) {
	// if (strContextQuery.length() > 0)
	// strContextQuery += " AND ";
	// strContextQuery += " pc._SelectedCareCenter = :selectedCareCenter ";
	// }
	//
	// if (strContextQuery.length() > 0) {
	//
	// // strQuery = "select  pc from PatientContext pc, Patient pat ";
	// strQuery = "select pc from PatientIdentifier pi, PatientContext pc, Patient pat ";
	//
	// strQuery += "where pi._Patient = pc._Patient " + "and pc._Patient.id = pat.id ";
	// strQuery += "and pat._Retired = :retired  AND ";
	// strQuery += strContextQuery;
	// } else {
	// // equivallent to select all
	// // do not authorise!
	// return null;
	// }
	//
	// // date param
	// Date startDate = null;
	// Date endDate = null;
	// if (yearDate != null) {
	// SimpleDateFormat sdf = new SimpleDateFormat(CommonConstants.DATEMINUTE_FORMAT);
	//
	// try {
	// int intYearDate = Integer.parseInt(yearDate);
	// startDate = sdf.parse("01.01." + intYearDate + " 00:00:00");
	// endDate = sdf.parse("01.01." + (intYearDate + 1) + " 00:00:00");
	// } catch (Exception e) {
	// throw new ParamException(e);
	// }
	// strQuery += " and pat._CreationDate >= :startdate and  pat._CreationDate < :enddate";
	// }
	//
	// if (idOrName != null && !idOrName.isEmpty()) {
	// if (!testOnId) // TN124
	// strQuery += " and ( upper(pat._FamilyName) like '%" + idOrName.toUpperCase() +
	// "%' or upper(pat._FirstName) like '%" + idOrName.toUpperCase() + "%' )";
	// else
	// strQuery += " and upper(pi._Identifier) like '%" + CommonConstants.PATIENTID_STRING_SEPARATOR +
	// idOrName.toUpperCase() + "%'";
	// }
	//
	// // order by clause
	// if (idOrName != null) {
	// if (!testOnId)
	// // order by name
	// strQuery += " order by pat._FamilyName ";
	// else
	// // order by patient id
	// strQuery += " order by pi._Identifier ";
	// } else {
	// // order by patient id
	// strQuery += " order by pi._Identifier ";
	// // order by patient creation date
	// // strQuery += " order by pat._CreationDate ";
	// }
	//
	// Query qry = em.createQuery(strQuery);
	//
	// if (selectionContext.getSelectedCountry() != null) {
	// qry.setParameter("selectedCountry", selectionContext.getSelectedCountry());
	// }
	//
	// if (selectionContext.getSelectedProject() != null) {
	// qry.setParameter("selectedProject", selectionContext.getSelectedProject());
	// }
	//
	// if (selectionContext.getSelectedCareCenter() != null) {
	// qry.setParameter("selectedCareCenter", selectionContext.getSelectedCareCenter());
	// }
	//
	// qry.setParameter("retired", false);
	//
	// if (yearDate != null) {
	// qry.setParameter("startdate", startDate).setParameter("enddate", endDate);
	// }
	//
	// List<PatientContext> allPatientContext = null;
	// boolean transactionProblem = false;
	// try {
	// allPatientContext = qry.getResultList();
	// } catch (Exception e) {
	// transactionProblem = true;// TN132
	// }
	// // TN83 entryform: PERF ISSUE: NE LIRE LES INFOS PATIENT QUE SI
	// // NECESSAIRE
	// boolean all = false;
	// for (PatientContext patientContext : allPatientContext) {
	// readPatientInfo(patientContext.getPatient(), all);
	// }
	//
	// getDbManager().endTransaction(em, transactionProblem);
	//
	// return allPatientContext;
	// }

	// TN134 //TN137
	/**
	 * @param selectionContext
	 * @param yearDate
	 * @param idOrName
	 * @param testOnId
	 * @param pageNumber
	 * @return
	 * @throws ParamException
	 */
	@Override
	public List<PatientContext> getAllSelectedPatientContext(SelectionContext selectionContext, String yearDate, String idOrName, boolean testOnId, int pageNumber)
			throws ParamException {
		EntityManager em = getDbManager().startTransaction();

		if (idOrName != null && idOrName.length() < SEARCH_MIN_SIZE) {
			throw new ParamException("" + CommonIpdConstants.MESSAGE_TEXT_TOO_SHORT); // TN9 search text too short
		}

		if (pageNumber < 1) {
			throw new ParamException("Programming error!! pageNumber should not be negative..." + pageNumber);
		}

		String strQuery = "";
		String strContextQuery = "";
		if (selectionContext.getSelectedCountry() != null) {
			strContextQuery += " sc.SELCTY_Code = :selectedCountry ";
		}

		if (selectionContext.getSelectedProject() != null) {
			if (strContextQuery.length() > 0)
				strContextQuery += " AND ";
			strContextQuery += " pr.PRJ_CODE = :selectedProject ";
		}

		if (selectionContext.getSelectedCareCenter() != null) {
			if (strContextQuery.length() > 0)
				strContextQuery += " AND ";
			strContextQuery += " cc.CCAR_CODE = :selectedCareCenter ";
		}

		if (strContextQuery.length() > 0) {

			strQuery = "select * from PatientContext pc inner join Patient pat on pc.PAC_Patient = pat.PAT_id inner join PatientIdentifier pi on pi.PID_Patient = pat.PAT_id ";
			strQuery += " inner join SelectCountries sc on pc.PAC_SELCTY = sc.SELCTY_id ";
			strQuery += " inner join Project pr on pc.PAC_SELPRJ = pr.PRJ_id ";
			strQuery += " inner join CareCenter cc on pc.PAC_SELCCAR = cc.CCAR_id ";

			strQuery += " where pat.PAT_RETIRED = :retired  AND ";
			strQuery += strContextQuery;
		} else {
			// equivallent to select all
			// do not authorise!
			return null;
		}

		// date param
		Date startDate = null;
		Date endDate = null;
		if (yearDate != null) {
			SimpleDateFormat sdf = new SimpleDateFormat(CommonConstants.DATEMINUTE_FORMAT);

			try {
				int intYearDate = Integer.parseInt(yearDate);
				startDate = sdf.parse("01.01." + intYearDate + " 00:00:00");
				endDate = sdf.parse("01.01." + (intYearDate + 1) + " 00:00:00");
			} catch (Exception e) {
				throw new ParamException(e);
			}
			strQuery += " and pat.PAT_CREA_DATE >= :startdate and  pat.PAT_CREA_DATE < :enddate";
		}

		if (idOrName != null && !idOrName.isEmpty()) {
			if (!testOnId) // TN124
				strQuery += " and ( upper(pat.PAT_NAME) like '%" + idOrName.toUpperCase() + "%' or upper(pat.PAT_FIRSTNAME) like '%" + idOrName.toUpperCase() + "%' )";
			else
				strQuery += " and upper(pi.PID_IDENTIFIER) like '%" + CommonConstants.PATIENTID_STRING_SEPARATOR + idOrName.toUpperCase() + "%'";
		}

		// order by
		strQuery += " order by pat.PAT_CREA_DATE DESC";

		// limit query
		strQuery += " LIMIT " + SEARCH_ITEMS_MAX_SIZE;
		strQuery += " OFFSET " + (pageNumber - 1) * SEARCH_ITEMS_MAX_SIZE;

		Query qry = em.createNativeQuery(strQuery, PatientContext.class);
		// qry.setFirstResult((pageNumber - 1) * SEARCH_ITEMS_MAX_SIZE + 1).setMaxResults(SEARCH_ITEMS_MAX_SIZE);//cause
		// TN137

		if (selectionContext.getSelectedCountry() != null) {
			qry.setParameter("selectedCountry", selectionContext.getSelectedCountry().getCode());
		}

		if (selectionContext.getSelectedProject() != null) {
			qry.setParameter("selectedProject", selectionContext.getSelectedProject().getCode());
		}

		if (selectionContext.getSelectedCareCenter() != null) {
			qry.setParameter("selectedCareCenter", selectionContext.getSelectedCareCenter().getCode());
		}

		qry.setParameter("retired", false);

		if (yearDate != null) {
			qry.setParameter("startdate", startDate).setParameter("enddate", endDate);
		}

		List<PatientContext> allPatientContext = null;
		boolean transactionProblem = false;
		try {
			allPatientContext = qry.getResultList();
		} catch (Exception e) {
			transactionProblem = true;// TN132
		}
		// TN83 entryform: PERF ISSUE: NE LIRE LES INFOS PATIENT QUE SI
		// NECESSAIRE
		boolean all = false;
		for (PatientContext patientContext : allPatientContext) {
			readPatientInfo(patientContext.getPatient(), all);
		}

		getDbManager().endTransaction(em, transactionProblem);

		return allPatientContext;
	}

	// TN142
	/**
	 * @param selectionContext
	 * @param yearDate
	 * @param searchToken
	 * @param patientConcept: the concept id to search the token value on
	 * @return
	 * @throws ParamException
	 */
	 @Override
	public List<PatientContext> getAllSelectedPatientContext(SelectionContext selectionContext, String yearDate, String searchToken, String patientConcept) throws ParamException {
		EntityManager em = getDbManager().startTransaction();

		if (searchToken != null && searchToken.length() < SEARCH_MIN_SIZE) {
			throw new ParamException("" + CommonIpdConstants.MESSAGE_TEXT_TOO_SHORT); // TN9 search text too short
		}

		String strQuery = "";
		String strContextQuery = "";
		if (selectionContext.getSelectedCountry() != null) {
			strContextQuery += " sc.SELCTY_Code = :selectedCountry ";
		}

		if (selectionContext.getSelectedProject() != null) {
			if (strContextQuery.length() > 0)
				strContextQuery += " AND ";
			strContextQuery += " pr.PRJ_CODE = :selectedProject ";
		}

		if (selectionContext.getSelectedCareCenter() != null) {
			if (strContextQuery.length() > 0)
				strContextQuery += " AND ";
			strContextQuery += " cc.CCAR_CODE = :selectedCareCenter ";
		}

		if (strContextQuery.length() > 0) {

			strQuery = "select * from PatientContext pc inner join Patient pat on pc.PAC_Patient = pat.PAT_id inner join PatientIdentifier pi on pi.PID_Patient = pat.PAT_id ";
			strQuery += " inner join SelectCountries sc on pc.PAC_SELCTY = sc.SELCTY_id ";
			strQuery += " inner join Project pr on pc.PAC_SELPRJ = pr.PRJ_id ";
			strQuery += " inner join CareCenter cc on pc.PAC_SELCCAR = cc.CCAR_id ";
			strQuery += " inner join PatientIdValue piv on pat.PAT_id = piv.PIV_Patient ";

			strQuery += " where pat.PAT_RETIRED = :retired  AND ";
			strQuery += strContextQuery;
		} else {
			// equivalent to select all
			// do not authorize!
			return null;
		}

		// date param
		Date startDate = null;
		Date endDate = null;
		if (yearDate != null) {
			SimpleDateFormat sdf = new SimpleDateFormat(CommonConstants.DATEMINUTE_FORMAT);

			try {
				int intYearDate = Integer.parseInt(yearDate);
				startDate = sdf.parse("01.01." + intYearDate + " 00:00:00");
				endDate = sdf.parse("01.01." + (intYearDate + 1) + " 00:00:00");
			} catch (Exception e) {
				throw new ParamException(e);
			}
			strQuery += " and pat.PAT_CREA_DATE >= :startdate and  pat.PAT_CREA_DATE < :enddate";
		}

		if (searchToken != null && !searchToken.isEmpty()) {
			strQuery += " and ( piv.PIV_CONCEPT_ID = '" + patientConcept + "' and upper(piv.PIV_CONCEPT_VAL) like '%" + searchToken.toUpperCase() + "%' ) ";
		}

		// order by
		strQuery += " order by pat.PAT_CREA_DATE DESC";


		Query qry = em.createNativeQuery(strQuery, PatientContext.class);

		if (selectionContext.getSelectedCountry() != null) {
			qry.setParameter("selectedCountry", selectionContext.getSelectedCountry().getCode());
		}

		if (selectionContext.getSelectedProject() != null) {
			qry.setParameter("selectedProject", selectionContext.getSelectedProject().getCode());
		}

		if (selectionContext.getSelectedCareCenter() != null) {
			qry.setParameter("selectedCareCenter", selectionContext.getSelectedCareCenter().getCode());
		}

		qry.setParameter("retired", false);

		if (yearDate != null) {
			qry.setParameter("startdate", startDate).setParameter("enddate", endDate);
		}

		List<PatientContext> allPatientContext = null;
		boolean transactionProblem = false;
		try {
			allPatientContext = qry.getResultList();
		} catch (Exception e) {
			transactionProblem = true;// TN132
		}
		// TN83 entryform: PERF ISSUE: NE LIRE LES INFOS PATIENT QUE SI
		// NECESSAIRE
		boolean all = false;
		for (PatientContext patientContext : allPatientContext) {
			readPatientInfo(patientContext.getPatient(), all);
		}

		getDbManager().endTransaction(em, transactionProblem);

		return allPatientContext;
	}

	@Override
	/**
	 * TN83 entryform: load detached object info (first loaded lazyly)
	 * http://www.jumpingbean.co.za/blogs/mark/jpa-merge-dettached-entities
	 */
	public Patient readDBPatientInfo(Patient patient, boolean all) {
		EntityManager em = getDbManager().startTransaction();

		// patient = em.merge(patient);
		patient = em.find(Patient.class, patient.getId());
		readPatientInfo(patient, all);

		getDbManager().endTransaction(em);
		return patient;
	}

	// // TN134
	// /**
	// * @param selectionContext
	// * @param yearDate
	// * @param idOrName
	// * @param testOnId
	// * @param pageNumber
	// * @return
	// * @throws ParamException
	// */
	// @Override
	// public List<PatientContext> getAllSelectedPatientContext(SelectionContext selectionContext, String yearDate,
	// String idOrName, boolean testOnId, int pageNumber)
	// throws ParamException {
	// EntityManager em = getDbManager().startTransaction();
	//
	// if (idOrName != null && idOrName.length() < SEARCH_MIN_SIZE) {
	// throw new ParamException("" + CommonIpdConstants.MESSAGE_TEXT_TOO_SHORT); // TN9 search text too short
	// }
	//
	// if (pageNumber < 1) {
	// throw new ParamException("Programming error!! pageNumber should not be negative..." + pageNumber);
	// }
	//
	// String strQuery = "";
	// String strContextQuery = "";
	// if (selectionContext.getSelectedCountry() != null) {
	// strContextQuery += " pc._SelectedCountry = :selectedCountry ";
	// }
	//
	// if (selectionContext.getSelectedProject() != null) {
	// if (strContextQuery.length() > 0)
	// strContextQuery += " AND ";
	// strContextQuery += " pc._SelectedProject = :selectedProject ";
	// }
	//
	// if (selectionContext.getSelectedCareCenter() != null) {
	// if (strContextQuery.length() > 0)
	// strContextQuery += " AND ";
	// strContextQuery += " pc._SelectedCareCenter = :selectedCareCenter ";
	// }
	//
	// if (strContextQuery.length() > 0) {
	//
	// // strQuery = "select  pc from PatientContext pc, Patient pat ";
	// strQuery = "select pc from PatientIdentifier pi, PatientContext pc, Patient pat ";
	//
	// strQuery += "where pi._Patient = pc._Patient " + "and pc._Patient.id = pat.id ";
	// strQuery += "and pat._Retired = :retired  AND ";
	// strQuery += strContextQuery;
	// } else {
	// // equivallent to select all
	// // do not authorise!
	// return null;
	// }
	//
	// // date param
	// Date startDate = null;
	// Date endDate = null;
	// if (yearDate != null) {
	// SimpleDateFormat sdf = new SimpleDateFormat(CommonConstants.DATEMINUTE_FORMAT);
	//
	// try {
	// int intYearDate = Integer.parseInt(yearDate);
	// startDate = sdf.parse("01.01." + intYearDate + " 00:00:00");
	// endDate = sdf.parse("01.01." + (intYearDate + 1) + " 00:00:00");
	// } catch (Exception e) {
	// throw new ParamException(e);
	// }
	// strQuery += " and pat._CreationDate >= :startdate and  pat._CreationDate < :enddate";
	// }
	//
	// if (idOrName != null && !idOrName.isEmpty()) {
	// if (!testOnId) // TN124
	// strQuery += " and ( upper(pat._FamilyName) like '%" + idOrName.toUpperCase() +
	// "%' or upper(pat._FirstName) like '%" + idOrName.toUpperCase() + "%' )";
	// else
	// strQuery += " and upper(pi._Identifier) like '%" + CommonConstants.PATIENTID_STRING_SEPARATOR +
	// idOrName.toUpperCase() + "%'";
	// }
	//
	// // order by
	// strQuery += " order by pat._CreationDate DESC";
	//
	// Query qry = em.createQuery(strQuery);
	// // TN137 pb en réseau
	// // qry.setFirstResult((pageNumber - 1) * SEARCH_ITEMS_MAX_SIZE + 1).setMaxResults(SEARCH_ITEMS_MAX_SIZE);
	//
	// if (selectionContext.getSelectedCountry() != null) {
	// qry.setParameter("selectedCountry", selectionContext.getSelectedCountry());
	// }
	//
	// if (selectionContext.getSelectedProject() != null) {
	// qry.setParameter("selectedProject", selectionContext.getSelectedProject());
	// }
	//
	// if (selectionContext.getSelectedCareCenter() != null) {
	// qry.setParameter("selectedCareCenter", selectionContext.getSelectedCareCenter());
	// }
	//
	// qry.setParameter("retired", false);
	//
	// if (yearDate != null) {
	// qry.setParameter("startdate", startDate).setParameter("enddate", endDate);
	// }
	//
	// List<PatientContext> allPatientContext = null;
	// boolean transactionProblem = false;
	// try {
	// allPatientContext = qry.getResultList();
	// } catch (Exception e) {
	// transactionProblem = true;// TN132
	// }
	// // TN83 entryform: PERF ISSUE: NE LIRE LES INFOS PATIENT QUE SI
	// // NECESSAIRE
	// boolean all = false;
	// for (PatientContext patientContext : allPatientContext) {
	// readPatientInfo(patientContext.getPatient(), all);
	// }
	//
	// getDbManager().endTransaction(em, transactionProblem);
	//
	// return allPatientContext;
	// }

	/**
	 * we are in a transaction and read the max of patient info
	 * 
	 * @param patient
	 */
	private void readPatientInfo(Patient patient, boolean all) {

		// if (patient.getPatientIdentifiers().get(0).getIdentifier().contains("udo"))
		// System.out.println("trouvé");

		// read concepts - values
		if (all) {
			List<PatientIdValue> patientIdValues = patient.getIdValues();
			for (PatientIdValue patientIdValue : patientIdValues)
				;
		}

		// identifiers
		for (PatientIdentifier patientIdentifier : patient.getPatientIdentifiers()) {
		}

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

	private Long getPatientCount(EntityManager em) {
		// we are already in a transaction
		return (Long) em.createQuery("select count(*) from Patient ").getSingleResult();

	}

}
