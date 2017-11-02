package com.hmhco.lrs.intellify.model;

import org.json.JSONObject;

public class R180uPerformancePerDayEvent extends IntellifyEvent {
	private Integer key;
	private Integer docCount;
	public R180uPerformancePerDayEvent()
	{
		super();
	}
	public R180uPerformancePerDayEvent(JSONObject json) {
		this.key = json.getInt("key");
		this.docCount = json.getInt("doc_count");
	}
	public R180uPerformancePerDayEvent(Integer key, Integer docCount)
	{
		super();
		this.key = key;
		this.docCount = docCount;
	}
	public Integer getKey()
	{
		return key;
	}
	public void setKey(Integer key)
	{
		this.key = key;
	}
	public Integer getDocCount()
	{
		return docCount;
	}
	public void setDocCount(Integer docCount)
	{
		this.docCount = docCount;
	}
	public String toString() {
		return key + ": " + docCount;
	}

	@Override
	public boolean equals(Object other) {
		R180uPerformancePerDayEvent otherEvent = (R180uPerformancePerDayEvent)other;
		return this.key.equals(otherEvent.getKey()) && this.docCount.equals(otherEvent.getDocCount());
	}
}
