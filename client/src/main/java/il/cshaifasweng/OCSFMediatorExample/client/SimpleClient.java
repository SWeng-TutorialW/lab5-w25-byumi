package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.GameOver;
import il.cshaifasweng.OCSFMediatorExample.entities.BoardStatus;
import org.greenrobot.eventbus.EventBus;

import il.cshaifasweng.OCSFMediatorExample.client.ocsf.AbstractClient;

public class SimpleClient extends AbstractClient {

	private static SimpleClient client = null;

	private SimpleClient(String host, int port) {
		super(host, port);
	}

	@Override
	protected void handleMessageFromServer(Object msg) {
		if (msg instanceof BoardStatus) {
			EventBus.getDefault().post(msg);
		} else if (msg instanceof GameOver) {
			EventBus.getDefault().post(msg);
		} else {
			String message = msg.toString();
			if (message.equals("client added successfully")) {
				EventBus.getDefault().post(new Signal());
			}
			System.out.println(message);
		}
	}

	public static SimpleClient getClient() {
		if (client == null) {
			client = new SimpleClient("172.20.10.8", 3000);
		}
		return client;
	}

}
