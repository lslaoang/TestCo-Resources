package com.testco.resources.controller;

import com.azure.spring.aad.AADOAuth2AuthenticatedPrincipal;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Logger;

import static org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction.clientRegistrationId;

@RestController
public class ResourceController {

    private static final Logger LOGGER = Logger.getLogger(ResourceController.class.getName());
    private static final String GRAPH_ENDPOINT = "https://graph.microsoft.com/v1.0/";

    private final WebClient webClient;

    @Value("${app.allowed-group}")
    private String allowedGroup;

    public ResourceController(WebClient webClient) {
        this.webClient = webClient;
    }

    @GetMapping("/resource")
    public ResponseEntity<?> getResource() {
        try {
            verifyAssignment();
            LOGGER.info("Resources retrieved successfully!");
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        }catch (RuntimeException e){
            LOGGER.severe("Access to the resource denied. " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    private void verifyAssignment(){

        LOGGER.info("Checking authorization.");

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        AADOAuth2AuthenticatedPrincipal user = (AADOAuth2AuthenticatedPrincipal) principal;
        String userEmail = user.getAttribute("preferred_username");
        LOGGER.info("Checking authorization for "+userEmail);
        if (userEmail == null) {
            throw new VerifyGroupException("Claim \"preferred_username\" with user email is not present in the token. ");
        }
        String groupEncoded = allowedGroup.replace(" ", "%20");
        String memberOfEndpoint = GRAPH_ENDPOINT
                + "users/" + userEmail
                + "/memberOf/microsoft.graph.group?$count=true&$filter=displayName%20eq%20'"
                + groupEncoded + "'";

        URI uri;
        try {
            uri = new URI(memberOfEndpoint);
        } catch (URISyntaxException e) {
            throw new VerifyGroupException(
                    "Unable to create Member of URI from link: . " + memberOfEndpoint + "\n" + e.getMessage()
            );
        }

        String graphResponse;
        try {
            graphResponse = webClient
                    .get()
                    .uri(uri)
                    .attributes(clientRegistrationId("graph"))
                    .header("ConsistencyLevel", "eventual")
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        } catch (Exception e) {
            throw new VerifyGroupException("Failed to call Graph API. " + e.getMessage());
        }

        if (graphResponse == null) {
            throw new VerifyGroupException("Failed verify Group from Graph API assignment.");
        }

        JSONObject jsonObject = new JSONObject(graphResponse);
        Integer groupCount = (Integer) jsonObject.get("@odata.count");
        if (groupCount != 1) {
            throw new VerifyGroupException(String.format("No group found for the user %s.", userEmail));
        }
        LOGGER.info("Authorization check passed.");
    }

}
