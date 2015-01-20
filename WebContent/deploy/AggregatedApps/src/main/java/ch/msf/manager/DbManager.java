package ch.msf.manager;

import javax.persistence.EntityManager;

public interface DbManager {
	
	public EntityManager startTransaction();
	
	public void endTransaction(EntityManager em);
	
	public Boolean isAdmin();
	
	public void setAdmin(boolean admin);

}
