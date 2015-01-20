package ch.msf.model;


/**
 * This is related to TN129 (presentation of villages of origin)
 * 
 * @author cmi
 *
 */

public class VillageArea implements Cloneable{


	private String _HealthArea;
	
	private String _VillageOrigin;
	


	public VillageArea(){
    	_VillageOrigin = "-";
    }
    
    public VillageArea(String name){
    	_VillageOrigin = name;
    }
    

	@Override
	public String toString() {
		return _VillageOrigin;
	}
	

	
	public String getHealthArea() {
		return _HealthArea;
	}

	public void setHealthArea(String area) {
		this._HealthArea = area;
	}

	
	/**
	 * @return the name
	 */
	public String getVillageOrigin() {
		return _VillageOrigin;
	}

	/**
	 * @param name the name to set
	 */
	public void setVillageOrigin(String village) {
		_VillageOrigin = village;
	}

}
