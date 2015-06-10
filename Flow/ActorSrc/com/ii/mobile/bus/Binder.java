package com.ii.mobile.bus;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import com.ii.mobile.util.L;

public class Binder {

	Messenger messageService = null;
	boolean messengerIsBound = false;
	Binder binder = null;
	private final Messenger incomingMessenger;
	private final Context context;
	private final Class<?> serviceClass;

	public Binder(Context context, Messenger incomingMessenger, Class<?> serviceClass) {
		this.context = context;
		this.incomingMessenger = incomingMessenger;
		this.serviceClass = serviceClass;
		L.out("created binder");
		doBindService();
	}

	private final ServiceConnection serviceConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			messageService = new Messenger(service);
			try {
				L.out("service connected");
				Message msg = Message.obtain(null, BindService.MESSAGE_REGISTER_CLIENT);
				msg.replyTo = incomingMessenger;
				messageService.send(msg);
				// sendMessageToService("Yahoo I am connected");
			} catch (Exception e) {
				// In case the service has crashed
			}
		}

		public void onServiceDisconnected(ComponentName className) {
			// This is called when the connection with the service has been
			// unexpectedly disconnected - process crashed.
			L.out("service onServiceDisconnected");
			messageService = null;
		}
	};

	protected void sendMessageToService(String string) {
		L.out("sendMessage: " + string);
		if (messengerIsBound) {
			if (messageService != null) {
				try {
					Message message = Message.obtain();
					Bundle bundle = new Bundle();
					bundle.putString("string", string);
					message.setData(bundle);
					message.replyTo = incomingMessenger;
					messageService.send(message);
				} catch (RemoteException e) {
				}
			}
		}
	}

	void doBindService() {
		L.out("is doBindService");
		context.bindService(new Intent(context, serviceClass), serviceConnection, Context.BIND_AUTO_CREATE);
		messengerIsBound = true;
	}

	void doUnbindService() {
		L.out("doUnbindService");
		if (messengerIsBound) {
			if (messageService != null) {
				try {
					Message msg = Message.obtain(null, BindService.MESSAGE_UNREGISTER_CLIENT);
					msg.replyTo = incomingMessenger;
					messageService.send(msg);
				} catch (RemoteException e) {
					// There is nothing to do if the service has crashed.
				}
			}
			context.unbindService(serviceConnection);
			messengerIsBound = false;
		}
	}

	public void onDestroy() {
		L.out("onDestroy");
		doUnbindService();
	}

}
