package org.zkoss.fiddler.executor.server;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;

/**
 * A configuration object to handle the complicated parse job , and make thing
 * easier.
 */

public class Configs {

	private static boolean _logMode = Boolean.getBoolean("debugmsg");

	private String context;

	private String webAppDir;

	private Integer port;

	private String[] webAppClasslibPaths;


	private Boolean parentLoaderPriority;


	private int pingRemoteInterval ;

	private String remoteResourceHost;
	
	private String localHostName;
	
	private String zkversion;
	
	private String instanceName;

	public Configs() {
		
		context = System.getProperty("context", "/test");
		
		webAppDir = System.getProperty("webapp");
		if (webAppDir != null) {
			if (webAppDir.matches("^\".*?\"$")) {
				webAppDir = webAppDir.substring(1, webAppDir.length() - 1);
			}
		}
		Integer mport = Integer.getInteger("port", -1);
		boolean autoport = Boolean.getBoolean("autoport");

		if (mport == -1) {
			if (autoport) {
				port = findAAvaiablePort(10000, 20000, 100);
			} else {
				port = 10158;
			}
		} else {
			port = mport;
		}
		
		webAppClasslibPaths = System.getProperty("libpaths", "").split(";"); // resovleWebappClasspath();

		parentLoaderPriority = true;
		if (System.getProperty("parentloaderpriority") != null)
			parentLoaderPriority = Boolean.getBoolean("parentloaderpriority");

		remoteResourceHost = System.getProperty("remoteResourceHost", "");
		
		pingRemoteInterval = Integer.getInteger("pingRemoteInterval",1000 * 60 * 30 );
		
		localHostName = System.getProperty("localHostName", "");
		
		zkversion = System.getProperty("zkversion", "");
		
		instanceName = System.getProperty("instName", ""); 
		
		instanceName = instanceName.replaceAll("\\#\\{port\\}", ""+port).replaceAll("\\#\\{ver\\}",""+zkversion);
		
	}

	private int findAAvaiablePort(int start, int end, int retry) {

		int range = end - start + 1;
		int port = -1;

		for (int i = 0; i < retry || retry == -1; ++i) {
			port = start + (int) (Math.random() * range);
			if (available(port))
				return port;
		}

		throw new IllegalStateException("no available port");
	}

	public String getContext() {
		return context;
	}

	public String getWebAppDir() {
		return webAppDir;
	}

	public Integer getPort() {
		return port;
	}


	public String[] getWebAppClasslibPaths() {
		return webAppClasslibPaths;
	}


	public Boolean getParentLoaderPriority() {
		return parentLoaderPriority;
	}

	public void validation() {
		if (getContext() == null) {
			throw new IllegalStateException("you need to provide argument -Dcontext");
		}
		if (getPort() == null ) {
			throw new IllegalStateException("you need to provide argument -Dport");
		}
		
		if (!available(port)) {
			throw new IllegalStateException("port :" + port + " already in use!");
		}
		
		if(remoteResourceHost == null || "".equals(remoteResourceHost.trim())  ){
			throw new IllegalStateException("remote host path is empty!");
		}
		
		if(localHostName == null ||"".equals(localHostName.trim())){
			throw new IllegalStateException("local host name is empty!");
		}
		
	}

	private static boolean available(int port) {
		if (port <= 0) {
			throw new IllegalArgumentException("Invalid start port: " + port);
		}

		ServerSocket ss = null;
		DatagramSocket ds = null;
		try {
			ss = new ServerSocket(port);
			ss.setReuseAddress(true);
			ds = new DatagramSocket(port);
			ds.setReuseAddress(true);
			return true;
		} catch (IOException e) {
		} finally {
			if (ds != null) {
				ds.close();
			}

			if (ss != null) {
				try {
					ss.close();
				} catch (IOException e) {
					/* should not be thrown */
				}
			}
		}

		return false;
	}

	public static boolean isLogMode() {
		return _logMode;
	}

	public int getPingRemoteInterval() {
		return pingRemoteInterval;
	}

	public void setPingRemoteInterval(int pingRemoteInterval) {
		this.pingRemoteInterval = pingRemoteInterval;
	}
	
	
	public String getFullLocalInstancePath(){
		String portString = (getPort() != 80 ? ":" + getPort() : "");
		String context = getContext();
		if(!context.endsWith("/")){
			context += "/";
		}
		
		return getLocalHostName() + portString + context;
	}

	public String getRemoteResourceHost() {
		return remoteResourceHost;
	}

	public void setRemoteResourceHost(String remoteResourceHost) {
		this.remoteResourceHost = remoteResourceHost;
	}

	
	/**
	 * you have to tell remote resource host where you are ,
	 * so they can access you from remote , you have to tell them at this case.
	 * 
	 * The base rule is hostname must be accessible for the end user.
	 * 
	 * @return
	 */
	public String getLocalHostName() {
		return localHostName;
	}
	
	public void setLocalHostName(String localHostName) {
		this.localHostName = localHostName;
	}

	
	public String getZkversion() {
		return zkversion;
	}

	public void setZkversion(String zkversion) {
		this.zkversion = zkversion;
	}

	
	public String getInstanceName() {
		return instanceName;
	}	
}