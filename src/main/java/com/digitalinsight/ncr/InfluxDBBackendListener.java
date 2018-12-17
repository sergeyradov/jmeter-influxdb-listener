package com.digitalinsight.ncr;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.visualizers.backend.AbstractBackendListenerClient;
import org.apache.jmeter.visualizers.backend.BackendListenerContext;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Point;
import org.slf4j.Logger;

import com.digitalinsight.ncr.influxdb.InfluxDBConfig;
import com.digitalinsight.ncr.influxdb.RequestMetrics;


public class InfluxDBBackendListener extends AbstractBackendListenerClient implements RequestMetrics{
	
	private final Logger LOGGER = getNewLogger();
	private static final String KEY_TEST_TYPE = "testType";
	private static final String KEY_PROJECT_NAME = "projectName";
	private static final String KEY_SAMPLERS_LIST = "samplersList";
	private static final String KEY_USE_REGEX_FOR_SAMPLER_LIST = "regexForSamplerList";
	private static final String DELIMITER = ";";
	private static final int ONE_MS_IN_NANOSECONDS = 1000000;
	InfluxDBConfig InfluxConfig;
	
	private InfluxDB influx;
	private String testType;
	private String projectName;
	private String samplersList="";
	private String regexForSamplerList;
	private Set<String> samplersToFilter;
	private Random randNumber;
	
	
	
	public void handleSampleResults(List<SampleResult> sampleRes, BackendListenerContext context) {
		
		for (SampleResult temp : sampleRes) {
			getUserMetrics().add(temp);

			if ((null != regexForSamplerList && temp.getSampleLabel().matches(regexForSamplerList)) || samplersToFilter.contains(temp.getSampleLabel())) {
				Point pnt = Point.measurement(RequestMetrics.MEASUREMENT_NAME).time(System.currentTimeMillis() * ONE_MS_IN_NANOSECONDS + getUniqueNumberForTheSamplerThread(), TimeUnit.NANOSECONDS)
						.tag(RequestMetrics.Tags.REQUEST_NAME, temp.getSampleLabel()).addField(RequestMetrics.Fields.ERROR_COUNT, temp.getErrorCount())
						.addField(RequestMetrics.Fields.THREAD_NAME, temp.getThreadName())
						.addField(RequestMetrics.Fields.TEST_TYPE, testType)
						.addField(RequestMetrics.Fields.PROJECT_NAME, projectName)
						.addField(RequestMetrics.Fields.RESPONSE_TIME, temp.getTime())
						.addField(RequestMetrics.Fields.MAX_ACTIVE_USERS, getUserMetrics().getMaxActiveThreads())
						.build();
				
				influx.write(InfluxConfig.getInfluxDBDatabase(), InfluxConfig.getInfluxRetentionPolicy(), pnt);
			}
		}		
		
	}


	@Override
	public Arguments getDefaultParameters() {
		
		Arguments arg = new Arguments();
		arg.addArgument(KEY_TEST_TYPE, "Baseline");
		arg.addArgument(KEY_PROJECT_NAME, "DI");
		arg.addArgument(InfluxDBConfig.KEY_INFLUX_DB_URL, InfluxDBConfig.DEFAULT_DB_URL);
		arg.addArgument(InfluxDBConfig.KEY_INFLUX_DB_USER, InfluxDBConfig.DEFAULT_DB_USER);
		arg.addArgument(InfluxDBConfig.KEY_INFLUX_DB_PASSWORD, InfluxDBConfig.DEFAULT_DB_PASSWORD);
		arg.addArgument(InfluxDBConfig.KEY_INFLUX_DB_DATABASE, InfluxDBConfig.DEFAULT_DATABASE);
		arg.addArgument(InfluxDBConfig.KEY_RETENTION_POLICY, InfluxDBConfig.DEFAULT_RETENTION_POLICY);
		arg.addArgument(KEY_SAMPLERS_LIST, ".*");
		arg.addArgument(KEY_USE_REGEX_FOR_SAMPLER_LIST, "true");
		
		return arg;	
	}
	

	@Override
	public void setupTest(BackendListenerContext context) throws Exception {
		
		testType = context.getParameter(KEY_TEST_TYPE, "Test");
		randNumber = new Random();
		projectName = context.getParameter(KEY_PROJECT_NAME, "DI");
		
		setupInfluxClient(context);
		parseSamplers(context);
		
	}


	private void setupInfluxClient(BackendListenerContext context) {
		
		InfluxConfig = new InfluxDBConfig(context);
		LOGGER.info("Influx URL: " + InfluxConfig.getInfluxDBURL().toString());
		
		try {
			
			influx = InfluxDBFactory.connect(InfluxConfig.getInfluxDBURL(), InfluxConfig.getInfluxUser(), InfluxConfig.getInfluxPassword());
			LOGGER.info("Connection to Influx is successful");
			influx.enableBatch(100, 5, TimeUnit.SECONDS);
			isDatabaseExist();
			
		} catch (Exception e) {
			LOGGER.info("Failed to connect to Influx");
		}
	}

	
	private void isDatabaseExist() {
		
		List<String> dbNames = influx.describeDatabases();
		
		if (dbNames.contains(InfluxConfig.getInfluxDBDatabase())) {
			LOGGER.info("Database already exist !!");
		  }else {
			  influx.createDatabase(InfluxConfig.getInfluxDBDatabase());
		  }
	}
	

	
	@Override
	public void teardownTest(BackendListenerContext context) throws Exception {
		LOGGER.info ("Powering down influxDB scheduler");
		influx.disableBatch();
		LOGGER.info("influxDB scheduler terminated!");
		samplersToFilter.clear();
		super.teardownTest(context);
	}
	
	
	
	private void parseSamplers(BackendListenerContext context) {
		samplersList = context.getParameter(KEY_SAMPLERS_LIST, "");
		LOGGER.info("Sampler list :" + samplersList);
		samplersToFilter = new HashSet<String>();
		if (context.getBooleanParameter(KEY_USE_REGEX_FOR_SAMPLER_LIST, false)) {
			regexForSamplerList = samplersList;
		} else {
			regexForSamplerList = null;
			String[] samplers = samplersList.split(DELIMITER);
			samplersToFilter = new HashSet<String>();
			for (String samplerName : samplers) {
				samplersToFilter.add(samplerName);
			}
		}
		
	}

	
	
	private int getUniqueNumberForTheSamplerThread() {
		return randNumber.nextInt(ONE_MS_IN_NANOSECONDS);
	}

	
}