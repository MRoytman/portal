package ch.msf.manager;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.persistence.EntityManager;

import ch.msf.form.ParamException;
import ch.msf.form.config.SectionTableKeyValueTableModel;
import ch.msf.model.Aggregation;
import ch.msf.model.ConceptIdValue;
import ch.msf.model.Section;
import ch.msf.model.SectionTable;
import ch.msf.service.ServiceHelper;
import ch.msf.util.IdType;

/**
 * 
 * @author cmi
 * 
 */

public class SectionManagerImpl implements SectionManager {

	@Override
	public List<Section> getAllAggregationSections(Aggregation currentAggregation, boolean retired) {
		EntityManager em = getDbManager().startTransaction();

		List<Section> retList = em.createQuery("select e from Section e join e._Aggregation agg " + "where agg.id = :sectionId and e._Retired = :retired ")
				.setParameter("sectionId", currentAggregation.getId()).setParameter("retired", retired).getResultList();

		if (retList != null)
			for (Section section : retList) {
				readSectionInfo(section);
			}

		getDbManager().endTransaction(em);

		return retList;
	}

	/**
	 * can also return a deleted Section
	 */
	@Override
	public Section getSection(Long SectionId) {

		if (SectionId == null)
			return null;

		EntityManager em = getDbManager().startTransaction();
		Section section = em.find(Section.class, SectionId);
		readSectionInfo(section);

		getDbManager().endTransaction(em);

		return section;
	}

	/**

	 */
	@Override
	public Section saveSection(Section section) throws ParamException {

		// before saving, put aside transient information that will be erased during the save
		List<SectionTable> sectionTables = section.getSectionTables();
		
		EntityManager em = getDbManager().startTransaction();

		try {
			section = em.merge(section);
		} catch (Exception e) {
			throw new ParamException(e);
		}
		getDbManager().endTransaction(em);

		// restore the section table
		section.setSectionTables(sectionTables);

		return section;
	}

	/**

	 */
	@Override
	public void removeSection(Section section) {

		EntityManager em = getDbManager().startTransaction();

		// do not physically remove the item
		// update Section
		section.setRetired(true);
		section.setRetiredDate(new Date());
		section = em.merge(section);
		getDbManager().endTransaction(em);

	}


	/**
	 * we are in a transaction and read the max of Section info
	 * 
	 * @param Section
	 */
	private void readSectionInfo(Section section) {

		// read concepts - values
		List<ConceptIdValue> conceptIdValues = section.getIdValues();
		for (ConceptIdValue conceptIdValue : conceptIdValues) {
		}

	}


	/**
	 * build all objects for this section
	 * @param currentSection
	 */
	@Override
	public ArrayList<SectionTable> buildSectionTablesFromConfig(Section currentSection) {
		//
		ArrayList<SectionTable> sectionTables = new ArrayList<SectionTable>();

		String sectionThemeId = currentSection.getThemeCode();

		// create all section tables 
		// HashMap<sectionTableid, ArrayList<IdType[]>>
//		HashMap<String, ArrayList<IdType[]>> sectionTableIdTypes = 
//				ServiceHelper.getEntryFormConfigurationManagerService().getResourceManager().getSectionTableIdTypes(sectionThemeId);
		
		ResourceManager resourceManager = ServiceHelper.getEntryFormConfigurationManagerService().getResourceManager();
				
		// get all sectiontables for this section
		ArrayList<String> sectionTableIds = resourceManager.getSectionTableIds(sectionThemeId);

		// for each sectionTable...
		for (String sectionTableId : sectionTableIds) {
			// ...create an empty model 
			SectionTableKeyValueTableModel sectionKeyValueTableModel = new SectionTableKeyValueTableModel(sectionTableId, null, null, null);

			//... get the table concept types
			HashMap<String, ArrayList<IdType[]>> sectionTableIdTypes = resourceManager.getSectionTableIdTypes(sectionThemeId);
			ArrayList<IdType[]> conceptIdTypeArray = sectionTableIdTypes.get(sectionTableId);
			
			// build section table object and attach it to the section object
			SectionTable sectionTable = new SectionTable(sectionTableId, conceptIdTypeArray.size(), sectionKeyValueTableModel);
			
//			// check table header coherence
//			String[] tableTitles = ServiceHelper.getEntryFormConfigurationManagerService().getTableTitleTranslation(sectionTableId);
//			if(tableTitles.length != sectionTable.getSectionKeyValueTableModel().getColumnCount()){
//				throw new ConfigException("Bad configuration (number of declared column is not identical with number of titles) for section table id:"+sectionTableId);
//			}
			sectionTables.add(sectionTable);
		}
		return sectionTables;
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
