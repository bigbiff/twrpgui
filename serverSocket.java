import java.net.*;
import java.util.Arrays;
import java.util.List;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.InputStreamReader;

public class serverSocket implements Runnable {
	private ServerSocket server;
	private Socket connection = null;
	private int port = 9000;
	private String message;
	private volatile MutableObject data;
	private String[] datacmds = {"readylsbackups", "readystorage", "readybackup"};

	public serverSocket(MutableObject data) {
		this.data = data;
	}

	public void run() {
		while (true) {
			try {
				Thread.sleep(10);
			}
			catch (InterruptedException ie) {
				System.out.println("interrupted");
			}
			try {
				List<String> cmdlist = Arrays.asList(datacmds);
				server = new ServerSocket(port);

				connection = server.accept();
				System.out.println("Connection received from " + connection.getInetAddress());
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
				BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				do {
					try {
						try {
							Thread.sleep(10);
						}
						catch (InterruptedException ie) {
							System.out.println("interrupted");
						}
						message = br.readLine();
						System.out.println("message: " + message);
						if ("readylsbackups".equals(message)) {
							data.setData("readylsbackups");
						}
						else if ("readystorage".equals(message)) {
							data.setData("readystorage");
						}
						else if ("readybackup".equals(message)) {
							data.setData("readybackup");
						}
						System.out.println("data: " + data.getData());
					}
					catch (IOException ie) {
						System.out.println(ie.getStackTrace());
					}
				} while (!(cmdlist.contains(message)));
				System.out.println("closing server");
				br.close();
				bw.close();
			}
			catch(IOException ie) {
				ie.printStackTrace();
			}
			finally {
				try {
					server.close();
				}
				catch(IOException ie) {
					ie.printStackTrace();
				}
			}
		}
	}
}
