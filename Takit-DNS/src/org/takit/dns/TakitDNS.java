package org.takit.dns;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.logging.Logger;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class TakitDNS extends JavaPlugin {
	public static Logger log = Logger.getLogger("Minecraft");
	
	//private String username = "";
	//private String password = "";
	private String urlCode = "";
	private String host = "";
	private long interval = 0L;
	private String lastIP = "";
	
	public void onDisable() {
		log.info(String.format(Messages.PLUGIN_DISABLE, getDescription().getName()));
	}
	public void onEnable() {
		initConfig();
		
		this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
			public void run() {
				String currentIP = "";
				try {
					currentIP = TakitDNS.getIP();
				}
				catch ( Exception ignore ) { }
				
				if ( lastIP.equals(currentIP) ) {
					return;
				}
				
				lastIP = currentIP;
				TakitDNS.log.info(String.format(Messages.IP_CHANGED, lastIP));
				
		    	if ( host.equals("freedns.afraid.org") ) {
		    		try {		
		    			URL url = new URL("http://freedns.afraid.org/dynamic/update.php?"+urlCode);
		    			InputStream is = url.openStream();
		    			BufferedReader dr = new BufferedReader(new InputStreamReader(is));
		    			
		    			while ( (dr.readLine())!=null)
		    				;
		    			
		    			dr.close();
		    			is.close();
		    		}
		    		catch (Exception e) {
						e.printStackTrace();
					}
		    	}
		    }
		}, (interval*20)*60);
		log.info(String.format(
				Messages.PLUGIN_ENABLE, 
				getDescription().getName(),
				lastIP
		));
	}
	
	public static String getIP() throws IOException  {
		StringBuffer sb = new StringBuffer();
		String document = null;
		String line = "";
		URL url = new URL("http://checkip.dyndns.com/");
		InputStream is = url.openStream();
		BufferedReader dr = new BufferedReader(new InputStreamReader(is));
		
		line=dr.readLine();
		while ( line!=null) {
			sb.append(line);
			line=dr.readLine();
		}
		document = sb.toString();
		dr.close();
		is.close();
		
		return document.substring(document.indexOf("Current IP Address: ")+20, document.indexOf("</body>"));
	}
	public void initConfig() {
		File file = new File("plugins"+File.separator+"Takit"+File.separator+"dns.yml");
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		
		if ( !file.exists() ) {
			config.set("dns.url-code", "url-code");
			config.set("dns.interval", 10);
			try {
				config.save(file);
				config.load(file);
			}
			catch ( Exception e ) {
				e.printStackTrace();
			}
		}
		
		host = config.getString("dns.host", "freedns.afraid.org");
		//username = config.getString("dns.username", "");
		//password = config.getString("dns.password", "");
		urlCode = config.getString("dns.url-code");
		interval = config.getInt("dns.interval");
		
		try {
			lastIP = getIP();
		}
		catch ( Exception e ) {
			e.printStackTrace();
		}
	}
}
