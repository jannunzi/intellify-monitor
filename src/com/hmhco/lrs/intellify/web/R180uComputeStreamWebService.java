package com.hmhco.lrs.intellify.web;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hmhco.lrs.intellify.model.IntellifyEvent;
import com.hmhco.lrs.intellify.service.R180uComputeStreamService;

@WebServlet("/api/lrs/intellify/r180u/performance")
public class R180uComputeStreamWebService extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public R180uComputeStreamWebService() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		response.setContentType("application/json");
		ObjectMapper mapper = new ObjectMapper();

		String fromString = "10/22/2017 08:00:00";
		String toString   = "10/30/2017 20:00:00";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		
		Date from = new Date(), to = new Date();
		try {
			from = simpleDateFormat.parse(fromString);
			to   = simpleDateFormat.parse(toString);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
