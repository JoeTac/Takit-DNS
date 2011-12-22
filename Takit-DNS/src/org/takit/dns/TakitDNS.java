package org.takit.dns;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.takit.dns.services.Afraid;

public class TakitDNS extends JavaPlugin {
	public static final String FREEDNS_AFRAID_ORG = "freedns.afraid.org";
	
	public static Logger log = Logger.getLogger("Minecraft");
	
	private String username;
	private String password;
	private String domain;
	private long interval;
	private String host;
	
	public void onDisable() {
		log.info(String.format(Messages.PLUGIN_DISABLE, getDescription().getName()));
	}
	public void onEnable() {
		initConfig();
		
		if ( host.equals(FREEDNS_AFRAID_ORG) ) {
			this.getServer().getScheduler().scheduleAsyncRepeatingTask(
				this, 
				new Afraid(this, username, password, domain),
				(interval*20)*60,
				(interval*20)*60
			);
		}
		else {
			log.log(Level.WARNING, String.format(
				Messages.HOST_NOT_FOUND, 
				getDescription().getName(),
				host
			));
		}
		
		log.info(String.format(
				Messages.PLUGIN_ENABLE, 
				getDescription().getName()
		));
	}
	
	
	public void initConfig() {
		File file = new File("plugins"+File.separator+"Takit"+File.separator+"dns.yml");
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		
		if ( !file.exists() ) {
			config.set("dns.domain", "your-domain");
			config.set("dns.username", "username");
			config.set("dns.password", "password");
			config.set("dns.interval", 10);
			config.set("dns.host", FREEDNS_AFRAID_ORG);
			try {
				config.save(file);
				config.load(file);
			}
			catch ( Exception e ) {
				e.printStackTrace();
			}
		}
		
		domain = config.getString("dns.domain");
		username = config.getString("dns.username");
		password = config.getString("dns.password");
		interval = config.getInt("dns.interval");
		host = config.getString("dns.host");
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
