package ch.msf.model;

/**
 * @author cmi this class is used to define a context (country, project,
 *         carecenter) it not persisted
 */

public class SelectionContext {

	private SelectedCountry _SelectedCountry;

	private Project _SelectedProject;

	private CareCenter _SelectedCareCenter;

	@Override
	public String toString() {
		return "Context [SelectedCountry=" + _SelectedCountry.getCode()
				+ ", _SelectedProject=" + _SelectedProject.getName()
				+ ", _SelectedCareCenter=" + _SelectedCareCenter.getName()
				+ "]";
	}
	
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((_SelectedCareCenter == null) ? 0 : _SelectedCareCenter
						.hashCode());
		result = prime
				* result
				+ ((_SelectedCountry == null) ? 0 : _SelectedCountry.hashCode());
		result = prime
				* result
				+ ((_SelectedProject == null) ? 0 : _SelectedProject.hashCode());
		return result;
	}



	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SelectionContext other = (SelectionContext) obj;
		if (_SelectedCareCenter == null) {
			if (other._SelectedCareCenter != null)
				return false;
		} else if (!_SelectedCareCenter.equals(other._SelectedCareCenter))
			return false;
		if (_SelectedCountry == null) {
			if (other._SelectedCountry != null)
				return false;
		} else if (!_SelectedCountry.equals(other._SelectedCountry))
			return false;
		if (_SelectedProject == null) {
			if (other._SelectedProject != null)
				return false;
		} else if (!_SelectedProject.equals(other._SelectedProject))
			return false;
		return true;
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

}
