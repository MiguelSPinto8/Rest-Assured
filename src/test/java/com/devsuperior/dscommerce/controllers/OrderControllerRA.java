package com.devsuperior.dscommerce.controllers;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.devsuperior.dscommerce.tests.TokenUtil;

import io.restassured.http.ContentType;

public class OrderControllerRA {
	
	private String clientUsername, clientPassword , adminUsername, adminPassword;
	private String clientToken, adminToken, invalidToken;
	private Long existingOrderId, nonExistingOrderId;
	
	@BeforeEach
	public void setUp() throws JSONException {
		baseURI = "http://localhost:8080";
		
		existingOrderId = 1L;
		nonExistingOrderId = 100L;
		
		clientUsername = "maria@gmail.com";
		clientPassword = "123456";
		adminUsername = "alex@gmail.com";
		adminPassword = "123456";
		
		clientToken = TokenUtil.obtainAccessToken(clientUsername, clientPassword);
		adminToken = TokenUtil.obtainAccessToken(adminUsername, adminPassword);
		invalidToken = adminToken + "xpto"; //Invalid Token
	}
	
	@Test
	public void findByIdShouldReturnOrderWhenIdExistsAndAdminLogged() throws JSONException{
		
		given()
			.header("Content-type", "application/json")
			.header("Authorization", "Bearer " + adminToken)
			.accept(ContentType.JSON)
		.when()
			.get("/orders/{id}", existingOrderId )
		.then()
			.statusCode(200)
			.body("id", is(1))
			.body("moment", equalTo("2022-07-25T13:00:00Z"))
			.body("status", equalTo("PAID"))
			.body("client.name", equalTo("Maria Brown"))
			.body("payment.moment", equalTo("2022-07-25T15:00:00Z"))
			.body("items.name", hasItems("The Lord of the Rings", "Macbook Pro"))
			.body("total", is(1431.0F));
	}
	
	@Test
	public void findByIdShouldReturnOrderWhenIdExistsAndClientLogged() throws JSONException{
		
			given()
			.header("Content-type", "application/json")
			.header("Authorization", "Bearer " + clientToken)
			.accept(ContentType.JSON)
		.when()
			.get("/orders/{id}", existingOrderId)
		.then()
			.statusCode(200)
			.body("id", is(1))
			.body("moment", equalTo("2022-07-25T13:00:00Z"))
			.body("status", equalTo("PAID"))
			.body("client.name", equalTo("Maria Brown"))
			.body("payment.moment", equalTo("2022-07-25T15:00:00Z"))
			.body("items.name", hasItems("The Lord of the Rings", "Macbook Pro"))
			.body("total", is(1431.0F));
	}
	
	@Test
	public void findByIdShouldReturnForbiddenWhenIdExistsAndClientLoggedAndOrderDoesNotBelongToUser() throws JSONException{
		Long otherOrderId = 2L;
			
			given()
				.header("Content-type", "application/json")
				.header("Authorization", "Bearer " + clientToken)
				.accept(ContentType.JSON)
			.when()
				.get("/orders/{id}", otherOrderId)
			.then()
				.statusCode(403);
	}
	
	@Test
	public void findByIdShouldReturnNotFoundWhenIdExistsAndAdminLogged() throws JSONException{
		
		given()
			.header("Content-type", "application/json")
			.header("Authorization", "Bearer " + adminToken)
			.accept(ContentType.JSON)
		.when()
			.get("/orders/{id}", nonExistingOrderId )
		.then()
			.statusCode(404);
	}
	
	@Test
	public void findByIdShouldReturnNotFoundWhenIdExistsAndClientLogged() throws JSONException{
		
		given()
			.header("Content-type", "application/json")
			.header("Authorization", "Bearer " + clientToken)
			.accept(ContentType.JSON)
		.when()
			.get("/orders/{id}", nonExistingOrderId )
		.then()
			.statusCode(404);
	}
	
	@Test
	public void findByIdShouldReturnUnauthorizedWhenIdExistsAndInvalidToken() throws JSONException{
		
		given()
			.header("Content-type", "application/json")
			.header("Authorization", "Bearer " + invalidToken)
			.accept(ContentType.JSON)
		.when()
			.get("/orders/{id}", existingOrderId )
		.then()
			.statusCode(401);
	}



	

}
