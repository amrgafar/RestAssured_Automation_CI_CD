import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class BostaApiSecurityTests {

    // Read the secret token from an environment variable for CI/CD
    // This is the token for API #1 and #3
    private static final String PICKUP_AUTH_TOKEN = System.getenv("BOSTA_PICKUP_TOKEN");

    // This is the token for API #2
    private static final String BANK_AUTH_TOKEN = System.getenv("BOSTA_BANK_TOKEN");

    @BeforeAll
    public static void setup() {
        // Set the base URL for all tests
        RestAssured.baseURI = "https://stg-app.bosta.co";
    }

    /**
     * Test Case: P-LOGIC-01 (API #1)
     * Description: Submitting a negative 'numberOfParcels' should be rejected.
     * Expected: HTTP 400 Bad Request (or similar error), NOT 200 or 500.
     */
    @Test
    public void testCreatePickupWithNegativeParcels() {
        // 1. Create the JSON payload
        JSONObject requestBody = new JSONObject();
        requestBody.put("businessLocationId", "MFqXsoFhxO");
        requestBody.put("scheduledDate", "2025-06-30");
        requestBody.put("numberOfParcels", -5); // The malicious payload
        // ... (add other required fields from the cURL)
        requestBody.put("contactPerson", new JSONObject()
                .put("name", "Test User")
                .put("phone", "+201055592829"));

        given()
                .header("Authorization", PICKUP_AUTH_TOKEN)
                .contentType(ContentType.JSON)
                .body(requestBody.toString())
                .when()
                .post("/api/v2/pickups")
        .then()
                .statusCode(is(not(200))) // Should NOT be successful
                .statusCode(is(not(500))) // Should NOT crash the server
                .statusCode(400); // Expects a "Bad Request"
    }

    /**
     * Test Case: B-LOGIC-01 (API #2)
     * Description: Attempting to update bank info without the OTP field.
     * Expected: HTTP 400 (or 422) error, as the 'paymentInfoOtp' is missing.
     */
    @Test
    public void testUpdateBankInfoWithoutOtp() {
        // 1. Create the JSON payload, omitting 'paymentInfoOtp'
        JSONObject bankInfo = new JSONObject();
        bankInfo.put("beneficiaryName", "Test Name");
        bankInfo.put("bankName", "Test Bank");
        bankInfo.put("accountNumber", "123456789");
        bankInfo.put("ibanNumber", "EG1234567890123456789012");

        JSONObject requestBody = new JSONObject();
        requestBody.put("bankInfo", bankInfo);
        // We are *intentionally* not adding 'paymentInfoOtp'

        given()
                .header("Authorization", BANK_AUTH_TOKEN)
                .contentType(ContentType.JSON)
                .body(requestBody.toString())
                .when()
                .post("/api/v2/businesses/add-bank-info")
        .then()
                .statusCode(is(not(200))) // Should fail
                .statusCode(401); // Should be a validation error
    }

    /**
     * Test Case: F-PARAM-01 (API #3)
     * Description: Calling 'forget-password' with an empty JSON body.
     * Expected: HTTP 400 Bad Request (or 500 if unhandled).
     */
    @Test
    public void testForgetPasswordWithEmptyBody() {
        String emptyBody = "{}";

        given()
                .header("Authorization", PICKUP_AUTH_TOKEN) // Using the token from the cURL
                .contentType(ContentType.JSON)
                .body(emptyBody)
                .when()
                .post("/api/v2/users/forget-password")
        .then()
                .statusCode(is(not(200)))
                .statusCode(anyOf(equalTo(400), equalTo(500))); // Should be 400 (Bad Request) or 500 (Unhandled Error)
    }

    /**
     * Bonus Test: Generate a new token (from API #2 instructions)
     * This proves we can call an endpoint and extract its response.
     */
    @Test
    public void testGenerateToken() {
        given()
                .contentType(ContentType.JSON)
                .when()
                .post("/api/v2/users/generate-token-for-interview-task")
        .then()
                .statusCode(200)
                .body("token", not(emptyOrNullString())); // Assert the response has a token
    }
}