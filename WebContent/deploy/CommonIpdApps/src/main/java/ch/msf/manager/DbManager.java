package ch.msf.manager;

import javax.persistence.EntityManager;

public interface DbManager {
	
	EntityManager startTransaction();
	
	Boolean isAdmin();
	
	void setAdmin(boolean admin);
	
	void endTransaction(EntityManager em);

	void endTransaction(EntityManager em, boolean problem);// TN132 (manage roll back)

}
