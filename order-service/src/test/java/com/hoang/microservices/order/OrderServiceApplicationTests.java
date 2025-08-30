package com.hoang.microservices.order;

import com.hoang.microservices.order.stubs.InventoryClientStub;
import io.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.context.annotation.Import;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@AutoConfigureWireMock(port = 0)

class OrderServiceApplicationTests {

	@Container
	@ServiceConnection
	static MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.3.0")
			.withDatabaseName("order_service_test")
			.withUsername("testuser")
			.withPassword("testpass");

	@LocalServerPort
	private int port;

	@BeforeEach
	void setUp() {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = port;
	}

	@Test
	void shouldPlaceOrder() {
		String requestBody = """
				{
				       "orderNumber": "ORD-2025-001",
				       "skuCode": "iphone_13",
				       "price": 999.99,
				       "quantity": 200
				}
				""";
		InventoryClientStub.stubInventoryCall("iphone_13", 200);
		RestAssured.given()
				.contentType("application/json")
				.body(requestBody)
				.when()
				.post("/api/order")
				.then()
				.statusCode(201)
				.body(Matchers.equalTo("Order Placed Successfully"));
	}
}