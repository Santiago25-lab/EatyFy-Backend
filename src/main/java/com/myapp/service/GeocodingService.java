package com.myapp.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

@Service
public class GeocodingService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public GeocodingService(ObjectMapper objectMapper) {
        this.restTemplate = new RestTemplate();
        this.objectMapper = objectMapper;
    }

    /**
     * Obtiene coordenadas (latitud, longitud) de una dirección usando OpenStreetMap Nominatim
     * @param address Dirección completa (ej: "Calle 10 #5-20, Bogotá, Colombia")
     * @return Mapa con "lat" y "lon", o null si no se encuentra
     */
    public Map<String, Double> getCoordinates(String address) {
        try {
            String url = "https://nominatim.openstreetmap.org/search?format=json&q=" +
                        java.net.URLEncoder.encode(address, "UTF-8") +
                        "&limit=1&countrycodes=co";

            String response = restTemplate.getForObject(url, String.class);

            if (response != null && !response.equals("[]")) {
                JsonNode jsonNode = objectMapper.readTree(response);
                if (jsonNode.isArray() && jsonNode.size() > 0) {
                    JsonNode firstResult = jsonNode.get(0);
                    double lat = firstResult.get("lat").asDouble();
                    double lon = firstResult.get("lon").asDouble();

                    return Map.of("lat", lat, "lon", lon);
                }
            }
        } catch (RestClientException e) {
            System.err.println("Error calling OpenStreetMap API: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error processing geocoding response: " + e.getMessage());
        }

        return null; // No se encontraron coordenadas
    }

    /**
     * Obtiene dirección formateada de coordenadas (reverse geocoding)
     * @param lat Latitud
     * @param lon Longitud
     * @return Dirección formateada o null
     */
    public String getAddressFromCoordinates(double lat, double lon) {
        try {
            String url = "https://nominatim.openstreetmap.org/reverse?format=json&lat=" + lat + "&lon=" + lon;

            String response = restTemplate.getForObject(url, String.class);

            if (response != null) {
                JsonNode jsonNode = objectMapper.readTree(response);
                if (jsonNode.has("display_name")) {
                    return jsonNode.get("display_name").asText();
                }
            }
        } catch (RestClientException e) {
            System.err.println("Error calling OpenStreetMap reverse API: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error processing reverse geocoding response: " + e.getMessage());
        }

        return null;
    }
}