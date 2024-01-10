package com.softwareag.core.models.map;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.softwareag.core.services.map.MapConfigService;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Objects;

import static com.softwareag.core.models.map.Map.OFFICE_LOCATIONS;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
public class MapTest {

    private static final String DEST_PATH = "/content/softwareag/test/map";

    private final AemContext context = new AemContext();

    @Mock
    private MapConfigService mapConfigService;

    private Map modelUnderTest;

    @BeforeEach
    public void setUp() {
        context.load().json("/components/map/map.json", DEST_PATH);
        context.registerService(MapConfigService.class, mapConfigService);
        context.addModelsForPackage("com.softwareag.core.models");
        context.currentResource(DEST_PATH + "/map-page/jcr:content/parsys/map");

        modelUnderTest = Objects.requireNonNull(context.request()).adaptTo(Map.class);

        assertThat(modelUnderTest).isNotNull();
    }

    @Test
    void testGetConfigurationPath() {
        assertEquals("/content/softwareag/test/map/map-page/jcr:content/parsys/map", modelUnderTest.getConfigurationPath());
    }

    @Test
    void testGetLatitude() {
        assertEquals("43.6514973", modelUnderTest.getLatitude());
    }

    @Test
    void testGetLongitude() {
        assertEquals("-79.3806597", modelUnderTest.getLongitude());
    }

    @Test
    void testGetZoomLevel() {
        assertEquals("1", modelUnderTest.getZoomLevel());
    }

    @Test
    void testGetTabItems() {
        JsonObject jsonObject = new JsonParser().parse(modelUnderTest.getMapItemsJson()).getAsJsonObject();

        assertNotNull(jsonObject.getAsJsonArray(OFFICE_LOCATIONS));

        assertEquals(6, jsonObject.getAsJsonArray(OFFICE_LOCATIONS).size());
    }

}
