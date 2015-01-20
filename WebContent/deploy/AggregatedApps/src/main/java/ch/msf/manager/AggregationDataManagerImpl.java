package ch.msf.manager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import ch.msf.CommonConstants;
import ch.msf.form.ParamException;
import ch.msf.model.Aggregation;
import ch.msf.model.AggregationContext;
import ch.msf.model.Section;
import ch.msf.model.SelectionContext;

/**
 * 
 * @author cmi
 * 
 */

public class AggregationDataManagerImpl implements AggregationDataManager {

	// min lenght search text size for finders
	public static int SEARCH_MIN_SIZE = 1;

	/**
	 * can also return a deleted aggregation
	 */
	@Override
	public Aggregation getSelectedAggregation(Long aggregationId) {

		if (aggregationId == null)
			return null;
		EntityManager em = getDbManager().startTransaction();
		Aggregation aggregation = em.find(Aggregation.class, aggregationId);

		getDbManager().endTransaction(em);

		return aggregation;
	}

	/**
	 * The creation of the AggregationContext comes before the Aggregation
	 * creation Therefore it is the responsibility of the AggregationContext to
	 * create a new aggregation or not
	 */
	@Override
	public synchronized AggregationContext saveSelectedAggregationContext(AggregationContext aggregationContext) throws ParamException {

		EntityManager em = getDbManager().startTransaction();

		// life of AggregationContext is linked with Aggregation
		// save aggregation before if needed
		Aggregation aggregation = aggregationContext.getAggregation();

		AggregationContext sc;
		try {
			aggregation = em.merge(aggregation);
			aggregationContext.setAggregation(aggregation);

			sc = em.merge(aggregationContext);
		} catch (Exception e) {
			throw new ParamException(e);
		}
		getDbManager().endTransaction(em);

		return sc;
	}

	@Override
	public AggregationContext getAggregatedContext(Long aggregationContextId) {
		if (aggregationContextId == null)
			throw new NoResultException("No results for aggregationIdentifier=" + aggregationContextId);
		EntityManager em = getDbManager().startTransaction();
		AggregationContext aggregationContext = em.find(AggregationContext.class, aggregationContextId);

		// entryform: load dependencies
		boolean all = false;
		readAggregationInfo(aggregationContext.getAggregation(), all);

		getDbManager().endTransaction(em);

		return aggregationContext;
	}

	/**
	 * return ALL
	 */
	@Override
	public List<AggregationContext> getAllSelectedAggregationContext() {
		EntityManager em = getDbManager().startTransaction();

		List<AggregationContext> allAggregationContext = em.createQuery("select pc from AggregationContext pc").getResultList();

		getDbManager().endTransaction(em);

		return allAggregationContext;
	}

	@Override
	public List<AggregationContext> getAllSelectedAggregationContext(int yearDate) throws ParamException {
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

		String strQuery = "select pc from AggregationContext pc join pc._Aggregation agg  where agg._Retired = :retired and agg._CreationDate >= :startdate and  agg._CreationDate < :enddate";

		Query qry = em.createQuery(strQuery).setParameter("retired", false).setParameter("startdate", startDate).setParameter("enddate", endDate);
		boolean all = false;
		List<AggregationContext> allAggregationContext = qry.getResultList();
		for (AggregationContext aggregationContext : allAggregationContext) {
			readAggregationInfo(aggregationContext.getAggregation(), all);
		}

		getDbManager().endTransaction(em);

		return allAggregationContext;
	}

	/**
	 * 
	 */
	@Override
	public List<AggregationContext> getAllSelectedAggregationContext(Aggregation aggregation) {
		EntityManager em = getDbManager().startTransaction();

		List<AggregationContext> allAggregationContexts = em
				.createQuery("select pc from AggregationContext pc where pc._Aggregation = :aggregation and pc._Aggregation._Retired = :retired")
				.setParameter("aggregation", aggregation).setParameter("retired", false).getResultList();

		if (allAggregationContexts != null) {
			boolean all = false;// TN83 entryform:
			for (AggregationContext aggregationContext : allAggregationContexts) {
				readAggregationInfo(aggregationContext.getAggregation(), all);
			}
		}
		getDbManager().endTransaction(em);

		return allAggregationContexts;
	}

	/**
	 * TN9 // get all aggregation contexts that belong to a context //
	 * aggregationContext must be filled with relevant information // return
	 * null if no selecting info has been setup
	 * 
	 * @param yearDate
	 *            : can be null or should represent a single number as a year
	 * @param idOrName
	 *            : select on id or aggregation name, can be null
	 * @param testOnId
	 *            : if param idOrName true, select on aggregationId - if false,
	 *            select on aggregation name
	 */
	@Override
	public List<AggregationContext> getAllSelectedAggregationContext(SelectionContext selectionContext, String yearDate) throws ParamException {
		EntityManager em = getDbManager().startTransaction();

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

			strQuery = "select pc from AggregationContext pc, Aggregation agg ";

			strQuery += "where pc._Aggregation.id = agg.id ";
			strQuery += "and agg._Retired = :retired  AND ";
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
			strQuery += " and agg._StartDate >= :startdate and  agg._StartDate < :enddate";
		}

		// order by year
		strQuery += " order by agg._StartDate ";

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

		List<AggregationContext> allAggregationContext = qry.getResultList();
		// TN83 entryform: PERF ISSUE: NE LIRE LES INFOS PATIENT QUE SI
		// NECESSAIRE
		boolean all = false;
		for (AggregationContext aggregationContext : allAggregationContext) {
			readAggregationInfo(aggregationContext.getAggregation(), all);
		}

		getDbManager().endTransaction(em);

		return allAggregationContext;
	}

	@Override
	/**
	 * TN83 entryform: load detached object info (first loaded lazyly)
	 * http://www.jumpingbean.co.za/blogs/mark/jpa-merge-dettached-entities
	 */
	public Aggregation readDBAggregationInfo(Aggregation aggregation, boolean all) {
		EntityManager em = getDbManager().startTransaction();

		// aggregation = em.merge(aggregation);
		aggregation = em.find(Aggregation.class, aggregation.getId());
		readAggregationInfo(aggregation, all);

		getDbManager().endTransaction(em);
		return aggregation;
	}

	/**
	 * we are in a transaction and read the max of aggregation info
	 * 
	 * @param aggregation
	 */
	private void readAggregationInfo(Aggregation aggregation, boolean all) {
		List<Section> sections = null;
		sections = aggregation.getSections();
		if (sections != null)
			for (Section section : sections) {
			}
		// read concepts - values

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
