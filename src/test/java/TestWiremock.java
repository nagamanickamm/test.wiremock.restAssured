import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.github.tomakehurst.wiremock.recording.SnapshotRecordResult;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.matcher.RestAssuredMatchers;
import io.restassured.parsing.Parser;
import org.apache.http.HttpStatus;

import static io.restassured.matcher.RestAssuredMatchers.matchesXsdInClasspath;
import static org.hamcrest.Matchers.equalTo;
import org.junit.Rule;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

public class TestWiremock {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().dynamicPort());

    @Test
    public void WiremockStandAloneServer(){
        WireMock.configureFor(8181); // Initiate the Standalone jar before using this
        WireMock.removeAllMappings();
        WireMock.stubFor(get("/my/resource")
                .willReturn(ok()
                        .withHeader("Content-Type", "text/xml")
                        .withBody("<response>SUCCESS2</response>")));
        System.out.println(WireMock.listAllStubMappings());
        // WireMock.shutdownServer();
    }

    @Test
    public void WiremockStandAloneRecord(){
        configureFor(8181); // Initiate the Standalone jar before using this
        WireMock.removeAllMappings();
        startRecording("https://reqres.in");
        RestAssured.get("https://reqres.in/api/users/2");
        RestAssured.get("http://localhost:8181/api/users/2");
//      List<StubMapping> recordedMappings = snapshotRecord();
        stopRecording(); // The recordings will be saved in Json format under Mapping folder in same path as jar file
        System.out.println(WireMock.listAllStubMappings().getMappings().stream().count());
        // WireMock.shutdownServer();
    }


    @Test
    public void JavaClassExample(){
        WireMockBase wireMockBase = new WireMockBase();

        wireMockBase.startService();

        wireMockBase.stubForRegisterFeature();

        RestAssured.defaultParser = Parser.XML; //Optional

        RestAssured.given().header("Content-Type",ContentType.XML.getAcceptHeader())
                .when().post(wireMockBase.getRegisterPath())
                .then().assertThat().statusCode(HttpStatus.SC_OK)
                .and().assertThat().body("Response", equalTo("SUCCESS"))
                .and().body(matchesXsdInClasspath("resource/template.xsd"));

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
