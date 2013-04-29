import javax.swing.*;
import net.miginfocom.swing.*;

public class twProgress implements Runnable {
	private static JProgressBar progress;
	
	public twProgress(JProgressBar progress) {
		this.progress = progress;
	}
	
	public static void setProgress(int i) {
		progress.setValue(i);
	}
	
    public void run() {
    	/*
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
        */
    	
    }
}