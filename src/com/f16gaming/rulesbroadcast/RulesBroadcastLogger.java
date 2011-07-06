package com.f16gaming.rulesbroadcast;

import java.util.logging.Logger;

public class RulesBroadcastLogger {
	private Logger log;
	private String prefix;
	
	public RulesBroadcastLogger(Logger log, String prefix) {
		this.log = log;
		this.prefix = prefix;
	}
	
	public void info(String message) {
		log.info(String.format("[%s] %s", prefix, message));
	}
	
	public void warning(String message) {
		log.warning(String.format("[%s] %s", prefix, message));
	}
	
	public void severe(String message) {
		log.severe(String.format("[%s] %s", prefix, message));
	}
}
