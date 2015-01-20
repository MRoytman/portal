package ch.msf.util.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import ch.msf.util.IOUtils;

/* ListDemo.java requires no other files. */
public class SimpleEntityList extends JPanel implements ListSelectionListener {

	private static final long serialVersionUID = 1L;

	private static int WIDTH_MIN_LENGTH = 300;
	private static int HEIGTH_MIN_LENGTH = 200;

	private JButton _AddButton;
	private JButton _DeleteButton;
	private JButton _RenameButton; //TN78
	private JTextField _EntityValue;
	private String _EntityName;
	private int _EntityValueLength;
	private boolean _ShowListOnly;

	private JList _EntityList;
	private DefaultListModel _EntityListModel;

	private DeleteListener _DeleteListener;
	private AddListener _AddListener;
	private RenameListener _RenameListener;

	public SimpleEntityList(String entityName, int entityLength, List<?> entities, boolean showListOnly, int maxWidth) {
		super(new BorderLayout());
		_ShowListOnly = showListOnly;
		setEntityName(entityName);
		_EntityValueLength = entityLength;

		_EntityListModel = new DefaultListModel();
		// fill with model with an existing collection
		setEntities(entities);

		_EntityList = new JList(_EntityListModel);
		getEntityList().setName(entityName);
		getEntityList().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		getEntityList().setSelectedIndex(0);
		getEntityList().addListSelectionListener(this);
		getEntityList().setVisibleRowCount(10);
		JScrollPane listScrollPane = new JScrollPane(getEntityList());

		JPanel buttonPane = null;

		// ImageIcon addIcon = getAddIcon();
		// Dimension iconButtonDim = new Dimension(addIcon.getIconWidth(),
		// addIcon.getIconHeight());
		Dimension dimButton = new Dimension(60, 20);
		// _AddButton = new JButton("+");
		_AddButton = new JButton("Add");
		_AddListener = new AddListener(_AddButton);
		_AddButton.addActionListener(_AddListener);
		_AddButton.setEnabled(false);
		_AddButton.setMaximumSize(dimButton);
		_AddButton.setSize(dimButton);
		_AddButton.setPreferredSize(dimButton);

		// ImageIcon subIcon = getSubIcon();
		// _DeleteButton = new JButton("-");
		_DeleteButton = new JButton("Del");
		_DeleteListener = new DeleteListener();
		_DeleteButton.addActionListener(_DeleteListener);
		// _DeleteButton.setMaximumSize(iconButtonDim);
		_DeleteButton.setMaximumSize(dimButton);
		_DeleteButton.setSize(dimButton);
		_DeleteButton.setPreferredSize(dimButton);

		_RenameButton = new JButton("Ren");//TN78
		_RenameListener = new RenameListener();
		_RenameButton.addActionListener(_RenameListener);
		_RenameButton.setMaximumSize(dimButton);
		_RenameButton.setSize(dimButton);
		_RenameButton.setPreferredSize(dimButton);

		_EntityValue = new JTextField(_EntityValueLength);
		_EntityValue.addActionListener(_AddListener);
		_EntityValue.getDocument().addDocumentListener(_AddListener);

		buttonPane = new JPanel();
		buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
		buttonPane.add(_DeleteButton);
		// buttonPane.add(Box.createHorizontalStrut(5));
		// buttonPane.add(new JSeparator(SwingConstants.VERTICAL));
		// buttonPane.add(Box.createHorizontalStrut(5));
		buttonPane.add(_RenameButton);
		buttonPane.add(_EntityValue);
		buttonPane.add(_AddButton);
		// buttonPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		JLabel title = new JLabel(_EntityName);
		title.setHorizontalAlignment(SwingConstants.CENTER);
		add(title, BorderLayout.NORTH);
		add(listScrollPane, BorderLayout.CENTER);

		add(buttonPane, BorderLayout.PAGE_END);
		if (_ShowListOnly) {
			_AddButton.setVisible(false);
			_EntityValue.setVisible(false);
			_DeleteButton.setVisible(false);
			_RenameButton.setVisible(false);
		}

		Dimension countryDim = new Dimension(WIDTH_MIN_LENGTH, HEIGTH_MIN_LENGTH);
		if (maxWidth != -1) {
			countryDim = new Dimension(maxWidth, HEIGTH_MIN_LENGTH);
		}
		setMinimumSize(countryDim);
		setMaximumSize(countryDim);
		setPreferredSize(countryDim);
		// setBorder(BorderFactory.createLineBorder(Color.red));
	}

	public void setEntities(List<?> entities) {
		getEntityListModel().clear();
		// fill with model with an existing collection
		if (entities != null) {
			for (Object obj : entities) {
				// use entity's toString() impl
				getEntityListModel().addElement(obj);
			}
		}

	}

	private ImageIcon getAddIcon() {
		String iconPlus = "addIcon2.gif";
		URL imgURL = IOUtils.getResource(iconPlus, getClass());
		if (imgURL != null) {
			return new ImageIcon(imgURL, "MSF icon");
		}
		return null;
	}

	private ImageIcon getSubIcon() {
		String iconPlus = "cancelIcon2.gif";
		URL imgURL = IOUtils.getResource(iconPlus, getClass());
		if (imgURL != null) {
			return new ImageIcon(imgURL, "MSF icon");
		}
		return null;
	}

	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		_EntityValue.setEnabled(enabled);
		_DeleteButton.setEnabled(enabled);
		_RenameButton.setEnabled(enabled);

	}

	/**
	 * listener on the entity data field (not the list)
	 * 
	 * @author cmi
	 * 
	 */
	public class AddListener implements ActionListener, DocumentListener {
		// ONE listener to keep track of changes in the list
		// AND check business rules to allow changes or not
		// use of ListDataListener is not simpler as we don't have an hand on
		// the changed element
		// private ArrayList<ListAddListener> _Listeners = new
		// ArrayList<SimpleEntityList.ListAddListener>();
		ListAddListener _RuleCheckerListener;
		private boolean _Enabled = false;
		private boolean _StayDisabled = false;
		private JButton _CurrentButton;

		public AddListener(JButton button) {
			_CurrentButton = button;
		}

		// Required by ActionListener.
		public void actionPerformed(ActionEvent e) {
			String name = _EntityValue.getText();

			// check unicity
			if (name.equals("") || getEntityListModel().contains(name)) {
				Toolkit.getDefaultToolkit().beep();
				_EntityValue.requestFocusInWindow();
				_EntityValue.selectAll();
				return;
			}

			int index = getEntityList().getSelectedIndex(); // get selected
															// index
			if (index == -1) { // no selection, so insert at beginning
				index = 0;
			} else { // add after the selected item
				index++;
			}

			// notify all listeners
			// for(ListAddListener listener :_Listeners){
			boolean rulesCheckedOk = _RuleCheckerListener.notify(this, _EntityValue.getText());

			// }

			if (rulesCheckedOk) {
				getEntityListModel().insertElementAt(_EntityValue.getText(), index);

				// Reset the text field.
				_EntityValue.requestFocusInWindow();
				_EntityValue.setText("");

				// Select the new item and make it visible.
				getEntityList().setSelectedIndex(index);
				getEntityList().ensureIndexIsVisible(index);
			}

		}

		public void changedUpdate(DocumentEvent e) {
			System.out.println(e);
			if (!handleEmptyTextField(e)) {
				enableButton();
			}
		}

		public void insertUpdate(DocumentEvent e) {
			// System.out.println(e);
			// e.getDocument();
			enableButton();
		}

		public void removeUpdate(DocumentEvent e) {
			// System.out.println(e);
			handleEmptyTextField(e);

		}

		private void enableButton() {
			if (!_Enabled && !_StayDisabled) {
				_CurrentButton.setEnabled(true);
			}
		}

		private boolean handleEmptyTextField(DocumentEvent e) {
			if (e.getDocument().getLength() <= 0) {
				_CurrentButton.setEnabled(false);
				_Enabled = false;
				return true;
			}
			return false;
		}

		// public void addChangeListener(ListAddListener listener){
		// _Listeners.add(listener);
		// }
		public void setListener(ListAddListener listener, boolean stayDisabled) {
			_RuleCheckerListener = listener;
			_StayDisabled = stayDisabled;
		}

		public String getEntityName() {
			return _EntityName;
		}
	}

	/**
	 * listener on the delete button
	 * 
	 * @author cmi
	 * 
	 */
	public class DeleteListener implements ActionListener {
		// ONE listener to keep track of changes in the list
		// AND check business rules to allow changes or not
		// use of ListDataListener is not simpler as we don't have an hand on
		// the changed element
		// private ArrayList<ListDeleteListener> _Listeners = new
		// ArrayList<SimpleEntityList.ListDeleteListener>();
		ListDeleteListener _RuleCheckerListener;
		private boolean _StayDisabled = false;

		public DeleteListener() {
		}

		public void actionPerformed(ActionEvent e) {
			int index = getEntityList().getSelectedIndex();

			if (index >= 0) {
				Object removed = getEntityListModel().get(index);
				// check business rules
				// notify all listeners
				// for(ListDeleteListener listener :_Listeners){
				boolean rulesCheckedOk = _RuleCheckerListener.notify(this, removed);
				// }
				if (rulesCheckedOk) {
					removed = getEntityListModel().remove(index);

					int size = getEntityListModel().getSize();

					if (size == 0) {
						_DeleteButton.setEnabled(false);

					} else {
						if (index == getEntityListModel().getSize()) {
							index--;
						}

						getEntityList().setSelectedIndex(index);
						getEntityList().ensureIndexIsVisible(index);
					}
				}
			}

		}

		// public void addChangeListener(ListDeleteListener listener){
		// _Listeners.add(listener);
		//
		// }
		public void setListener(ListDeleteListener listener, boolean stayDisabled) {
			_RuleCheckerListener = listener;
			_StayDisabled = stayDisabled;
		}

		public String getEntityName() {
			return _EntityName;
		}
	}

	/**
	 * listener on the delete button
	 * 
	 * @author cmi
	 * 
	 */
	public class RenameListener implements ActionListener {
		// ONE listener to keep track of changes in the list
		// AND check business rules to allow changes or not
		// use of ListDataListener is not simpler as we don't have an hand on
		// the changed element
		// private ArrayList<ListDeleteListener> _Listeners = new
		// ArrayList<SimpleEntityList.ListDeleteListener>();
		ListRenameListener _RuleCheckerListener;
		private boolean _StayDisabled = false;

		public RenameListener() {
		}

		public void actionPerformed(ActionEvent e) {
			int index = getEntityList().getSelectedIndex();
			String newName = _EntityValue.getText();

			if (index >= 0) {
				Object renamed = getEntityListModel().get(index);
				// check business rules
				// notify all listeners
				// for(ListDeleteListener listener :_Listeners){
				boolean rulesCheckedOk = _RuleCheckerListener.notify(this, renamed, newName);
				// }
				if (rulesCheckedOk) {
					getEntityListModel().setElementAt(newName, index);
					if (rulesCheckedOk) {
						// Reset the text field.
						_EntityValue.requestFocusInWindow();
						_EntityValue.setText("");

						// Select the new item and make it visible.
						getEntityList().setSelectedIndex(index);
						getEntityList().ensureIndexIsVisible(index);
					}
				}
			}

		}

		// public void addChangeListener(ListDeleteListener listener){
		// _Listeners.add(listener);
		//
		// }
		public void setListener(ListRenameListener listener, boolean stayDisabled) {
			_RuleCheckerListener = listener;
			_StayDisabled = stayDisabled;
		}

		public String getEntityName() {
			return _EntityName;
		}
	}

	/**
	 * will allow a listener of the list to be notified when values are about to
	 * be added
	 * 
	 * @author cmi
	 * 
	 */
	public interface ListAddListener {
		public boolean notify(AddListener source, Object objAdded);
	}

	/**
	 * will allow a listener of the list to be notified when values are about to
	 * be deleted
	 * 
	 * @author cmi
	 * 
	 */
	public interface ListDeleteListener {
		public boolean notify(DeleteListener source, Object objDeleted);
	}

	/**
	 * will allow a listener of the list to be notified when values are about to
	 * be Renamed
	 * 
	 * @author cmi
	 * 
	 */
	public interface ListRenameListener {
		public boolean notify(RenameListener source, Object objRenamed, Object objAdded);
	}

	/**
	 * 
	 */
	public void valueChanged(ListSelectionEvent e) {
		if (e.getValueIsAdjusting() == false) {

			if (getEntityList().getSelectedIndex() == -1) {
				_DeleteButton.setEnabled(false);
				_RenameButton.setEnabled(false);

			} else {
				if (!_ShowListOnly) {
					_DeleteButton.setEnabled(true);
					_RenameButton.setEnabled(true);
				}
			}
		}
	}

	/**
	 * let's be notified by any list selection change
	 * 
	 * @param listener
	 */
	public void addListListener(ListSelectionListener listener) {
		getEntityList().addListSelectionListener(listener);
	}

	/**
	 * @return an entity name sorted list
	 */
	// public ArrayList<String> getEntitieNames() {
	// // TreeSet<String> ts = new TreeSet<String>();
	// ArrayList<String> list = new ArrayList<String>();
	// Enumeration<String> enu = (Enumeration<String>) getEntityListModel()
	// .elements();
	// while (enu.hasMoreElements()) {
	// Object entity = enu.nextElement();
	// list.add(entity.toString());
	// }
	// Collections.sort(list);
	// return list;
	// }

	/**
	 * @return the _EntityList
	 */
	public JList getEntityList() {
		return _EntityList;
	}

	public void setSelectedIndex(int i) {
		if (getEntityListModel().getSize() > 0)
			getEntityList().setSelectedIndex(i);

	}

	/**
	 * @return the _EntityListModel
	 */
	public DefaultListModel getEntityListModel() {
		return _EntityListModel;
	}

	/**
	 * @return the _DeleteListener
	 */
	public DeleteListener getDeleteListener() {
		return _DeleteListener;
	}

	/**
	 * @return the _DeleteListener
	 */
	public RenameListener getRenameListener() {
		return _RenameListener;
	}

	/**
	 * @return the _AddListener
	 */
	public AddListener getAddListener() {
		return _AddListener;
	}

	/**
	 * @return the _EntityName
	 */
	public String getEntityName() {
		return _EntityName;
	}

	/**
	 * @param _EntityName
	 *            the _EntityName to set
	 */
	public void setEntityName(String entityName) {
		_EntityName = entityName;
	}

}
