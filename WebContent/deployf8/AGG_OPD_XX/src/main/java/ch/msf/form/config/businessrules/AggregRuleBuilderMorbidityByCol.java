package ch.msf.form.config.businessrules;

/**
 * 16 ao�t 2011
 */

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ch.msf.util.IOUtils;

/**
 * @author cmiville This program reads an input file and apply the regexp on its content. The output is printed on
 *         screen
 */
public class AggregRuleBuilderMorbidityByCol {

	// permet de fabriquer toutes les business rules par rangées qui n'ont que des ADD
	// l'input est le fichier de concepts de définition des tables de section

	// il faut générer écran par écran

	private static final String INPUT_FILE = "H:\\devel\\workspace64Bits\\AGG_OPD_LB\\src\\main\\resources\\Section-opd_morbidity-TablesIds.txt";

	private static final String SECTION_TABLE_TOKEN = "SectionTable";
	private static final String[] SECTION_TABLE_CAPTURE_ARRAY = { "opd_morbidity_ncd", "opd_morbidity_acute" };
	private static final int[][] COL_INDEX_CAPTURE_ARRAY = {{ 1, 2, 3, 4 }, { 1, 2, 4, 5 }};

	//

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			AggregRuleBuilderMorbidityByCol prog = new AggregRuleBuilderMorbidityByCol(args);
		} catch (Exception e) {
			//
			e.printStackTrace();
		}

	}

	public AggregRuleBuilderMorbidityByCol(String[] args) throws Exception {
		// if (init(args))
		{

			// open input file
			ArrayList<String> lineList = new ArrayList<String>();
			// loadFile(lineList, _InputFile, null);
			IOUtils.loadFile(lineList, INPUT_FILE, "UTF-8");

			// apply pattern
			for (int j = 0; j < SECTION_TABLE_CAPTURE_ARRAY.length; j++) {
				for (int i = 0; i < COL_INDEX_CAPTURE_ARRAY[j].length; i++) {
					StringBuilder sbResult = applyPattern(lineList, SECTION_TABLE_CAPTURE_ARRAY[j], COL_INDEX_CAPTURE_ARRAY[j][i]);
					// show result on screen
					System.out.println(sbResult.toString());
				}
			}

			// IOUtils.writeFile(OUTPUT_FILE, sbResult.toString(), "UTF-8");
		}
	}

	/**
	 * @param sbInputFile
	 * @param _RegExp2
	 * @return
	 */
	private StringBuilder applyPattern(ArrayList<String> lineList, String sectionTableToCapture, int indexCol) {

		String sectionTable = null;

		StringBuilder sbResult = new StringBuilder();
		boolean capture = false;
		ArrayList<String> source = new ArrayList<String>(lineList);
		String rg = ".*[<COL>[\\w:|]+\\tNumeric]+<COL>[\\w:|]+\\tCalculated(<COL>[\\w:|]+\\tCalculated)?";//
		String rg2 = "(<COL>[\\w:|><=_]+\\t(?:Numeric|Calculated))";//
		Pattern p = Pattern.compile(rg);
		Pattern p2 = Pattern.compile(rg2);
		ArrayList<String> lineItems = new ArrayList<String>();
		int countLine = 0;
		// take 1rst word
		for (String line : source) {
			countLine++;
			if (!line.equals("")) {

				// track SectionTable
				String[] words = line.split("\\t");
				if (words != null && words.length == 4 && words[0].equals(SECTION_TABLE_TOKEN)) {
					sectionTable = words[1];
					if (sectionTable.equals(sectionTableToCapture)) {
						capture = true;
						sbResult.append("\n//-------------------").append(sectionTable).append(" by column-------------------\n");
					} else {
						if (capture)
							sbResult.append("\n//-------------------END ").append(sectionTable).append("-------------------\n");
						capture = false;
					}
					continue;
				}
				if (capture) {
					Matcher matcher = p.matcher(line.trim());
					Matcher matcher2 = p2.matcher(line.trim());
					boolean matchOk = matcher.matches();
					if (matchOk) { // section table opd_morbidity_acute
						String replacedString = null;
						// String replacedString = matcher2.group(indexCol);// NOK!?
						int matchCount = 0;
						while (matcher2.find()) {
							matchCount++;
							if (matchCount == indexCol) {
								replacedString = line.substring(matcher2.start(), matcher2.end());
								break;
							}
						}

						String rg3 = "^<COL>([\\w:|><=_]+).*";
						Pattern p3 = Pattern.compile(rg3);
						Matcher matcher3 = p3.matcher(replacedString);
						matchOk = matcher3.matches();
						if (matchOk) {
							if (replacedString.contains("Numeric")) {// last line
																		// get conceptId
								String numericConceptId = matcher3.replaceAll("$1");
								lineItems.add(numericConceptId);
							} else {
								if (replacedString.contains("Calculated")) {// last line
									String totCalculatedId = matcher3.replaceAll("$1");

									// go through the list of numerics
									if (lineItems.size() > 0) {
										for (int i = 0; i < lineItems.size(); i++) {
											String numericConceptId = lineItems.get(i);
											// String numericItem = lineItems.get(i);
											// matcher3 = p3.matcher(numericItem);
											// String numericId = matcher3.replaceAll("$1");
											sbResult.append(numericConceptId + '\t' + "MapperNotifierActionRunner" + '\t' + "2114" + '\t' + totCalculatedId + '\t'
													+ "CalculatorActionRunner" + '\t' + "NOTIFY" + '\n');
										}
										// add total
										sbResult.append(totCalculatedId + '\t' + "CalculatorActionRunner" + '\t' + "2121" + '\t' + sectionTable);
										// go through the list of numerics
										for (int i = 0; i < lineItems.size(); i++) {
											String numericConceptId = lineItems.get(i);
											sbResult.append('\t' + "NOTIFY" + '\t' + numericConceptId + '\t' + 'X' + '\t' + "ADD");
										}
										sbResult.append('\n');
									}
								} else {
									System.out.println("Big problem!");
									return null;
								}
							}

						}

					}
				}
			}
		}

		return sbResult;
	}

}
