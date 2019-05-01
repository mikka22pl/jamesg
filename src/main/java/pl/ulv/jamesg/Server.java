package pl.ulv.jamesg;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

// https://github.com/buckyroberts/Source-Code-from-Tutorials/tree/master/Java_Intermediate
public class Server extends JFrame {

	private JTextField userText;
	private JTextArea chatWindow;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private ServerSocket server;
	private Socket connection;
	
	public Server() {
		super("Jamesg - simple messenger");
		setUpLayout();
	}
	
	public void startRunning() {
		try {
			//6789 is a dummy port for testing, this can be changed.
			// The 100 is the maximum people waiting to connect.
			server = new ServerSocket(6789, 100);
			while(true) {
				try {
					// try to connect
					waitForConnection();
					setUpStreams();
					whileChatting();
				} catch (EOFException ex) {
					showMessage("\n Server ended the connection!");
				} finally {
					closeConnection();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void waitForConnection() throws IOException {
		showMessage(" Waiting for someone to connect...\n");
		connection = server.accept();
		showMessage(" Now connected to " + connection.getInetAddress().getHostName());
	}
	
	// set up streams to send and receive data
	private void setUpStreams() throws IOException {
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		
		input = new ObjectInputStream(connection.getInputStream());
		showMessage("\n Streams are now setup \n");
	}
	
	private void whileChatting() throws IOException {
		String message = " You are now connected! ";
		sendMessage(message);
		
		ableToType(true);
		do {
			try {
				message = (String) input.readObject();
				showMessage("\n" + message);
			} catch (ClassNotFoundException e) {
				showMessage("The user has sent an unknown object!");
			}
		} while (!message.equals("CLIENT - END"));
	}
	
	private void ableToType(final boolean status) {
		SwingUtilities.invokeLater(
				new Runnable() {
					@Override
					public void run() {
						userText.setEditable(status);
					}
				});
	}
	
	private void setUpLayout() {
		userText = new JTextField();
		userText.setEditable(false);
		userText.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sendMessage(e.getActionCommand());
				userText.setText("");
			}
		});
		add(userText, BorderLayout.NORTH);
		chatWindow = new JTextArea();
		add(new JScrollPane(chatWindow));
		setSize(300, 150);
		setVisible(true);
	}
	
	// Send a mesage to the client
	private void sendMessage(String message) {
		try {
			output.writeObject("SERVER - " + message);
			output.flush();
			showMessage("\nSERVER -" + message);
		} catch (IOException e) {
			chatWindow.append("\n ERROR: CANNOT SEND MESSAGE, PLEASE RETRY");
		}
	}

	// update ChatWindow
	private void showMessage(final String text) {
		SwingUtilities.invokeLater(
				new Runnable() {
					@Override
					public void run() {
						chatWindow.append(text);
					}
				}
		);
	}
	
	private void closeConnection() {
		showMessage("\n Closing Connections...\n");
		ableToType(false);
		try {
			output.close();
			input.close();
			connection.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
