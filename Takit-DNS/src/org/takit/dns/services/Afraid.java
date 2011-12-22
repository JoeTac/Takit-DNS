package org.takit.dns.services;

import org.takit.dns.Messages;
import org.takit.dns.Security;
import org.takit.dns.TakitDNS;

public class Afraid implements Runnable {
	private TakitDNS plugin;
	private String username;
	private String password;
	private String domain;
	
	public Afraid(TakitDNS instance, String username, String password, String domain) {
		this.plugin = instance;
		this.username = username;
		this.password = password;
		this.domain = domain;
	}
	public void run() {
		String currentIP = TakitDNS.getIP();
		String file = TakitDNS.getURL(
				"http://freedns.afraid.org/api/?action=getdyndns&sha=" + 
				Security.SHA1(username.toLowerCase()+"|"+password)
		);
		if ( file==null ) {
			return;
		}
		String[] entries = file.split("\n");
		String[] entry = null;
		for ( int i=0; i<entries.length; ++i ) {
			entry = entries[i].split("\\|");
			if ( domain.equals(entry[0]) ) {
				break;
			}
			else {
				entry = null;
			}
		}
		if ( entry==null ) {
			TakitDNS.log.info(String.format(
				Messages.DOMAIN_NOT_FOUND,
				plugin.getDescription().getName(),
				domain
			));
			return;
		}
		
		if ( !currentIP.equals(entry[1]) ) {
    		TakitDNS.getURL(entry[2]);
    		TakitDNS.log.info(String.format(
    			Messages.IP_CHANGED, 
    			plugin.getDescription().getName(),
    			currentIP
    		));
		}
	}
}
