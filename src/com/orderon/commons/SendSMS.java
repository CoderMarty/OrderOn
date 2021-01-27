package com.orderon.commons;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

import com.orderon.services.Services;
 
public class SendSMS {
	public String sendSms(String messageText, String number) {
		try {
			// Construct data
			String apiKey = "apikey=" + "1WK1+X6+8M8-zcEnJd5Z0MFN7iHF2Cjq7OhSfr7d4J";
			String message = "&message=" + messageText;
			String sender = "&sender=" + "ORDRON";
			String numbers = "&numbers=" + "91"+number;
			
			// Send data
			HttpURLConnection conn = (HttpURLConnection) new URL("https://api.textlocal.in/send/?").openConnection();
			String data = apiKey + numbers + message + sender;
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Length", Integer.toString(data.length()));
			conn.getOutputStream().write(data.getBytes("UTF-8"));
			final BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			final StringBuffer stringBuffer = new StringBuffer();
			String line;
			while ((line = rd.readLine()) != null) {
				stringBuffer.append(line);
			}
			rd.close();
			
			return stringBuffer.toString();
		} catch (Exception e) {
			System.out.println("Error SMS "+e);
			return "Error "+e;
		}
	}
	
	public String sendPromotionalSms(String userId, String messageText, String number, String apiKey, String senderId) {
		JSONObject response = new JSONObject();
		try {
			// Construct data
			//apiKey = "apikey=" + apiKey;
			String message = "&message=" + messageText.replaceAll(" ", "+");
			String sender = "&senderid=" + senderId;
			String numbers = "&dest_mobileno=" + "91"+number;
			userId = "username=" + userId;
			String password = "&pass=" + apiKey;
			
			// Send data
			response.put("status", "failure");
			message = "http://www.smsjust.com/sms/user/urlsms.php?"+ userId + password + sender + numbers + message + "&response=Y";
			String r = Services.getHTML(message);
			System.out.println(r);
			
			response.put("status", "success");
			
		} catch (Exception e) {
			System.out.println("Error SMS "+e);
		}
		return response.toString();
	}
}