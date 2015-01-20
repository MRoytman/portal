package ch.msf.form.wizard;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import javax.persistence.NoResultException;
import javax.swing.JOptionPane;

import ch.msf.CommonConstants;
import ch.msf.CommonIpdConstants;
import ch.msf.error.ConfigException;
import ch.msf.form.ParamException;
import ch.msf.form.StopNextException;
import ch.msf.form.config.AggregationListTableModel.AggregatedModelData;
import ch.msf.manager.EntryFormConfigurationManager;
import ch.msf.model.Aggregation;
import ch.msf.model.AggregationContext;
import ch.msf.model.AggregationPeriod.PeriodType;
import ch.msf.model.SelectionContext;
import ch.msf.service.ServiceHelper;

import com.nexes.wizard.WizardPanelDescriptor;

public class AggregationListFormDescriptor extends WizardPanelDescriptor {

	public static final String IDENTIFIER = "AGGREGATION_LIST_FORM_PANEL";

	AggregationListFormPanel _FormPanel;

	EntryFormConfigurationManager _ConfigurationManager;

	private SelectionContext _LastSelectionContext;
	private String _LastSelectionLanguage;

	public AggregationListFormDescriptor() {
		_FormPanel = new AggregationListFormPanel();
		_FormPanel.setDescriptor(this);

		setPanelComponent(_FormPanel);

		_ConfigurationManager = ServiceHelper.getEntryFormConfigurationManagerService();
	}

	public Object getNextPanelDescriptor() {
		return SectionListFormDescriptor.IDENTIFIER;
	}

	public Object getBackPanelDescriptor() {

		return ParamDescriptor.IDENTIFIER;
	}

	public void aboutToDisplayPanel() {
		// show header info
		_FormPanel.buildBannerInfo();

		// check if there is a change in the context
		SelectionContext selectionContext = _ConfigurationManager.getCurrentSelectionContext();

		boolean contextChanged = false;
		// build the list of aggregations
		if (!selectionContext.equals(_LastSelectionContext) || !_ConfigurationManager.getDefaultLanguage().equals(_LastSelectionLanguage)) {
			contextChanged = true;

			// update aggregation list
			updateAggregationList();

			// save the Context and language
			_LastSelectionContext = selectionContext;
			_LastSelectionLanguage = _ConfigurationManager.getDefaultLanguage();

		}

		// display current list choice
		// _FormPanel.buildCurAggregationInfo(2);

		_FormPanel.configurePanelStartState(contextChanged);

	}

	/**
	 * 
	 */
	public void updateAggregationList() {

		SelectionContext selectionContext = _ConfigurationManager.getCurrentSelectionContext();

		// get the list of aggregation contexts
		String date = null;
		String idOrName = null;
		boolean testOnId = true;
		List<AggregationContext> aggregationContexts = getAggregationContexts(selectionContext, date, idOrName, testOnId);

		// ...and update the view,
		_FormPanel.updateModelView(aggregationContexts);
	}

	/**
	 * search on aggregations
	 * 
	 * @param selectionContext
	 * @param date
	 * @param idOrName
	 * @param testOnId
	 * @return a list of aggregationcontext that matches with passed criteria
	 */
	private List<AggregationContext> getAggregationContexts(SelectionContext selectionContext, String date, String idOrName, boolean testOnId) {
		List<AggregationContext> aggregatedContexts = null;
		try {
			aggregatedContexts = ServiceHelper.getAggregationDataManagerService().getAllSelectedAggregationContext(selectionContext, date);
		} catch (ParamException e) {
			// get the error message
			String errMessNumber = e.getMessage();
			int errorCode = -1;
			String errMess = null;
			try {
				errorCode = Integer.parseInt(errMessNumber);
				errMess = ServiceHelper.getMessageService().getMessage(errorCode);
			} catch (NumberFormatException n) {
				System.out.println("Programming problem? (no number passed to error!)");
			}
			errMess = ServiceHelper.getMessageService().getMessage(CommonConstants.MESSAGE_OPERATION_FAILED) + ", " + errMess;
			System.out.println(errMess);
			JOptionPane.showMessageDialog(null, errMess, "", JOptionPane.OK_OPTION, null);

		}
		return aggregatedContexts;
	}

	public void aboutToHidePanel() {

		if (getWizard()._MyDirectionNext) // if we go next...
		{
			// save the aggregation selection
			if (_FormPanel.getTable().getSelectedRow() == -1) {
				JOptionPane.showMessageDialog(null, "The application is in an inconsistent state, please exit if moving back does not work", "Inconsistent state",
						JOptionPane.OK_OPTION, null);
				// reset flags
				// _ConfigurationManager.isAggregationDataSaved();
				_ConfigurationManager.setNewAggregatedDataInsertion(false);
				getWizard().setCurrentPanel(ParamDescriptor.IDENTIFIER, false);
			}

			AggregationContext currentAggregContext = buildCurrentAggregation();
			// if the currentAggregationContext has not been saved its id will
			// be null
			_ConfigurationManager.setCurrentAggregationContext(currentAggregContext);

			// save aggregation and associated sections
			// only if it is a new one
			if (currentAggregContext.getId() == null)
				saveResults();
		}

	}

	/**
	 * manage all cases to get the currentAggregContext
	 * 
	 * @return the currentAggregContext
	 */
	private AggregationContext buildCurrentAggregation() {
		AggregationContext currentAggregContext = null;
		Aggregation currentAggregation = null;

		AggregatedModelData modelData = _FormPanel.getTableModel().getModelValues().get(_FormPanel.getTable().getSelectedRow());
		_ConfigurationManager.setCurrentAggregatedModelData(modelData);

		Long currentAggregationContextId = modelData.getAggregatedContextId();

//		SelectionContext selectionContext = _ConfigurationManager.getCurrentSelectionContext();
//		String contextId = MiscelaneousUtils.getContextKey(selectionContext.getSelectedProject().getCode(), selectionContext.getSelectedCareCenter().getCode());

		if (_ConfigurationManager.getAggregationContextCache().get(currentAggregationContextId) == null) {
			// this aggregation has not been parsed yet
			try {
				// load the aggregation
				currentAggregContext = ServiceHelper.getAggregationDataManagerService().getAggregatedContext(currentAggregationContextId);

				if (currentAggregContext == null) {
					// this should never happen as there is a uniq db constraint
					// on the id
					JOptionPane.showMessageDialog(null, CommonConstants.ERR_MESS + "Aggregation not found with this id, please contact the application administator.", "Error!",
							JOptionPane.OK_OPTION, null);
					getWizard().setNextFinishButtonEnabled(false);
					return null;
				}
				currentAggregation = currentAggregContext.getAggregation();

				// ...and update the view,
				// _FormPanel.updateModelView(currentAggregation);

			} catch (NoResultException e) {
				// this happen when it's a new aggregation
				currentAggregContext = _ConfigurationManager.createNewAggregationContext();

				currentAggregation = new Aggregation();
				// getting the type will allow to build associated sections
				String aggregationType = _ConfigurationManager.getAggregationType(modelData.getAggregatedLabel());
				if (aggregationType == null)
					throw new ConfigException("Configuration problem with aggregationType, could not get it from " + modelData.getAggregatedLabel());

				currentAggregation.setThemeType(aggregationType);

				String aggregationId = _ConfigurationManager.getAggregationId(modelData.getAggregatedLabel());
				currentAggregation.setThemeCode(aggregationId);
				currentAggregation.setPeriodMultiple(1);
				currentAggregation.setPeriodType(PeriodType.WEEK_PERIOD);
				try {
					currentAggregation.setStartDate(CommonConstants._tsSdf.parse(modelData.getStartDate()));
					currentAggregation.setEndDate(CommonConstants._tsSdf.parse(modelData.getEndDate()));
				} catch (ParseException e1) {
					// normally impossible to happen
					e1.printStackTrace();
				}
				currentAggregation.setCreationDate(new Date());
				currentAggregContext.setAggregation(currentAggregation);

				_ConfigurationManager.createNewSections(currentAggregation);

			}
			// aggregationContextId can be null if not saved (new aggregation),
			// but it
			// works...(AS
			// LONG THERE IS ONLY ONE LINE ALLOWED IN EDITION)
			_ConfigurationManager.getAggregationContextCache().put(currentAggregationContextId, currentAggregContext);

		} else { // this aggregation is already in cache

			currentAggregContext = _ConfigurationManager.getAggregationContextCache().get(currentAggregationContextId);
			currentAggregation = currentAggregContext.getAggregation();

			// ...and update the view,
			// entryform:...update the aggregation first!...
			boolean all = true;
			currentAggregation = ServiceHelper.getAggregationDataManagerService().readDBAggregationInfo(currentAggregation, all);
			currentAggregContext.setAggregation(currentAggregation);
		}
		return currentAggregContext;
	}

	private void saveResults() {
		try {
			_ConfigurationManager.saveCurrentAggregation();
		} catch (ParamException e) {
			e.printStackTrace();
			String errMess = ServiceHelper.getMessageService().getMessage(CommonConstants.MESSAGE_SAVE_FAILED) + ", " + e.getCause();

			if (e.getCause().toString().contains("ConstraintViolationException: Unique index or primary key violation"))
				errMess = ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_ID_ALREADY_EXIST);// + ", " + errMess;
			JOptionPane.showMessageDialog(null, errMess, "Error", JOptionPane.OK_OPTION, null);
			getWizard().setNextFinishButtonEnabled(false);
			getWizard().setBackButtonEnabled(false);

			// remove from cache
			AggregatedModelData modelData = _ConfigurationManager.getCurrentAggregatedModelData();
			Long currentAggregationContextId = modelData.getAggregatedContextId();
			_ConfigurationManager.getAggregationContextCache().remove(currentAggregationContextId);

			StopNextException ex = new StopNextException(errMess);
			ex._DescriptorIdentifier = IDENTIFIER;
			throw ex;
		}
	}

}
