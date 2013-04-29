import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.JList;
import javax.swing.JDialog;
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
    private static JProgressBar progress;
	private JDialog twProgressD;
	private static JTextArea textArea = new JTextArea(5, 60);
	private JButton getStorage = new JButton("Get Contents");
	private JButton connectButton = new JButton("Connect");
	private JButton parButton = new JButton("..");
	private JButton toButton = new JButton("->");
	private JButton fromButton = new JButton("<-");
	private JButton saveLogButton = new JButton("Save Log");
	private JButton twParButton = new JButton("..");
	private static DefaultListModel twrpListModel = new DefaultListModel();
	private DefaultListModel fileListModel = new DefaultListModel();
	private static DefaultComboBoxModel comboModel = new DefaultComboBoxModel();
	private static JComboBox storageComboBox = new JComboBox(comboModel);
	private static twProgress twp = new twProgress(progress);
	private File fileList = new File(System.getProperty("user.home"));
	private JScrollPane ctwrp = getTWListStrings(true);
	private JScrollPane ftwrp = getLocalFiles(fileList.listFiles(new TextFileFilter()), true);
	private JFileChooser saveLogFileChooser = new JFileChooser();
	private String twDirSelected = System.getProperty("user.home");
	private JList fileListVals, twListVals;
	private String parDir, origParDir, storagearg, twParDir, twFile, sendFile;
	private Boolean nofiles;
	static volatile MutableObject cmd = new MutableObject();
	static volatile MutableObject data = new MutableObject();
	
	public static void clearTWStorageCombo() {
		if (!SwingUtilities.isEventDispatchThread()) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					clearStorageCombo();
				}
			});
		}
	}
	
	public static void clearTWRPFiles() {
		if (!SwingUtilities.isEventDispatchThread()) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					clearTWListFiles();
				}
			});
		}
	}
	
	public static void updateTWRPFiles(final String text) {
		if (!SwingUtilities.isEventDispatchThread()) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					updateTWFiles(text);
				}
			});
		}
	}
	
	public static void twProgressUpdate(final int val) {
		if (!SwingUtilities.isEventDispatchThread()) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					updateProgress(val);
				}
			});
		}
	}
	
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
					updateCombo(text);
					System.out.println("text: " + text);
				}
			});
		}
	}
	
	static private void clearStorageCombo() {
		comboModel.removeAllElements();
	}
	
	static private void clearTWListFiles() {
		twrpListModel.removeAllElements();
	}
	
	static private void updateTWFiles(String text) {
		twrpListModel.addElement(text);
	}
	
	static private void updateCombo(String text) {
		comboModel.addElement(text);
	}
	
	static private void updateConsole(String text) {
		textArea.append(text);
	}
	
	static private void updateProgress(int val) {
		progress.setValue(val);
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
	
	private JScrollPane getTWListStrings(boolean vertical) {
		twListVals = new JList(twrpListModel);
		twListVals.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		if (!vertical) {
            twListVals.setLayoutOrientation(javax.swing.JList.HORIZONTAL_WRAP);
            twListVals.setVisibleRowCount(-1);
        } else {
            twListVals.setVisibleRowCount(9);
        }
        return new JScrollPane(twListVals);
	}
    
	public twrpGui() {
		serverSocket recoveryServer = new serverSocket(data);
		Thread server = new Thread(recoveryServer);
		server.start();
        progress = new JProgressBar(0, 2000);
        progress.setValue(0);
        progress.setStringPainted(true);
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
		top.add(storageComboBox, "growx");
		top.add(getStorage, "gapleft 10, growx");
		left.add(twParButton, "gaptop 10, gapleft 130, wrap");
		left.add(ctwrp, "gapleft 35, grow, push");
		center.add(connectButton, "gaptop 30, center, wrap, pushx");
		center.add(toButton, "center, wrap");
		center.add(fromButton, "center, wrap");
		bottom.add(textArea, "center, grow, push");
		bottom.add(saveLogButton, "gapleft 10");
		right.add(parButton, "center, gapright 10, wrap");
		right.add(ftwrp, "right, gapright 35, grow, push");
		toButton.addActionListener(new toListener());
		getStorage.addActionListener(new storageListener());
		connectButton.addActionListener(new connectListener());
		twParButton.addActionListener(new twParListener());
		parButton.addActionListener(new parListener());
		saveLogButton.addActionListener(new saveLogListener());
		twListVals.addMouseListener(new twrpFileListener()); 
		fileListVals.addMouseListener(new fileListener());
		twrpTab.addTab("Backup File Manager", tabFileMgr);
		ftwrp.setPreferredSize(new Dimension(200, 200));
		ctwrp.setPreferredSize(new Dimension(200, 200));
		comboModel.addElement("Press Connect button");
		f.pack();
		f.setVisible(true);
	}

	public void launchTWRP() {
		nofiles = false;
	}

	private class twrpFileListener extends MouseAdapter {
		public void mouseClicked(MouseEvent event) {
			if (event.getClickCount() == 1) {
				int index = twListVals.locationToIndex(event.getPoint());
				Object item = twrpListModel.getElementAt(index);
				twFile = item.toString();
				System.out.println("strValue: " + twFile);
				if (twFile != "" || twFile != null) {
					sendFile = twFile; 
				}
			}
			if (event.getClickCount() == 2) {
				int index = twListVals.locationToIndex(event.getPoint());
				Object item = twrpListModel.getElementAt(index);
				twFile = item.toString();
				System.out.println("strValue: " + twFile);
				if (twFile != "" || twFile != null) {
					cmd.setData("lsdir " + twFile);
					String tokens[] = twFile.split("/");
					int count = tokens.length;
					twParDir = "/";
					System.out.println("count: " + count);
					for (int i = 1; i < count - 1; ++i) {
						twParDir = twParDir + tokens[i];
						if (i < count - 2)
							twParDir += "/";
					}
				}
			}
		}
	}
	
	private class fileListener extends MouseAdapter {
		public void mouseClicked(MouseEvent event) {
			String strValue;
			File[] newAll;
			if (event.getClickCount() == 2) {
				int index = fileListVals.locationToIndex(event.getPoint());
				Object item = fileListModel.getElementAt(index);
				strValue = item.toString();
				if (strValue != "" || strValue != null) {
					nofiles = false;
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
	}
	
	private class toListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String fileToWrite = "";
			String tokens[] = sendFile.split("/");
			int count = tokens.length;
			for (int i = 1; i < count; ++i) {
				fileToWrite = tokens[i];
			}
			cmd.setData("send " + sendFile);
			cmd.setArg(fileToWrite);
			Thread progressT = new Thread(twp);
			progressT.start();
			twProgressD = new JDialog(f, "File Progress", Dialog.ModalityType.DOCUMENT_MODAL);
			twProgressD.setLayout(new  MigLayout("", "[grow]", "[grow]"));
			twProgressD.setSize(100, 100);
			twProgressD.add(progress, "grow, push");
			twProgressD.setVisible(true);
		}
	}
	
	private class storageListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (connectButton.getText() == "Connect") {
				JOptionPane.showMessageDialog(f, "Press Connect button first");
				return;
			}
			String selected = comboModel.getSelectedItem().toString();
			cmd.setData("lsdir " + selected);
		}
	}
	
	private class twParListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (twParDir == "") {
				System.out.println("twFile: " + twFile);
				String tokens[] = twFile.split("/");
				int count = tokens.length;
				twParDir = "/";
				for (int i = 1; i < count - 1; ++i) {
					twParDir = twParDir + tokens[i];
					System.out.println("i: " + i);
					System.out.println("twParDir: " + twParDir);
					if (i < count - 2)
						twParDir += "/";
				}
			}
			System.out.println("twParDir: " + twParDir);
			cmd.setData("lsdir " + twParDir);
			twFile = twParDir;
			twParDir = "";
		}
	}
	
	private class connectListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (connectButton.getText() == "Disconnect") {
				cmd.setData("disconnect");
				connectButton.setText("Connect");
				return;
			}
			connectButton.setText("Disconnect");
			clientSocket recovery = new clientSocket(cmd, data);
			Thread client = new Thread(recovery);
			client.start();
			comboModel.removeAllElements();
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