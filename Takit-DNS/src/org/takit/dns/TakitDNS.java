package org.takit.dns;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.logging.Logger;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class TakitDNS extends JavaPlugin {
	public static final String FREEDNS_AFRAID_ORG = "freedns.afraid.org";
	
	public static Logger log = Logger.getLogger("Minecraft");
	
	private static String username = "";
	private static String password = "";
	private static String domain = "";
	private static long interval = 0L;
	private static String host = "";
	
	public void onDisable() {
		log.info(String.format(Messages.PLUGIN_DISABLE, getDescription().getName()));
	}
	public void onEnable() {
		initConfig();
		update();
		
		this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
			public void run() {
				TakitDNS.update();
		    }
		}, (interval*20)*60);
		
		log.info(String.format(
				Messages.PLUGIN_ENABLE, 
				getDescription().getName()
		));
	}
	
	public static void update() {
		String currentIP = getIP();
		
    	if ( host.equals(FREEDNS_AFRAID_ORG) ) {
    		String file = getURL(
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
    			log.info(String.format(
    				Messages.DOMAIN_NOT_FOUND,
    				domain
    			));
    			return;
    		}
    		
    		if ( !currentIP.equals(entry[1]) ) {
	    		getURL(entry[2]);
	    		log.info(String.format(Messages.IP_CHANGED, currentIP));
    		}
    	}
	}
	
	
	public void initConfig() {
		File file = new File("plugins"+File.separator+"Takit"+File.separator+"dns.yml");
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		
		if ( !file.exists() ) {
			config.set("dns.domain", "your-domain");
			config.set("dns.username", "username");
			config.set("dns.password", "password");
			config.set("dns.interval", 10);
			try {
				config.save(file);
				config.load(file);
			}
			catch ( Exception e ) {
				e.printStackTrace();
			}
		}
		
		domain = config.getString("dns.domain", "freedns.afraid.org");
		username = config.getString("dns.username");
		password = config.getString("dns.password");
		interval = config.getInt("dns.interval");
	}
	
	public static String getIP() {
		String ret = getURL("http://checkip.dyndns.com/");
		if ( ret!=null ) {
			ret = ret.substring(ret.indexOf("Current IP Address: ")+20, ret.indexOf("</body>"));
		}
		return ret;
	}
	public static String getURL(String url) {
		String fileContents = null;
		try {
			StringBuffer sb = new StringBuffer();
			String line = "";
			URL u = new URL(url);
			InputStream is = u.openStream();
			BufferedReader dr = new BufferedReader(new InputStreamReader(is));
			
			line=dr.readLine();
			while ( line!=null) {
				sb.append(line).append("\n");
				line=dr.readLine();
			}
			fileContents = sb.toString();
			dr.close();
			is.close();
		}
		catch ( Exception ignore ) { }
		
		return fileContents;
	}
}
