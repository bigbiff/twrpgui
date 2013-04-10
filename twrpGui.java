import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.JList;
import javax.swing.border.EmptyBorder;
import javax.swing.border.Border;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.*;
import net.miginfocom.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

public class twrpGui {
	private JFrame f = new JFrame("TWRP Manager");
	private JTabbedPane twrpTab = new JTabbedPane();
	private JPanel main = new JPanel(new MigLayout());
	private JPanel tabFileMgr = new JPanel(new MigLayout());
	private JPanel left = new JPanel(new MigLayout("insets 0"));
	private JPanel center = new JPanel(new MigLayout());
	private JPanel right = new JPanel(new MigLayout());
	private JPanel bottom = new JPanel(new MigLayout());
	private JPanel top = new JPanel(new MigLayout("insets 0"));
	private static JTextArea textArea = new JTextArea(5, 60);
	private JButton connectButton = new JButton("Connect");
	private JButton parButton = new JButton("Parent Directory");
	private JButton toButton = new JButton("->");
	private JButton fromButton = new JButton("<-");
	private JButton saveLogButton = new JButton("Save Log");
	private DefaultListModel twrpListModel = new DefaultListModel();
	private DefaultListModel fileListModel = new DefaultListModel();
	private static DefaultComboBoxModel comboModel = new DefaultComboBoxModel();
	private static JComboBox storageComboBox = new JComboBox(comboModel);
	private File fileList = new File(System.getProperty("user.home"));
	private JScrollPane ctwrp = getTWListStrings(twrpFiles(), true);
	private JScrollPane ftwrp = getLocalFiles(fileList.listFiles(new TextFileFilter()), true);
	private JFileChooser saveLogFileChooser = new JFileChooser();
	private String twDirSelected = System.getProperty("user.home");
	private JList fileListVals;
	private String parDir, origParDir;
	private Boolean nofiles;
	static volatile MutableObject cmd = new MutableObject();
	static volatile MutableObject data = new MutableObject();
	
	public static void updateTWRPConsole(final String text) {
		if (!SwingUtilities.isEventDispatchThread()) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					updateConsole(text);
				}
			});
		}
	}

	public static void updateStorageCombo(final String text) {
		if (!SwingUtilities.isEventDispatchThread()) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					updateStorageCombo(text);
				}
			});
		}
	}

	static private void updateCombo(String text) {
		comboModel.addElement(text);
	}
	
	static private void updateConsole(String text) {
		textArea.append(text);
	}
	
	private String[] twrpFiles() {
		String[] twFiles;
		twFiles = new String[10];
		twFiles[0] = "test10000000000000";
		twFiles[1] = "test";
		twFiles[2] = "test";
		twFiles[3] = "test";
		twFiles[4] = "test";
		twFiles[5] = "test";
		twFiles[6] = "test";
		twFiles[7] = "test";
		twFiles[8] = "test";
		twFiles[9] = "test";
		return twFiles;
	}
	
	private JScrollPane getLocalFiles(File[] all, boolean vertical) {
		Arrays.sort(all);
		fileListVals = new JList(fileListModel);
		fileListVals.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        for (int i = 0; i < all.length - 1; i++)
        	fileListModel.add(i, all[i].getAbsoluteFile());
        fileListVals.setCellRenderer(new FileRenderer(!vertical));
        if (!vertical) {
            fileListVals.setLayoutOrientation(javax.swing.JList.HORIZONTAL_WRAP);
            fileListVals.setVisibleRowCount(-1);
        } else {
            fileListVals.setVisibleRowCount(9);
        }
        return new JScrollPane(fileListVals);
    }
	
	private JScrollPane getTWListStrings(String[] all, boolean vertical) {
		JList list = new JList(all);
		if (!vertical) {
            list.setLayoutOrientation(javax.swing.JList.HORIZONTAL_WRAP);
            list.setVisibleRowCount(-1);
        } else {
            list.setVisibleRowCount(9);
        }
        return new JScrollPane(list);
	}
    
	public twrpGui() {
		clientSocket recovery = new clientSocket(cmd, data);
		serverSocket recoveryServer = new serverSocket(data);
		Thread client = new Thread(recovery);
		Thread server = new Thread(recoveryServer);
		client.start();
		server.start();
		/*
		int ret = recovery.testConnect();
		if (ret == -1)
			JOptionPane.showMessageDialog(f, "Please enable RNDIS!");
		*/
		fileListVals.addListSelectionListener(new ListSelectionListener() {
    		public void valueChanged(ListSelectionEvent event) {
    			String strValue;
    			File fileListNew;
    			File[] newAll;
    			if (event.getSource() == fileListVals && !event.getValueIsAdjusting()) {
    				JList list = (JList) event.getSource();
    				//int selection = list.getSelectedIndex();
    				if (list.getSelectedValue() != null) {
    					nofiles = false;
    					strValue = list.getSelectedValue().toString();
    					parDir = strValue;
    					twDirSelected = parDir;
    					origParDir = parDir;
    					System.out.println("twrpGUI parDir:" + parDir);
    				}
    				else {
    					nofiles = true;
    					if (twDirSelected == null) {
    						parDir = "/";
    					}
    					origParDir = parDir;
    					twDirSelected = parDir;
    					System.out.println("gui here parDir: " + parDir);
    					return;
    				}
    				newAll = new File(strValue).listFiles(new TextFileFilter());
    				Arrays.sort(newAll);
    				if (newAll.length == 0) {
    					parDir = common.getParentDir(parDir);
    					twDirSelected = parDir;
    					origParDir = parDir;
    					System.out.println("here3 oarDur: " + parDir);
    				}
      				else {			
	    				fileListModel.removeAllElements();
	    				for (int i = 0; i < newAll.length; i++) {
	    					fileListModel.addElement(newAll[i]);
	    	 				ftwrp.revalidate();
	        				ftwrp.repaint();
	    				}
    				}
    			}
    		}
        });
	
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setLayout(new MigLayout("", "[grow]", "[grow]"));
		f.getContentPane().add(main, "grow, push");
		main.add(twrpTab, "grow, push");
		main.add(tabFileMgr, "grow, push");
		tabFileMgr.add(top, "north, gaptop 30, gapleft 35, grow, push");
		tabFileMgr.add(left, "left, grow, push");
		tabFileMgr.add(center, "center, grow, push");
		tabFileMgr.add(right, "right, grow, push");
		tabFileMgr.add(bottom, "south, grow, push");
		textArea.setEditable(false);
		top.add(storageComboBox);
		left.add(ctwrp, "gaptop 30, gapleft 35, grow, push");
		center.add(connectButton, "gaptop 30, center, wrap, pushx");
		center.add(toButton, "center, wrap");
		center.add(fromButton, "center, wrap");
		bottom.add(textArea, "center, grow, push");
		bottom.add(saveLogButton, "gapleft 10");
		right.add(parButton, "center, growy, wrap");
		right.add(ftwrp, "right, gapright 35, grow, push");
		connectButton.addActionListener(new connectListener());
		parButton.addActionListener(new parListener());
		saveLogButton.addActionListener(new saveLogListener());
		twrpTab.addTab("Backup File Manager", tabFileMgr);
		ftwrp.setPreferredSize(new Dimension(200, 200));
		ctwrp.setPreferredSize(new Dimension(200, 200));
		f.pack();
		f.setVisible(true);
	}

	public void launchTWRP() {
		nofiles = false;
		twrpListModel.addElement("test");
		twrpListModel.addElement("test2");
		twrpListModel.addElement("test3");
		twrpListModel.addElement("test4");
	}
	
	private class connectListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			cmd.setData("getstorage");
		}
	}
	
	private class saveLogListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {	
			String text;
			textArea.append("Saving log...\n");
			int ret = saveLogFileChooser.showOpenDialog(f);
			if (ret == JFileChooser.APPROVE_OPTION) {
				File file = saveLogFileChooser.getSelectedFile().getAbsoluteFile();
				try {
					PrintWriter out = new PrintWriter(file);
					text = textArea.getText();
					out.print(text);
					out.close();
				}
				catch (FileNotFoundException fe) {
					System.out.println("Cannot find file!");
					fe.printStackTrace();
				}
			}
		}
	}
	
	private class parListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			File parFiles[];
			if (parDir == null) {
				parDir =  common.getParentDir(twDirSelected);
				origParDir = parDir;
				twDirSelected = parDir;
			}
			else {
				if (parDir != twDirSelected) {
					System.out.println("twDirSelected: " + twDirSelected);
					System.out.println("parDir: " + parDir);
					parDir =  common.getParentDir(twDirSelected);
					twDirSelected = parDir;
					if (parDir == null)
						parDir = "/";
					System.out.println("here parDir: " + parDir);
				}
				else {
					if (!nofiles) {
						System.out.println("!nofiles");
						parDir = common.getParentDir(twDirSelected);
					}
					else {
						System.out.println("files");
						parDir = common.getParentDir(origParDir);
						origParDir = parDir;
					}
					origParDir = parDir;
					twDirSelected = parDir;
					System.out.println("here2 parDir: " + parDir);
				}
			}
			if (parDir != null) {
				System.out.println("redoing");
				parFiles = new File(parDir).listFiles(new TextFileFilter());
				Arrays.sort(parFiles);
				if (parFiles.length == 0) {
					parDir =  common.getParentDir(twDirSelected);
					System.out.println("parListener");
					twDirSelected = parDir;
					origParDir = parDir;
					return;
				}
				else {
    				fileListModel.removeAllElements();
    				for (int i = 0; i < parFiles.length; i++) {
    					System.out.println("parDir: " + parDir);
    					fileListModel.addElement(parFiles[i]);
    	 				ftwrp.revalidate();
        				ftwrp.repaint();
    				}
				}
			}
		}
	}
	
	public static void main(String args[]) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				twrpGui gui = new twrpGui();
				gui.launchTWRP();
			}
		});	
	}
}

class TextFileFilter implements FileFilter {

    public boolean accept(File file) {
    	Boolean result = false; 
        if (file.isDirectory())
        	result = true;
      	return result;
    }
}

class FileRenderer extends DefaultListCellRenderer {

    private boolean pad;
    private Border padBorder = new EmptyBorder(3,3,3,3);

    FileRenderer(boolean pad) {
        this.pad = pad;
    }

    @Override
    public Component getListCellRendererComponent(
        JList list,
        Object value,
        int index,
        boolean isSelected,
        boolean cellHasFocus) {

        Component c = super.getListCellRendererComponent(
            list,value,index,isSelected,cellHasFocus);
        JLabel l = (JLabel)c;
        File f = (File)value;
        l.setText(f.getName());
        l.setIcon(FileSystemView.getFileSystemView().getSystemIcon(f));
        if (pad) {
            l.setBorder(padBorder);
        }

        return l;
    }
}