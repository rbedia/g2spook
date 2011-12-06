package org.trillinux.geoip;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;

import com.maxmind.geoip.Country;
import com.maxmind.geoip.LookupService;

public class GeoIP {
	private static LookupService cl = null;
	
	private GeoIP() {
	}
	
	private static synchronized void init() {
		if (cl == null) {
			URL resource = LookupService.class.getClassLoader().getResource("com/maxmind/geoip/GeoIP.dat");
			// Strip off the "file:" prefix
		    String dbfile = resource.toExternalForm().substring(5);
	 
		    try {
				cl = new LookupService(dbfile,LookupService.GEOIP_MEMORY_CACHE);
			} catch (IOException e) {
				System.out.println("Problem initializing GeoIP service.");
				e.printStackTrace();
			}
		}
	}
	
	public static String getCode(String address) {
		init();
		Country country = cl.getCountry(address);
		if (country != null) {
			return country.getCode();
		}
		return "";
	}

	public static String getCode(InetAddress address) {
		init();
		Country country = cl.getCountry(address);
		if (country != null) {
			return country.getCode();
		}
		return "";
	}
}
