package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;

public class PrimaryController {

	@FXML
	private TextField ipField;

	@FXML
	private TextField portField;

	@FXML
	private Button playBtn;

	@FXML
	private Label statusLabel;

	@FXML
	void initialize() {
		EventBus.getDefault().register(this);
		playBtn.setDisable(true); // Disable the Play button initially
		statusLabel.setText(""); // Clear the status label

		// Add listeners to IP and Port fields
		ChangeListener<String> inputChangeListener = (observable, oldValue, newValue) -> validateInputs();
		ipField.textProperty().addListener(inputChangeListener);
		portField.textProperty().addListener(inputChangeListener);
	}

	private void validateInputs() {
		String ip = ipField.getText().trim();
		String port = portField.getText().trim();
		boolean validPort = port.matches("\\d+"); // Port must be numeric
		playBtn.setDisable(ip.isEmpty() || port.isEmpty() || !validPort); // Enable Play only if both inputs are valid
	}

	@FXML
	void startGame(ActionEvent event) {
		String ip = ipField.getText().trim();
		String portText = portField.getText().trim();

		try {
			int port = Integer.parseInt(portText);
			SimpleClient.initializeClient(ip, port);  // Initialize the client with provided IP and port
			SimpleClient.getClient().openConnection();
			playBtn.setDisable(true);
			playBtn.setText("Connecting...");
			statusLabel.setText("Connecting to server...");
			System.out.println(ip);
			System.out.println(port);
		} catch (NumberFormatException e) {
			statusLabel.setText("Invalid port number!");
		} catch (IOException e) {
			statusLabel.setText("Failed to connect! Check IP and Port.");
			e.printStackTrace();
		}
		try {
			SimpleClient.getClient().sendToServer("add player");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Subscribe
	public void onConnected(Signal event) {
		Platform.runLater(() -> {
			playBtn.setText("Searching for opponent...");
			statusLabel.setText("Connected!");
		});
	}
}
