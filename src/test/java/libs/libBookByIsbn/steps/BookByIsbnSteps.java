package libs.libBookByIsbn.steps;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import libs.libCore.modules.BaseSteps;
import libs.libCore.modules.Log;
import libs.libCore.modules.SharedContext;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;

public class BookByIsbnSteps extends BaseSteps {

    // PicoContainer injects class SharedContext
    public BookByIsbnSteps(SharedContext ctx) {
        super(ctx);
    }

    /**
     * Creates Request Specification and stores it as request ctx.obj
     *
     * Uses following objects:
     *  TestData.isbn
     *
     */
    @Given("^a book exists with an isbn$")
    public void a_book_exists_with_isbn() {
        Log.info("* Step started a_book_exists_with_isbn");

        String isbn = Storage.get("TestData.isbn");
        RequestSpecification request = given().param("q", "isbn:" + isbn);
        ctx.Object.put("request",RequestSpecification.class, request);
    }

    /**
     * Triggers http GET request as specified in RequestSpecification
     * Response is stored as response ctx.obj and attached to the report
     *
     * Uses following objects:
     *  ctx.Object.request
     *
     */
    @When("^a user retrieves the book by isbn$")
    public void a_user_retrieves_the_book_by_isbn(){
        Log.info("* Step started a_user_retrieves_the_book_by_isbn");

        String url = Storage.get("Environment.Active.Rest.url");
        RequestSpecification request = ctx.Object.get("request",RequestSpecification.class);
        Response response = request.when().log().all().get(url);
        ValidatableResponse response2 = response.then();
        ctx.Object.put("response",Response.class, response);
        ctx.Object.put("response",ValidatableResponse.class, response2);
        ctx.Object.put("json",ValidatableResponse.class, response2);
        StepCore.attachMessageToReport("Json response", response.prettyPrint());
    }


    /**
     * Verifies that response includes some fields {} and their value contains {}
     * Input requires a table
     *
     * Uses following objects:
     *  ctx.Object.json
     *
     * @param responseFields content of a table that contains key and expected value pairs to verify
     *
     */
    @And("^response includes the following in any order$")
    public void response_contains_in_any_order(Map<String,String> responseFields){
        Log.info("* Step started response_contains_in_any_order");

        ValidatableResponse json = ctx.Object.get("json",ValidatableResponse.class);
        for (Map.Entry<String, String> field : responseFields.entrySet()) {
            Object expectedValue = StepCore.checkIfInputIsVariable(field.getValue());
            String type = expectedValue.getClass().getName();
            if(type.contains("Int")){
                Integer iExpVal = (int) expectedValue;
                Log.debug("Expected is " + field.getKey() + "=" + iExpVal);
                Log.debug("Current is " + json.extract().path(field.getKey()));

                try {
                    json.body(field.getKey(), containsInAnyOrder(iExpVal));
                } catch (AssertionError e) {
                    Log.error("", e);
                }

                //if ( (Integer) json.extract().path(field.getKey()) != lExpVal.intValue() ) {
                //    Log.error("Expected " + lExpVal.intValue() + " but was " + json.extract().path(field.getKey()));
                //}
            }
            else {
                String sExpVal = (String) expectedValue;
                Log.debug("Expected is " + field.getKey() + "=" + sExpVal);
                Log.debug("Current is " + json.extract().path (field.getKey()));

                try {
                    json.body(field.getKey(), containsInAnyOrder(sExpVal));
                } catch (AssertionError e) {
                    Log.error("", e);
                }

                //if ( ! json.extract().path(field.getKey()).equals(sExpVal) ) {
                //    Log.error("Expected " + sExpVal + " but was " + json.extract().path(field.getKey()));
                //}
            }
        }
    }

    /**
     * Verifies that response includes some fields {} nad their value equals to {}
     * Input requires a table
     *
     * Uses following objects:
     *  ctx.Object.json
     *
     * @param responseFields content of a table that contains key and expected value pairs to verify
     *
     */
    @And("^response includes the following$")
    public void response_includes_the_following(Map<String,String> responseFields){
        Log.info("* Step started response_includes_the_following");

        ValidatableResponse json = ctx.Object.get("json",ValidatableResponse.class);
        for (Map.Entry<String, String> field : responseFields.entrySet()) {
            Object expectedValue = StepCore.checkIfInputIsVariable(field.getValue());
            String type = expectedValue.getClass().getName();
            if(type.contains("Int")){
                Integer iExpVal = (int) expectedValue;
                Log.debug("Expected is " + field.getKey() + "=" + iExpVal.intValue());
                Log.debug("Current is " + json.extract().path(field.getKey()));

                try {
                    json.body(field.getKey(), equalTo(iExpVal.intValue()));
                } catch (AssertionError e) {
                    Log.error("", e);
                }

                //if ( (Integer) json.extract().path(field.getKey()) != lExpVal.intValue() ) {
                //    Log.error("Expected " + lExpVal.intValue() + " but was " + json.extract().path(field.getKey()));
                //}
            }
            else {
                String sExpVal = (String) expectedValue;
                Log.debug("Expected is " + field.getKey() + "=" + sExpVal);
                Log.debug("Current is " + json.extract().path (field.getKey()));

                try {
                    json.body(field.getKey(), equalTo(sExpVal));
                } catch (AssertionError e) {
                    Log.error("", e);
                }

                //if ( ! json.extract().path(field.getKey()).equals(sExpVal) ) {
                //    Log.error("Expected " + sExpVal + " but was " + json.extract().path(field.getKey()));
                //}

            }
        }
    }

}