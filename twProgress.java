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
import java.beans.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

public class twProgress implements Runnable {
	private JFrame f = new JFrame("Progress");
	private JPanel main = new JPanel(new MigLayout());
	private JProgressBar progress;
	
	public twProgress() {
		progress = new JProgressBar(0, 2000);
		progress.setValue(0);
		progress.setStringPainted(true);
		f.setLayout(new MigLayout("", "[grow]", "[grow]"));
		f.getContentPane().add(main, "grow, push");
		main.add(progress);
		f.pack();
		f.setVisible(true);
	}
	
	public void run() {
		for (int i = 0; i <= 2000; ++i) {
			progress.setValue(i);
			try {
				Thread.sleep(100);
			}
			catch (InterruptedException ie) {
				System.out.println("interrupted");
			}
			i += 10;
		}
	}
}
