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
        wireMockServer.stubFor(get(urlPathMatching("/api/v1/vehicles/([a-zA-Z0-9\\-]+)"))
                .willReturn(okJson("{ \"status\": \"AVAILABLE\" }")));

        /*
        intercepta qualquer requisição HTTP GET para a rota /api/v1/vehicles/{algum-UUID} e devolver uma resposta
        estática com JSON dizendo que o veículo está disponível.

        get(...)
        Indica que o stub é para requisições HTTP GET

        urlPathMatching(...)
        expressão regular que combina qualquer URL que comece com /api/v1/vehicles/ seguido por um UUID.
        A regex: ([a-zA-Z0-9\\-]+)

        .willReturn(...)
        Define o que WireMock deve responder quando essa requisição for feita

         */

        return Map.of(
                "quarkus.rest-client.vehicle-api.url", wireMockServer.baseUrl() + "/api/v1"
        );
    }

    @Override
    public void stop() {
        wireMockServer.stop();
    }
}