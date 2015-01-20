package ch.msf;

import java.util.Date;

import ch.msf.util.DateUtil;


public class CommonConstants {
	
	public static final String ERR_MESS = "A fatal error has occured. Please forward the following message to the administrator:\nUn error fatal ha ocurrido: por favor enviar este mensaje a su administrador:\nUne erreur fatale est survenue, veuillez svp transmettre le message suivant à votre administrateur:\n\n\n";
//	public static final String FIELD_MISSING = "At least one field is missing.\n\n\n";
//	public static final String OPERATION_FAILED = "Operation did not succeed.\n\n\n";
	public static final String ENTER = "Connect";
	
	
	public static final String DATEMINUTE_FORMAT = "dd.MM.yyyy HH:mm:ss";
	public static final String SIMPLE_DATE_FORMAT = "dd.MM.yyyy";
	public static final String SIMPLE_TIME_FORMAT = "HH:mm";
	
	public static final Date INVALID_DATE= new Date(0);
	
	public static final DateUtil.ThreadSafeSimpleDateFormat _tsSdf = new DateUtil().new ThreadSafeSimpleDateFormat(SIMPLE_DATE_FORMAT);
	public static final DateUtil.ThreadSafeSimpleDateFormat _tsDmf = new DateUtil().new ThreadSafeSimpleDateFormat(DATEMINUTE_FORMAT);

	
	
	// use to get the encounters property files types
	public static final String PROPS_STRING_SEPARATOR = "-";
//	public static final String ENCOUNTER_TYPE_FILENAME_PREFIX = "EncounterType";
//	public static final String PROPERTY_FILE_NAME = "defaultProperties.properties";
	
//	public final static String FILE_SEPARATOR = System.getProperty("file.separator");
	
	public static final String PROPERTY_FILE_NAME = "defaultProperties.properties";
	
	public static final String MSF_BASE_DIR =	"D:\\MSFMedAppData";
	
	public static final String MESSAGES_FILENAME = "localizedMessages.txt";
	
	public static final String PATIENTID_STRING_SEPARATOR = "|";
	
	public final static String RESOURCE_FIELD_SEPARATOR = "\t";
	
	public static final int MESSAGE_BAD_PARAM = 1; // "Other";
	public static final int MESSAGE_NOT_DEFINED= 2;
	public static final int MESSAGEADMIN_CONFIG_NOT_SAVED= 3;
	public static final int MESSAGEADMIN_BAD_CONFIG= 4;
	public static final int MESSAGE_CONFIRM_QUIT= 5;
	public static final int MESSAGE_CANCEL= 6;
	
	public static final int MESSAGE_OPERATION_FAILED= 8;
	public static final int MESSAGE_SAVE_FAILED= 9;
	
	//TN129
	public static final String CODEDLIST_EXTENTION_CONCEPT_TYPE = "[codedList]"; // TEMP
	
	
	public static final String ENCOUNTER_LABELS_FILENAME = "encounterLabels.txt";
	public static final String AGGREGATION_SECTION_FILENAME_PREFIX = "AggregatedTheme-";
	public static final String AGGREGATION_LABELS_FILENAME = "aggregationLabels.txt";
	public static final String SECTION_FILENAME_PREFIX = "Section-";
	public static final String SECTIONTABLE_PREFIX = "SectionTable";
	public static final String SECTIONTABLE_SEPARATOR = "SectionSeparator";
	public static final String SECTIONTABLE_COL_SEPARATOR = "<COL>";
	
}
