package ch.msf.manager;

//import static org.eclipse.persistence.config.PersistenceUnitProperties.JDBC_URL;
//import static org.eclipse.persistence.config.PersistenceUnitProperties.LOGGING_LEVEL;
//import static org.eclipse.persistence.config.PersistenceUnitProperties.LOGGING_SESSION;
//import static org.eclipse.persistence.config.PersistenceUnitProperties.LOGGING_THREAD;
//import static org.eclipse.persistence.config.PersistenceUnitProperties.LOGGING_TIMESTAMP;
//import static org.eclipse.persistence.config.PersistenceUnitProperties.TARGET_SERVER;
//import static org.eclipse.persistence.config.PersistenceUnitProperties.TRANSACTION_TYPE;
//import org.eclipse.persistence.config.TargetServer;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import ch.msf.error.FatalException;
import ch.msf.javadb.h2.H2Base;
import ch.msf.util.StackTraceUtil;

public class DbManagerImpl implements DbManager {

	private EntityManagerFactory _Factory;

	private Boolean _IsAdmin;

	// /**
	// *
	// * @param isAdmin
	// * (this param should normally have the same value at any
	// * subsequent calls)
	// * @return
	// */
	// @Override
	// public EntityManager startTransaction(boolean isAdmin) {
	//
	// if (_Factory == null) {
	// // String persistenceUnit = null;
	//
	// // ECLIPSELINK
	// //
	// // // Ensure RESOURCE_LOCAL transactions is used.
	// // properties.put(TRANSACTION_TYPE,
	// // PersistenceUnitTransactionType.RESOURCE_LOCAL.name());
	// //
	// // if (isAdmin) {
	// // // Configure the internal EclipseLink connection pool
	// // // properties.put(JDBC_DRIVER, "org.h2.Driver");
	// // properties.put(JDBC_URL,
	// // "jdbc:h2:W:\\medical\\devel\\test\\H2DBAdmin");
	// // // properties.put(JDBC_USER, "user1");
	// // // properties.put(JDBC_PASSWORD, "user1");
	// // persistenceUnit = "IpdOpdAdmin";
	// //
	// // }
	// // else{
	// // properties.put(JDBC_URL,
	// // "jdbc:h2:W:\\medical\\devel\\test\\H2DB");
	// // persistenceUnit = "IpdOpd";
	// // }
	// //
	// // // Configure logging. FINE ensures all SQL is shown
	// // properties.put(LOGGING_LEVEL, "FINE");
	// // properties.put(LOGGING_TIMESTAMP, "false");
	// // properties.put(LOGGING_THREAD, "false");
	// // properties.put(LOGGING_SESSION, "false");
	// //
	// // // Ensure that no server-platform is configured
	// // properties.put(TARGET_SERVER, TargetServer.None);
	// // // _Factory = Persistence
	// // //
	// //
	// .createEntityManagerFactory(ServiceHelper.getConfigurationManagerService().getPersistenceUnitName());
	// //
	// // _Factory =
	// // Persistence.createEntityManagerFactory(persistenceUnit,
	// // properties);
	//
	// if (isAdmin)
	// _Factory = Persistence
	// .createEntityManagerFactory("IpdOpdAdmin");
	// else
	// _Factory = Persistence.createEntityManagerFactory("IpdOpd");
	// }
	//
	// EntityManager em = _Factory.createEntityManager();
	// // refresh the cache
	// // useful?
	// em.getEntityManagerFactory().getCache().evictAll();
	// //
	// em.getTransaction().begin();
	//
	// return em;
	// }

	@Override
	public void endTransaction(EntityManager em) {
		em.getTransaction().commit();

		em.close();

	}

	/**
	 * (LAST VERSION)
	 * 
	 * @param isAdmin
	 * @return
	 */
	@Override
	public EntityManager startTransaction() {

		if (_Factory == null) {
			Boolean isAdmin = isAdmin();
			if (isAdmin == null) {
				try {
					isAdmin.booleanValue(); // force NullPointerException
				} catch (NullPointerException e) {
					throw new FatalException(
							StackTraceUtil.getCustomStackTrace(e));

				}
			}
			
			Map<String, Object> configOverrides = new HashMap<String, Object>();
			configOverrides.put("javax.persistence.jdbc.url", H2Base.getDBUrl(isAdmin)/*"jdbc:h2:D:\\H2DBAdmin;MVCC=TRUE"*/);
			// in admin tool, do
			// ALTER USER user1 SET PASSWORD 'newpasswd'
			configOverrides.put("javax.persistence.jdbc.password", "hndsgbv");
			

			if (isAdmin){

				_Factory =
				    Persistence.createEntityManagerFactory("IpdOpdAdmin", configOverrides);
				
//				_Factory = Persistence
//						.createEntityManagerFactory("IpdOpdAdmin");
			}
			else{

				_Factory =
				    Persistence.createEntityManagerFactory("IpdOpd", configOverrides);
//				_Factory = Persistence.createEntityManagerFactory("IpdOpd");
			}
		}

		EntityManager em = _Factory.createEntityManager();
		// refresh the cache
		// useful?
		em.getEntityManagerFactory().getCache().evictAll();
		//
		em.getTransaction().begin();

		return em;
	}

	@Override
	public Boolean isAdmin() {
		return _IsAdmin;
	}

	@Override
	public void setAdmin(boolean admin) {
		_IsAdmin = admin;
	}

}
