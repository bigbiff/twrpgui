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
    	
    	
    }
}
