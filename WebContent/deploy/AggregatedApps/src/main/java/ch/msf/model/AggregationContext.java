package ch.msf.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * @author cmi
 * this class is used to locate a aggregation in a context (country, project, carecenter)
 * there is a one to one relationship with a aggregation.
 * when an instance of this class is created, it must be associated with a new or existing Aggregation
 * The deletion of an instance normally implies the deletion of associated aggregation 
 */

@Entity
@Table(name="AggregationContext")
public class AggregationContext  implements Cloneable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="AGC_id")
	private Long id;
	
	
	@Column(name = "AGC_NAME")
	// will/can be used to differentiate the contexts in an integrated system
	private String _ContextName;

	@OneToOne(cascade=CascadeType.ALL)
	@JoinColumn(name="AGC_AGGREG", nullable = false)
	private Aggregation _Aggregation;
	
	@OneToOne
	@JoinColumn(name="AGC_SELCTY", nullable = false)
	private SelectedCountry _SelectedCountry;
	
	
	@OneToOne
	@JoinColumn(name="AGC_SELPRJ", nullable = false)
	private Project _SelectedProject;	
	
	
	@OneToOne
	@JoinColumn(name="AGC_SELCCAR", nullable = false)
	private CareCenter _SelectedCareCenter;	
	

	@Override
	public String toString() {
		return "AggregationContext [_Aggregation=" + _Aggregation.toString() + ", " + ", _SelectedCountry="
				+ _SelectedCountry.getCode() + ", _SelectedProject=" + _SelectedProject.getName()
				+ ", _SelectedCareCenter=" + _SelectedCareCenter.getName() + "]";
	}


	public Long getId() {
		return id;
	}


	public Aggregation getAggregation() {
		return _Aggregation;
	}




	public void setAggregation(Aggregation aggregation) {
		this._Aggregation = aggregation;
	}
	
	
	public SelectedCountry getSelectedCountry() {
		return _SelectedCountry;
	}


	public void setSelectedCountry(SelectedCountry _SelectedCountry) {
		this._SelectedCountry = _SelectedCountry;
	}


	public Project getSelectedProject() {
		return _SelectedProject;
	}


	public void setSelectedProject(Project _SelectedProject) {
		this._SelectedProject = _SelectedProject;
	}


	public CareCenter getSelectedCareCenter() {
		return _SelectedCareCenter;
	}


	public void setSelectedCareCenter(CareCenter _SelectedCareCenter) {
		this._SelectedCareCenter = _SelectedCareCenter;
	}
	
	/**
	 * @return the _ContextName
	 */
	public String getContextName() {
		return _ContextName;
	}


	/**
	 * @param _ContextName the _ContextName to set
	 */
	public void setContextName(String _ContextName) {
		this._ContextName = _ContextName;
	}

	
}
