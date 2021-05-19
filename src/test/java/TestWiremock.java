import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import org.apache.http.HttpStatus;

import static org.hamcrest.Matchers.equalTo;
import org.junit.Rule;
import org.junit.jupiter.api.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

public class TestWiremock {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().dynamicPort());

    @Test
    public void JavaClassExample(){
        WireMockBase wireMockBase = new WireMockBase();

        wireMockBase.startService();

        wireMockBase.stubForRegisterFeature();

        RestAssured.defaultParser = Parser.XML; //Optional

        RestAssured.given().header("Content-Type",ContentType.XML.getAcceptHeader())
                .when().post(wireMockBase.getRegisterPath())
                .then().assertThat().statusCode(HttpStatus.SC_OK)
                .and().assertThat().body("Response", equalTo("SUCCESS"));

        wireMockBase.stopService();
    }

    @Test
    public void JunitExample(){
        wireMockRule.start();

        System.out.println(wireMockRule.baseUrl()+"/my/resource");

        StubMapping stubMapping = stubFor(get("/my/resource")
                .willReturn(ok()
                        .withHeader("Content-Type", "text/xml")
                        .withBody("<response>SUCCESS</response>")));
        wireMockRule.addStubMapping(stubMapping);

        RestAssured.given()
                .when().get(wireMockRule.baseUrl()+"/my/resource")
                .then().assertThat().contentType("text/xml")
                .and().assertThat().statusCode(HttpStatus.SC_OK);

        wireMockRule.stop();
    }
}
