/*
 * Created with [y] YaaS Service SDK version 4.12.1.
 */
package com.sample.wishlistDemo;

import com.sap.cloud.yaas.servicesdk.jerseysupport.features.JsonFeature;
import com.sap.cloud.yaas.servicesdk.jerseysupport.features.JerseyFeature;
import com.sap.cloud.yaas.servicesdk.jerseysupport.features.SecurityFeature;
import com.sap.cloud.yaas.servicesdk.jerseysupport.features.BeanValidationFeature;
import com.sap.cloud.yaas.servicesdk.jerseysupport.logging.RequestResponseLoggingFilter;

import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.LoggerFactory;

import com.sample.wishlistDemo.api.generated.ApiFeature;

/**
 * Defines the REST application.
 */
public class JerseyApplication extends ResourceConfig
{
	private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(JerseyApplication.class);
	
	/**
	 * Initialized the jersey application.
	 */
	public JerseyApplication()
	{
		// enable error responses in JSON format
		register(JerseyFeature.class);

		// enable JSON support
		register(JsonFeature.class);

		// enable custom resources
		register(ApiFeature.class);

		// hybris-scopes support for @RolesAllowed
		register(SecurityFeature.class);

		// bean validation support
		register(BeanValidationFeature.class);
		
		// log incoming requests
		register(new RequestResponseLoggingFilter(LOG));
	}
}
