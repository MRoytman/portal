package ch.msf.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import ch.msf.util.DateUtil;

@Entity
// keep UniqueConstraint in this application to ensure no more than one line exists per aggregation
@Table(name = "Aggregation", uniqueConstraints = @UniqueConstraint(columnNames = { "AGR_THEME_COD", "AGR_PERIODTYPE", "AGR_STARTDATE" })/**/)
public class Aggregation implements Cloneable, AggregationPeriod {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "AGR_ID")
	private Long id;

	@Column(name = "AGR_THEME_COD", nullable = false)
	// this is a config id, not a technical id
	private String _ThemeCode;

	@Column(name = "AGR_TYPE", nullable = false)
	private String _ThemeType;

	@Column(name = "AGR_PERIODTYPE", nullable = false)
	@Enumerated(EnumType.STRING)
	private PeriodType _PeriodType; // PeriodType

	@Column(name = "AGR_STARTDATE", nullable = false)
	private Date _StartDate;

	@Column(name = "AGR_PERIODMULT")
	// multiplicity of the period type
	private int _PeriodMultiple = 1;

	@OneToMany(mappedBy = "_Aggregation", /* fetch=FetchType.EAGER, */cascade = CascadeType.ALL, orphanRemoval = true)
	//
	private List<Section> _AttachedSections;

	// depending on PeriodType, this field is not obviously calculated
	// so store it anyway
	@Column(name = "AGR_ENDDATE")
	private Date _EndDate;

	// ///////////// technical fields

	@Column(name = "AGR_CREA_DATE")
	private Date _CreationDate;

	@Column(name = "AGR_MODIF_DATE")
	private Date _ModificationDate;

	@Column(name = "AGR_RETIRED")
	private boolean _Retired;

	@Column(name = "AGR_RETIRED_DATE")
	private Date _RetiredDate;

	// transient private ArrayList<Section> _AttachedSections;

	public Aggregation() {
		// System.out.println("::This empty constructor is used...");
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getThemeCode() {
		return _ThemeCode;
	}

	public void setThemeCode(String _ThemeConfigId) {
		this._ThemeCode = _ThemeConfigId;
	}

	public String getThemeType() {
		return _ThemeType;
	}

	public void setThemeType(String _ThemType) {
		this._ThemeType = _ThemType;
	}

	public Date getStartDate() {
		return _StartDate;
	}

	public void setStartDate(Date _StartDate) {
		// reset minutes, seconds...
		this._StartDate = DateUtil.midnight(_StartDate);
	}

	@Override
	public PeriodType getPeriodType() {
		return _PeriodType;
	}

	@Override
	public void setPeriodType(PeriodType type) {
		_PeriodType = type;
		// compute end date if we have startdate
		if (getStartDate() != null) {
			setEndDate(type);
		}
	}

	@Override
	public int getPeriodMultiple() {
		return _PeriodMultiple;
	}

	@Override
	public void setPeriodMultiple(int mult) {
		_PeriodMultiple = mult;

	}

	public Date getEndDate() {
		return _EndDate;
	}

	public void setEndDate(Date _EndDate) {
		this._EndDate = _EndDate;
	}

	public void setEndDate(PeriodType periodType) {
		if (getStartDate() != null) {
			Calendar cal = new GregorianCalendar();
			cal.setTime(getStartDate());

			switch (periodType) {
			case DAY_PERIOD:
				cal.add(Calendar.DATE, getPeriodMultiple());
				break;
			case WEEK_PERIOD:
				cal.add(Calendar.WEEK_OF_YEAR, getPeriodMultiple());
				break;
			case MONTH_PERIOD:
				cal.add(Calendar.MONTH, getPeriodMultiple());
				break;
			case TRIMESTER_PERIOD:
				cal.add(Calendar.MONTH, getPeriodMultiple() * 3);
				break;
			case SEMESTER_PERIOD:
				cal.add(Calendar.MONTH, getPeriodMultiple() * 6);
				break;
			case YEAR_PERIOD:
				cal.add(Calendar.YEAR, getPeriodMultiple() * 6);
				break;
			case OPEN_PERIOD:
				// use setEndDate()
				break;

			}
			setEndDate(cal.getTime());
		}
	}

	// //////////////////

	public Date getCreationDate() {
		return _CreationDate;
	}

	public void setCreationDate(Date creationDate) {
		_CreationDate = creationDate;
	}

	public Date getModificationDate() {
		return _ModificationDate;
	}

	public void setModificationDate(Date modificationDate) {
		_ModificationDate = modificationDate;
	}

	public boolean isRetired() {
		return _Retired;
	}

	public void setRetired(boolean retired) {
		this._Retired = retired;
	}

	public Date getRetiredDate() {
		return _RetiredDate;
	}

	public void setRetiredDate(Date retiredDate) {
		this._RetiredDate = retiredDate;
	}

	@Override
	public String toString() {
		return "ThematicPeriod [type=" + _ThemeType + ", periodtype=" + _PeriodType + ", startdate=" + _StartDate + "]";
	}

	public void addSection(Section section) {
		getSections().add(section);
	}

	public List<Section> getSections() {
		if (_AttachedSections == null)
			_AttachedSections = new ArrayList<Section>();
		return _AttachedSections;
	}

}
