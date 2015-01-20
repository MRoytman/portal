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
public class AggregRuleBuilderMorbidity {

	// permet de fabriquer toutes les business rules par rangées qui n'ont que des ADD
	// l'input est le fichier de concepts de définition des tables de section

	// il faut générer écran par écran

//	private static final String INPUT_FILE = "H:\\devel\\workspace64Bits\\AGG_OPD_LB\\src\\main\\resources\\Section-opd_morbidity-TablesIds.txt";
	private static final String INPUT_FILE = "H:\\devel\\workspace64Bits\\AGG_OPD_LB\\src\\main\\resources\\Section-opd_vaccination-TablesIds.txt";
	

	private static final String SECTION_TABLE_TOKEN = "SectionTable";

	//

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			AggregRuleBuilderMorbidity prog = new AggregRuleBuilderMorbidity(args);
		} catch (Exception e) {
			//
			e.printStackTrace();
		}

	}

	public AggregRuleBuilderMorbidity(String[] args) throws Exception {
		// if (init(args))
		{

			// open input file
			ArrayList<String> lineList = new ArrayList<String>();
			// loadFile(lineList, _InputFile, null);
			IOUtils.loadFile(lineList, INPUT_FILE, "UTF-8");

			// apply pattern
			StringBuilder sbResult = applyPattern(lineList);

			// show result on screen
			System.out.println(sbResult.toString());

			// IOUtils.writeFile(OUTPUT_FILE, sbResult.toString(), "UTF-8");
		}
	}

	/**
	 * @param sbInputFile
	 * @param _RegExp2
	 * @return
	 */
	private StringBuilder applyPattern(ArrayList<String> lineList) {

		String sectionTable = null;

		StringBuilder sbResult = new StringBuilder();
		ArrayList<String> source = new ArrayList<String>(lineList);
		String rg = ".*[<COL>[\\w:|]+\\tNumeric]+<COL>[\\w:|]+\\tCalculated<COL>[\\w:|]+\\tCalculated";//
		String rg2 = "<COL>([\\w:|><=]+\\t(?:Numeric|Calculated))";//
		Pattern p = Pattern.compile(rg);
		Pattern p2 = Pattern.compile(rg2);

		int countLine = 0;
		// take 1rst word
		for (String line : source) {
			countLine++;
			if (!line.equals("")) {

				// track SectionTable
				String[] words = line.split("\\t");
				if (words != null && words.length == 4 && words[0].equals(SECTION_TABLE_TOKEN)) {

					sectionTable = words[1];
					
					sbResult.append("\n//-------------------").append(sectionTable).append("-------------------\n");
				}

				Matcher matcher = p.matcher(line.trim());
				Matcher matcher2 = p2.matcher(line.trim());
				boolean matchOk = matcher.matches();
				if (matchOk) { // section table opd_morbidity_acute
					ArrayList<String> lineItems = new ArrayList<String>();
					ArrayList<String> lineItemTotals = new ArrayList<String>();
//					ArrayList<String> lineItem2s = lineItems;
					while (matcher2.find()) {
						// String replacedString = matcher.group();
						// String replacedString = matcher.group();
						// use start, end because group() takes extra last char!?
						String replacedString = line.substring(matcher2.start(), matcher2.end());
						String rg3 = "^<COL>([\\w:|><=]+).*";
						Pattern p3 = Pattern.compile(rg3);
						Matcher matcher3 = p3.matcher(replacedString);
						matchOk = matcher3.matches();
						if (replacedString.contains("Numeric")) {
							// get conceptId
							if (matchOk) {
								String numericConceptId = matcher3.replaceAll("$1");
								lineItems.add(numericConceptId);
								lineItemTotals.add(numericConceptId);
							}

						} else {
							if (replacedString.contains("Calculated")) {
								if (matchOk) {
									String totCalculatedId = matcher3.replaceAll("$1");

									// go through the list of numerics
									if (lineItems.size() > 0){
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
										lineItems.clear();
									}
									else{
										//last line tot
//										lineItem2s = lineItemTotals;
										for (int i = 0; i < lineItemTotals.size(); i++) {
											String numericConceptId = lineItemTotals.get(i);
											sbResult.append(numericConceptId + '\t' + "MapperNotifierActionRunner" + '\t' + "2114" + '\t' + totCalculatedId + '\t'
													+ "CalculatorActionRunner" + '\t' + "NOTIFY" + '\n');
										}
										// add total
										sbResult.append(totCalculatedId + '\t' + "CalculatorActionRunner" + '\t' + "2121" + '\t' + sectionTable);
										// go through the list of numerics
										for (int i = 0; i < lineItemTotals.size(); i++) {
											String numericConceptId = lineItemTotals.get(i);
											sbResult.append('\t' + "NOTIFY" + '\t' + numericConceptId + '\t' + 'X' + '\t' + "ADD");
										}
										sbResult.append('\n');
									}
								}
							}
						}

					}

					sbResult.append('\n');
				}
				else{
					// section table opd_morbidity_ncd
					String regExp = ".*[<COL>[\\w:|]+\\tNumeric]+<COL>[\\w:|]+\\tCalculated";//
					String regExp2 = "(<COL>([\\w:|\\-><=]+\\t(Numeric|Calculated)))";//

					Pattern pattern = Pattern.compile(regExp);
					Pattern pattern2 = Pattern.compile(regExp2);
					Matcher match = pattern.matcher(line.trim());
					Matcher match2 = pattern2.matcher(line.trim());
					boolean matchOk2 = match.matches();
					if (matchOk2) {
						ArrayList<String> lineItems = new ArrayList<String>();
						while (match2.find()) {
							// String replacedString = matcher.group();
							// String replacedString = matcher.group();
							// use start, end because group() takes extra last char!?
							String replacedString = line.substring(match2.start(), match2.end());
							// if (replacedString.contains("Numeric")){
							//
							// }
							// else{
							// if (replacedString.contains("Calculated")){
							//
							// }
							// }
							lineItems.add(replacedString);
						}

						// the last record contains the total to be calculated
						String totCalculatedItem = lineItems.get(lineItems.size() - 1);
						String totCalculatedId = null;
						// get conceptId
						String rg3 = "^<COL>([\\w:|\\-><=]+).*";
						Pattern p3 = Pattern.compile(rg3);
						Matcher matcher3 = p3.matcher(totCalculatedItem);
						matchOk = matcher3.matches();
						if (matchOk) {
							totCalculatedId = matcher3.replaceAll("$1");

							// go through the list of numerics
							for (int i = 0; i< lineItems.size()-1; i++) {
								String numericItem = lineItems.get(i);
								matcher3 = p3.matcher(numericItem);
								String numericId = matcher3.replaceAll("$1");
								sbResult.append(numericId + '\t' + "MapperNotifierActionRunner" + '\t' + "2114" + '\t' + totCalculatedId + '\t' + "CalculatorActionRunner"+ '\t' + "NOTIFY"+ '\n');
							}
							// add total
							sbResult.append(totCalculatedId + '\t' + "CalculatorActionRunner" + '\t' + "2121" + '\t' + sectionTable);
							// go through the list of numerics
							for (int i = 0; i< lineItems.size()-1; i++) {
								String numericItem = lineItems.get(i);
								matcher3 = p3.matcher(numericItem);
								String numericId = matcher3.replaceAll("$1");
								sbResult.append('\t' + "NOTIFY" + '\t' + numericId + '\t' + 'X' + '\t' + "ADD");
							}						
//							+ '\t' + totCalculatedId + '\t' + "CalculatorActionRunner"+ '\t' + "NOTIFY"+ '\n');
							sbResult.append('\n');
						}

						sbResult.append('\n');
					}
				}
			}
		}
		return sbResult;
	}

}
