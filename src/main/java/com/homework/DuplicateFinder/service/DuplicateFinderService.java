package com.homework.DuplicateFinder.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.homework.DuplicateFinder.Exception.CustomError;
import com.homework.DuplicateFinder.model.Product;

@RestController
public class DuplicateFinderService {
	/*
	 * This REST API consumes JSON from post request and produces JSON.
	 * The JSON is expected to be array of Products with productId and skuId. It finds the Products with same skuIds and returns them.
	 * The methods uses jackson json to object mapping capabilities to un-marshall the incoming json to List of POJO's.
	 * Most of the exceptions related to parsing are handled in un-marshalling and are dealt with Http 400 and error json response.
	 * The algorithm is discussed in detail inside the method.
	 * Assumptions:
	 * No duplicate productIds.
	 * 
	 */
	@RequestMapping(value="/DuplicateFinderService", method=RequestMethod.POST, consumes= {"application/json"}, produces="application/json")
	public List<Product> findDuplicates(@RequestBody List<Product> productList) throws CustomError {
		
		try {
			
			//Edge cases
			if(productList == null)
				return new ArrayList<Product>();
			if(productList.size() == 0)
				return new ArrayList<Product>();
			
			/*
			 * Approach:
			 * Hash the products by their skuId: (groupBy operation does this in lazy fashion)
			 * Iterate over the values of hash and collect those which more than one product in the list. (filter, flatMap, collect, does this in lazy fashion):
			 * Time complexity - O(n) , in two pass.
			 * Space Complexity - O(n), due to hashing.
			 * the first filter is to avoid cases like productId not present (or corrupted fieldName) or empty (or corrupted) skuIds in the request data.
			 * Assumption: Structure ID cannot be null or empty for a product. 
			 * Other approaches considered are discussed below this method.
			 */
			List<Product> duplicateList = productList
										  .stream()
										  .filter(product -> (product.getProductId() > 0 && product.getSkuId() != null && !product.getSkuId().equals("")))
										  .collect(Collectors.groupingBy(product -> product.getSkuId()))
										  .values()
										  .stream()
										  .filter(item -> item.size() > 1)
										  .flatMap(item -> item.stream())
										  .collect(Collectors.toList());
			return duplicateList;
			
		} catch(Exception e) {
			//most of the potential exceptions will be handled and responded with 400 error code with the help of jackson object binding before even reaching this method
			//any other exceptions during the execution of this method will be handled gracefully here.
			throw new CustomError(e.getMessage());
		}
	}
	
	/*
	 * Other approaches considered:
	 * #1. Using an HashMap like seen collection and try to accumulate the products whose skuIds were seen before, in one pass O(n). 
	 * But that would require adding extra data, to include those products which were seen for the first time as well, which adds up enough overhead in space and time
	 * like the current implemented approach. 
	 * Also, using seen like approach would introduce stateful behavior to the lambda and hence can't be used for parallel processing to boost efficiency.
	 * https://docs.oracle.com/javase/8/docs/api/java/util/stream/package-summary.html#Statelessness
	 * 
	 * #2. Using the JSON Array data directly and working on the properties of JSON without collections. Dropped as it may not be a better design to work the
	 * properties of the json directly. working with properties of a model object (like Product) is an elegant way.
	 * 
	 * #3. If the service deals with large JSON all the time, then processing the JSON through jackson library's event streaming way might be beneficiary.
	 * https://www.ngdata.com/parsing-a-large-json-file-efficiently-and-easily/
	 * https://blog.overops.com/the-ultimate-json-library-json-simple-vs-gson-vs-jackson-vs-json/
	 * https://alfonsohidalgogonzalez.com/2017/09/28/the-ultimate-json-library-json-simple-vs-gson-vs-jackson-vs-jsonp-takipi-blog/
	 * 
	 */

}
