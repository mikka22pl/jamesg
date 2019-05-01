package pl.ulv.jamesg;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class Client extends JFrame {
	
	private JTextField userText;
	private JTextArea chatWindow;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private String message = "";
	private String serverIP;
	private Socket connection;
	
	public Client(String host) {
		super("Client");
		setUpLayout(host);
	}
	
	public void startRunning() {
		try {
			connectToServer();
			setUpStreams();
			whileChatting();
		} catch (EOFException e) {
			showMessage("\n Client terminated the connection");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			closeConnection();
		}
	}
	
	private void connectToServer() throws IOException {
		showMessage("Attempting connection...\n");
		connection = new Socket(InetAddress.getByName(serverIP), 6789);
		showMessage("Connection Established! Connection to: " + connection.getInetAddress().getHostName());
	}
	
	private void closeConnection() {
		showMessage("\n Closing the connection");
		ableToType(false);
		try {
			output.close();
			input.close();
			connection.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	private void setUpStreams() throws IOException {
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input = new ObjectInputStream(connection.getInputStream());
		showMessage("\n The streams are now set up! \n");
	}
	
	private void whileChatting() throws IOException {
		ableToType(true);
		do {
			try {
				message = (String) input.readObject();
				showMessage("n" + message);
			} catch (ClassNotFoundException e) {
				showMessage("Unknown data received!");
			}
		} while (!message.equals("SERVER - END"));
	}
	
	private void showMessage(String message) {
		
	}
	
	private void setUpLayout(String host) {
		serverIP = host;
		userText = new JTextField();
		userText.setEditable(false);
		userText.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sendData(e.getActionCommand());
				userText.setText("");
			}
		});
		add(userText, BorderLayout.NORTH);
		chatWindow = new JTextArea();
		add(new JScrollPane(chatWindow));
		setSize(300, 150);
		setVisible(true);
	}
	
	private void sendData(String data) {
		try {
			output.writeObject("CLIENT - " + data);
			output.flush();
		} catch (IOException e) {
			chatWindow.append("\n Oops! Something went wrong!");
		}
	}
	
	private void ableToType(final boolean status) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				userText.setEditable(status);
			}
		});
	}
}
