//package ch.msf.model.datavalidator.businessrule;
//
//import java.io.IOException;
//import java.util.HashMap;
//
//import ch.msf.Constants;
//import ch.msf.error.ConfigException;
//import ch.msf.form.FatalException;
//import ch.msf.util.Reflex;
//
//public class BusinessRuleManagerImpl implements BusinessRuleManager {
//
//	// map HashMap<BusinessRuleChecker ClassName, BusinessRuleCheckerI>
//	protected static HashMap<String, BusinessRuleCheckerI> _BusinessRuleCheckers = new HashMap<String, BusinessRuleCheckerI>();
//
//	// map HashMap<conceptId, ConceptBusinessRuleChecker>
//	private HashMap<String, BusinessRuleSerieCheckerI> _ConceptRuleCheckers = new HashMap<String, BusinessRuleSerieCheckerI>();
//
//	private <T extends BusinessRuleCheckerI> T instanciateBusinessRuleChecker(String className, Integer errorCode, String[] args) throws ConfigException {
//		BusinessRuleCheckerI businessRuleChecker = null;
//		String errMess = "BusinessRuleManager::BusinessRuleChecker: instanciateBusinessRuleChecker problem on "+className;
//
//		className = Constants.BUSINESS_RULES_CHECKER_PREFIX+ className;
//		Class<? extends Object> cls = Reflex.getClassInstance(className);
//		if (cls != null) {
//			try {
//				businessRuleChecker = (BusinessRuleCheckerI) cls.newInstance();
//				if (businessRuleChecker == null)
//					throw new FatalException(errMess);
//				businessRuleChecker.setErrorCode(errorCode);
//				businessRuleChecker.verifyConfigParameters(args);
//			}
//			catch (ConfigException e) {
//				throw e;
//			}
//			catch (Exception e) {
//				throw new ConfigException(errMess, e);
//			}
//		} else
//			throw new ConfigException(errMess);
//
//		return (T) businessRuleChecker;
//	}
//
//	/**
//	 * 
//	 * @param checkableId
//	 * @return the checker for the given conceptId build it if it dows not exist
//	 */
//	protected BusinessRuleSerieCheckerI getBusinessRuleSerieChecker(String checkableId) {
//		BusinessRuleSerieCheckerI cbrc = getConceptRuleCheckers().get(checkableId);
//		if (cbrc == null) {
//			cbrc = new BusinessRuleSerieCheckerImpl();
//			getConceptRuleCheckers().put(checkableId, cbrc);
//		}
//		return cbrc;
//	}
//
//	/**
//	 * 
//	 * @param businessRuleCheckerName
//	 * @param args 
//	 * @param errorCode 
//	 * @return the BusinessRuleChecker identified by businessRuleCheckerName,
//	 *         build it if it dows not exist
//	 */
//	protected BusinessRuleCheckerI getBusinessRuleChecker(String businessRuleCheckerName, Integer errorCode, String[] args) {
//		BusinessRuleCheckerI brc = getBusinessRuleCheckers().get(businessRuleCheckerName);
//		if (brc == null) {
//			brc = instanciateBusinessRuleChecker(businessRuleCheckerName, errorCode, args);
//			getBusinessRuleCheckers().put(businessRuleCheckerName, brc);
//		}
//		return brc;
//	}
//
//	/**
//	 * load all business rules configuration
//	 * @param resourcePathName
//	 * @return true if concept resources have been loaded correctly
//	 * @throws IOException
//	 */
//	@Override
////	public boolean loadBusinessRulesResources(String resourcePathName)  {
////		// read the file resources
////
////		// get all resource file names
////		ArrayList<String> conceptsRules = ServiceHelper.getConfigurationManagerService().getResourceManager()
////				.readResourceFile(resourcePathName);
////
////		if (conceptsRules == null || conceptsRules.size() == 0) {
////			return false;
////		}
////
////		// determine the separator
////		String separator = Constants.RESOURCE_FIELD_SEPARATOR;
////		if (separator == null)
////			throw new ConfigException(getClass().getName()+"::loadConceptResources: No field separator was found in " + resourcePathName);
////
////		// go through all concepts business rules
////		int lineCount = 0;
////		boolean ret = true;
////		try {
////			for (String conceptsRule : conceptsRules) {
////				lineCount++;
////				if (!conceptsRule.equals("") && !conceptsRule.startsWith("//")) {// if not empty and not a comment
////					String[] parts = conceptsRule.split(separator);
////					String conceptId = parts[0].trim();
////					boolean b1Ok = conceptId != null && !conceptId.isEmpty();
////					String conceptCheckerName = parts[1].trim();
////					boolean b2Ok = conceptCheckerName != null && !conceptCheckerName.isEmpty();
////					String errorMessageCodeStr = parts[2].trim();
////					boolean b3Ok = errorMessageCodeStr != null && !errorMessageCodeStr.isEmpty();
////					if (b1Ok && b2Ok && b3Ok) {
////						// get the ConceptBusinessRuleChecker
////						BusinessRuleSerieCheckerI conceptBusinessRuleChecker = getBusinessRuleSerieChecker(conceptId);
////						// get/create the BusinessRuleChecker
////						// copy the remaining array into new array
////						String[] args = Arrays.copyOfRange(parts, 3, parts.length);
////						Integer errorCode = Integer.parseInt(errorMessageCodeStr);
////						BusinessRuleCheckerI businessRuleChecker = getBusinessRuleChecker(conceptCheckerName, errorCode, args);
////						// add the rule to the concept checker
////						conceptBusinessRuleChecker.addBRChecker(businessRuleChecker);
////					}
////					else{
////						System.out.println(getClass().getName()+"::loadConceptResources:value skipped on line " + lineCount);
////						ret = false;
////					}
////				}
////			}
////		} 
////		catch (Exception e) {
////			
////			throw new ConfigException(getClass().getName()+"::loadConceptResources: Problem reading " + resourcePathName + " file on line " + lineCount+".\n"+e.getMessage(), e);
////		}
////
////		return ret;
////	}
//	
//	
//	/**
//	 * perform a check on valueToCheck using a checker identified by businessRuleCheckerId
//	 * @param businessRuleCheckerId
//	 * @param valueToCheck
//	 * @param runtimeParams: params for checker
//	 * @return null or the first checker that fail
//	 */
//	@Override
//	public BusinessRuleCheckerI check(/*CheckableI checkable*/ String businessRuleCheckerId, String valueToCheck, Object[] runtimeParams){
////		String key = checkable.getKey();
//		// get the serie checker
//		BusinessRuleSerieCheckerI serieChecker = getBusinessRuleSerieChecker(businessRuleCheckerId);
//		// perform all checks and return null or the first checker that fail
//		return serieChecker.checkAllBR(valueToCheck, runtimeParams);
//	}
//
//	/**
//	 * @param args
//	 */
//	public static void main(String[] args) {
//
////		BusinessRuleManager prog = new BusinessRuleManager();
////
////		BusinessRuleCheckerI futureDateChecker;
////		try {
////			futureDateChecker = prog.("businessrule.date.FutureDateChecker", 1, null);
////			boolean ret = futureDateChecker.verifyParam("01.01.2000", null);
////			assert ret;
////			ret = futureDateChecker.verifyParam("", null);
////			try {
////				assert ret;
////			} catch (AssertionError e) {
////				// ok case
////				ret = true;
////			}
////			System.out.println("Fin");
////		} catch (Exception e) {
////			// TODO Auto-generated catch block
////			e.printStackTrace();
////		}
//
//	}
//
//	protected HashMap<String, BusinessRuleCheckerI> getBusinessRuleCheckers() {
//		return _BusinessRuleCheckers;
//	}
//
//	protected HashMap<String, BusinessRuleSerieCheckerI> getConceptRuleCheckers() {
//		return _ConceptRuleCheckers;
//	}
//
//}
