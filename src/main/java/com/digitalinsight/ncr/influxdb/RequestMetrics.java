package com.digitalinsight.ncr.influxdb;

public interface RequestMetrics {

	String MEASUREMENT_NAME = "requestsMetrics";
	
	public interface Tags {
			
		String REQUEST_NAME = "requestName";
		
	}
	
	public interface Fields {

		String RESPONSE_TIME = "respTime";
		String ERROR_COUNT = "errCount";
		String THREAD_NAME = "threadName";
		String TEST_TYPE = "testType";
		String PROJECT_NAME = "projectName";
		String MAX_ACTIVE_USERS = "maxActiveThreads";
	}

}
