import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import org.junit.Rule;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

public class WireMockBase {

    public WireMockServer wireMockServer = new WireMockServer(wireMockConfig().port(8089));

    public void startService() {
        wireMockServer.start();
    }
    public void stubForRegisterFeature(){
        StubMapping stubMapping =  stubFor(post(urlEqualTo("/register"))
                .withHeader("Content-Type", containing("xml"))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/xml")
                        .withBody("<response>SUCCESS</response>")));

        wireMockServer.addStubMapping(stubMapping);
    }

    public String getRegisterPath(){
       return wireMockServer.baseUrl()+"/register";
    }

    public void stopService(){
        wireMockServer.stop();
    }
}
