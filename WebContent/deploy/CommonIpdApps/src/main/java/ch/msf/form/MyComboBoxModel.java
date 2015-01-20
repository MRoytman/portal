package ch.msf.form;

import javax.swing.DefaultComboBoxModel;

public class MyComboBoxModel extends DefaultComboBoxModel  /*implements FocusListener
																					 * JComboBox
																					 * .
																					 * KeySelectionManager
																					 * implements
																					 * KeyListener
																					 */{

	private static final String EMPTY = "   ";

	public MyComboBoxModel() {
		super();
		insertElementAt(EMPTY, 0);
	}

	@Override
	public Object getSelectedItem() {
		Object obj = super.getSelectedItem();
		return obj;
	}

	@Override
	public void setSelectedItem(Object anObject) {
		String choice = (String) anObject;
		if (choice != null && choice.equals(EMPTY)) {
			super.setSelectedItem(null);
		} else
			super.setSelectedItem(anObject);
	}

	// @Override
	// public int selectionForKey(char aKey, ComboBoxModel aModel) {
	// System.out.println("selectionForKey " + aKey);
	// if ((aKey >= '0') && (aKey <= '9'))
	// return (aKey - '0');
	// else
	// return -1;
	// }

//	@Override
//	public void focusGained(FocusEvent evt) {
//		System.out.println("focusGained");
//		JComboBox cb = (JComboBox) evt.getSource();
//		if (cb.isEditable()) {
//			Component editor = cb.getEditor().getEditorComponent();
//			if (editor != null) {
//				editor.requestFocus();
//			}
//		}
//	}

//	@Override
//	public void focusLost(FocusEvent e) {
//		System.out.println("focusLost");
//
//	}

	// @Override
	// public void keyTyped(KeyEvent e) {
	// System.out.println("keyTyped " + e.getKeyChar());
	// }
	//
	// @Override
	// public void keyPressed(KeyEvent e) {
	//
	// }
	//
	// @Override
	// public void keyReleased(KeyEvent e) {
	//
	// }

}
