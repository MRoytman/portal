package ch.msf;

import java.awt.Color;
import java.util.Date;

public class CommonIpdConstants {

	// dictionary fields types
	public static final int NUMERIC_TYPE = 1;
	public static final int DATE_TYPE = 2;
	public static final int STRING_TYPE = 3;
	public static final int BOOLEAN_TYPE = 4;
	public static final int COMBO_TYPE = 5;
	public static final int TIME_TYPE = 6;
	public static final int DECIMAL_TYPE = 7; // new added
	public static final int TITLE_TYPE = 8; // new added //TN96
	

//	public static final String DATEMINUTE_FORMAT = "dd.MM.yyyy HH:mm:ss";
//	public static final String SIMPLE_DATE_FORMAT = "dd.MM.yyyy";
//	public static final String SIMPLE_TIME_FORMAT = "HH:mm";
	public static final Date INVALID_DATE = new Date(0);

	public static final int IDENTITY_TYPE_DRIVINGLICENSE = 1; // "Driving license";
	public static final int IDENTITY_TYPE_IDENTITYCARD = 2; // "Identity card";
	public static final int IDENTITY_TYPE_OTHER = 3; // "Other";

	// use to get the encounters property files types
	public static final String PROPS_STRING_SEPARATOR = "-";
	public static final String ENCOUNTER_TYPE_FILENAME_PREFIX = "EncounterType";

	
	public static final int MESSAGE_EDIT= 30;
	public static final int MESSAGE_REVERT= 31;
	public static final int MESSAGE_ACCEPT= 32;
	
	public static final int MESSAGE_TEXT_TOO_SHORT= 107;

	public static final int MESSAGE_ID_ALREADY_EXIST= 110;
	public static final int MESSAGE_ID_ADMIN_NEEDED= 111;
	public static final int MESSAGE_FORMAT_NOT_COMPATIBLE= 112;
	public static final int MESSAGE_NO_ENCOUNTER_PERMISSIONS= 113;

	public static final int MESSAGE_PANNEL_NEWCOMMAND= 50;
	public static final int MESSAGE_PANNEL_DELETECOMMAND= 51;
	public static final int MESSAGE_PANNEL_CANCELCOMMAND= 52;
	public static final int MESSAGE_PANNEL_CLEARCOMMAND= 53;
	public static final int MESSAGE_PANNEL_FINDCOMMAND= 54;
	public static final int MESSAGE_PANNEL_SAVECOMMAND= 55;
	public static final int MESSAGE_PANNEL_HELPCOMMAND= 56;
	public static final int MESSAGE_PANNEL_CONFIRMDELETE= 57;
	public static final int MESSAGE_PANNEL_ADDCOMMAND= 58;
	public static final int MESSAGE_PANNEL_VILLAGELABEL= 59;
	public static final int MESSAGE_PANNEL_AREALABEL= 60;
	public static final int MESSAGE_PANNEL_SEARCHNEXTCOMMAND= 61;
	public static final int MESSAGE_PANNEL_SEARCHBACKCOMMAND= 62;
	public static final int MESSAGE_PANNEL_FINDLABEL= 63;
	
	
	
	public static final int MESSAGE_PANNEL_STATE_ADMINCOUNTRIES= 210;
	public static final int MESSAGE_PANNEL_STATE_ADMINPANEL= 211;
	public static final int MESSAGE_PANNEL_STATE_ENCOUNTERDETAILSPANEL= 212;
	public static final int MESSAGE_PANNEL_STATE_ENCOUNTERLISTPANEL= 213;
	public static final int MESSAGE_PANNEL_STATE_FINALPANEL= 214;
	public static final int MESSAGE_PANNEL_STATE_PARAMPANEL= 215;
	public static final int MESSAGE_PANNEL_STATE_PATIENTDETAILSPANEL= 216;
	public static final int MESSAGE_PANNEL_STATE_PATIENTLISTPANEL= 217;
	public static final int MESSAGE_PANNEL_STATE_LOGINPANEL= 218;
	public static final int MESSAGE_PANNEL_STATE_VILLAGELISTPANEL= 219;
	public static final int MESSAGE_PANNEL_STATE_PATIENTLISTPANEL2= 220;


	public static final int MESSAGE_PANNEL_TOOLTIP_PATIENTID= 1000;
	public static final int MESSAGE_PANNEL_TOOLTIP_PATIENTNAME= 1001;
	public static final int MESSAGE_PANNEL_TOOLTIP_PATIENTSEX= 1002;
	public static final int MESSAGE_PANNEL_TOOLTIP_PATIENTBIRTHDAY= 1003;
	public static final int MESSAGE_PANNEL_TOOLTIP_PATIENTNEW= 1004;
	public static final int MESSAGE_PANNEL_TOOLTIP_PATIENTDELETE= 1005;
	public static final int MESSAGE_PANNEL_TOOLTIP_PATIENTCANCEL= 1006;
	public static final int MESSAGE_PANNEL_TOOLTIP_PATIENTLISTHELP= 1007;
	public static final int MESSAGE_PANNEL_TOOLTIP_PATIENTHELP= 1008;
	public static final int MESSAGE_PANNEL_TOOLTIP_ENCOUNTERLISTHELP= 1009;
	public static final int MESSAGE_PANNEL_TOOLTIP_ENCOUNTERHELP= 1010;	
	public static final int MESSAGE_PANNEL_TOOLTIP_PATIENTFIND= 1011;
	public static final int MESSAGE_PANNEL_TOOLTIP_PATIENTCLEAR= 1012;
	public static final int MESSAGE_PANNEL_TOOLTIP_ENCOUNTERNEW= 1013;
	public static final int MESSAGE_PANNEL_TOOLTIP_ENCOUNTERDELETE= 1014;
	public static final int MESSAGE_PANNEL_TOOLTIP_ENCOUNTERCANCEL= 1015;
	public static final int MESSAGE_PANNEL_TOOLTIP_ENCOUNTERTYPE= 1016;
	public static final int MESSAGE_PANNEL_TOOLTIP_ENCOUNTERDATE= 1017;
	public static final int MESSAGE_PANNEL_TOOLTIP_VILLAGENEW= 1018;
	public static final int MESSAGE_PANNEL_TOOLTIP_VILLAGELISTHELP= 1019;
	public static final int MESSAGE_PANNEL_TOOLTIP_BACKPAGEHELP= 1020;
	public static final int MESSAGE_PANNEL_TOOLTIP_NEXTPAGEHELP= 1021;
	
	
	
	
	
	
	public static final String ACTION_RULES_FILENAME = "actionRules.txt";
	public static final String ACTION_RULES_RUNNER_PREFIX = "ch.msf.model.actionrunner.";
	public static final String BUSINESS_RULES_CHECKER_PREFIX = "ch.msf.model.datavalidator.businessrule.";

	public final static Color MAIN_PANEL_BACK_GRND_COLOR = new Color(242, 242, 242);
//	public final static Color MAIN_PANEL_CONTEXT_BACK_GRND_COLOR = new Color(245, 230, 245);//1.3
	public final static Color MAIN_PANEL_CONTEXT_BACK_GRND_COLOR = new Color(231, 240, 245);//1.4
	
//	public final static Color LIST_LABEL_COL_BACK_GRND_COLOR = new Color(242, 237, 239);//
//	public final static Color LIST_LABEL_COL_TITLE_BACK_GRND_COLOR = new Color(250, 235, 241);//

	public static final int EVENT_DO_ACTION = 0;
	public static final int EVENT_STOP_ACTION = 1;

	public final static String RESOURCE_FIELD_SEPARATOR = "\t";

	
	public static final String ACTION_RULES_REINIT =	"REINIT";
	public static final String ACTION_RULES_VALUECHANGED =	"VALUECHANGED";
	public static final String ACTION_RULES_NOTIFY =	"NOTIFY";
	
}
