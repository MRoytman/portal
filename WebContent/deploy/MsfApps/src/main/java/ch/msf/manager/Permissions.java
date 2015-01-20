package ch.msf.manager;

import java.util.ArrayList;

public class Permissions {
	
	private ArrayList<String> _EncounterIds;
	
	public ArrayList<String> getEncounterIds(){
		return _EncounterIds;
	}

	public void addEncounter(String encounterId) {
		if (_EncounterIds == null){
			_EncounterIds = new ArrayList<String>();
		}
		_EncounterIds.add(encounterId);		
	}
	
	public boolean hasEncounterPermission(String encounterId){
		return _EncounterIds.contains(encounterId);
	}

}
