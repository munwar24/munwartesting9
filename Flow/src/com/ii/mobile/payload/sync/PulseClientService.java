package com.ii.mobile.payload.sync;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import org.w3c.dom.Document;

import com.ii.mobile.flowing.UpdateController;
import com.ii.mobile.payload.Compress;
import com.ii.mobile.payload.Dictionary;
import com.ii.mobile.payload.PayloadWrapper;
import com.ii.mobile.payload.PlainString;
import com.ii.mobile.payload.StatusWrapper;
import com.ii.mobile.soap.DOMElementWriter;
import com.ii.mobile.soap.gson.ValidateUser;
import com.ii.mobile.util.L;
import com.ii.mobile.util.M;
import com.ii.mobile.util.PrettyPrint;

public enum PulseClientService implements Runnable {
	INSTANCE;

	private static final int CONNECTION_TIMEOUT = 10000;
	private static final int SOCKET_TIMEOUT = 10000;
	private final int SERVER_PORT = 8888;
	// syncpulse.cloudapp.net
	private final String SERVER_HOSTNAME = "137.117.8.243";
	private int threadCount = 0;

	public Socket currentSocket = null;
	public String payload = null;
	private static PulseClientWatcher pulseClientWatcher = null;

	// private PayloadWrapperCallback payloadWrapperCallback = null;
	// public StatusWrapper statusWrapper = null;
	// public ValidateUser validateUser = null;

	public void setPayload(String payload) {
		L.out("ignoring payload!");
		if (true)
			return;
		if (pulseClientWatcher == null) {
			pulseClientWatcher = new PulseClientWatcher(this);
		}
		L.out("set payload: " + payload);
		this.payload = payload;
		pulseClientWatcher.setPayLoad(payload);
	}

	@Override
	public void run() {

		StatusWrapper statusWrapper = UpdateController.INSTANCE.statusWrapper;
		ValidateUser validateUser = UpdateController.INSTANCE.validateUser;
		Thread.currentThread().setName("PulseClientService - " + threadCount);
		threadCount += 1;
		// String oldPayload = payload;
		L.out("Thread started: " + Thread.currentThread().getName());
		if (payload == null) {
			if (validateUser == null || statusWrapper == null) {
				L.out("ERROR: Employee has not logged in with ValidateUser: " + validateUser + "\n"
						+ statusWrapper);

				return;
			}
		}
		execute(payload);
		// L.out("Thread finished: " + Thread.currentThread().getName());
		// if (!payload.equals(oldPayload)) {
		// currentThread = new Thread(this);
		// currentThread.start();
		// } else {
		// PayloadHelper.getRole(PayloadHelper.createNormalTest());
		// }
		// // run();
		// L.out("really finished thread");
	}

	public void validateUser(String employeeName, String employeePIN) {
		PayloadWrapper payloadWrapper = new PayloadWrapper();
		StatusWrapper statusWrapper = UpdateController.INSTANCE.statusWrapper;
		statusWrapper.currentStatus.employeeName = employeeName;
		statusWrapper.currentStatus.employeePIN = employeePIN;
		payloadWrapper.addPayload(statusWrapper, false);
		payloadWrapper.addPayload(new ValidateUser(), false);
		setPayload(payloadWrapper.getNewJson());
	}

	// private String getDefaultPayload() {
	//
	// PayloadWrapper payloadWrapper = new PayloadWrapper();
	// payloadWrapper.addPayload(getStatusWrapper(), false);
	// setPayload(payloadWrapper.getNewJson());
	// // setPayload(payloadWrapper.getNewJson());
	// return payloadWrapper.getNewJson();
	// }

	private byte[] readBytes(DataInputStream dataInputStream) throws Exception {
		int length = dataInputStream.readInt();
		// L.out("length: " + length);
		byte[] data = new byte[length];
		if (length > 0) {
			dataInputStream.readFully(data);
		}
		return data;
	}

	private void writeBytes(DataOutputStream dataOutputStream, byte[] data) throws Exception {
		L.out("length: " + data.length);
		dataOutputStream.writeInt(data.length);
		dataOutputStream.write(data, 0, data.length);
	}

	private synchronized String execute(String payload) {
		if (payload == null) {
			L.out("Waiting payload is null");
			L.sleep(2000);
		}
		try {
			String json = Compress.INSTANCE.compressString(payload, false, false);
			M.out("\n\nStep 5a Write bytes to Server: " + json.length());
			SocketAddress sockaddr = new InetSocketAddress(SERVER_HOSTNAME, SERVER_PORT);
			currentSocket = new Socket();
			currentSocket.connect(sockaddr, CONNECTION_TIMEOUT);
			// currentSocket = new Socket(SERVER_HOSTNAME, SERVER_PORT);
			// L.out("created socket: " + currentSocket);
			L.out("Connected to server " + SERVER_HOSTNAME + ":" + SERVER_PORT);
			// if (currentSocket == null) {
			// L.out("Unable to open socket: " + currentSocket);
			// return "failed socket connection";
			// }
			currentSocket.setSoTimeout(SOCKET_TIMEOUT);
			DataOutputStream dataOutputStream = new DataOutputStream(currentSocket.getOutputStream());
			DataInputStream dataInputStream = new DataInputStream(currentSocket.getInputStream());
			String reply = null;
			String temp = UpdateController.BLACK + "Write: " + UpdateController.END + payload;
			UpdateController.INSTANCE.callback(new PlainString(temp), UpdateController.PLAIN_STRING);
			writeBytes(dataOutputStream, json.getBytes("UTF-8"));
			UpdateController.INSTANCE.incrementOutputCounter(json.length());
			while (true) {
				// L.out("waiting to read");
				byte[] data = readBytes(dataInputStream);

				reply = new String(data, "UTF-8");

				// if (reply.length() != 1)
				// L.out("Read bytes: " + data.length);
				// M.print();
				if (reply.length() == 1) {
					// L.out("Heartbeat!");
					UpdateController.INSTANCE.incrementInputCounter(1);
				} else {
					L.out("Read bytes: " + reply.length());
					M.out("\n\nStep 5b:  Read bytes from server: " + reply.length() + " bytes\n" + reply);
					// M.out("\n\nStep 6: Substitute Uncompress: ");
					UpdateController.INSTANCE.incrementInputCounter(reply.length());
					String uncompressed = Dictionary.INSTANCE.unSubstitute(reply);
					M.out("\n\nStep 6: Substitute Uncompress: " + uncompressed.length() + " bytes");
					L.out("Read bytes: " + uncompressed);
					temp = UpdateController.BLACK + "Read: " + UpdateController.END + uncompressed;
					UpdateController.INSTANCE.callback(new PlainString(temp), UpdateController.PLAIN_STRING);
					dataInputStream.close();
					dataOutputStream.close();
					currentSocket.close();

					M.out("\n\nStep 7: Show received package:");
					PrettyPrint.prettyPrint(uncompressed);
					// M.print();
					// need this for mobile layer
					// PayloadWrapper payloadWrapper =
					// UpdateController.INSTANCE.processSync(uncompressed);
					// if (payloadWrapperCallback != null) {
					// payloadWrapperCallback.callback(payloadWrapper);
					// payloadWrapperCallback = null;
					// }
					return "normal finish";
				}
			}
		} catch (IOException e) {
			L.out("Lost connection to " +
					SERVER_HOSTNAME + ":" + SERVER_PORT + " " + e);
			return "finished execute";
		} catch (Exception e) {
			L.out("Can not establish write / read connection to " +
					SERVER_HOSTNAME + ":" + SERVER_PORT + " " + e + L.p());
		}
		L.out("Finished reply from the server");

		// setPayload(getDefaultPayload());
		return "finished execute";
	}

	// private synchronized PayloadWrapper processJSon(String json) {
	// PayloadWrapper payloadWrapper = null;
	// try {
	// JSONObject jsonObject = new JSONObject(json);
	// if (jsonObject != null) {
	// // L.out("\nPayload: " + jsonObject.toString(2) + "\n");
	// payloadWrapper = PayloadWrapper.getGJon(json);
	//
	// // L.out("payloadWrapper: " + payloadWrapper);
	// if (payloadWrapper != null) {
	// List<Payload> loads = payloadWrapper.payloads.loads;
	// for (Payload payload : loads) {
	// L.out("process payload: " + payload);
	// generateReplyPayload(payloadWrapper, payload);
	// }
	// }
	// }
	// } catch (Exception e) {
	// L.out("failed: " + e + L.p());
	//
	// }
	// return payloadWrapper;
	// }
	//
	// private void generateReplyPayload(PayloadWrapper payloadWrapper, Payload
	// payload) {
	//
	// String name = payload.jSonName;
	// L.out("payload: " + name);
	// // L.out("json: " + payload.jSon);
	// String json = payload.jSon.substring(0, payload.jSon.length() - 1);
	// if (payload.jSonName.equals("StatusWrapper")) {
	// statusWrapper =
	// StatusWrapper.getGJon(json);
	// if (validateUser != null) {
	// statusWrapper.currentStatus.employeePIN = validateUser.getMobilePIN();
	// } else
	// L.out("ERROR: No ValidateUser - Failed to set PIN!");
	// L.out("    StatusWrapper: " + statusWrapper);
	// } else if (payload.jSonName.equals("Monitor")) {
	// Monitor monitor = Monitor.getGJon(json);
	// L.out("    Monitor: " + monitor);
	// } else if (payload.jSonName.equals("ValidateUser")) {
	// validateUser = ValidateUser.getGJon(json);
	// L.out("    ValidateUser: " + validateUser);
	// } else if
	// (payload.jSonName.equals("GetTaskInformationByTaskNumberAndFacilityID"))
	// {
	// GetTaskInformationByTaskNumberAndFacilityID task =
	// GetTaskInformationByTaskNumberAndFacilityID.getGJon(json);
	// L.out("    Task: " + task);
	//
	// } else {
	// L.out("ERROR: unknown payload: " + name);
	// }
	//
	// }

	// public String execute(String urlString, ContentValues contentValues) {
	// try {
	// String temp;
	// L.out("before old ! socket");
	// Socket socket = new Socket(SERVER_HOSTNAME, SERVER_PORT);
	// L.out("created socket: " + socket);
	// L.out("Connected to server " + SERVER_HOSTNAME + ":" + SERVER_PORT);
	//
	// PrintWriter out = new PrintWriter(
	// new OutputStreamWriter(socket.getOutputStream()));
	// out.println("hello from the client");
	// out.println((String) null);
	// // out.println("bye");
	// out.println("bye");
	// out.flush();
	// // out.close();
	// BufferedReader in = new BufferedReader(
	// new InputStreamReader(socket.getInputStream()));
	// out.println("trying to read server");
	// while ((temp = in.readLine()) != null) {
	// L.out("temp: " + temp);
	// }
	// L.out("finished hello from the client");
	// out.close();
	// in.close();
	// socket.close();
	//
	// } catch (Exception ioe) {
	// L.out("Can not establish connection to " +
	// SERVER_HOSTNAME + ":" + SERVER_PORT + " " + ioe);
	// // System.exit(-1);
	// }
	// return "finished execute";
	// }

	/**
	 * Convert a string to a Dom document
	 * 
	 * @param message
	 * @return
	 */
	// private static Document convertStringToDocument(String message) {
	// try {
	// // System.out.println("convertStringtodocument: \n"+message);
	//
	// DocumentBuilder docBuilder =
	// DocumentBuilderFactory.newInstance().newDocumentBuilder();
	// Document doc = docBuilder.parse(new InputSource(new
	// StringReader(message)));
	// // getDom().printDom(doc, "converted string", true);
	// return doc;
	// } catch (Exception e) {
	// System.out.println("convertStringTodocument error: " + e + L.p());
	// // e.printStackTrace();
	// return null;
	// }
	// }

	private static String serialize(Document doc) throws IOException {
		if (doc == null) {
			System.out.println("Dom.serializeOld has no document! " + L.p());
			return "";
		}
		DOMElementWriter domWriter = new DOMElementWriter();
		StringWriter sw = new StringWriter();
		domWriter.write(doc.getDocumentElement(), sw, 3, " ");
		// Out.out("serialize: "+sw);
		return sw.toString();
	}

	public String printDom(Document doc, String header, boolean printOut) {
		return prettyPrint(doc, header, printOut);
	}

	public String printDom(Document doc, String header) {
		return prettyPrint(doc, header, false);
	}

	public static String printDom(Document doc) {
		return prettyPrint(doc, null, false);
	}

	private static String prettyPrint(Document doc, String header, boolean printout) {
		try {
			String sw = serialize(doc);
			if (printout) {
				if (header != null) {
					System.out.println("prettyPrint: " + header);
				}
				System.out.println(sw);
			}
			if (header != null) {
				String temp = "prettyPrint: " + header + "\n" + sw;
				if (printout)
					System.out.println(temp);
				return temp;
			}
			return sw;
		} catch (Exception exception) {
			System.out.println("pretty print" + L.p());
		}
		return null;
	}

	// private String convertStreamToString(InputStream is)
	// throws IOException {
	// if (is != null) {
	// Writer writer = new StringWriter();
	// char[] buffer = new char[1024];
	// try {
	// Reader reader = new BufferedReader(
	// new InputStreamReader(is, "UTF-8"));
	// int n;
	// while ((n = reader.read(buffer)) != -1) {
	// writer.write(buffer, 0, n);
	// }
	// } finally {
	// is.close();
	// }
	// return writer.toString();
	// } else {
	// return "";
	// }
	// }

	// protected String convertXMLToJSON(String xmlString) {
	// try {
	// JSONObject jsonObject = XML.toJSONObject(xmlString);
	// return jsonObject.toString();
	// } catch (JSONException e) {
	// L.out("*** ERROR on xmlString: " + e + " " + xmlString);
	// e.printStackTrace();
	// }
	// return null;
	// }

}
