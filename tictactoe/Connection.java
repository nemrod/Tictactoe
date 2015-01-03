package tictactoe;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class Connection implements PropertyChangeListener {
	private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	private String pcsValue;
	
	private String hostname;
	private int port;
	
	private Socket clientSocket;
	private ServerSocket serverSocket;
	
	private ConnectionInputHandler inputHandler;
	private ConnectionOutputHandler outputHandler;
	private Thread inputHandlerThread;
	
	public Connection(boolean isServer, String hostname, int port) {
		this.hostname = hostname;
		this.port = port;

		try {
		    if(isServer) {
		        this.serverSocket = new ServerSocket(this.port);
		        this.clientSocket = this.serverSocket.accept();
		    } else {
		        this.clientSocket = new Socket(this.hostname, this.port);
		    }
            this.inputHandler = new ConnectionInputHandler(this.clientSocket);
            this.outputHandler = new ConnectionOutputHandler(this.clientSocket);
            this.inputHandler.addPropertyChangeListener(this);
            this.inputHandlerThread = new Thread(this.inputHandler);
            this.inputHandlerThread.start();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

    public void disconnect() {
        try {   
            this.inputHandler.close();
            this.outputHandler.close();
            this.inputHandlerThread = null;
            this.clientSocket.close();
            this.serverSocket.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public void send(String message) {
        outputHandler.send(message);
    }
	
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		this.pcs.addPropertyChangeListener(listener);
	}

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.removePropertyChangeListener(listener);
    }
    
    public String getPcsValue() {
    	return this.pcsValue;
    }
    
    public void setPcsValue(String newPcsValue) {
    	String oldPcsValue = this.pcsValue;
    	this.pcsValue = newPcsValue;
    	this.pcs.firePropertyChange("value", oldPcsValue, newPcsValue);
    }
    
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		Object obj = evt.getSource();
		String message = (String) evt.getNewValue();
		
		if(obj == this.inputHandler) {
			if(message != null) {
				this.setPcsValue(message);
			}
		}
	}
}