package com.devsuperior.dscommerce.controllers;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.devsuperior.dscommerce.tests.TokenUtil;

import io.restassured.http.ContentType;

public class OrderControllerRA {
	
	private String clientUsername, clientPassword , adminUsername, adminPassword;
	private String clientToken, adminToken, invalidToken;
	private Long existingOrderId, nonExistingOrderId;
	
	@BeforeEach
	public void setUp() {
		baseURI = "http://localhost:8080";
		
		existingOrderId = 1L;
		nonExistingOrderId = 100L;
		
		clientUsername = "alex@gmail.com";
		clientPassword = "123456";
		adminUsername = "maria@gmail.com";
		adminPassword = "123456";
		
		clientToken = TokenUtil.obtainAccessToken(clientUsername, clientPassword);
		adminToken = TokenUtil.obtainAccessToken(adminUsername, adminPassword);
		invalidToken = adminToken + "xpto"; //Invalid Token
	}
	
	@Test
	public void findByIdShouldReturnOrderWhenIdExistsAndAdminLogged() {
		
		given()
			.header("Content-type", "application/json")
			.header("Authorization", "Bearer" + adminToken)
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
			.body("items.id", hasItems("The Lord of the Rings","Macbook Pro"))
			.body("total", is(1431.0F));
	}
	
	@Test
	public void findByIdShouldReturnOrderWhenIdExistsAndClientLogged() {
		
		given()
			.header("Content-type", "application/json")
			.header("Authorization", "Bearer" + clientToken)
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
			.body("items.id", hasItems("The Lord of the Rings","Macbook Pro"))
			.body("total", is(1431.0F));
	}

}
