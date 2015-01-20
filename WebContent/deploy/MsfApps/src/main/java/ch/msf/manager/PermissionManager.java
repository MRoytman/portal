package ch.msf.manager;

import java.util.Map;




public interface PermissionManager {


	/**
	 * load all application roles/pwd
	 * @return true if ok
	 */
	boolean loadRoles();
	
	/**
	 * load role permission mapping
	 * @return true if ok
	 */
	boolean loadPermissions();
	
	/**
	 *  
	 * @param roleName
	 * @return the permissions for given roleName
	 */
	Permissions getPermissions(String roleName);

	/**
	 * 
	 * @param roleName
	 * @param password
	 * @return true if roleName is known and password is valid for this user.
	 */
	boolean isAuthorized(String roleName, String password);

	/**
	 * current logged role in application
	 * @param roleName
	 */
	void setCurrentRole(String roleName);
	
	/**
	 * 
	 * @return the current role
	 */
	String getCurrentRole();

	/**
	 * 
	 * @return true if application configuration allows more than one role with permissions
	 */
	boolean isUserPermissionsActive();

	Map<String, String> getRolePwds();


}
