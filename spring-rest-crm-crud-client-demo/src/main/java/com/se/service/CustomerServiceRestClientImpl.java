package com.se.service;

import java.nio.charset.Charset;
import org.apache.commons.codec.binary.Base64;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.se.model.Customer;
@Service
public class CustomerServiceRestClientImpl implements CustomerService {
    private RestTemplate restTemplate;
    private String crmRestUrl;
    private Logger logger = Logger.getLogger(getClass().getName());
    @Autowired
    public CustomerServiceRestClientImpl(RestTemplate theRestTemplate, 
        @Value("${crm.rest.url}") String theUrl) {
        restTemplate = theRestTemplate;
        crmRestUrl = theUrl;
        logger.info("Loaded property:  crm.rest.url=" + crmRestUrl);}
   
    @Override
    public List<Customer> getCustomers() {
        logger.info("in getCustomers(): Calling REST API " + crmRestUrl);
        HttpHeaders headers = new HttpHeaders();
        // make REST call
        final String USER_NAME = "john";
		final String PASSWORD = "test123";
		String auth = USER_NAME + ":" + PASSWORD;
		byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(Charset.forName("US-ASCII")));
		String authHeader = "Basic " + new String(encodedAuth);
		headers.set("Authorization", authHeader);
		// 
		headers.setAccept(Arrays.asList(new MediaType[] { MediaType.APPLICATION_JSON }));
		// Yêu cầu trả về định dạng JSON
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("my_other_key", "my_other_value");

		// HttpEntity<String>: To get result as String.
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		
		
        ResponseEntity<List<Customer>> responseEntity = 
        		restTemplate.exchange(crmRestUrl, HttpMethod.GET, entity  , 
        	            new ParameterizedTypeReference<List<Customer>>() {});
            /*restTemplate.exchange(crmRestUrl, HttpMethod.GET, null, 
            new ParameterizedTypeReference<List<Customer>>() {}); */
            // get the list of customers from response
            List<Customer> customers = responseEntity.getBody();
            logger.info("in getCustomers(): customers" + customers);
            return customers;
	}
    @Override
    public Customer getCustomer(int theId) {
        logger.info("in getCustomer(): Calling REST API " + crmRestUrl);
        // make REST call
        Customer theCustomer = 
        restTemplate.getForObject(crmRestUrl + "/" + theId, 
                                  Customer.class);
        logger.info("in saveCustomer(): theCustomer=" + theCustomer);
        return theCustomer;
    }
    @Override
    public void saveCustomer(Customer theCustomer) {
        logger.info("in saveCustomer(): Calling REST API " + crmRestUrl);
        int customerId = theCustomer.getId();
        // make REST call
        if (customerId == 0) {
            // add employee
            restTemplate.postForEntity(crmRestUrl, theCustomer, String.class);			
        } else {
            // update employee
            restTemplate.put(crmRestUrl, theCustomer);}
            logger.info("in saveCustomer(): success");	
    }
    @Override
    public void deleteCustomer(int theId) {
        logger.info("in deleteCustomer(): Calling REST API " + crmRestUrl);
        // make REST call
        restTemplate.delete(crmRestUrl + "/" + theId);
        logger.info("in deleteCustomer(): deleted customer theId=" + theId);
    }
}
