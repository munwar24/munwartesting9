package com.ii.mobile.soap;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.json.JSONObject;
//import org.ksoap2.SoapEnvelope;
//import org.ksoap2.serialization.SoapObject;
//import org.ksoap2.serialization.SoapSerializationEnvelope;
//import org.ksoap2.transport.HttpTransportSE;

import android.content.ContentValues;
import android.widget.TextView;

import com.ii.mobile.util.L;

public class BaseSoap {
	private static final boolean WANT_DEBUG = false;

	private static TextView textView = null;

	boolean showJSON = false;
	protected static final String OUTPUT_FILE = "out.txt";
	protected static final boolean DEBUG_OUTPUT = true;

	protected static final String TEST_NAMESPACE = "https://198.84.29.22/CrothallHHDWService";
	protected static final String PROD_NAMESPACE = "http://ttmobile.crothall.com/CrothallHHDWService";

	protected static final String II_NAMESPACE = "http://crothall2.persistech.com";

	protected static final String II_TEST_URL = "http://crothall2.persistech.com/HRCAjax/DOM/StateEngineWebSite/StateEngineWebService.asmx";

	protected static final String NODE_URL = "http://192.168.1.3:8124";

	protected static final String II_URL = "http://198.84.29.16/HRCAjax/DOM/StateEngineWebSite/StateEngineWebService.asmx";
	// public static String URL = II_URL;
	public static String URL = "https://www.submitarequest.com/HRCAjax/DOM/StateEngineWebSite/StateEngineWebService.asmx";
	// private static final String URL = NODE_URL;
	protected static final String NAMESPACE = II_NAMESPACE;
	// protected static final String test_url =
	// "http://198.84.29.16/HRCAjax/DOM/StateEngineWebSite/StateEngineWebService.asmx";
	// protected static final String test_url = NODE_URL;

	public static String[] soapCalls = {
			"ValidateUser",
			"ListDelayTypes",
			"ListFunctionalAreasByFacilityID",
			"ListTaskClassesByFacilityID",
			"ListRoomsByFacilityID",
			"ListRecentTasksByEmployeeID",
			"GetTaskDefinitionFieldsForScreenByFacilityID",
			"GetTaskDefinitionFieldsDataForScreenByFacilityID",
			"GetCurrentTaskByEmployeeID",
			"GetEmployeeAndTaskStatusByEmployeeID",
			"GetFacilityInformationByFacilityID",
			"GetTaskInformationByTaskNumberAndFacilityID",
			"TaskTest (show all task detail)" };

	static BaseSoap soap = null;

	public static TextView getTextView() {
		return textView;
	}

	public static void setTextView(TextView textView) {
		BaseSoap.textView = textView;
	}

	public static String getPlatform() {
		return URL;
	}

	public static void setPlatform(String url) {
		L.out("setting url: " + url);
		BaseSoap.URL = url;
	}

	// need for trusting NovoCoast
	// private static boolean inited = false;
	private static boolean inited = true;
	static int counter = 0;

	public JSONObject createJSONObject(String methodName, ContentValues contentValues) {

		JSONObject jSonObject = null;
		if (URL == null) {
			L.out("URL is null");
			return null;
		}

		try {
			// L.out("URL: " + URL);
			// need to turn this on to get output in file!
			debugOutput("\n" + counter++ + ": " + methodName + " " +
					contentValues + " " + URL);
			// result is a JSON string
			String result = new RestService().execute(URL + "/" + methodName, contentValues);

			debugOutput("result: " + result);
			if (result == null || result.equals("")) {
				debugOutput("*** Error recieved a null message!", true);
				return null;
			}
			jSonObject = parseJSON(result);
			if (jSonObject == null) {
				debugOutput("\njSonObject is " + jSonObject + "\n", true);
				return null;
			}
			// need to turn this on to get output in file!
			debugOutput(jSonObject.toString(3));
		} catch (Exception e) {

			L.out("*** ERROR in Soap call: " + e + " " + methodName + " " + contentValues);
			return null;
		}

		return jSonObject;
	}

	// JSONObject callSoap(SoapObject request) {
	// SoapObject soapObject = null;
	// JSONObject jSonObject = null;
	// L.out("test: " + request.getName());
	// L.out("test: " + request);
	// try {
	// debugOutput("\n" + counter++ + ": " + request.toString());
	//
	// String result = "";
	// SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
	// SoapEnvelope.VER11);
	// L.out("envelope: " + envelope);
	// envelope.dotNet = false;
	// envelope.setOutputSoapObject(request);
	//
	// String temp = NAMESPACE + "/MobileValidateUser";
	// L.out("temp: " + temp);
	// HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
	// androidHttpTransport.call(temp, envelope);
	// result = (String) envelope.getResponse();
	// L.out("result: " + result);
	// if (result == null || result.equals("")) {
	// debugOutput("*** Error recieved a null message!", true);
	// return null;
	// }
	//
	// soapObject = (SoapObject) envelope.bodyIn;
	//
	// if (soapObject == null) {
	// debugOutput("soapObject is null: " + soapObject, true);
	// return null;
	// }
	// jSonObject = parseSoap(soapObject);
	// if (jSonObject == null) {
	// debugOutput("\njSonObject is " + jSonObject + "\n", true);
	// return null;
	// }
	//
	// debugOutput(jSonObject.toString(3));
	//
	// } catch (Exception e) {
	// // if (textView != null) {
	// // textView.setText("Error in callSoap: " + e.getMessage());
	// // }
	// L.out("*** ERROR in Soap call: " + e + " " + request.toString());
	// return null;
	// }
	//
	// return jSonObject;
	// }

	// JSONObject parseSoap(SoapObject result) {
	//
	// JSONObject jSONObject = null;
	// try {
	// /* gets our result in JSON String */
	// String resultObject = result.getProperty(0).toString();
	// // L.out("resultObject: " + resultObject);
	// String converted = new String(Base64.decode(resultObject,
	// Base64.DEFAULT));
	// // L.out("resultObject: " + converted);
	// if (converted == null || converted.equals(""))
	// return null;
	// jSONObject = new JSONObject(converted);
	//
	// } catch (Exception e) {
	// L.out("*** ERROR parsing: " + result.toString());
	// }
	// return jSONObject;
	// }

	public static JSONObject parseJSON(String temp) {
		JSONObject jSONObject = null;
		try {
			jSONObject = new JSONObject(temp);

		} catch (Exception e) {
			L.out("*** ERROR parsing: " + temp);
		}
		return jSONObject;
	}

	protected BaseSoap() {
		if (!inited) {
			L.out("Installed TrustManager");
			// delete the output file
			L.debugOutput(true, OUTPUT_FILE);
			debugOutput("\nWSDL: " + URL + "\n");

			for (int i = 0; i < soapCalls.length; i++)
				debugOutput("  " + i + ": " + soapCalls[i]);
			inited = true;
			// Create a trust manager that does not validate certificate chains
			TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
				@Override
				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				@Override
				public void checkClientTrusted(
						java.security.cert.X509Certificate[] certs, String authType) {
				}

				@Override
				public void checkServerTrusted(
						java.security.cert.X509Certificate[] certs, String authType) {
				}
			} };

			// Install the all-trusting trust manager
			try {
				SSLContext sc = SSLContext.getInstance("SSL");
				sc.init(null, trustAllCerts, new java.security.SecureRandom());
				HttpsURLConnection
						.setDefaultSSLSocketFactory(sc.getSocketFactory());
			} catch (Exception e) {
				L.out("trustManager failed: " + e);
			}
		}
	}

	public static void debugOutput(String json) {
		debugOutput(json, true);
	}

	private static int MAX_LENGTH = 20000;

	@SuppressWarnings("unused")
	public static void debugOutput(String json, boolean printMessage) {
		if (!WANT_DEBUG)
			return;
		if (DEBUG_OUTPUT || printMessage) {
			String temp = json;
			if (json.length() > MAX_LENGTH) {
				temp = temp.substring(0, MAX_LENGTH);
				temp += "\nTruncated " + (json.length() - MAX_LENGTH) + " characters out of "
						+ json.length() + " characters";
			}
			L.out(temp + "\n");
			if (DEBUG_OUTPUT)
				L.debugOutput(temp, OUTPUT_FILE);
			TextView textView = BaseSoap.getTextView();
			// L.out("Length: " + temp.length() + "\n");
			if (textView != null) {
				// L.out("x Length: " + textView.getText().length() + "\n");
				textView.setText(textView.getText() + temp + "\n");
			}
		}
	}
}
