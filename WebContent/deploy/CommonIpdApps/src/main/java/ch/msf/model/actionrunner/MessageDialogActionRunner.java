package ch.msf.model.actionrunner;

import javax.swing.JOptionPane;

import ch.msf.CommonIpdConstants;
import ch.msf.error.ConfigException;

public class MessageDialogActionRunner extends AbstractActionRuleRunnerBase {

	// private String _ThisNotifierId;
	//
	// private String _ObserverId;
	// // observer class name
	// private String _ConceptRunnerName;
	// // the command to send to the observer
	// private String _ObserverCommand;

	@Override
	public void verifyConfigParameters() throws ConfigException {

		// String errMess = getClass().getName() +
		// "::verifyParameters: args problem :" + getConfigParameters()
		// +
		// ", there should be 3 parameters (observer conceptId, observer action, command).";
		// // try {
		//
		// if (getConfigParameters() != null && getConfigParameters().length ==
		// 4) {
		// _ThisNotifierId = getConfigParameters()[0];
		// _ObserverId = getConfigParameters()[1];
		// _ConceptRunnerName = getConfigParameters()[2];
		// _ObserverCommand = getConfigParameters()[3];
		// }
		// else
		// throw new ConfigException(errMess);
	}

	@Override
	public boolean runAction(String command, Object[] runtimeParams) {

		boolean ret = true;
		if (command.equals(CommonIpdConstants.ACTION_RULES_REINIT)) {
			// we should reinit our components
			return true; //ok

		} else if (command.equals(CommonIpdConstants.ACTION_RULES_VALUECHANGED) || command.equals(CommonIpdConstants.ACTION_RULES_NOTIFY)) {

			JOptionPane.showMessageDialog(null, "The changed id is: " + runtimeParams[0] + " and the value changed is: " + runtimeParams[1], "Information", JOptionPane.OK_OPTION,
					null);

		}

		return ret;
	}

}
