import java.net.*;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class clientSocket implements Runnable {
	private String host = "10.250.1.1";
	private int port = 9000;
	private String arg;
	private static String storage;
	private InetAddress address;
	private Socket connection;
	private volatile MutableObject cmd;
	private String data;
	
	public clientSocket(MutableObject cmd) {
		this.cmd = cmd;
	}
	
	public static void setStorage(final String s) {
		storage = s;
		System.out.println("Setting storage to: " + storage);
	}
	
	private void lsbackups(String argument) {
		int c;
		StringBuffer instr = new StringBuffer();
		try {
			//InetAddress address = InetAddress.getByName(host);
			connection = new Socket(host, port);
			twrpGui.updateTWRPConsole("Connected to phone...");
		}
		catch (IOException e) {
			twrpGui.updateTWRPConsole("IOException: " + e);
			twrpGui.updateTWRPConsole("Make sure rndis is enabled!\n");
		}
		catch (Exception g) {
			twrpGui.updateTWRPConsole("Exception: " + g);
			twrpGui.updateTWRPConsole("Make sure rndis is enabled!\n");
		}
		try {
			
			BufferedOutputStream bos = new BufferedOutputStream(connection.getOutputStream());
			OutputStreamWriter osw = new OutputStreamWriter(bos, "US-ASCII");
			BufferedInputStream bis = new BufferedInputStream(connection.getInputStream());
			InputStreamReader isr = new InputStreamReader(bis, "US-ASCII");
			System.out.println("storage: " + argument);
			osw.write("lsbackups " + argument);
			osw.flush();
			while ((c = isr.read()) > 0) {
				instr.append((char) c);
			}
			System.out.println(instr);	
			bos.close();
			osw.close();
			bis.close();
			isr.close();
			twrpGui.updateTWRPConsole(instr.toString());
		}
		catch (IOException e) {
			twrpGui.updateTWRPConsole("IOException: " + e);
		}
		catch (Exception g) {
			StringBuilder st = new StringBuilder();
			for (StackTraceElement element: g.getStackTrace()) {
				st.append(element);
				st.append("\n");
			}
			twrpGui.updateTWRPConsole("Exception: " + g + "\n");
			twrpGui.updateTWRPConsole(st.toString());
		}		
	}

	public void run() {
		while (true) {
			if ((data = cmd.getData()) != "") {
				String[] cmddata = data.split(" ");
				String cmdToSend, argument;
				cmdToSend = cmddata[0].trim();
				argument = cmddata[1].trim().toLowerCase();
				System.out.println(cmdToSend);
				if ("lsbackups".equals(cmdToSend)) {
					cmd.setData("");
					lsbackups(argument);
				}
				try {
					Thread.sleep(10);
				}
				catch (InterruptedException ie) {
					System.out.println("interrupted");
				}
			}
 		}
	}
}