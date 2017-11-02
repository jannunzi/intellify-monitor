package com.hmhco.lrs.intellify.service;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;

import org.json.JSONObject;

import com.hmhco.lrs.intellify.model.IntellifyEvent;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public abstract class BaseIntellifyService {

	private static String USERNAME = "mryan";
	private static String PASSWORD = "scholastic";
	private static String INTELLIFY_API_TOKEN_PAYLOAD = "{\"username\": \"mryan\", \"password\": \"scholastic\"}";
	private static String INTELLIFY_API_TOKEN_URL     = "https://hmh2.intellifylearning.com/user/apiToken";
	protected static String PAYLOAD = "{from: FROM, to: TO}";
	private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
	protected String apiToken = "e5c78f0d429538cb9f9dc4e8f8030c90d84af76e1f40f2888d00a530b2f347cbd28eeb32cad013e79a1b95fec60a7edf88d9450dc04f7e763dd05fbf876e9f39";

	protected String url = null;
	protected String payload = PAYLOAD;
	protected String response = null;
	protected Date from;
	protected Date to;

	public BaseIntellifyService() {
		this(INTELLIFY_API_TOKEN_URL, INTELLIFY_API_TOKEN_PAYLOAD);
	}

	public BaseIntellifyService(String url, String payload) {
		this.setUrl(url);
		this.setPayload(payload);
		this.setFrom(this.getLastNightAtMidnight());
		this.setTo(this.minusOneDay(this.getFrom()));
	}
	
	public BaseIntellifyService(Date from, Date to) {
		this(INTELLIFY_API_TOKEN_URL, INTELLIFY_API_TOKEN_PAYLOAD, from, to);
	}

	public BaseIntellifyService(String url, String payload, Date from, Date to) {
		this.setUrl(url);
		this.setPayload(payload);
		this.setFrom(from);
		this.setTo(to);
	}

	public BaseIntellifyService(String url, String payload, String from, String to) {
		this.setPayload(payload);
		this.setUrl(url);

		Date fromDate = new Date();
		Date toDate = new Date();
		try {
			fromDate = simpleDateFormat.parse(from);
			toDate   = simpleDateFormat.parse(to);
		}
		catch (ParseException e) {
			e.printStackTrace();
		}
		this.setFrom(fromDate);
		this.setTo(toDate);
	}
	
	public abstract ArrayList<IntellifyEvent> parseIntellifyEvents(String jsonEvents);

	public void setFrom(String from) {
		Date fromDate = new Date();
		try {
			fromDate = simpleDateFormat.parse(from);
		}
		catch (ParseException e) {
			e.printStackTrace();
		}
		this.setFrom(fromDate);
	}
	
	public void setFrom(Date from) { this.from = from; }
	public void setTo(String to) {
		Date toDate = new Date();
		try {
			toDate = simpleDateFormat.parse(to);
		}
		catch (ParseException e) {
			e.printStackTrace();
		}
		this.setTo(toDate);
	}
	public void setTo(Date to) { this.to = to; }

	public Date getFrom() { return this.from; }
	public Date getTo() { return this.to; }
	public String getResponse() { return this.response; }

	public void setUrl(String url) {
		this.url = url;
	}
	public void setPayload(String payload) {
		this.payload = payload;
	}
	public String getPayload() {
		return this.payload;
	}

	public Date minusOneDay(Date date) {
		Date newDate = (Date) date.clone();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(newDate);
		calendar.add(Calendar.DAY_OF_MONTH, -1);
		return calendar.getTime();
	}

	public long getLastNightAtMidnightInMilliseconds() {
		Date lastMidnight = getLastNightAtMidnight();
		return lastMidnight.getTime();
	}

	private Date getLastNightAtMidnight() {
		GregorianCalendar now = new GregorianCalendar();
		now.setTimeZone(TimeZone.getTimeZone("America/New_York"));
		// reset hour, minutes, seconds and millis
		now.set(Calendar.HOUR_OF_DAY, 0);
		now.set(Calendar.MINUTE, 0);
		now.set(Calendar.SECOND, 0);
		now.set(Calendar.MILLISECOND, 0);

		// lastMidnight
		Calendar lastMidnight = (Calendar) now.clone();
		lastMidnight.add(Calendar.DAY_OF_MONTH, -1);
		return lastMidnight.getTime();
	}

	public String getApiToken() {
		if(apiToken != null) {
			return apiToken;
		}
		String apiTokenResponse = sendPostRequest(INTELLIFY_API_TOKEN_URL, INTELLIFY_API_TOKEN_PAYLOAD, null);
		JSONObject apiTokenJson = new JSONObject(apiTokenResponse);
		apiToken = apiTokenJson.getString("apiToken");
		return apiToken;
	}

	public String requestIntellifyEvents() {
		String authorizationToken = this.getApiToken();
		return this.sendPostRequest(this.url, this.payload, authorizationToken);
	}

	public String sendPostRequest() {
		return sendPostRequest(this.url, this.payload, this.apiToken);
	}

	public String sendPostRequest(String requestUrl, String payload, String authenticationToken) {
		this.payload = payload
				.replaceAll("FROM", this.getFrom().getTime() + "")
				.replaceAll("TO", this.getTo().getTime() + "");
		StringBuffer jsonString = new StringBuffer();
		try {
	        URL url = new URL(requestUrl);
	        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

	        connection.setDoInput(true);
	        connection.setDoOutput(true);
	        connection.setRequestMethod("POST");
	        connection.setRequestProperty("Accept", "*/*");
	        connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
	        if(authenticationToken != null) {
	        		connection.setRequestProperty("Authorization", "Bearer " + authenticationToken);
	        }
	        OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
	        writer.write(this.payload);
	        writer.close();
	        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	        String line;
	        while ((line = br.readLine()) != null) {
                jsonString.append(line);
	        }
	        br.close();
	        connection.disconnect();
	    } catch (Exception e) {
	    		System.out.println(authenticationToken);
	        e.printStackTrace();
	    }
		this.response = jsonString.toString();
	    return response;
	}
	
	public String sendGetRequest(String requestUrl, String payload, String authenticationToken) {
		StringBuffer jsonString = new StringBuffer();
		try {
	        URL url = new URL(requestUrl);
	        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

	        connection.setDoInput(true);
	        connection.setDoOutput(true);
	        connection.setRequestMethod("GET");
	        connection.setRequestProperty("Accept", "*/*");
	        connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
	        if(authenticationToken != null) {
	        		connection.setRequestProperty("Authorization", "Bearer " + authenticationToken);
	        }
	        OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
	        writer.write(payload);
	        writer.close();
	        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	        String line;
	        while ((line = br.readLine()) != null) {
                jsonString.append(line);
	        }
	        br.close();
	        connection.disconnect();
	    } catch (Exception e) {
	    		System.out.println(authenticationToken);
	        e.printStackTrace();
	    }
		this.response = jsonString.toString();
	    return response;
	}
}
