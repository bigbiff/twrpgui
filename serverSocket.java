import java.net.*;
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
	
	public serverSocket(MutableObject data) {
		this.data = data;
	}
	
	public void run() {
		try {
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
					System.out.println("here");
					message = br.readLine();
					System.out.println("message: " + message);
					if ("readydata".equals(message)) {
						data.setData("readylsbackups");
					}
					else if ("readystorage".equals(message)) {
						data.setData("readystorage");
					}
					System.out.println("data: " + data.getData());
				}
				catch (IOException ie) {
					System.out.println(ie.getStackTrace());
				}
			} while (!message.equals("readystorage"));
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
