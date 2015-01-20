package ch.msf.javadb.h2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;

import org.h2.tools.Server;

import ch.msf.error.ConfigException;
import ch.msf.manager.ConfigurationManagerBaseImpl;
import ch.msf.service.ServiceHelper;

/**
 * <p>
 * This sample program is a minimal Java application showing JDBC access to a
 * database.
 * </p>
 */
public class H2Base {

	private String _Driver = "org.h2.Driver";

	// the name of the database
	private String _DbName = null;

	Server _Server = null;

	// tcp mode (allow multiple connections)
	protected boolean _ServerMode = true; // default mode

	private boolean _MixedMode = true;

	// db connection
	Connection _Conn;

	/**
	 * 
	 * @param conn
	 * @param tableName
	 * @return
	 */
	protected ArrayList<String> readResults(Connection conn, String tableName) {
		ArrayList<String> results = new ArrayList<String>();

		// Connection conn = null;
		Statement stmt = null;
		ResultSet resultSet = null;
		try {
			stmt = conn.createStatement();
			resultSet = stmt.executeQuery("select * from " + tableName);
			ResultSetMetaData rsmd = resultSet.getMetaData();
			int numberCols = rsmd.getColumnCount();
			for (int i = 1; i <= numberCols; i++) {
				// print Column Names
				System.out.print(rsmd.getColumnLabel(i) + "\t\t");
			}

			System.out.println("\n-------------------------------------------------");

			while (resultSet.next()) {
				int id = resultSet.getInt(1);
				String colValue1 = resultSet.getString(2);
				// String cityName = resultSet.getString(3);
				// System.out.println(id + "\t\t" + colValue1 );
				results.add(id + "\t\t" + colValue1);
			}
			// resultSet.close();
			// stmt.close();
		} catch (SQLException sqlExcept) {
			sqlExcept.printStackTrace();
		}

		finally {
			try {
				if (resultSet != null) {
					resultSet.close();
					resultSet = null;
				}
				if (stmt != null) {
					stmt.close();
					stmt = null;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}

		return results;
	}

	/**
	 * NOT IN USE IN THIS CONFIGURATION
	 * 
	 * @param properties
	 * @return
	 * @throws SQLException
	 */
	protected Connection connectDb(Properties properties) throws SQLException {

		/* parse the arguments to determine the options */
		parseArguments(properties);

		_Conn = null;

		/* load the desired JDBC driver */
		boolean ok = loadDriver();

		DbConnectionStrategy connectionStrategy = null;

		// init db according to needs
		if (properties.get("connectionStrategy") != null) {
			String str = properties.getProperty("connectionStrategy");

			if (str.equals(DbConnectionStrategy.MUST_USE_EXISTING_DB.toString()))
				connectionStrategy = DbConnectionStrategy.MUST_USE_EXISTING_DB;
			else if (str.equals(DbConnectionStrategy.USE_OR_CREATE_DB.toString()))
				connectionStrategy = DbConnectionStrategy.USE_OR_CREATE_DB;

		} else
			throw new ConfigException("The db connectionStrategy is not defined!");

		// get db name
		_DbName = properties.getProperty("dbName");
		if (_DbName == null) {
			throw new ConfigException("The db name is not defined!");
		}
		Properties props = new Properties(); // connection properties

		props.put("user", "user1");
		props.put("password", "user1");
		// UTILE?
		// props.put("maxWait", "3000");
		// props.put("maxIdle", "-1");
		// props.put("maxActive", "-1");

		try {
			// "jdbc:h2:"+dbName+","+user+","+password+";AUTO_SERVER=TRUE;IFEXISTS=TRUE";
			String url = "jdbc:h2:";
			// String url = "jdbc:h2:tcp://localhost/" + dbName;
			if (_ServerMode) {
				url += "tcp://localhost/";
				// start the TCP Server
				_Server = Server.createTcpServer(new String[0]).start();
			}
			url += _DbName;
			if (_MixedMode) {
				// Automatic Mixed Mode
				url += ";AUTO_SERVER=TRUE";
				// System.out.println("NO MIXEDMODE");
			}
			if (connectionStrategy == DbConnectionStrategy.MUST_USE_EXISTING_DB) {
				// Opening a Database Only if it Already Exists
				url += ";IFEXISTS=TRUE";
			} else if (connectionStrategy == DbConnectionStrategy.USE_OR_CREATE_DB) {
				url += "";
			}

			System.out.println("Trying to connect to " + url);
			_Conn = DriverManager.getConnection(url, props);

			System.out.println("Connected to database ");

			// We want to control transactions manually. Autocommit is on by
			// default in JDBC.
			_Conn.setAutoCommit(false);
		} catch (SQLException e) {
			e.printStackTrace();
			if (connectionStrategy == DbConnectionStrategy.MUST_USE_EXISTING_DB && e.getErrorCode() == 90013) {
				System.out.println("Cannot open Db because it does not exist!");
				return null;
			} else
				throw new ConfigException(e.getMessage(), e);

		}
		return _Conn;
	}

	public static String getDBUrl(Boolean isAdmin) {
		// configOverrides.put("javax.persistence.jdbc.url",
		// "jdbc:h2:D:\\H2DBAdmin;MVCC=TRUE");
		ConfigurationManagerBaseImpl configurationManager = ServiceHelper.getConfigurationManagerService();

		String url = "jdbc:h2:";

		String appDir = configurationManager.getApplicationDirectory();
		url += appDir;

		url += "\\H2DB";
		if (isAdmin)
			url += "Admin";

		String applicationShortName = configurationManager.getConfigField("applicationShortName"); // TN95
		url += applicationShortName;

		String testModeDB = configurationManager.getConfigField("testModeDB"); // TN95
		if (testModeDB != null && testModeDB.equalsIgnoreCase("true"))
			url += "TEST";

		String dbVersion = System.getProperty("dbVersion");
		if (dbVersion == null)
			dbVersion = "1.0";
		url += dbVersion;
		url += "_";

		// TN124
		String mixedModeDB = configurationManager.getConfigField("mixedModeDB");
		if (mixedModeDB != null && mixedModeDB.equals("true")) {
			url += ";AUTO_SERVER=TRUE";
			// url += ";AUTO_SERVER=TRUE;LOCK_TIMEOUT=1000";// utile?
			// url += ";AUTO_SERVER=TRUE;FILE_LOCK=FS";
			// url += ";FILE_LOCK=FS";
		} else
			url += ";MVCC=TRUE"; // multi-version concurrency

		return url;
	}

	boolean shutdownDb() {

		// stop the TCP Server
		if (_ServerMode)
			_Server.stop();

		/*

		 */

		// if (framework.equals("embedded")) {
		// try {
		// // the shutdown=true attribute shuts down Derby
		// DriverManager.getConnection("jdbc:derby:;shutdown=true");
		//
		// // To shut down a specific database only, but keep the
		// // engine running (for example for connecting to other
		// // databases), specify a database in the connection URL:
		// // DriverManager.getConnection("jdbc:derby:" + dbName +
		// // ";shutdown=true");
		// } catch (SQLException se) {
		// if (((se.getErrorCode() == 50000) && ("XJ015".equals(se
		// .getSQLState())))) {
		// // we got the expected exception
		// System.out.println("Derby shut down normally");
		// // Note that for single database shutdown, the expected
		// // SQL state is "08006", and the error code is 45000.
		// return true;
		// } else {
		// // if the error code or SQLState is different, we have
		// // an unexpected exception (shutdown failed)
		// System.err.println("Derby did not shut down normally");
		// printSQLException(se);
		//
		// }
		// }
		// return false;
		// } else
		return true;
	}

	protected void releaseAllObjectDb(ResultSet rs, ArrayList<Statement> statements) {
		// release all open resources to avoid unnecessary memory usage

		// ResultSet
		try {
			if (rs != null) {
				rs.close();
				rs = null;
			}
		} catch (SQLException sqle) {
			printSQLException(sqle);
		}

		// Statements and PreparedStatements
		int i = 0;
		while (!statements.isEmpty()) {
			// PreparedStatement extend Statement
			Statement st = (Statement) statements.remove(i);
			try {
				if (st != null) {
					st.close();
					st = null;
				}
			} catch (SQLException sqle) {
				printSQLException(sqle);
			}
		}

		// Connection
		try {
			if (_Conn != null) {
				_Conn.close();
				_Conn = null;
			}
		} catch (SQLException sqle) {
			// Message: Connection is broken: "session closed" (prob du au
			// shutdown de la base)
			printSQLException(sqle);
		}
	}

	/**
	 * Loads the appropriate JDBC driver for this environment/framework.
	 * 
	 * @return TODO
	 */
	private boolean loadDriver() {

		try {
			Class.forName(_Driver).newInstance();
			// System.out.println("Loaded the appropriate driver");
			return true;
		} catch (ClassNotFoundException cnfe) {
			System.err.println("\nUnable to load the JDBC driver " + _Driver);
			System.err.println("Please check your CLASSPATH.");
			cnfe.printStackTrace(System.err);
		} catch (InstantiationException ie) {
			System.err.println("\nUnable to instantiate the JDBC driver " + _Driver);
			ie.printStackTrace(System.err);
		} catch (IllegalAccessException iae) {
			System.err.println("\nNot allowed to access the JDBC driver " + _Driver);
			iae.printStackTrace(System.err);
		}
		return false;
	}

	/**
	 * Reports a data verification failure to System.err with the given message.
	 * 
	 * @param message
	 *            A message describing what failed.
	 */
	protected void reportFailure(String message) {
		System.err.println("\nData verification failed:");
		System.err.println('\t' + message);
	}

	/**
	 * Prints details of an SQLException chain to <code>System.err</code>.
	 * Details included are SQL State, Error code, Exception message.
	 * 
	 * @param e
	 *            the SQLException from which to print details.
	 */
	public static void printSQLException(SQLException e) {
		// Unwraps the entire exception chain to unveil the real cause of the
		// Exception.
		while (e != null) {
			System.err.println("\n----- SQLException -----");
			System.err.println("  SQL State:  " + e.getSQLState());
			System.err.println("  Error Code: " + e.getErrorCode());
			System.err.println("  Message:    " + e.getMessage());

			e = e.getNextException();
		}
	}

	/**

	 */
	private void parseArguments(Properties properties) {
		// take care of db server config
		String serverMode = properties.getProperty("serverMode");
		if (serverMode != null && serverMode.equalsIgnoreCase("NoServerMode")) {
			_ServerMode = false;
			_MixedMode = false;
		}
	}
}
