package ch.msf.manager;

import java.util.ArrayList;
import java.util.List;

import ch.msf.form.ParamException;
import ch.msf.model.Aggregation;
import ch.msf.model.Section;
import ch.msf.model.SectionTable;

public interface SectionManager {
	
	Section getSection(Long SectionId) ;

	Section saveSection(Section Section) throws ParamException ;

	void removeSection(Section Section);

	List<Section> getAllAggregationSections(Aggregation aggregation, boolean retired);

	ArrayList<SectionTable> buildSectionTablesFromConfig(Section currentSection);
}
