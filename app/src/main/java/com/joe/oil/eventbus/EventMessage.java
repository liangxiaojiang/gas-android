package com.joe.oil.eventbus;

import java.util.EventObject;

public class EventMessage extends EventObject 
{
	private static final long serialVersionUID = 6255664332581555248L;
	private Object  mSource;
	private String mEventId = "";
	private Object  mParameters;
	
	public EventMessage(Object source, String eventid, Object params) 
	{
		super(source);
		this.mSource = source;
		this.mEventId = eventid;
		this.mParameters = params;
		// TODO Auto-generated constructor stub
	}
	public Object getSource()
	{
		return mSource;
	}
	public String getEventId()
	{
		return mEventId;
	}
	public Object getParameters()
	{
		return mParameters;
	}
}
