package ch.msf.manager;

import java.util.List;

import ch.msf.form.ParamException;
import ch.msf.model.Aggregation;
import ch.msf.model.AggregationContext;
import ch.msf.model.SelectionContext;

public interface AggregationDataManager {
	
	public Aggregation getSelectedAggregation(Long thematicPeriodId);
	
//	public List<AggregationContext> getSelectedAggregation(Long aggregationIdentifier, Boolean retired);
	
	public List<AggregationContext> getAllSelectedAggregationContext();// not in use yet
	
	public List<AggregationContext> getAllSelectedAggregationContext(int yearDate) throws ParamException;// not in use yet
	
	public List<AggregationContext> getAllSelectedAggregationContext(Aggregation aggregation);
	
	public AggregationContext getAggregatedContext(Long aggregationContextId);
		
	public AggregationContext saveSelectedAggregationContext(AggregationContext aggregationContext) throws ParamException;

//	public void removeSelectedAggregationContext(AggregationContext thematicPeriodContext);
	// TN83 entryform:
	public Aggregation readDBAggregationInfo(Aggregation aggregation, boolean all);

	public List<AggregationContext> getAllSelectedAggregationContext(SelectionContext selectionContext, String yearDate) throws ParamException;
	
}
