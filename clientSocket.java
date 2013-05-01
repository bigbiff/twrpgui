import java.net.*;
import java.util.Arrays;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.DataOutputStream;
import java.io.OutputStreamWriter;

public class clientSocket implements Runnable {
	private String host = "10.250.1.1";
	//private String host = "127.0.0.1";
	private int ctrlPort = 9000;
	private int dataPort = 9001;
	private String arg;
	private static String storage;
	private InetAddress address;
	private Socket connection;
	private volatile MutableObject cmd;
	private volatile MutableObject data;
	private String cdata;
	private StringBuffer instr;

	public clientSocket(MutableObject cmd, MutableObject data) {
		this.cmd = cmd;
		this.data = data;
	}

	private int getStringData() {
		int c;
		instr = new StringBuffer();
		try {
			connection = new Socket(host, dataPort);
		}
		catch (IOException e) {
			twrpGui.updateTWRPConsole("IOException: " + e);
			twrpGui.updateTWRPConsole("\nMake sure rndis is enabled!\n");
			return -1;
		}
		catch (Exception g) {
			twrpGui.updateTWRPConsole("Exception: " + g);
			twrpGui.updateTWRPConsole("\nMake sure rndis is enabled!\n");
			return -1;
		}
		try {
			BufferedOutputStream bos = new BufferedOutputStream(connection.getOutputStream());
			OutputStreamWriter osw = new OutputStreamWriter(bos, "US-ASCII");
			BufferedInputStream bis = new BufferedInputStream(connection.getInputStream());
			InputStreamReader isr = new InputStreamReader(bis, "US-ASCII");

			while ((c = isr.read()) > 0 ) {
				//System.out.println("c: " + Integer.toHexString(c));
				instr.append((char) c);
			}	
			System.out.println("here");
			bos.close();
			osw.close();
			bis.close();
			isr.close();
			connection.close();
			System.out.println(instr.toString());
			//twrpGui.updateTWRPConsole("\n" + instr.toString());
		}
		catch (IOException e) {
			twrpGui.updateTWRPConsole("IOException: " + e);
			return -1;
		}
		catch (Exception g) {
			StringBuilder st = new StringBuilder();
			for (StackTraceElement element: g.getStackTrace()) {
				st.append(element);
				st.append("\n");
			}
			twrpGui.updateTWRPConsole("Exception: " + g + "\n");
			twrpGui.updateTWRPConsole(st.toString());
			return -1;	
		}		
		return 0;
	}

	private int getBinData(String fn, double size) {
		double s = 0;
		double y = 0;
		int packetSize = 4096;
		int val, pval = 0, read = 0;
		double integral, bytesRead = 0;
		byte[] bytestream = new byte[4096];
		
		try {
			System.out.println("file: " + fn);
			FileOutputStream fos = new FileOutputStream(fn);
			DataOutputStream dos = new DataOutputStream(fos);
			//2000 is the number of units in the progressbar
			//y is a factor of the size and progressbar
			y = size / 2000;
			System.out.println("y: " + y);
			if (size < packetSize)
				packetSize = (int) size;
			
			while (s < size) {
				if (s + packetSize > size)
					s = s + (size - s);
				else
					s += packetSize;
				//System.out.println("s: " + s);
				//System.out.println("size: " + size);
				
				//4096 is max packet size
				if (y < 1) {
					integral = 2000;
					System.out.println("here");
				}
				else if (packetSize >= y)
					integral = 1;
				else 
					integral = (int) Math.floor(y / packetSize);
				
				pval += integral;
				System.out.println("pval: " + pval);
				twrpGui.twProgressUpdate(pval);
				
				try {
					connection = new Socket(host, dataPort);
				}
				catch (IOException e) {
					twrpGui.updateTWRPConsole("IOException: " + e);
					twrpGui.updateTWRPConsole("\nMake sure rndis is enabled!\n");
					return -1;
				}
				catch (Exception g) {
					twrpGui.updateTWRPConsole("Exception: " + g);
					twrpGui.updateTWRPConsole("\nMake sure rndis is enabled!\n");
					return -1;
				}
				try {
					InputStream is = new BufferedInputStream(connection.getInputStream());
					while (read < packetSize && (bytesRead = is.read(bytestream, read, bytestream.length - read)) != -1)
						read += bytesRead;
					for (int i = 0; i < packetSize; ++i) {
						val = bytestream[i] & 0xFF;
						dos.writeByte(val);
						//System.out.println("val " + i + ": " + Integer.toHexString(val));	
					}
					is.close();
					connection.close();
					read = 0;
				}
				catch (IOException e) {
					twrpGui.updateTWRPConsole("IOException: " + e);
					return -1;
				}
			}
			dos.close();
			fos.close();
			connection.close();
		}
		catch (IOException e) {
			return -1;
		}
		return 0;
	}

	
	private void sendCmd(String cmd) {
		try {
			connection = new Socket(host, ctrlPort);
			twrpGui.updateTWRPConsole("Connected to phone...\n");
		}
		catch (IOException e) {
			twrpGui.updateTWRPConsole("IOException: " + e);
			twrpGui.updateTWRPConsole("\nMake sure rndis is enabled!\n");
		}
		catch (Exception g) {
			twrpGui.updateTWRPConsole("Exception: " + g);
			twrpGui.updateTWRPConsole("\nMake sure rndis is enabled!\n");
		}
		try {	
			BufferedOutputStream bos = new BufferedOutputStream(connection.getOutputStream());
			OutputStreamWriter osw = new OutputStreamWriter(bos, "US-ASCII");
			BufferedInputStream bis = new BufferedInputStream(connection.getInputStream());
			InputStreamReader isr = new InputStreamReader(bis, "US-ASCII");
			osw.write(cmd);
			osw.flush();	
			bos.close();
			osw.close();
			bis.close();
			isr.close();
			connection.close();
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
	
	private void sendbackup(String argument) {
		sendCmd("send " + argument);
	}
	
	private void getstorage(String argument) {
		sendCmd("getstorage " + argument);
	}
	
	private void lsdir(String argument) {
		sendCmd("lsdir " + argument);
	}

	public void run() {
		while (true) {
			try {
				Thread.sleep(10);
			}
			catch (InterruptedException ie) {
				System.out.println("interrupted");
			}
			if ((cdata = cmd.getData()) != "") {
				String[] cmddata = cdata.split(" ");
				String cmdToSend, argument;
				cmdToSend = cmddata[0].trim();
				if (cmddata.length > 1)
					argument = cmddata[1].trim().toLowerCase();
				else
					argument = "";
				if ("send".equals(cmdToSend)) {
					System.out.println("cmd: " + cmdToSend);
					sendbackup(argument);
					cmd.setData("");
					while (data.getData() != "readysend") {
						try {
							Thread.sleep(10);
						}
						catch (InterruptedException ie) {
							System.out.println("interrupted");
						}
					}
					if (getBinData(cmd.getArg(), Double.parseDouble(data.getArg())) != 0) 
						return;
				}
				if ("lsdir".equals(cmdToSend)) {
					System.out.println("cmd: " + cmdToSend);
					lsdir(argument);
					cmd.setData("");
					while (data.getData() != "readylsdir") {
						try {
							Thread.sleep(10);
						}
						catch (InterruptedException ie) {
							System.out.println("interrupted");
						}
					}
					if (getStringData() != 0)
						return;
					String[] elements = instr.toString().split(" ");
					twrpGui.clearTWRPFiles();
					for (String element: elements) {
						twrpGui.updateTWRPFiles(element);
					}
				}
				if ("getstorage".equals(cmdToSend)) {
					System.out.println("cmd: " + cmdToSend);
					getstorage(argument);
					cmd.setData("");
					while (data.getData() != "readystorage") {
						try {
							Thread.sleep(10);
						}
						catch (InterruptedException ie) {
							System.out.println("interrupted");
						}
					}
					if (getStringData() != 0)
						return;
					String[] elements = instr.toString().split(" ");
					for (String element: elements) {
						twrpGui.updateStorageCombo(element);
					}
				}
				if ("disconnect".equals(cmdToSend)) {
					System.out.println("cmd: " + cmdToSend);
					twrpGui.clearTWStorageCombo();
					twrpGui.updateStorageCombo("Press connect button");
					twrpGui.clearTWRPFiles();
					break;
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
