package ch.msf.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ch.msf.CommonConstants;
import ch.msf.basicservice.BasicServiceHelper;
import ch.msf.error.ConfigException;

//TN148
public class PermissionManagerImpl implements PermissionManager {

	private Map<String, String> _RolePwds; // Map<userrole, pwd>
	private Map<String, Permissions> _RolePermissions;// Map<userrole, perms>
	private String _CurrentRole; // current logged role in application

	@Override
	public boolean loadRoles() {
		if (_RolePwds == null) {
			_RolePwds = new HashMap<String, String>();
			String configFile = "application.roles.filename";
			String resourceFileName = BasicServiceHelper.getConfigurationManagerService().getConfigField(configFile);

			if (resourceFileName != null) {
				ArrayList<String> rolePwds = BasicServiceHelper.getResourceService().loadResource(resourceFileName);
				String separator = CommonConstants.RESOURCE_FIELD_SEPARATOR;
				int lineCount = 0;
				for (String rolePwd : rolePwds) {
					lineCount++;
					if (!rolePwd.equals("") && !rolePwd.startsWith("//")) {
						String[] parts = rolePwd.split(separator);
						if (parts.length != 2)
							throw new ConfigException(getClass().getName() + "::loadRoles: Fatal: Bad field on line " + lineCount + " of " + resourceFileName);

						_RolePwds.put(parts[0], parts[1]);
					}
				}
			}

		}
		if (_RolePwds.isEmpty())
			_RolePwds = null;
		return _RolePwds != null;
	}

	@Override
	public boolean loadPermissions() {

		if (_RolePermissions == null) {
			_RolePermissions = new HashMap<String, Permissions>();
			String configFile = "application.permissions.filename";
			String resourceFileName = BasicServiceHelper.getConfigurationManagerService().getConfigField(configFile);

			ArrayList<String> rolePerms = BasicServiceHelper.getResourceService().loadResource(resourceFileName);
			String separator = CommonConstants.RESOURCE_FIELD_SEPARATOR;
			int lineCount = 0;
			for (String rolePerm : rolePerms) {
				lineCount++;
				if (!rolePerm.equals("") && !rolePerm.startsWith("//")) {
					String[] parts = rolePerm.split(separator);
					if (parts.length != 2)
						throw new ConfigException(getClass().getName() + "::loadPermissions: Fatal: Bad field on line " + lineCount + " of " + resourceFileName);

					String role = parts[0];
					Permissions permissions = _RolePermissions.get(role);
					if (permissions == null) {
						permissions = new Permissions();
						_RolePermissions.put(role, permissions);
					}
					permissions.addEncounter(parts[1]);
				}
			}
		}

		return _RolePermissions != null;
	}

	@Override
	public Permissions getPermissions(String roleName) {
		return _RolePermissions.get(roleName);
	}

	@Override
	public boolean isAuthorized(String roleName, String password) {
		boolean ret = false;
		String passwordRole = _RolePwds.get(roleName);
		if (passwordRole != null)
			ret = (passwordRole.equalsIgnoreCase(password));

		if (ret)
			setCurrentRole(roleName);
		return ret;
	}

	// /////////accessors//////////
	@Override
	public void setCurrentRole(String roleName) {
		_CurrentRole = roleName;
	}

	@Override
	public String getCurrentRole() {
		return _CurrentRole;
	}

	@Override
	public boolean isUserPermissionsActive() {
		return loadRoles();
	}

	@Override
	public Map<String, String> getRolePwds() {
		return _RolePwds;
	}

	public Map<String, Permissions> getRolePermissions() {
		return _RolePermissions;
	}

}
