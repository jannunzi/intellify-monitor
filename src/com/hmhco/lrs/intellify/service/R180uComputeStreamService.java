package com.hmhco.lrs.intellify.service;

import com.hmhco.lrs.intellify.model.IntellifyEvent;
import com.hmhco.lrs.intellify.model.R180uPerformancePerDayEvent;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class R180uComputeStreamService extends BaseIntellifyService {

	protected static String CURRENT_R180U_COMPUTE_STREAM  = "https://hmh2.intellifylearning.com/intellisearch/xxx-test-r180u-synthetic-a5-b3-aggregate-with-student-info/_search";
	protected static String PAYLOAD = "{\"query\":{ \"filtered\":{ \"filter\":{ \"bool\":{ \"must\":[ { \"range\":{ \"timestamp\":{ \"from\":\"FROM\", \"to\":\"TO\" } } }, { \"term\": { \"triggerType\": \"R180U_Performance\" } } ] } } } }, \"highlight\":{ \"fields\":{ }, \"fragment_size\":2147483647, \"pre_tags\":[ \"@start-highlight@\" ], \"post_tags\":[ \"@end-highlight@\" ] }, \"size\":0, \"aggs\": { \"R180U_COMPUTE_PERF_events_per_day\" : { \"date_histogram\" : { \"field\" : \"timestamp\", \"interval\" : \"day\" } } }, \"sort\":[ { \"timestampISO\":{ \"order\":\"asc\" } } ] }";

	public R180uComputeStreamService() {
		super(R180uComputeStreamService.CURRENT_R180U_COMPUTE_STREAM,
				R180uComputeStreamService.PAYLOAD);
	}
	
	public R180uComputeStreamService(String url, String payload) {
		super(url, payload);
	}

	public R180uComputeStreamService(Date from, Date to) {
		this(R180uComputeStreamService.CURRENT_R180U_COMPUTE_STREAM,
				R180uComputeStreamService.PAYLOAD, from, to);
	}

	public R180uComputeStreamService(String url, String payload, Date from, Date to) {
		super(url, payload, from, to);
	}

	public R180uComputeStreamService(String url, Date from, Date to) {
		this(url, R180uComputeStreamService.PAYLOAD, from, to);
	}

	public ArrayList<IntellifyEvent> parseIntellifyEvents(String jsonEvents) {
		ArrayList<IntellifyEvent> events = new ArrayList<IntellifyEvent>();

		JSONObject jsonObj = new JSONObject(jsonEvents);
		JSONObject aggregations = jsonObj.getJSONObject("aggregations");
		JSONObject R180U_COMPUTE_PERF_events_per_day = aggregations.getJSONObject("R180U_COMPUTE_PERF_events_per_day");
		JSONArray buckets = R180U_COMPUTE_PERF_events_per_day.getJSONArray("buckets");
		int eventCount = buckets.length();
		for(int i = 0; i < eventCount; i++) {
			JSONObject eventJson = buckets.getJSONObject(i);
			R180uPerformancePerDayEvent event = new R180uPerformancePerDayEvent(eventJson);
			events.add(event);
		}

		return events;
	}

	public static ArrayList<IntellifyEvent> compareEvents(ArrayList<IntellifyEvent> list1, ArrayList<IntellifyEvent> list2) {
		ArrayList<IntellifyEvent> missingEvents = new ArrayList<IntellifyEvent>();
		for(IntellifyEvent event1 : list1) {
			boolean found = false;
			for(IntellifyEvent event2 : list2) {
				if(event1.equals(event2)) {
					found = true;
					break;
				}
			}
			if(!found) {
				missingEvents.add(event1);
			}
		}
		return missingEvents;
	}

	public static void main(String[] args) throws ParseException
	{
		String fromString = "10/22/2017 08:00:00";
		String toString   = "10/30/2017 20:00:00";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		
		Date from = simpleDateFormat.parse(fromString);
		Date to   = simpleDateFormat.parse(toString);
		
		String currentR180uComputeStream  = "https://hmh2.intellifylearning.com/intellisearch/xxx-test-r180u-synthetic-a5-b3-aggregate-with-student-info/_search";
//		String newR180uComputeStreamAlias = "https://hmh2.intellifylearning.com/intellisearch/data-r180u-performance/_search";
		String newR180uComputeStream      = "https://hmh2.intellifylearning.com/intellisearch/data-r180u-performance-v19/_search";
		
		R180uComputeStreamService r180uComputeStreamService = new R180uComputeStreamService(currentR180uComputeStream, from, to);
		String currentEventsJson = r180uComputeStreamService.requestIntellifyEvents();
		System.out.println(currentEventsJson);
		ArrayList<IntellifyEvent> currentEvents = r180uComputeStreamService
				.parseIntellifyEvents(currentEventsJson);
		System.out.println();
		r180uComputeStreamService.printInfo();
		System.out.println(currentEvents);
		System.out.println(currentEvents.size());

		r180uComputeStreamService.setUrl(newR180uComputeStream);
		String newEventsJson = r180uComputeStreamService.requestIntellifyEvents();
//		System.out.println(newEventsJson);
		ArrayList<IntellifyEvent> newEvents = r180uComputeStreamService
				.parseIntellifyEvents(newEventsJson);
		System.out.println();
		r180uComputeStreamService.printInfo();
		System.out.println(newEvents);
		System.out.println(newEvents.size());

		System.out.println();
		System.out.println("Missing Events:");
		ArrayList<IntellifyEvent> missingEvents =
				R180uComputeStreamService.compareEvents(currentEvents, newEvents);
		System.out.println(missingEvents);
	}

	public void printInfo() {
		System.out.println("URL: " + this.url);
		System.out.println("Auth Token: " + this.apiToken);
		System.out.println("From:\n" + this.from);		
		System.out.println("To:\n" + this.to);		
		System.out.println("Payload:\n" + this.payload);		
		System.out.println("Response:\n" + this.response);		
	}

}
