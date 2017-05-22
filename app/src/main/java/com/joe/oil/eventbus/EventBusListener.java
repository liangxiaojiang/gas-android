package com.joe.oil.eventbus;
import java.util.EventListener;

public interface EventBusListener extends EventListener {
	public void onReceive(String eventid, Object parameters);
}