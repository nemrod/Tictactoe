package tictactoe;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ConnectionOutputHandler {
	@SuppressWarnings("unused")
    private Socket socket;
	private DataOutputStream output;

	public ConnectionOutputHandler(Socket socket) {
		this.socket = socket;
		
		try {
			this.output = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void close() {
		System.out.println("ConnectionOutputHandler.close()");
		try {
			this.output.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void send(String message) {
		try {
			this.output.writeBytes(message + "\r\n");
			System.out.println("Output: " + message);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}