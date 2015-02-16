/**
 * this will build the adminConfig.xml file from excel .csv countries file
 * and create the liquibase.xml insert part file
 */

package ch.msf.buildcountries;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import ch.msf.form.FatalException;
import ch.msf.model.Country;
import ch.msf.model.CountryName;
import ch.msf.service.ServiceHelper;
import ch.msf.util.IOUtils;


public class LoadCountriesFromExcel {
	String _ConfigFileName = null;
	private ArrayList<Country> _Countries;
	private ArrayList<CountryName> _CountriesNames;
//	private AllCountriesAdminConfig _AllCountriesConfig;
	
	public String getPathExcel()
	{
		return _ConfigFileName;
	}
	/**
	 * @param args
	 */
//	// public static void main(String[] args) {
//	//
//	//
//	// try {
//	// LoadCountriesFromExcel app = new LoadCountriesFromExcel();
//	// app.init(args);
//	//
//	// ArrayList<String> countries = app.readFile();
//	// app.parseCountries(countries);
//	// app.makeConfigObject();
//	// app.saveConfigResults("C:\\devel\\run\\dicoAllCountriesConfig.xml");
//	// app.saveLiquibaseFile("C:\\devel\\run\\liquibase.txt");
//	// } catch (IOException e) {
//	// // TODO Auto-generated catch block
//	// e.printStackTrace();
//	// }
//	// System.out.println("C'est fini...");
//	// }
//
////	private static final String PERSISTENCE_UNIT_NAME = "IpdOpd";
//	private static EntityManagerFactory factory;

//	public static void main(String[] args) {
//
//		try {
//			LoadCountriesFromExcel app = new LoadCountriesFromExcel();
//			app.init(args);
//			app.run(false);
//
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//		System.out.println("C'est fini...");
//
//	}

	public void run(boolean javaWebStart) throws IOException {
		
		if (_ConfigFileName == null)
			throw new FatalException(
					"LoadCountriesFromExcel:: Fatal: file parameter for application configuration is not defined: ");

		ArrayList<String> countries = readFile(javaWebStart);
		parseCountries(countries);
//		makeConfigObject();
		
//		saveConfigResults("C:\\devel\\run\\dicoAllCountriesConfig.xml");

//		factory = Persistence.createEntityManagerFactory(ServiceHelper.getConfigurationManagerService().getPersistenceUnitName());
//		EntityManager em = factory.createEntityManager();
	//
//		em.getTransaction().begin();
		
		ServiceHelper.getAllCountriesManagerService().saveCountryConfig(_Countries, _CountriesNames);

//		em.getTransaction().commit();
//
//		em.close();

	}


	// private void saveLiquibaseFile(String fileName) throws IOException {
	// StringBuilder sb = new StringBuilder();
	// int countId = 1;
	//
	// // save the countries
	//
	// // build a map of countrycode -> id
	// HashMap<String, Integer> ids = new HashMap<String, Integer>();
	// for (Country country : _Countries) {
	// ids.put(country.getCode(), countId);
	// sb.append("<insert tableName=\"AllCountries\">");
	// sb.append("<column name=\"CTY_id\" valueNumeric=\""+(countId++)+"\"/>");
	// sb.append("<column name=\"CTY_Continent\" value=\""+country.getContinent().trim()+"\"/>");
	// sb.append("<column name=\"CTY_Region\" value=\""+country.getRegion().trim()+"\"/>");
	// sb.append("</insert>");
	// sb.append(IOUtils.LS);
	//
	// }
	// // IOUtils.writeFile(fileName, sb.toString().replace("\"\"", "\""),
	// "UTF-8");
	//
	// // save the countries localization
	// countId = 1;
	// for (CountryName countryName : _CoutriesNames) {
	//
	// sb.append("<insert tableName=\"AllCountries_loc\">");
	// sb.append("<column name=\"CTY_LC_id\" valueNumeric=\""+(countId++)+"\"/>");
	// int idCountry = ids.get(countryName.getCode());
	// sb.append("<column name=\"CTY_LC_entity_id\" valueNumeric=\""+(idCountry)+"\"/>");
	// sb.append("<column name=\"CTY_LC_locale_code\" value=\""+countryName.getLocale().trim()+"\"/>");
	// sb.append("<column name=\"CTY_LC_localized_data\" value=\""+countryName.getLabel().trim()+"\"/>");
	// sb.append("</insert>");
	// sb.append(IOUtils.LS);
	// }
	// String str = sb.toString().replace("\"\"", "\"");
	// str = str.toString().replace("\"\"", "\"");
	// IOUtils.writeFile(fileName, str, "UTF-8");
	//
	//
	// }

//	private void makeConfigObject() {
//		_AllCountriesConfig = new AllCountriesAdminConfig();
//		_AllCountriesConfig.setCountries(_Countries);
//		_AllCountriesConfig.setCountriesNames(_CoutriesNames);
//
//	}

	// private void parseCountries(ArrayList<String> countriesString) {
	//
	// _Countries = new ArrayList<Country>();
	// _CoutriesNames = new ArrayList<CountryName>();
	//
	// for (String countryLine : countriesString) {
	// String[] fields = countryLine.split(",");
	// if (fields != null) {
	// Country country = new Country();
	// _Countries.add(country);
	// if (fields.length > 0) {
	// String countryCode2Letters = fields[0];
	// country.setCode(countryCode2Letters);
	// if (fields.length > 2) {
	// String countryCodeLabel_fr = fields[2];
	// CountryName countryName = new CountryName();
	// _CoutriesNames.add(countryName);
	// countryName.setLabel(countryCodeLabel_fr);
	// countryName.setLocale("fr");
	// countryName.setCode(countryCode2Letters);
	// if (fields.length > 3) {
	// String countryCodeLabel_en = fields[3];
	// countryName = new CountryName();
	// _CoutriesNames.add(countryName);
	// countryName.setLabel(countryCodeLabel_en);
	// countryName.setLocale("en");
	// countryName.setCode(countryCode2Letters);
	// if (fields.length > 4) {
	// String countryCodeLabel_sp = fields[4];
	// countryName = new CountryName();
	// _CoutriesNames.add(countryName);
	// countryName.setLabel(countryCodeLabel_sp);
	// countryName.setLocale("sp");
	// countryName.setCode(countryCode2Letters);
	// if (fields.length > 5) {
	// String countryContinent = fields[5];
	// country.setContinent(countryContinent);
	// if (fields.length > 6) {
	// String countryRegion = fields[6];
	// country.setRegion(countryRegion);
	// }
	// }
	// }
	// }
	// }
	// }
	// }
	//
	// }
	//
	// }

	private void parseCountries(ArrayList<String> countriesString) {

		_Countries = new ArrayList<Country>();
		_CountriesNames = new ArrayList<CountryName>();

		for (String countryLine : countriesString) {
			String[] fields = countryLine.split("\\t");
			if (fields != null) {
				Country country = new Country();
				_Countries.add(country);
				if (fields.length > 0) {
					String countryCode2Letters = fields[0];
					country.setCode(countryCode2Letters);
					if (fields.length > 2) {
						String countryCodeLabel_fr = fields[2].replace("\"\"",
								"\"");
						CountryName countryName = new CountryName();
						countryName.setCountry(country); //
						country.getCountryNames().add(countryName); //
						_CountriesNames.add(countryName);
						countryName.setLabel(countryCodeLabel_fr);
						countryName.setLocale("en");
						countryName.setCode(countryCode2Letters);
						if (fields.length > 3) {
							String countryCodeLabel_en = fields[3].replace(
									"\"\"", "\"");
							countryName = new CountryName();
							countryName.setCountry(country); //
							country.getCountryNames().add(countryName); //
							_CountriesNames.add(countryName);
							countryName.setLabel(countryCodeLabel_en);
							countryName.setLocale("fr");
							countryName.setCode(countryCode2Letters);
							if (fields.length > 4) {
								String countryCodeLabel_sp = fields[4].replace(
										"\"\"", "\"");
								countryName = new CountryName();
								countryName.setCountry(country); //
								country.getCountryNames().add(countryName); //
								_CountriesNames.add(countryName);
								countryName.setLabel(countryCodeLabel_sp);
								countryName.setLocale("sp");
								countryName.setCode(countryCode2Letters);
								if (fields.length > 5) {
									String countryContinent = fields[5]
											.replace("\"\"", "\"");
									country.setContinent(countryContinent);
									if (fields.length > 6) {
										String countryRegion = fields[6]
												.replace("\"\"", "\"");
										country.setRegion(countryRegion);
									}
								}
							}
						}
					}
				}
			}

		}

	}

	private ArrayList<String> readFile(boolean javaWebStart) {
		//taivd modify function readFile
		ArrayList<String> strList = new ArrayList<String>();
		if (!javaWebStart) {

			IOUtils.loadFile(strList, _ConfigFileName, "UTF-8");
			
			//ArrayList<Class> classList = new ArrayList<Class>();//taivd add
			//classList.add(this.getClass());//taivd add
			//String strPath = IOUtils.getResource("list_countries.csv", classList).getPath();//taivd add
			//IOUtils.loadFile(strList, strPath, "UTF-8");//taivd add

			
		} else {

			ArrayList<Class> classList = new ArrayList<Class>();
			classList.add(LoadCountriesFromExcel.class);
			//
			
			System.out.println("statementFileName = " + _ConfigFileName);
			URL fileURL = IOUtils.getResource(_ConfigFileName, classList);

			if (fileURL != null){
				System.out.println("LoadCountriesFromExcel: fileURL ok");
				try {
					strList = readFileUrl(fileURL);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			else
				System.out.println("LoadCountriesFromExcel: fileURL NULL !!");
		}

		return strList;
	}

	public ArrayList<String> readFileUrl(URL inputFileUrl)
			throws IOException {
		System.out.println("reading file from url : " + inputFileUrl);
		ArrayList<String> stringList = new ArrayList<String>();

		InputStream is = inputFileUrl.openStream();
		BufferedReader in = new BufferedReader(
				new InputStreamReader(is, "UTF8"));

		String thisLine = null;
		while ((thisLine = in.readLine()) != null) {
			stringList.add(thisLine);
		}

		in.close();

		return stringList;
	}

	public void init(String[] args) {

		// take care of configuration file name parameter
		_ConfigFileName = args[0];
	}

//	/**
//	 * 
//	 * @param string
//	 * @return
//	 */
//	private int saveConfigResults(String fileName) {
//
//		// save the results to xml
//		JAXBContext jc;
//		try {
//			jc = JAXBContext.newInstance(AllCountriesAdminConfig.class);
//			Marshaller marshaller = jc.createMarshaller();
//			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
//			JAXBElement<AllCountriesAdminConfig> jaxbElement = new JAXBElement<AllCountriesAdminConfig>(
//					new QName("allCountriesAdminConfig"),
//					AllCountriesAdminConfig.class, _AllCountriesConfig);
//
//			// String fileName = "C:\\devel\\run\\allCountriesConfig.xml";
//			OutputStream out = new FileOutputStream(fileName);
//			marshaller.marshal(jaxbElement, out);
//		} catch (Exception e) {
//			e.printStackTrace();
//
//			JOptionPane.showMessageDialog(null, e.getMessage(), "Fatal error",
//					JOptionPane.ERROR_MESSAGE, null);
//		}
//		return 0;
//	}

}
