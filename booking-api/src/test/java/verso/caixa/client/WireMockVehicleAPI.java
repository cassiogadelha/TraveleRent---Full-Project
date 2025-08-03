package verso.caixa.client;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class WireMockVehicleAPI implements QuarkusTestResourceLifecycleManager {

    private WireMockServer wireMockServer;

    @Override
    public Map<String, String> start() {
        wireMockServer = new WireMockServer(8080); // porta que seu client usa
        wireMockServer.start();

        // Stub do veículo
        wireMockServer.stubFor(get(urlPathMatching("/api/v1/vehicles/.*"))
                .willReturn(okJson("{ \"status\": \"AVAILABLE\" }")));

        return Map.of(
                "quarkus.rest-client.vehicle-api.url", wireMockServer.baseUrl() + "/api/v1"
        );
    }

    @Override
    public void stop() {
        wireMockServer.stop();
    }
}