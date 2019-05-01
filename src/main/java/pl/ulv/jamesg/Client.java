package pl.ulv.jamesg;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Client extends JFrame {
	
	private JTextField userText;
	private JTextArea chatWindow;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private String message = "";
	private String ServerIP;
	private Socket connection;
	
	public Client() {
		
	}
}
