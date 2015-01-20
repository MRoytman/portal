package ch.msf.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class EntryFormConfig implements Cloneable {
	
	private List<SelectedCountry> _SelectedCountry;	

	/**
	 * @return the countries
	 */
	public List<SelectedCountry> getSelectedCountries() {
		if (_SelectedCountry == null)
			_SelectedCountry = new ArrayList<SelectedCountry>();
		return _SelectedCountry;
	}

	/**
	 * @param countries
	 *            the countries to set
	 */
	public void setSelectedCountries(List<SelectedCountry> countries) {
		this._SelectedCountry = countries;
	}

	
	public Object clone() {
		Object o = null;
		try {
			// On récupère l'instance à renvoyer par l'appel de la 
			// méthode super.clone()
			o = super.clone();
			((EntryFormConfig)o).setSelectedCountries(new ArrayList<SelectedCountry>() );
			for (SelectedCountry selCountry : getSelectedCountries()) {
				SelectedCountry newSelCountry = (SelectedCountry)selCountry.clone();
				newSelCountry.setProjects(new ArrayList<Project>());
				((EntryFormConfig)o).getSelectedCountries().add(newSelCountry);
				for(Project project: selCountry.getProjects()){
					Project newProject = (Project)project.clone();
					newProject.setCareCenters(new ArrayList<CareCenter>());
					newSelCountry.getProjects().add(newProject);
					for(CareCenter careCenter:project.getCareCenters()){
						CareCenter newCareCenter = (CareCenter)careCenter.clone();
						newProject.getCareCenters().add(newCareCenter);
					}
				}
			}
			
		} catch(CloneNotSupportedException cnse) {
			// Ne devrait jamais arriver car nous implémentons 
			// l'interface Cloneable
			cnse.printStackTrace(System.err);
		}
		// on renvoie le clone
		return o;
	}

	

}
