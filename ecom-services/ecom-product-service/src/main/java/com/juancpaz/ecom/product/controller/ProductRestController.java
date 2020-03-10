package com.juancpaz.ecom.product.controller;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.juancpaz.ecom.product.model.Product;
import com.juancpaz.ecom.product.repository.ProductRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class ProductRestController {

	@Autowired
	private ProductRepository productRepository;

	// ------------------- Update a Product
	// --------------------------------------------------------
	@RequestMapping(value = "/products/{id}", method = RequestMethod.PUT)
	public ResponseEntity<Resource<Product>> updateProduct(@PathVariable("id") String id,
			@RequestBody Product product) {

		log.info("Start");
		log.debug("Updating Product with id: {}", id);
		Optional<Product> currentProductFromDB = productRepository.findById(id);
		if (!currentProductFromDB.isPresent()) {
			log.debug("Product with id: {} not found", id);
			return new ResponseEntity<Resource<Product>>(HttpStatus.NOT_FOUND);
		}
		Product currentProduct = currentProductFromDB.get();
		currentProduct.setName(product.getName());
		currentProduct.setCode(product.getCode());
		currentProduct.setTitle(product.getTitle());
		currentProduct.setDescription(product.getDescription());
		currentProduct.setImgUrl(product.getImgUrl());
		currentProduct.setPrice(product.getPrice());
		currentProduct.setProductCategoryName(product.getProductCategoryName());

		Product newProduct = productRepository.save(currentProduct);

		Resource<Product> productRes = new Resource<Product>(newProduct,
				linkTo(methodOn(ProductRestController.class).getProduct(newProduct.getId())).withSelfRel());
		log.info("Ending");
		return new ResponseEntity<Resource<Product>>(productRes, HttpStatus.OK);
	}

	// ------------------- Create a Product
	// --------------------------------------------------------
	@RequestMapping(value = "/products", method = RequestMethod.POST)
	public ResponseEntity<Resource<Product>> postProduct(@RequestBody Product product, UriComponentsBuilder ucBuilder) {

		log.info("Start");
		log.debug("Creating Product with code: {}", product.getCode());
		List<Product> products = productRepository.findByCode(product.getCode());
		if (products.size() > 0) {
			log.debug("A Product with code {} already exist", product.getCode());
			return new ResponseEntity<Resource<Product>>(HttpStatus.CONFLICT);
		}

		Product newProduct = productRepository.save(product);

		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/products/{id}").buildAndExpand(product.getId()).toUri());
		Resource<Product> productRes = new Resource<Product>(newProduct,
				linkTo(methodOn(ProductRestController.class).getProduct(newProduct.getId())).withSelfRel());
		log.info("Ending");
		return new ResponseEntity<Resource<Product>>(productRes, headers, HttpStatus.OK);
	}

	// ------------------- Retreive a Product
	// --------------------------------------------------------
	@RequestMapping(value = "/products/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Resource<Product>> getProduct(@PathVariable("id") String id) {

		log.info("Start");
		log.debug("Fetching Product with id: {}", id);
		Optional<Product> currentProductFromDB = productRepository.findById(id);
		if (!currentProductFromDB.isPresent()) {
			log.debug("Product with id: {} not found", id);
			return new ResponseEntity<Resource<Product>>(HttpStatus.NOT_FOUND);
		}
		Product product = currentProductFromDB.get();
		Resource<Product> productRes = new Resource<Product>(product, new Link[] {
				linkTo(methodOn(ProductRestController.class).getProduct(product.getId())).withSelfRel(),
				linkTo(ProductRestController.class).slash("productImg").slash(product.getImgUrl()).withRel("imgUrl") });
		log.info("Ending");
		return new ResponseEntity<Resource<Product>>(productRes, HttpStatus.OK);
	}

	// ------------------- Retreive all Products
	// --------------------------------------------------------
	@RequestMapping(value = "/products", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<Resources<Resource<Product>>> getAllProducts() {

		log.info("Start");
		List<Product> products = productRepository.findAll();
		Link links[] = { linkTo(methodOn(ProductRestController.class).getAllProducts()).withSelfRel(),
				linkTo(methodOn(ProductRestController.class).getAllProducts()).withRel("getAllProducts"),
				linkTo(methodOn(ProductRestController.class).getAllProductsByCategory(""))
						.withRel("getAllProductsByCategory"),
				linkTo(methodOn(ProductRestController.class).getAllProductsByName(""))
						.withRel("getAllProductsByName") };
		if (products.isEmpty()) {
			log.debug("No products retreived from repository");
			return new ResponseEntity<Resources<Resource<Product>>>(HttpStatus.NOT_FOUND);
		}
		List<Resource<Product>> list = new ArrayList<Resource<Product>>();
		addLinksToProduct(products, list);
		Resources<Resource<Product>> productRes = new Resources<Resource<Product>>(list, links);// ,
		log.info("Ending");
		return new ResponseEntity<Resources<Resource<Product>>>(productRes, HttpStatus.OK);
	}

	// ------------------- Delete a Product
	// --------------------------------------------------------
	// --------------------------------------------------------
	@RequestMapping(value = "/products/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<Product> deleteProduct(@PathVariable("id") String id) {

		log.info("Start");
		log.debug("Fetching & Deleting Product with id: {}", id);
		Optional<Product> currentProductFromDB = productRepository.findById(id);
		if (!currentProductFromDB.isPresent()) {
			log.debug("Product with id: {} not found, hence not deleted", id);
			return new ResponseEntity<Product>(HttpStatus.NOT_FOUND);
		}
		productRepository.deleteById(id);
		log.info("Ending");
		return new ResponseEntity<Product>(HttpStatus.NO_CONTENT);
	}

	// ------------------- Delete All Products
	// --------------------------------------------------------
	// --------------------------------------------------------
	@RequestMapping(value = "/products", method = RequestMethod.DELETE)
	public ResponseEntity<Product> deleteAllProducts() {

		log.info("Start");
		long count = productRepository.count();
		log.debug("Deleting {} products", count);
		productRepository.deleteAll();
		log.info("Ending");
		return new ResponseEntity<Product>(HttpStatus.NO_CONTENT);
	}

	// ------------------- Retreive all Products by category
	// --------------------------------------------------------
	@RequestMapping(value = "/productsByCategory", method = RequestMethod.GET, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<Resources<Resource<Product>>> getAllProductsByCategory(
			@RequestParam("category") String category) {

		log.info("Start");
		List<Product> products = productRepository.findByProductCategoryName(category);
		Link links[] = {
				// linkTo(methodOn(ProductRestController.class)).slash("/products/category/").withSelfRel()
		};
		if (products.isEmpty()) {
			log.debug("No products retreived from repository for Product Category {}", category);
			return new ResponseEntity<Resources<Resource<Product>>>(HttpStatus.NOT_FOUND);
		}
		List<Resource<Product>> list = new ArrayList<Resource<Product>>();
		addLinksToProduct(products, list);
		Resources<Resource<Product>> productRes = new Resources<Resource<Product>>(list, links);// ,
																								// linkTo(methodOn(UserRestController.class).listAllUsers()).withSelfRel());
		log.info("Ending");
		return new ResponseEntity<Resources<Resource<Product>>>(productRes, HttpStatus.OK);
	}

	private void addLinksToProduct(List<Product> products, List<Resource<Product>> list) {
		for (Product product : products) {
			list.add(new Resource<Product>(product,
					new Link[] {
							linkTo(methodOn(ProductRestController.class).getProduct(product.getId())).withSelfRel(),
							linkTo(ProductRestController.class).slash("productImg").slash(product.getImgUrl())
									.withRel("imgUrl") }));
		}
	}

	// ------------------- Retreive all Products by name like % %
	// --------------------------------------------------------
	@RequestMapping(value = "/productsByName", method = RequestMethod.GET, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<Resources<Resource<Product>>> getAllProductsByName(@RequestParam("name") String name) {

		log.info("Start");
		List<Product> products = productRepository.findByNameRegex(name);
		Link links[] = {
				// linkTo(methodOn(ProductRestController.class)).slash("/products/category/").withSelfRel()
		};
		if (products.isEmpty()) {
			log.debug("No products retreived from repository for Product Name like {}", name);
			return new ResponseEntity<Resources<Resource<Product>>>(HttpStatus.NOT_FOUND);
		}
		List<Resource<Product>> list = new ArrayList<Resource<Product>>();
		addLinksToProduct(products, list);
		Resources<Resource<Product>> productRes = new Resources<Resource<Product>>(list, links);// ,
																								// linkTo(methodOn(UserRestController.class).listAllUsers()).withSelfRel());
		log.info("Ending");
		return new ResponseEntity<Resources<Resource<Product>>>(productRes, HttpStatus.OK);
	}
	// ------------------- Retreiving product image
	// --------------------------------------------------------

}
