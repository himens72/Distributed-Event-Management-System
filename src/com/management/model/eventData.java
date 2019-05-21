package com.management.model;

import java.util.ArrayList;
import java.util.HashMap;

public class eventData {
	HashMap<String, HashMap<String, ArrayList<String>>> serverData;
	String serverName;

	public HashMap<String, HashMap<String, ArrayList<String>>> getServerData() {
		return serverData;
	}

	public void setServerData(HashMap<String, HashMap<String, ArrayList<String>>> serverData) {
		this.serverData = serverData;
	}

	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public eventData() {
		// TODO Auto-generated constructor stub
		serverData = new HashMap<>();
		HashMap<String, ArrayList<String>> temp = new HashMap<>();
		serverData.put("Conferences", temp);
		serverData.put("Seminars", temp);
		serverData.put("Trade Shows", temp);
	}

}
