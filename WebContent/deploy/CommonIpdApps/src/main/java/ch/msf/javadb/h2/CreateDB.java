package ch.msf.javadb.h2;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;

import ch.msf.buildcountries.LoadCountriesFromExcel;
import ch.msf.error.ConfigException;
import ch.msf.form.FatalException;
import ch.msf.form.wizard.MSFForm;
import ch.msf.util.IOUtils;

/**

 */
public class CreateDB extends H2Base {

	// list of db statement to create db
	ArrayList<String> _SqlStatements = null;

	/**
	 * @return
	 */
	public boolean createDb(Properties properties) {

		boolean ret = false;

		try {

			boolean doLiquibase = false; // now init countries db with JPA
			// boolean doLiquibase = true; // now init countries db with
			if (doLiquibase) {
				_Conn = connectDb(properties);

				updateLiquibaseDb(properties);

				/*
				 * We commit the transaction. Any changes will be persisted to
				 * the database now.
				 */
				_Conn.commit();
				System.out.println("Transactions committed");
				ret = true;
			} else {

				ret = buildAllCountriesTables(properties);

			}

		} catch (Exception sqle) {
			if (sqle instanceof SQLException)
				printSQLException((SQLException) sqle);
			try {
				if (_Conn != null)
					_Conn.rollback();
				System.out.println("Rollbacked the transactions");
			} catch (SQLException e) {
				System.out.println("Exception " + e);
			}
			if (sqle instanceof FatalException)
				throw (FatalException) sqle;
			if (sqle instanceof ConfigException)
				throw (ConfigException) sqle;

			throw new ConfigException(sqle);
		} finally {
			if (_Server != null)
				shutdownDb();
		}

		return ret;
	}

	private boolean buildAllCountriesTables(Properties properties) throws IOException {
		boolean ret = false;
		//taivd cmt: init country for the first time run
		// We create the countries tables...
		String allCountriesFileName = properties.getProperty("allCountriesFileName");
		if (allCountriesFileName == null)
			throw new ConfigException(getClass().getName() + "::allCountriesFileName not found!" );

		LoadCountriesFromExcel app = new LoadCountriesFromExcel();
		String[] args = new String[1];
		boolean modeJavaWebStart = (Boolean) properties.get("modeJavaWebStart");
		if (modeJavaWebStart) {
			args[0] = allCountriesFileName;
		} else

			// TN77 (admin)
		args[0] = properties.get("workspaceresourceCommnon") + allCountriesFileName;
		//args[0] =getClass().getProtectionDomain().getCodeSource().getLocation().getPath()+allCountriesFileName;//taivd add, read country at resource
		
		System.out.println("Excel path = "+args[0]);
		app.init(args);
		app.run(modeJavaWebStart);
		ret = true;

		System.out.println("updateJPADb:Database has been started and structure model updated.");
		return ret;

	}

	private void updateLiquibaseDb(
	/* String changelog, boolean javaWebStart */Properties properties)
			throws SQLException {

		Statement s = null;
		ResultSet rs = null;

		// list of Statements
		ArrayList<Statement> statements = new ArrayList<Statement>();

		if (_Conn != null) {
			/*
			 * Creating a statement object that we can use for running various
			 * SQL statements commands against the database.
			 */
			s = _Conn.createStatement();
			statements.add(s);

			// We create the tables...

			String statementFileName = properties
					.getProperty("sqlStatementFileName");
			if (statementFileName == null)
				throw new ConfigException(getClass().getName()
						+ "::statementFileName not found!");
			//
			boolean javaWebStart = new Boolean(
					(Boolean) properties.get("modeJavaWebStart"));

			liquibaseUpdate(statementFileName, javaWebStart);
		}

	}

	private void liquibaseUpdate(String changelog, boolean javaWebStart) {
		/*
		 * 
		 * Liquibase liquibase = null; try { Database database =
		 * DatabaseFactory.getInstance() .findCorrectDatabaseImplementation( new
		 * JdbcConnection(_Conn));
		 * 
		 * // ArrayList<Class> classList = new ArrayList<Class>(); //
		 * classList.add(AdminApp.class); // classList.add(CreateDB.class); //
		 * classList.add(IOUtils.class);
		 * 
		 * if (javaWebStart) { liquibase = new Liquibase(changelog, // new
		 * CompositeResourceAccessor(), database); // new
		 * SpringResourceOpener(xx), database); new
		 * ClassLoaderResourceAccessor(), database); // new
		 * MyClassLoaderResourceAccessor(changelog, classList), // new
		 * FileSystemResourceAccessor(), database); } else liquibase = new
		 * Liquibase(changelog, new FileSystemResourceAccessor(), database);
		 * 
		 * liquibase.update(null); System.out.println("Db updated!"); // return
		 * true;
		 * 
		 * } catch (Exception e) { e.printStackTrace(); throw new
		 * FatalException(StackTraceUtil.getCustomStackTrace(e));
		 * 
		 * } finally {
		 * 
		 * }
		 */

	}
}
