package com.ii.mobile.bus;

import java.util.ArrayList;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import com.ii.mobile.util.L;

public class BindService extends Service {

	ArrayList<Messenger> messengerClients = new ArrayList<Messenger>();

	public static final int MESSAGE_REGISTER_CLIENT = 1;
	public static final int MESSAGE_UNREGISTER_CLIENT = 2;

	final Messenger incomingMessenger = new Messenger(new IncomingHandler());

	@Override
	public void onCreate() {
		super.onCreate();
		L.out("Created: " + this.getClass());
	}

	@Override
	public IBinder onBind(Intent intent) {
		return incomingMessenger.getBinder();
	}

	class IncomingHandler extends Handler {

		@Override
		public void handleMessage(Message message) {
			Bundle bundle = message.getData();
			// L.out("Message message: " + message.what);
			switch (message.what) {
			case MESSAGE_REGISTER_CLIENT:
				messengerClients.add(message.replyTo);
				L.out("add: " + messengerClients.size());
				sideEffectRegisterClient();
				break;
			case MESSAGE_UNREGISTER_CLIENT:
				messengerClients.remove(message.replyTo);
				L.out("remove: " + messengerClients.size());
				break;

			default:
				L.out("Service what: " + bundle.getString("string"));
			}
		}
	}

	protected void sideEffectRegisterClient() {
	}

	protected int sendMessage(Bundle bundle) {
		// L.out("clients: " + messengerClients.size());
		// if (messengerClients.size() == 0)
		// L.out("clients: " + messengerClients.size());
		for (int i = messengerClients.size() - 1; i >= 0; i--) {
			try {
				Message message = Message.obtain();
				message.setData(bundle);
				messengerClients.get(i).send(message);

			} catch (RemoteException e) {
				messengerClients.remove(i);
			}
		}
		return messengerClients.size();
	}

	@Override
	public void onDestroy() {
		L.out("destroy service");
		super.onDestroy();
	}
}
