package com.ii.mobile.soap;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.ii.mobile.util.L;

public class SecureRestService {

	public SecureRestService() {
		// StrictMode.ThreadPolicy policy = new
		// StrictMode.ThreadPolicy.Builder()
		// .permitAll().build();
		// StrictMode.setThreadPolicy(policy);
	}

	// byte[] bytes = new byte[] { 47,
	// 110, 97, 109, 101, 34, 62, 60, 65, 116, 116, 114,
	// 105, 98, 117, 116, 101, 86, 97, 108, 117, 101,
	// 62, 105, 118, 97, 110, 46, 110, 101, 108,
	// 115, 111, 110, 64, 105, 105, 99, 111, 114, 112,
	// 111, 114, 97, 116, 101, 46, 99, 111, 109,
	// 60, 47, 65, 116, 116, 114, 105, 98, 117,
	// 116, 101, 86, 97, 108, 117, 101, 62 };

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

			if (urlString.startsWith("https"))
				// return secureExecute(urlString);
				// return test2Connection(urlString);

				L.out("urlString: " + urlString);
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

			conn.setRequestProperty("Accept", "application/json");
			// L.out("responseCode: " + conn.getResponseCode());
			if (conn.getResponseCode() != 200) {

				throw new RuntimeException("Failed : HTTP error code : "
						+ conn.getResponseCode());
			}
			// L.out("conn.getResponseCode(): " + conn.getResponseCode());
			// Get xml document as a string
			responseString = convertStreamToString(conn.getInputStream());

			L.out("\nRaw responseString: " + responseString);
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
			// L.out("convertStringtodocument: \n"+message);

			DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = docBuilder.parse(new InputSource(new StringReader(message)));
			// getDom().printDom(doc, "converted string", true);
			return doc;
		} catch (Exception e) {
			L.out("convertStringTodocument error: " + e + L.p());
			// e.printStackTrace();
			return null;
		}
	}

	public static String serialize(Document doc) throws IOException {
		if (doc == null) {
			L.out("Dom.serializeOld has no document! " + L.p());
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
					L.out("prettyPrint: " + header);
				}
				L.out(sw);
			}
			if (header != null) {
				String temp = "prettyPrint: " + header + "\n" + sw;
				if (printout)
					L.out(temp);
				return temp;
			}
			return sw;
		} catch (Exception exception) {
			L.out("pretty print" + L.p());
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
			return "";
		} catch (Exception e) {
			L.out("*** ERROR on xmlString: " + e + " " + xmlString);
			e.printStackTrace();
		}
		return null;
	}

	// public class MySSLSocketFactory extends SSLSocketFactory {
	// SSLContext sslContext = SSLContext.getInstance("TLS");
	//
	// public MySSLSocketFactory(KeyStore truststore) throws
	// NoSuchAlgorithmException,
	// KeyManagementException, KeyStoreException, UnrecoverableKeyException {
	// super(truststore);
	//
	// TrustManager tm = new X509TrustManager() {
	// public void checkClientTrusted(X509Certificate[] chain, String authType)
	// throws CertificateException {
	// }
	//
	// public void checkServerTrusted(X509Certificate[] chain, String authType)
	// throws CertificateException {
	// }
	//
	// public X509Certificate[] getAcceptedIssuers() {
	// return null;
	// }
	// };
	//
	// sslContext.init(null, new TrustManager[] { tm }, null);
	// }
	//
	// @Override
	// public Socket createSocket(Socket socket, String host, int port, boolean
	// autoClose)
	// throws IOException, UnknownHostException {
	// return sslContext.getSocketFactory().createSocket(socket, host, port,
	// autoClose);
	// }
	//
	// @Override
	// public Socket createSocket() throws IOException {
	// return sslContext.getSocketFactory().createSocket();
	// }
	//
	// }

	private final String USER_AGENT = "Mozilla/5.0";

	public HttpClient getNewHttpClient() {
		try {
			KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
			trustStore.load(null, null);

			SSLSocketFactory sf = new HttpSSLSocketFactory(trustStore);
			sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

			HttpParams params = new BasicHttpParams();
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

			SchemeRegistry registry = new SchemeRegistry();
			registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
			registry.register(new Scheme("https", sf, 443));

			ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

			return new DefaultHttpClient(ccm, params);
		} catch (Exception e) {
			L.out("error: " + e);
			return new DefaultHttpClient();
		}
	}

	// always verify the host - dont check for certificate
	// final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
	// public boolean verify(String hostname, SSLSession session) {
	// return true;
	// }
	// };

	/**
	 * Trust every server - dont check for any certificate
	 */
	private static void trustAllHosts() {
		// Create a trust manager that does not validate certificate chains
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			@Override
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return new java.security.cert.X509Certificate[] {};
			}

			@Override
			public void checkClientTrusted(X509Certificate[] chain,
					String authType) throws CertificateException {
			}

			@Override
			public void checkServerTrusted(X509Certificate[] chain,
					String authType) throws CertificateException {
			}
		} };

		// Install the all-trusting trust manager
		try {
			SSLContext sc = SSLContext.getInstance("TLS");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection
					.setDefaultSSLSocketFactory(sc.getSocketFactory());
		} catch (Exception e) {
			e.printStackTrace();
		}
		L.out("installed Trust all hosts");
	}

	private HttpURLConnection getTestConnection(String urlString) {
		HttpURLConnection http = null;
		URL url = null;
		try {
			url = new URL(urlString);
			L.out("url: " + url);
			if (url.getProtocol().toLowerCase().equals("https")) {
				trustAllHosts();
				HttpsURLConnection https = (HttpsURLConnection) url.openConnection();
				// https.setHostnameVerifier(DO_NOT_VERIFY);
				http = https;
			} else {
				http = (HttpURLConnection) url.openConnection();
			}
		} catch (MalformedURLException e) {
			L.out("e: " + e);
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			L.out("e: " + e);
			e.printStackTrace();
		}
		L.out("http: " + http);

		return http;
	}

	// canada
	private String secureConnection(String urlString) {
		// L.out("urlString: " + urlString);
		// if (true)
		// return testConnection(urlString);
		urlString = "https://crothall-canada.com/HRCAjax/DOM/StateEngineWebSite/StateEngineWebService.asmx/MobileValidateUser";
		urlString = "https://crothall-canada.com/HRCAjax/DOM/StateEngineWebSite/StateEngineWebService.asmx?wsdl";
		L.out("urlString: " + urlString);
		HttpClient client = getNewHttpClient();
		// HttpPost request = new HttpPost(urlString);
		// request.setHeader("User-Agent", "Mozilla/5.0");
		// HttpPost post = new
		// HttpPost("https://crothall-canada.com/HRCAjax/DOM/StateEngineWebSite/StateEngineWebService.asmx/MobileValidateUser");
		HttpPost post = new HttpPost(urlString);
		// HttpPost post = new HttpPost(urlString);
		post.setHeader("User-Agent", USER_AGENT);

		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		nameValuePairs.add(new BasicNameValuePair("PIN", "1972"));
		nameValuePairs.add(new BasicNameValuePair("UserName", "BJAWS"));

		try {
			// post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = client.execute(post);
			HttpEntity entity = response.getEntity();

			String responseText = EntityUtils.toString(entity);
			L.out("responseText: " + responseText);
			// HttpResponse response = client.execute(request);
			// Check if server response is valid
			StatusLine status = response.getStatusLine();
			L.out("status: " + status);
			if (status.getStatusCode() != 200) {
				// throw new IOException("Invalid response from server: " +
				// status.toString());
			}
			BufferedReader rd = new BufferedReader(
					new InputStreamReader(response.getEntity().getContent()));

			StringBuffer result = new StringBuffer();
			String line = "";
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}

			L.out(result.toString());

			if (true)
				return null;
			// Pull content stream from response
			// HttpEntity entity = response.getEntity();
			InputStream inputStream = entity.getContent();

			ByteArrayOutputStream content = new ByteArrayOutputStream();

			// Read response into a buffered stream
			int readBytes = 0;
			byte[] sBuffer = new byte[512];
			while ((readBytes = inputStream.read(sBuffer)) != -1) {
				content.write(sBuffer, 0, readBytes);
			}

			// Return result from buffered stream
			String dataAsString = new String(content.toByteArray());
			L.out("dataAsString: " + dataAsString);
			return dataAsString;
		} catch (Exception e) {
			L.out("error: " + e.getLocalizedMessage());
		}
		return null;
	}

	private String testConnection(String urlString) {
		urlString = "http://chp-s1-web-cs.cloudapp.net";
		urlString = "https://crothall-canada.com/HRCAjax/DOM/StateEngineWebSite/StateEngineWebService.asmx/MobileValidateUser?PIN=1972&UserName=BJAWS";
		urlString = "https://crothall-canada.com/HRCAjax/DOM/StateEngineWebSite/StateEngineWebService.asmx?wsdl";
		HttpsURLConnection conn = null;
		String responseString = "";
		try {
			conn = (HttpsURLConnection) getTestConnection(urlString);
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			// http.setRequestProperty("Content-Length",
			// Integer.toString(postData.length));
			conn.setUseCaches(false);
			conn.setHostnameVerifier(new AllowAllHostnameVerifier());
			// String authString = "mobile1:123";
			// L.out("Auth string: " + authString);
			// authString = Base64.encodeToString(authString.getBytes("UTF-8"),
			// Base64.NO_WRAP);
			// conn.setRequestProperty("Authorization", "Basic " + authString);
			L.out("Response Code : " + conn.getResponseCode());
			L.out("Cipher Suite : " + conn.getCipherSuite());
			L.out("\n");

			Certificate[] certs = conn.getServerCertificates();
			for (Certificate cert : certs) {
				L.out("Cert Type : " + cert.getType());
				L.out("Cert Hash Code : " + cert.hashCode());
				L.out("Cert Public Key Algorithm : "
						+ cert.getPublicKey().getAlgorithm());
				L.out("Cert Public Key Format : "
						+ cert.getPublicKey().getFormat());
				L.out("\n");
			}
			L.out("reponseMessage: " + conn.getResponseMessage());
			// HttpPost post = new HttpPost(urlString);
			// HttpResponse response = ((HttpClient) conn).execute(post);
			// HttpEntity entity = response.getEntity();
			//
			// String responseText = EntityUtils.toString(entity);
			// L.out("responseText: " + responseText);
			// conn.setRequestProperty("Accept", "text/xml");
			L.out("responseCode: " + conn.getResponseCode());
			if (conn.getResponseCode() != 200) {
				//
				// throw new RuntimeException("Failed : HTTP error code : "
				// + conn.getResponseCode());
			}
			// L.out("conn.getResponseCode(): " + conn.getResponseCode());
			// Get xml document as a string
			responseString = convertStreamToString(conn.getInputStream());

			L.out("\nRaw responseString: " + responseString);
			// clean up encodings, could add HTTPUtils to do this
			// responseString = responseString.replaceAll("&lt;", "<");
			// responseString = responseString.replaceAll("&gt;", ">");
			// responseString = responseString.replaceAll("amp;", "");
			// responseString = responseString.replaceAll("&", "_");
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
		} catch (SSLPeerUnverifiedException e) {
			L.out("unverified e: " + e);
			e.printStackTrace();
		} catch (IOException e) {
			L.out("IO e: " + e);
			e.printStackTrace();
		} catch (Exception e) {
			L.out("error:" + e + " urlString: " + urlString + L.p());
			return null;
		}
		return responseString;
	}

	private String test2Connection(String urlString) {
		urlString = "http://chp-s1-web-cs.cloudapp.net";
		urlString = "https://crothall-canada.com/HRCAjax/DOM/StateEngineWebSite/StateEngineWebService.asmx/MobileValidateUser?PIN=1972&UserName=BJAWS";
		urlString = "https://crothall-canada.com/HRCAjax/DOM/StateEngineWebSite/StateEngineWebService.asmx/MobileValidateUser";
		// urlString =
		// "https://crothall-canada.com/HRCAjax/DOM/StateEngineWebSite/StateEngineWebService.asmx?wsdl";
		HttpClient httpClient = null;
		String responseString = "";
		L.out("urlString: " + urlString);
		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("PIN", "1972"));
			nameValuePairs.add(new BasicNameValuePair("UserName", "BJAWS"));

			httpClient = HttpSSLSocketFactory.getNewHttpClient();
			// HttpGet httpGet = new HttpGet(urlString);
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(nameValuePairs, "UTF-8");
			HttpPost httpPost = new HttpPost(urlString);
			httpPost.setEntity(entity);
			L.out("getURI:" + httpPost.getURI());
			L.out("getRequestLine:" + httpPost.getRequestLine());
			HttpResponse response = httpClient.execute(httpPost);
			L.out("getProtocolVersion: " + response.getProtocolVersion());
			L.out("getStatusCode: " + response.getStatusLine().getStatusCode());
			L.out("getReasonPhrase: " + response.getStatusLine().getReasonPhrase());
			L.out("getStatusLine: " + response.getStatusLine().toString());
			HttpEntity responseEntity = response.getEntity();
			String responseText = EntityUtils.toString(responseEntity);
			L.out("responseText: " + responseText);
			// HttpResponse response = client.execute(request);
			// Check if server response is valid
			StatusLine status = response.getStatusLine();
			L.out("status: " + status);
			if (status.getStatusCode() != 200) {
				// throw new IOException("Invalid response from server: " +
				// status.toString());
			}
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(response.getEntity().getContent()));

			StringBuffer result = new StringBuffer();
			String line = "";
			while ((line = bufferedReader.readLine()) != null) {
				result.append(line);
			}
			bufferedReader.close();

			L.out(result.toString());
			//
			// if (entity != null) {
			// InputStream inputStream = entity.getContent();
			// try {
			//
			// ByteArrayOutputStream content = new ByteArrayOutputStream();
			//
			// // Read response into a buffered stream
			// int readBytes = 0;
			// byte[] sBuffer = new byte[512];
			// while ((readBytes = inputStream.read(sBuffer)) != -1) {
			// content.write(sBuffer, 0, readBytes);
			// }
			//
			// // Return result from buffered stream
			// String dataAsString = new String(content.toByteArray());
			// L.out("dataAsString: " + dataAsString);
			// return dataAsString;
			// } finally {
			// inputStream.close();
			// }
			// }

			return null;
		} catch (SSLPeerUnverifiedException e) {
			L.out("unverified e: " + e);
			e.printStackTrace();
		} catch (IOException e) {
			L.out("IO e: " + e);
			e.printStackTrace();
		} catch (Exception e) {
			L.out("error:" + e + " urlString: " + urlString + L.p());
			return null;
		}
		return responseString;
	}

	private String test2Connectiona(String urlString) {
		urlString = "http://chp-s1-web-cs.cloudapp.net";
		urlString = "https://crothall-canada.com/HRCAjax/DOM/StateEngineWebSite/StateEngineWebService.asmx/MobileValidateUser?PIN=1972&UserName=BJAWS";
		urlString = "https://crothall-canada.com/HRCAjax/DOM/StateEngineWebSite/StateEngineWebService.asmx/MobileValidateUser";
		// urlString =
		// "https://crothall-canada.com/HRCAjax/DOM/StateEngineWebSite/StateEngineWebService.asmx?wsdl";
		HttpClient httpClient = null;
		String responseString = "";
		L.out("urlString: " + urlString);
		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("PIN", "1972"));
			nameValuePairs.add(new BasicNameValuePair("UserName", "BJAWS"));

			httpClient = HttpSSLSocketFactory.getNewHttpClient();
			// HttpGet httpGet = new HttpGet(urlString);
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(nameValuePairs, "UTF-8");
			HttpPost httpPost = new HttpPost(urlString);
			httpPost.setEntity(entity);
			L.out("getURI:" + httpPost.getURI());
			L.out("getRequestLine:" + httpPost.getRequestLine());
			HttpResponse response = httpClient.execute(httpPost);
			L.out("getProtocolVersion: " + response.getProtocolVersion());
			L.out("getStatusCode: " + response.getStatusLine().getStatusCode());
			L.out("getReasonPhrase: " + response.getStatusLine().getReasonPhrase());
			L.out("getStatusLine: " + response.getStatusLine().toString());
			HttpEntity responseEntity = response.getEntity();
			String responseText = EntityUtils.toString(responseEntity);
			L.out("responseText: " + responseText);
			// HttpResponse response = client.execute(request);
			// Check if server response is valid
			StatusLine status = response.getStatusLine();
			L.out("status: " + status);
			if (status.getStatusCode() != 200) {
				// throw new IOException("Invalid response from server: " +
				// status.toString());
			}
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(response.getEntity().getContent()));

			StringBuffer result = new StringBuffer();
			String line = "";
			while ((line = bufferedReader.readLine()) != null) {
				result.append(line);
			}
			bufferedReader.close();

			L.out(result.toString());
			//
			// if (entity != null) {
			// InputStream inputStream = entity.getContent();
			// try {
			//
			// ByteArrayOutputStream content = new ByteArrayOutputStream();
			//
			// // Read response into a buffered stream
			// int readBytes = 0;
			// byte[] sBuffer = new byte[512];
			// while ((readBytes = inputStream.read(sBuffer)) != -1) {
			// content.write(sBuffer, 0, readBytes);
			// }
			//
			// // Return result from buffered stream
			// String dataAsString = new String(content.toByteArray());
			// L.out("dataAsString: " + dataAsString);
			// return dataAsString;
			// } finally {
			// inputStream.close();
			// }
			// }

			return null;
		} catch (SSLPeerUnverifiedException e) {
			L.out("unverified e: " + e);
			e.printStackTrace();
		} catch (IOException e) {
			L.out("IO e: " + e);
			e.printStackTrace();
		} catch (Exception e) {
			L.out("error:" + e + " urlString: " + urlString + L.p());
			return null;
		}
		return responseString;
	}

	public String execute() {
		String urlString = "http://chp-s1-web-cs.cloudapp.net";

		HttpClient httpClient = null;
		String responseString = "";
		L.out("urlString: " + urlString);
		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("PIN", "1972"));
			nameValuePairs.add(new BasicNameValuePair("UserName", "BJAWS"));

			httpClient = HttpSSLSocketFactory.getNewHttpClient();
			// HttpGet httpGet = new HttpGet(urlString);
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(nameValuePairs, "UTF-8");
			HttpPost httpPost = new HttpPost(urlString);
			httpPost.setEntity(entity);
			L.out("getURI:" + httpPost.getURI());
			L.out("getRequestLine:" + httpPost.getRequestLine());
			HttpResponse response = httpClient.execute(httpPost);
			L.out("getProtocolVersion: " + response.getProtocolVersion());
			L.out("getStatusCode: " + response.getStatusLine().getStatusCode());
			L.out("getReasonPhrase: " + response.getStatusLine().getReasonPhrase());
			L.out("getStatusLine: " + response.getStatusLine().toString());
			HttpEntity responseEntity = response.getEntity();
			String responseText = EntityUtils.toString(responseEntity);
			L.out("responseText: " + responseText);
			// HttpResponse response = client.execute(request);
			// Check if server response is valid
			StatusLine status = response.getStatusLine();
			L.out("status: " + status);
			if (status.getStatusCode() != 200) {
				// throw new IOException("Invalid response from server: " +
				// status.toString());
			}
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(response.getEntity().getContent()));

			StringBuffer result = new StringBuffer();
			String line = "";
			while ((line = bufferedReader.readLine()) != null) {
				result.append(line);
			}
			bufferedReader.close();

			L.out(result.toString());
		} catch (Exception e) {
			L.out("error:" + e + " urlString: " + urlString + L.p());
			return null;
		}
		return null;

	}
}
