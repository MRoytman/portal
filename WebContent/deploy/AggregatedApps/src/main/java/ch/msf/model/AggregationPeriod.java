package ch.msf.model;


public interface AggregationPeriod {

//	public final int DAY_PERIOD = 1;
//	public final int WEEK_PERIOD = 2;
//	public final int MONTH_PERIOD = 3;
//	public final int TRIMESTER_PERIOD = 4;
//	public final int SEMESTER_PERIOD = 5;
//	public final int YEAR_PERIOD = 6;
//	// need to mention interval
//	public final int OPEN_PERIOD = 7;
	
	public enum PeriodType{
		DAY_PERIOD,
		WEEK_PERIOD,
		MONTH_PERIOD,
		TRIMESTER_PERIOD,
		SEMESTER_PERIOD,
		YEAR_PERIOD,
		// need to mention interval
		OPEN_PERIOD,		
	}

	// type of period
	public PeriodType getPeriodType();
	public void setPeriodType(PeriodType type);	
	
	// number of repetition
	public int getPeriodMultiple();
	public void setPeriodMultiple(int mult);
	
}
