package org.takit.dns.services;

import org.takit.dns.Messages;
import org.takit.dns.TakitDNS;

public class NoIp implements Runnable {
	private String storedIP;
	private TakitDNS plugin;
	private String username;
	private String password;
	private String domain;
	
	public NoIp(TakitDNS instance, String username, String password, String domain) {
		this.plugin = instance;
		this.username = username;
		this.password = password;
		this.domain = domain;
		this.storedIP = "";
	}
	
	public void run() {
		String currentIP = TakitDNS.getIP();
		if ( !storedIP.equals(currentIP) ) {
			String url = "http://" + username + ":" + password + 
							"@dynupdate.no-ip.com/nic/update?" + 
							"hostname=" + domain + 
							"&myip=" + currentIP;
			
			String retPage = TakitDNS.getURL(url);
			if ( retPage.contains(new StringBuffer("good")) ) {
				storedIP = currentIP;
	
				TakitDNS.log.info(String.format(
	    			Messages.IP_CHANGED, 
	    			plugin.getDescription().getName(),
	    			currentIP
	    		));
			}
		}
	}
}
