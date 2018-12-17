package com.digitalinsight.ncr.influxdb;

/*
 * Reference: http://www.oznetnerd.com/getting-know-influxdb/
 * Reference: https://docs.influxdata.com/influxdb/v1.7/introduction/getting-started/
 * 
 *
 */

import org.apache.commons.lang3.StringUtils;
import org.apache.jmeter.visualizers.backend.BackendListenerContext;

public class InfluxDBConfig {
	
	
	/*DEFAULT_PARAMS if not provided */
	
	public static final String DEFAULT_DB_URL = "http://localhost:8086";
	public static final String DEFAULT_DATABASE = "jmeter";
	public static final String DEFAULT_DB_USER = "admin";
	public static final String DEFAULT_DB_PASSWORD = "admin";
	public static final String DEFAULT_RETENTION_POLICY = "autogen";
	
	
	/* 
	 * User Defined Parameters to connect to Influx, this will override DEFAULT_PARAMS 
	 */

	public static final String KEY_INFLUX_DB_DATABASE = "influxDBDatabase";
	public static final String KEY_INFLUX_DB_URL = "influxDBURL";
	public static final String KEY_INFLUX_DB_USER = "influxDBUser";
	public static final String KEY_INFLUX_DB_PASSWORD = "influxDBPassword";
	public static final String KEY_RETENTION_POLICY = "retentionPolicy";
	
	/*
	 * Variables Initialized for getters and setters. 
	 */
	private String influxDatabase;
	private String influxDBURL;
	private String influxUser;
	private String influxPassword;
	private String influxRetentionPolicy;
	
	
	public InfluxDBConfig(BackendListenerContext context) {
		String influxDBURL = context.getParameter(KEY_INFLUX_DB_URL);
		//String influxDBHost = context.getParameter(KEY_INFLUX_DB_HOST);
		if (StringUtils.isEmpty(influxDBURL)) {
			throw new IllegalArgumentException(KEY_INFLUX_DB_URL + "InfluxDB URL Missing.. Connection parameter required !!");
		}
		setInfluxDBURL(influxDBURL);

		String influxUser = context.getParameter(KEY_INFLUX_DB_USER);
		setInfluxUser(influxUser);

		String influxPassword = context.getParameter(KEY_INFLUX_DB_PASSWORD);
		setInfluxPassword(influxPassword);

		String influxDatabase = context.getParameter(KEY_INFLUX_DB_DATABASE);
		if (StringUtils.isEmpty(influxDatabase)) {
			throw new IllegalArgumentException(KEY_INFLUX_DB_DATABASE + "must not be empty!");
		}
		setInfluxDatabase(influxDatabase);

		String influxRetentionPolicy = context.getParameter(KEY_RETENTION_POLICY, DEFAULT_RETENTION_POLICY);
		if (StringUtils.isEmpty(influxRetentionPolicy)) {
			influxRetentionPolicy = DEFAULT_RETENTION_POLICY;
		}
		setInfluxRetentionPolicy(influxRetentionPolicy);
	}	
	

	// Getters and setters to assign values to InfluxDB config 	
	
	public String getInfluxDBURL() {
		return influxDBURL;
	}
	
	public void setInfluxDBURL(String influxDBURL) {	
		this.influxDBURL=influxDBURL;
	}
	
	
	
	public String getInfluxUser() {
		return influxUser;
	}
	
	private void setInfluxUser(String influxDBUser) {
		this.influxUser=influxDBUser;
	}
	
	
	
	
	public String getInfluxPassword() {
		return influxPassword;
	}
	
	private void setInfluxPassword(String influxDBPassword) {
		this.influxPassword=influxDBPassword;		
	}
	
	
	
	public String getInfluxDBDatabase() {
		return influxDatabase;
	}	

	private void setInfluxDatabase(String influxDBDatabase) {
		this.influxDatabase=influxDBDatabase;
	}
	
	
    /*
     * Retention policy part of InfluxDB’s data structure that describes for how long InfluxDB keeps data (duration).
     * Refer: http://www.oznetnerd.com/influxdb-retention-policies-shard-groups/
     * Default will be autogen
     */
	
	public String getInfluxRetentionPolicy() {
		return influxRetentionPolicy;
	}
	
	private void setInfluxRetentionPolicy(String retentionPolicy) {
		this.influxRetentionPolicy=retentionPolicy;
	}
	
}
