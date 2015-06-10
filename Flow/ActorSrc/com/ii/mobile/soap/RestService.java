package com.ii.mobile.soap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import android.content.ContentValues;
import android.os.StrictMode;

import com.ii.mobile.util.L;

public class RestService {

	public RestService() {
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);
	}

	public String execute(String urlString, ContentValues contentValues) {

		String responseString = "";
		try {
			// add params
			if (contentValues.size() > 0)
			{
				urlString += "?";
				Set<Entry<String, Object>> s = contentValues.valueSet();
				Iterator<Entry<String, Object>> itr = s.iterator();
				boolean first = true;
				while (itr.hasNext())
				{
					Entry<String, Object> entry = itr.next();
					String key = entry.getKey().toString();
					String value = (String) entry.getValue();
					// L.out("ContentValue: " + key + " " + value);
					if (!first)
						urlString += "&";
					else
						first = false;
					urlString += key + "=" + value;
				}
			}
			// L.out("urlString: " + urlString);
			URL url = new URL(urlString);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			// conn.setDoOutput(true);
			conn.setRequestMethod("GET");
			// conn.setHostnameVerifier(new AllowAllHostnameVerifier());
			// String authString = "mobile1:123";
			// L.out("Auth string: " + authString);
			// authString = Base64.encodeToString(authString.getBytes("UTF-8"),
			// Base64.NO_WRAP);
			// conn.setRequestProperty("Authorization", "Basic " + authString);

			conn.setRequestProperty("Accept", "text/xml");
			// L.out("responseCode: " + conn.getResponseCode());
			if (conn.getResponseCode() != 200) {

				throw new RuntimeException("Failed : HTTP error code : "
						+ conn.getResponseCode());
			}
			// L.out("conn.getResponseCode(): " + conn.getResponseCode());
			// Get xml document as a string
			responseString = convertStreamToString(conn.getInputStream());

			BaseSoap.debugOutput("\nRaw responseString: " + responseString);
			// clean up encodings, could add HTTPUtils to do this
			responseString = responseString.replaceAll("&lt;", "<");
			responseString = responseString.replaceAll("&gt;", ">");
			responseString = responseString.replaceAll("amp;", "");
			responseString = responseString.replaceAll("&", "_");
			// BaseSoap.debugOutput("new foo: " + responseString);

			// clean up the xml document!
			int index = responseString.indexOf("1.0\"?") + 6;
			if (index == -1)
				return null;
			responseString = responseString.substring(index,
					responseString.length());
			index = responseString.indexOf("</string>");
			if (index == -1)
				return null;
			responseString = responseString.substring(0, index);
			// L.out("responseString: " + responseString);
			String temp = convertXMLToJSON(responseString);
			// L.out("temp: " + temp);
			// conn.disconnect();
			return temp;
		} catch (Exception e) {
			L.out("error:" + e + " urlString: " + urlString + L.p());
			return null;
		}
	}

	/**
	 * Convert a string to a Dom document
	 * 
	 * @param message
	 * @return
	 */
	public static Document convertStringToDocument(String message) {
		try {
			// System.out.println("convertStringtodocument: \n"+message);

			DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = docBuilder.parse(new InputSource(new StringReader(message)));
			// getDom().printDom(doc, "converted string", true);
			return doc;
		} catch (Exception e) {
			System.out.println("convertStringTodocument error: " + e + L.p());
			// e.printStackTrace();
			return null;
		}
	}

	public static String serialize(Document doc) throws IOException {
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

	public String convertStreamToString(InputStream is)
			throws IOException {
		if (is != null) {
			Writer writer = new StringWriter();
			char[] buffer = new char[1024];
			try {
				Reader reader = new BufferedReader(
						new InputStreamReader(is, "UTF-8"));
				int n;
				while ((n = reader.read(buffer)) != -1) {
					writer.write(buffer, 0, n);
				}
			} finally {
				is.close();
			}
			return writer.toString();
		} else {
			return "";
		}
	}

	protected String convertXMLToJSON(String xmlString) {
		try {

			// JSONObject jsonObject = XML.toJSONObject(xmlString);
			return "";
		} catch (Exception e) {
			L.out("*** ERROR on xmlString: " + e + " " + xmlString);
			e.printStackTrace();
		}
		return null;
	}
}
