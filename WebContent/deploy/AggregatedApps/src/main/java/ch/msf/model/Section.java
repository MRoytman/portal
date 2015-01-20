
package ch.msf.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;


/**
 * @author cmi
 *
 */
@Entity
@Table(name="Section")
public class Section  implements Cloneable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="SEC_id")
	private Long id;
	
	@Column(name = "SEC_DONE")
	private boolean _Done;

	@OneToOne
	@JoinColumn(name="SEC_AGGREG", nullable = false)
	private Aggregation _Aggregation;
	
//	@Column(name = "SEC_TYPE")
//	private String _Type;
	
	@Column(name = "SEC_THEME_COD")
	private String _ThemeCode;
	

	@OneToMany(mappedBy = "_Section", /*fetch=FetchType.EAGER,*/ cascade=CascadeType.ALL, orphanRemoval=true)	// 
	private List<ConceptIdValue> _ConceptIdValues;
	
	transient private List<SectionTable> _SectionTables;
	
	
	// 0 = not validated, 1 = validation pending, 2 = validated 
//	@Column(name = "SEC_STATUS")
//	private Integer _Status;
	
	
	/////////////// technical fields
	
	@Column(name = "SEC_CREA_DATE")
	private Date _CreationDate;	
	
	
	@Column(name = "SEC_MODIF_DATE")
	private Date _ModificationDate;
	
	
	@Column(name = "SEC_RETIRED")
	private boolean _Retired;
	
	
	@Column(name = "SEC_RETIRED_DATE")
	private Date _RetiredDate;

	
    
	public Section() {
//		System.out.println("::This empty constructor is used...");
	}



	public List<ConceptIdValue> getIdValues() {
		if (_ConceptIdValues == null)
			_ConceptIdValues = new ArrayList<ConceptIdValue>();
		return _ConceptIdValues;
	}

	
	public void setIdValues(List<ConceptIdValue> idValues) {
		_ConceptIdValues = idValues;
	}
	

	public Long getId() {
		return id;
	}
	
	@Override
	public String toString() {
		return "Section [ _Type="
				+ _ThemeCode + "]";
	}



	public boolean isDone() {
		return _Done;
	}

	public void setDone(boolean _Done) {
		this._Done = _Done;
	}	

	public Aggregation getAggregation() {
		return _Aggregation;
	}

	public void setAggregation(Aggregation aggregation) {
		this._Aggregation = aggregation;
	}

	public String getThemeCode() {
		return _ThemeCode;
	}


	public void setThemeCode(String type) {
		_ThemeCode = type;
	}
	
	

//	public Integer getStatus() {
//		return _Status;
//	}
//
//
//	public void setStatus(Integer status) {
//		_Status = status;
//	}

	
	
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
	
	public void addSectionTable(SectionTable SectionTable) {
		getSectionTables().add(SectionTable);
	}

	public List<SectionTable> getSectionTables() {
		if (_SectionTables == null)
			_SectionTables = new ArrayList<SectionTable>();
		return _SectionTables;
	}
	
	public void setSectionTables(List<SectionTable> sectionTables) {
		this._SectionTables = sectionTables;
	}

}

