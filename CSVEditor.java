import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.EmptyStackException;
import java.util.Stack;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

public class CSVEditor extends JFrame{
	private static final long serialVersionUID = 1L;

	public static void main(String[] args){
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				new CSVEditor();
			}
		});
	}
	
	CSVEditor(){
		super("CSV Editor");
		launch();
	}
	
	JToolBar createJToolBar(){
		JToolBar tb = new JToolBar();
		tb.add(createNewAction());
		tb.add(createOpenAction());
		tb.addSeparator();
		tb.add(createSaveAction());
		tb.addSeparator();
		tb.add(createUndoAction());
		tb.add(createRedoAction());
		tb.addSeparator();
		tb.add(createCutAction());
		tb.add(createCopyAction());
		tb.add(createPasteAction());
		tb.addSeparator();
		tb.add(createAddColumnAction());
		tb.add(createAddRowAction());
		tb.addSeparator();
		tb.add(createRemoveColumnAction());
		tb.add(createRemoveRowAction());
		tb.addSeparator();
		tb.add(createQuitAction());
		return tb;
	}
	
	JMenuBar createJMenuBar(){
		JMenuBar mb = new JMenuBar();
		mb.add(createFileJMenu());
		mb.add(createEditJMenu());
		mb.add(createHelpJMenu());
		return mb;
	}
	
	JMenu createFileJMenu(){
		JMenu m = new JMenu("File");
		m.add(createFileNewJMenuItem());
		m.add(createFileOpenJMenuItem());
		m.addSeparator();
		m.add(createFileSaveJMenuItem());
		m.add(createFileSaveAsJMenuItem());
		m.addSeparator();
		m.add(createFileQuitJMenuItem());
		return m;
	}
	
	JMenuItem createFileNewJMenuItem(){
		JMenuItem mi = new JMenuItem("New");
		mi.setAction(createNewAction());
		return mi;
	}
	
	private void performNewAction(){
		dispose();
		new CSVEditor();
	}
	
	AbstractAction createNewAction(){
		if(newAction == null){
			newAction = new AbstractAction("New"){
				{
					putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_MASK));
					putValue(MNEMONIC_KEY, KeyEvent.VK_N);
				}
				public void actionPerformed(ActionEvent e){
					performNewAction();
				}
			};
		}
		
		return newAction;
	}
	
	JMenuItem createFileOpenJMenuItem(){
		JMenuItem mi = new JMenuItem("Open...");
		mi.setAction(createOpenAction());
		return mi;
	}
		
	public void performOpenAction(){	
		if (view != null) {
			int choice = JOptionPane.showConfirmDialog(this, "Do you want to save your current work?", "Do You Want To Save?", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
			if (choice == JOptionPane.YES_OPTION) {
				performSaveAsAction();
				remove(view);
				
				if(chooser == null){
					chooser = createJFileChooser();
				}
				int choice2 = chooser.showOpenDialog(this);
				
				File file = chooser.getSelectedFile();
				if(choice2 == JFileChooser.APPROVE_OPTION){
					try {
						table = CSVParser.parse(chooser.getSelectedFile());
					}
					catch (Exception e) {}
					
					String title = getTitle();
					int index;
					if((index = title.indexOf(" - ")) >= 0){
						title = title.substring(0, index);
					}
					setTitle(title + " - " + file);
					
					if (view != null) {
						remove(view);
					}

					add(new JScrollPane(table));
					validate();
				}
			}
			if (choice == JOptionPane.NO_OPTION) {
				remove(view);
				
				if(chooser == null){
					chooser = createJFileChooser();
				}
				int choice2 = chooser.showOpenDialog(this);
				
				File file = chooser.getSelectedFile();
				if(choice2 == JFileChooser.APPROVE_OPTION){
					try {
						table = CSVParser.parse(chooser.getSelectedFile());
					}
					catch (Exception e) {}
					
					String title = getTitle();
					int index;
					if((index = title.indexOf(" - ")) >= 0){
						title = title.substring(0, index);
					}
					setTitle(title + " - " + file);
					
					if (view != null) {
						remove(view);
					}

					add(new JScrollPane(table));
					validate();
				}
			}
			if (choice == JOptionPane.CANCEL_OPTION) {}
		}
	}
	
	private JFileChooser createJFileChooser(){
		if(chooser == null){
			chooser = new JFileChooser();
			chooser.setFileFilter(new FileFilter() {
				public String getDescription(){
					return "CSV Files";
				}
				public boolean accept(File file){
					return file.getName().endsWith(".csv");
				}
			});
		}
		
		return chooser;
	}
	
	AbstractAction createOpenAction(){
		if(openAction == null){
			openAction = new AbstractAction("Open..."){
				{
					putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_MASK));
					putValue(MNEMONIC_KEY, KeyEvent.VK_O);
				}
				public void actionPerformed(ActionEvent e){
					performOpenAction();
				}
			};
		}
		
		return openAction;
	}
	
	JMenuItem createFileSaveJMenuItem(){
		JMenuItem mi = new JMenuItem("Save");
		mi.setAction(createSaveAction());
		return mi;
	}
	
	private void performSaveAsAction(){
		if(chooser == null){
			chooser = createJFileChooser();
		}
		
		int choice = chooser.showSaveDialog(this);
		
		if(choice == JFileChooser.APPROVE_OPTION){
			performSaveAction();
		}
	}
	
	private void performSaveAction(){
		if(chooser == null){
			performSaveAsAction();
		}
		else{
			CSVExporter.export(chooser.getSelectedFile(), table.getModel());
		}
	}
	
	AbstractAction createSaveAction(){
		if(saveAction == null){
			saveAction = new AbstractAction("Save"){
				{
					putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_MASK));
					putValue(MNEMONIC_KEY, KeyEvent.VK_S);
				}
				public void actionPerformed(ActionEvent e){
					performSaveAction();
				}
			};
		}
		
		return saveAction;
	}
	
	JMenuItem createFileSaveAsJMenuItem(){
		JMenuItem mi = new JMenuItem("Save As...");
		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_MASK | KeyEvent.SHIFT_MASK));
		mi.setMnemonic(KeyEvent.VK_S);
		mi.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				performSaveAsAction();
			}
		});
		return mi;
	}
	
	JMenuItem createFileQuitJMenuItem(){
		JMenuItem mi = new JMenuItem("Quit");
		mi.setAction(createQuitAction());
		return mi;
	}
	
	private void performQuitAction(){
		int choice = JOptionPane.showConfirmDialog(this, "Do you want to save before you quit?", "Quit?", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
		if (choice == JOptionPane.YES_OPTION){
			if(chooser == null){
				chooser = createJFileChooser();
			}
			int choice2 = chooser.showSaveDialog(this);
			
			if(choice2 == JFileChooser.APPROVE_OPTION){
				performSaveAction();
			}
			System.exit(0);
		}
		if(choice == JOptionPane.NO_OPTION){
			System.exit(0);
		}
	}
	
	AbstractAction createQuitAction(){
		if(quitAction == null){
			quitAction = new AbstractAction("Quit"){
				{
					putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_Q, KeyEvent.CTRL_MASK));
					putValue(MNEMONIC_KEY, KeyEvent.VK_Q);
				}
				public void actionPerformed(ActionEvent e){
					performQuitAction();
				}
			};
		}
		
		return quitAction;
	}
	
	JMenu createEditJMenu(){
		JMenu m = new JMenu("Edit");
		m.add(createEditUndoJMenuItem());
		m.add(createEditRedoJMenuItem());
		m.addSeparator();
		m.add(createEditCutJMenuItem());
		m.add(createEditCopyJMenuItem());
		m.add(createEditPasteJMenuItem());
		m.addSeparator();
		m.add(createEditAddJMenu());
		m.add(createEditRemoveJMenu());
		return m;
	}
	
	JMenuItem createEditUndoJMenuItem(){
		JMenuItem mi = new JMenuItem("Undo");
		mi.setAction(createUndoAction());
		return mi;
	}
	
	private void performUndoAction(){
		System.out.println("No current functionality");
	}
	
	AbstractAction createUndoAction(){
		if(undoAction == null){
			undoAction = new AbstractAction("Undo"){
				{
					putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_U, KeyEvent.CTRL_MASK));
					putValue(MNEMONIC_KEY, KeyEvent.VK_U);
				}
				public void actionPerformed(ActionEvent e){
					performUndoAction();
				}
			};
		}
		
		return undoAction;
	}
	
	JMenuItem createEditRedoJMenuItem(){
		JMenuItem mi = new JMenuItem("Redo");
		mi.setAction(createRedoAction());
		return mi;
	}
	
	private void performRedoAction(){
		System.out.println("No current functionality");
	}
	
	AbstractAction createRedoAction(){
		if(redoAction == null){
			redoAction = new AbstractAction("Redo"){
				{
					putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.CTRL_MASK));
					putValue(MNEMONIC_KEY, KeyEvent.VK_R);
				}
				public void actionPerformed(ActionEvent e){
					performRedoAction();
				}
			};
		}
		
		return redoAction;
	}
	
	JMenuItem createEditCutJMenuItem(){
		JMenuItem mi = new JMenuItem("Cut");
		mi.setAction(createCutAction());
		return mi;
	}
	
	public void performCutAction(){	
		table.setValueAt("", table.getSelectedRow(), table.getSelectedColumn());
		table.updateUI();
	}
	
	AbstractAction createCutAction(){
		if(cutAction == null){
			cutAction = new AbstractAction("Cut"){
				{
					putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.CTRL_MASK));
					putValue(MNEMONIC_KEY, KeyEvent.VK_X);
				}
				public void actionPerformed(ActionEvent e){
					performCutAction();
				}
			};
		}
		
		return cutAction;
	}
	
	JMenuItem createEditCopyJMenuItem(){
		JMenuItem mi = new JMenuItem("Copy");
		mi.setAction(createCopyAction());
		return mi;
	}
	
	private void performCopyAction(){
		copyRow = table.getSelectedRow();
		copyColumn = table.getSelectedColumn();
	}
	
	AbstractAction createCopyAction(){
		if(copyAction == null){
			copyAction = new AbstractAction("Copy"){
				{
					putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_MASK));
					putValue(MNEMONIC_KEY, KeyEvent.VK_C);
				}
				public void actionPerformed(ActionEvent e){
					performCopyAction();
				}
			};
		}
		
		return copyAction;
	}
	
	JMenuItem createEditPasteJMenuItem(){
		JMenuItem mi = new JMenuItem("Paste");
		mi.setAction(createPasteAction());
		return mi;
	}
	
	private void performPasteAction(){
		if((copyRow >= 0) && (copyColumn >= 0)){
			Object copyObject = table.getValueAt(copyRow, copyColumn);
			table.setValueAt(copyObject, table.getSelectedRow(), table.getSelectedColumn());
			copyRow = -1; copyColumn = -1;
		}
		else{
			System.out.println("Nothing to Paste.");
		}
		
	table.updateUI();
	}
	
	AbstractAction createPasteAction(){
		if(pasteAction == null){
			pasteAction = new AbstractAction("Paste"){
				{
					putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_MASK));
					putValue(MNEMONIC_KEY, KeyEvent.VK_V);
				}
				public void actionPerformed(ActionEvent e){
					performPasteAction();
				}
			};
		}
		
		return pasteAction;
	}
	
	JMenuItem createEditAddJMenu(){
		JMenu m = new JMenu("Add");
		m.add(createEditAddColumnJMenuItem());
		m.add(createEditAddRowJMenuItem());
		return m;
	}
	
	JMenuItem createEditAddColumnJMenuItem(){
		JMenuItem mi = new JMenuItem("Add Column");
		mi.setAction(createAddColumnAction());
		return mi;
	}
	
	private void performAddColumnAction(){
		model = (DefaultTableModel)table.getModel();
		TableColumn col = new TableColumn(model.getColumnCount()-1);
		table.setAutoCreateColumnsFromModel(true);
		table.addColumn(col);
	}
	
	AbstractAction createAddColumnAction(){
		if(addColumnAction == null){
			addColumnAction = new AbstractAction("Add Column"){
				public void actionPerformed(ActionEvent e){
					performAddColumnAction();
				}
			};
		}
		
		return addColumnAction;
	}
	
	JMenuItem createEditAddRowJMenuItem(){
		JMenuItem mi = new JMenuItem("Add Row");
		mi.setAction(createAddRowAction());
		return mi;
	}
	
	private void performAddRowAction(){
		model = (DefaultTableModel)table.getModel();
		model.insertRow(table.getRowCount(), new Object[]{});
	}
	
	AbstractAction createAddRowAction(){
		if(addRowAction == null){
			addRowAction = new AbstractAction("Add Row"){
				public void actionPerformed(ActionEvent e){
					performAddRowAction();
				}
			};
		}
		
		return addRowAction;
	}
	
	JMenuItem createEditRemoveJMenu(){
		JMenu m = new JMenu("Remove");
		m.add(createEditRemoveColumnJMenuItem());
		m.add(createEditRemoveRowJMenuItem());
		return m;
	}
	
	JMenuItem createEditRemoveRowJMenuItem(){
		JMenuItem mi = new JMenuItem("Remove Row");
		mi.setAction(createRemoveRowAction());
		return mi;
	}
	
	private void performRemoveRowAction(){
		if(table.getSelectedRow() >= 0){
			model = (DefaultTableModel)table.getModel();
			model.removeRow(table.getSelectedRow());
		}
		else{
			model = (DefaultTableModel)table.getModel();
			model.removeRow(table.getRowCount()-1);
		}
	}
	
	AbstractAction createRemoveRowAction(){
		if(removeRowAction == null){
			removeRowAction = new AbstractAction("Remove Row"){
				public void actionPerformed(ActionEvent e){
					performRemoveRowAction();
				}
			};
		}
		
		return removeRowAction;
	}
	
	JMenuItem createEditRemoveColumnJMenuItem(){
		JMenuItem mi = new JMenuItem("Remove Column");
		mi.setAction(createRemoveColumnAction());
		return mi;
	}
	
	private void performRemoveColumnAction(){
		if(table.getSelectedColumn() >= 0){
			TableColumn col = table.getColumnModel().getColumn(table.getSelectedColumn());
			table.removeColumn(col); 
		}
		else{
			TableColumn col = table.getColumnModel().getColumn(table.getColumnCount()-1);
			table.removeColumn(col); 
		}
	}
	
	AbstractAction createRemoveColumnAction(){
		if(removeColumnAction == null){
			removeColumnAction = new AbstractAction("Remove Column"){
				public void actionPerformed(ActionEvent e){
					performRemoveColumnAction();
				}
			};
		}
		
		return removeColumnAction;
	}
	
	JMenu createHelpJMenu(){
		JMenu m = new JMenu("Help");
		m.add(createHelpAboutJMenuItem());
		return m;
	}
	
	JMenuItem createHelpAboutJMenuItem(){
		JMenuItem mi = new JMenuItem("About");
		mi.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				performAboutAction();
			}
		});
		return mi;
	}
	
	private void performAboutAction(){
		JOptionPane.showMessageDialog(null, "CSV Editor Program Version 1.0.0\nAuthor: Eric Zebrowski\nStudent ID: 21675421\nCS 288-002", "About", JOptionPane.INFORMATION_MESSAGE);
	}
	
	private void launch(){
		int height = Toolkit.getDefaultToolkit().getScreenSize().height;
		int width = Toolkit.getDefaultToolkit().getScreenSize().width;
		setBounds(width / 8, height / 8, 3 * width / 4, 3 * height / 4);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setResizable(false);
		setJMenuBar(createJMenuBar());
		add(createJToolBar(), BorderLayout.PAGE_START);
		
		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				performQuitAction();
			}
		});
		
		CSVParser parser = new CSVParser();
		CSVExporter exporter = new CSVExporter();
		
		table = new JTable(35, 16);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		
		view = new JScrollPane(table);
		add(view);
		
		setVisible(true);
	}
	
	private AbstractAction newAction;
	private AbstractAction openAction;
	private AbstractAction saveAction;
	private AbstractAction quitAction;
	private AbstractAction redoAction;
	private AbstractAction undoAction;
	private AbstractAction cutAction;
	private AbstractAction copyAction;
	private AbstractAction pasteAction;
	private AbstractAction addColumnAction;
	private AbstractAction addRowAction;
	private AbstractAction removeColumnAction;
	private AbstractAction removeRowAction;
	
	private CSVParser parser;
	private CSVExporter exporter;
	
	private JFileChooser chooser;
	private JScrollPane view;
	private JTable table;
	private DefaultTableModel model;
	private int copyRow;
	private int copyColumn;
	private Object copyObject;
}