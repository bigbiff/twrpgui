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

	private int getBinData(double size) {
		double s = 0;
		try {
			FileOutputStream fos = new FileOutputStream("/tmp/boot2.img");
			DataOutputStream dos = new DataOutputStream(fos);
			
			while (s < size) {
				System.out.println("s: " + s);
				System.out.println("size: " + size);
				if (s + 4096 > size)
					s = s + (size - s);
				else
					s += 4096;
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
					int bytesRead, val;
					byte[] bytestream = new byte[4096];
					InputStream is = connection.getInputStream();
					bytesRead = is.read(bytestream, 0, bytestream.length);
					for (int i = 0; i < bytestream.length; ++i) {
						val = bytestream[i] &0xFF;
						dos.writeByte(val);
						//System.out.println("val: " + Integer.toHexString(val));	
					}
					is.close();
					connection.close();
				}
				catch (IOException e) {
					twrpGui.updateTWRPConsole("IOException: " + e);
					return -1;
				}
			}
			dos.close();
			fos.close();
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
					if (getBinData(Double.parseDouble(data.getArg())) != 0) 
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
