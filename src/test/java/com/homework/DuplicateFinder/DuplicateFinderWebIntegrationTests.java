package com.homework.DuplicateFinder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.homework.DuplicateFinder.model.Product;

import junit.framework.TestCase;

/*
 * Test cases are designed to test the REST API after hosting.
 * All the cases are created in the form of input json and validations are performed.
 * To be invoked after initializing the application.
 */
public class DuplicateFinderWebIntegrationTests extends TestCase {

// can be retrieved from a properties file
	final static String app_url = "http://localhost:8080/DuplicateFinderService";
	private HttpPost request = null;

	//helper method get open the http client post connection
	public HttpPost getPostConnectionInstance() {

		HttpPost request = new HttpPost(app_url);

		request.setHeader("Content-Type", "application/json");
		request.setHeader("Accept", "application/json");
		return request;
	}

	//Basic Sanity - Service availability check
	@Test
	public void testServicePathExists() throws ClientProtocolException, IOException {

		request = getPostConnectionInstance();
		String json = "[]";
		request.setEntity(new StringEntity(json));

		HttpResponse response = HttpClientBuilder.create().build().execute(request);

		assertEquals(response.getStatusLine().getStatusCode(), HttpStatus.OK.value());
	}
	
	//test for media type consumes
	@Test
	public void testConsumesMediaType() throws Exception {
		request = getPostConnectionInstance();
		request.setHeader("Content-Type", "text/plain");
		String json = "[]";
		request.setEntity(new StringEntity(json));
		HttpResponse response = HttpClientBuilder.create().build().execute(request);
		assertEquals(response.getStatusLine().getStatusCode(), HttpStatus.UNSUPPORTED_MEDIA_TYPE.value());
	}
	
	//test for media type produces
	@Test
	public void testResponseMediaType() throws Exception {
		String mimeType = "application/json";
		ObjectMapper objectMapper = new ObjectMapper();
		request = getPostConnectionInstance();
		List<Product> products = new ArrayList<Product>();
		products.add(new Product(1, "a10"));
		products.add(new Product(2, "a10"));

		String jsonString = objectMapper.writeValueAsString(products);
		request.setEntity(new StringEntity(jsonString));

		HttpResponse response = HttpClientBuilder.create().build().execute(request);
		assertEquals(mimeType, ContentType.getOrDefault(response.getEntity()).getMimeType());
	}

	//test for bad or corrupted json
	@Test
	public void testBadJson() throws Exception {

		request = getPostConnectionInstance();
		String json = "[improper json]";
		request.setEntity(new StringEntity(json));

		HttpResponse response = HttpClientBuilder.create().build().execute(request);

		assertEquals(response.getStatusLine().getStatusCode(), HttpStatus.BAD_REQUEST.value());
	}

	//basic positive case
	@Test
	public void testBasicCaseDuplicatesExpected() throws Exception {

		ObjectMapper objectMapper = new ObjectMapper();

		List<Product> products = new ArrayList<Product>();
		products.add(new Product(1, "a10"));
		products.add(new Product(2, "a10"));
		products.add(new Product(3, "a11"));
		products.add(new Product(4, "a12"));

		String jsonString = objectMapper.writeValueAsString(products);
		request = getPostConnectionInstance();
		request.setEntity(new StringEntity(jsonString));

		HttpResponse response = HttpClientBuilder.create().build().execute(request);
		String responseString = EntityUtils.toString(response.getEntity());

		assertEquals(response.getStatusLine().getStatusCode(), HttpStatus.OK.value());
		JSONAssert.assertEquals("[{\"productId\":1,\"skuId\":\"a10\"},{\"productId\":2,\"skuId\":\"a10\"}]",
				responseString, false);
	}

	//test with corrupted keys in json
	@Test
	public void testBadJsonCorruptedFieldNames() throws Exception {

		request = getPostConnectionInstance();
		String json = "[{\"productdfeId\":1,\"skuId\":\"a10\"}, {\"productId\":2,\"skuId\":\"a10\"}]";
		request.setEntity(new StringEntity(json));

		HttpResponse response = HttpClientBuilder.create().build().execute(request);
		String responseString = EntityUtils.toString(response.getEntity());

		assertEquals(response.getStatusLine().getStatusCode(), HttpStatus.OK.value());
		JSONAssert.assertEquals("[]", responseString, false);
	}
	
	//mix of corrupted keys and proper values which yield valid result
	@Test
	public void testBadJsonSomeCorruptedFieldNamesDuplicatesInRest() throws Exception {

		request = getPostConnectionInstance();
		String json = "[{\"productdfeId\":1,\"skuId\":\"a10\"}, {\"productId\":2,\"skuId\":\"a10\"}, {\"productId\":3,\"skuId\":\"a10\"}]";
		request.setEntity(new StringEntity(json));

		HttpResponse response = HttpClientBuilder.create().build().execute(request);
		String responseString = EntityUtils.toString(response.getEntity());

		assertEquals(response.getStatusLine().getStatusCode(), HttpStatus.OK.value());
		JSONAssert.assertEquals("[{\"productId\":2,\"skuId\":\"a10\"}, {\"productId\":3,\"skuId\":\"a10\"}]",
				responseString, false);
	}

	//test case - first and last are the only candidates to appear in the result
	@Test
	public void testPostitiveFirstAndLastHasDuplicates() throws Exception {

		request = getPostConnectionInstance();
		String json = "[{\"productId\":1,\"skuId\":\"a10\"}, {\"productId\":2,\"skuId\":\"a11\"}, {\"productId\":3,\"skuId\":\"a12\"}, {\"productId\":4, \"skuId\":\"a13\"}, {\"productId\":5,\"skuId\":\"a15\"}, {\"productId\":6,\"skuId\":\"a10\"}]";
		request.setEntity(new StringEntity(json));

		HttpResponse response = HttpClientBuilder.create().build().execute(request);
		String responseString = EntityUtils.toString(response.getEntity());

		assertEquals(response.getStatusLine().getStatusCode(), HttpStatus.OK.value());
		JSONAssert.assertEquals("[{\"productId\":1,\"skuId\":\"a10\"}, {\"productId\":6,\"skuId\":\"a10\"}]",
				responseString, false);
	}

	//all duplicates
	@Test
	public void testPostitiveAllDuplicates() throws Exception {

		request = getPostConnectionInstance();
		String json = "[{\"productId\":1,\"skuId\":\"a10\"}, {\"productId\":2,\"skuId\":\"a10\"}, {\"productId\":3,\"skuId\":\"a10\"}, {\"productId\":4, \"skuId\":\"a10\"}, {\"productId\":5,\"skuId\":\"a10\"}, {\"productId\":6,\"skuId\":\"a10\"}]";
		request.setEntity(new StringEntity(json));

		HttpResponse response = HttpClientBuilder.create().build().execute(request);
		String responseString = EntityUtils.toString(response.getEntity());

		assertEquals(response.getStatusLine().getStatusCode(), HttpStatus.OK.value());
		JSONAssert.assertEquals(
				"[{\"productId\":1,\"skuId\":\"a10\"}, {\"productId\":2,\"skuId\":\"a10\"}, {\"productId\":3,\"skuId\":\"a10\"}, {\"productId\":4, \"skuId\":\"a10\"}, {\"productId\":5,\"skuId\":\"a10\"}, {\"productId\":6,\"skuId\":\"a10\"}]",
				responseString, false);
	}

	@Test
	public void testPostitiveManyDuplicates() throws Exception {
		request = getPostConnectionInstance();
		String json = "[{\"productId\":1,\"skuId\":\"a10\"}, {\"productId\":2,\"skuId\":\"a10\"}, {\"productId\":3,\"skuId\":\"a11\"}, {\"productId\":4, \"skuId\":\"a12\"}, {\"productId\":5,\"skuId\":\"a12\"}, {\"productId\":6,\"skuId\":\"a13\"}]";
		request.setEntity(new StringEntity(json));

		HttpResponse response = HttpClientBuilder.create().build().execute(request);
		String responseString = EntityUtils.toString(response.getEntity());

		assertEquals(response.getStatusLine().getStatusCode(), HttpStatus.OK.value());
		JSONAssert.assertEquals(
				"[{\"productId\":1,\"skuId\":\"a10\"}, {\"productId\":2,\"skuId\":\"a10\"}, {\"productId\":4, \"skuId\":\"a12\"}, {\"productId\":5,\"skuId\":\"a12\"}]",
				responseString, false);
	}

	//null or empty sids in the json
	@Test
	public void testNullOrEmptySids() throws Exception {
		request = getPostConnectionInstance();
		String json = "[{\"productId\":1,\"skuId\":\"\"}, {\"productId\":2,\"skuId\":\"a10\"}, {\"productId\":3,\"skuId\":\"\"}]";

		request.setEntity(new StringEntity(json));

		HttpResponse response = HttpClientBuilder.create().build().execute(request);
		String responseString = EntityUtils.toString(response.getEntity());

		assertEquals(response.getStatusLine().getStatusCode(), HttpStatus.OK.value());
		JSONAssert.assertEquals("[]", responseString, false);
	}

	@Test
	public void testNullOrEmptySidsSomeValidDuplicates() throws Exception {
		request = getPostConnectionInstance();
		String json = "[{\"productId\":1,\"skuId\":\"\"}, {\"productId\":2,\"skuId\":\"a10\"}, {\"productId\":3,\"skuId\":\"a10\"}]";

		request.setEntity(new StringEntity(json));

		HttpResponse response = HttpClientBuilder.create().build().execute(request);
		String responseString = EntityUtils.toString(response.getEntity());

		assertEquals(response.getStatusLine().getStatusCode(), HttpStatus.OK.value());
		JSONAssert.assertEquals("[{\"productId\":2,\"skuId\":\"a10\"}, {\"productId\":3,\"skuId\":\"a10\"}]",
				responseString, false);
	}

	//large payload
	@Test
	public void testLargeJSON1000NoDuplicates() throws Exception {
		request = getPostConnectionInstance();

		StringBuilder sbr = new StringBuilder();
		sbr.append("[");
		for (int i = 0; i < 10000; i++) {
			if (i > 0)
				sbr.append(",");
			sbr.append("{");
			sbr.append("\"productId\":");
			sbr.append(i);
			sbr.append(",");
			sbr.append("\"skuId\":");
			sbr.append("\"");
			sbr.append("a");
			sbr.append(i);
			sbr.append("\"");
			sbr.append("}");
		}
		sbr.append("]");

		request.setEntity(new StringEntity(sbr.toString()));
		HttpResponse response = HttpClientBuilder.create().build().execute(request);
		String responseString = EntityUtils.toString(response.getEntity());

		assertEquals(response.getStatusLine().getStatusCode(), HttpStatus.OK.value());
		JSONAssert.assertEquals("[]", responseString, false);
	}
}
