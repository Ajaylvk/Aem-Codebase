package com.softwareag.core.models.map;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.softwareag.core.services.map.MapConfigService;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;


@Model(adaptables = SlingHttpServletRequest.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class Map extends MapConfigModel {

    private static final String[] TAB_NAMES = {"americaOffice", "europeOffice", "africaOffice", "asiaOffice", "australiaOffice"};

    protected static final String OFFICE_LOCATIONS = "locations";

    @OSGiService
    private MapConfigService mapConfigService;

    @SlingObject
    private ResourceResolver resourceResolver;

    @Self
    private SlingHttpServletRequest request;

    private final List<MapItem> mapItems = new ArrayList<>();

    private String apiKeyToken;

    @Override
    @PostConstruct
    protected void init() {
        super.init();
        if (mapConfigService != null) {
            apiKeyToken = mapConfigService.getApiKeyToken();
        }

        Stream.of(TAB_NAMES).forEach(tabName ->
            {
                final Resource tabResource = resource.getChild(tabName);
                if (tabResource != null && tabResource.hasChildren()) {
                    tabResource.getChildren().forEach(officeItem -> {
                            final MapItem mapItem = officeItem.adaptTo(MapItem.class);
                            if (mapItem != null) {
                                mapItem.getLink().rewriteLink();
                                mapItems.add(mapItem);
                            }
                        }
                    );
                }
            }
        );
    }

    public String getMapItemsJson() {
        JsonObject locationJsonObject = new JsonObject();
        JsonArray mapPointArray = new JsonArray();

        for (MapItem item : mapItems) {
            //create an array of "properties"
            JsonArray mapPoint = new JsonArray();
            mapPoint.add(item.getLatitude());
            mapPoint.add(item.getLongitude());
            mapPoint.add(item.getText());
            mapPoint.add(item.getLink().getHref());
            mapPoint.add(item.getLink().getTarget());
            mapPoint.add(item.getLink().getText());

            mapPointArray.add(mapPoint);
        }

        locationJsonObject.add(OFFICE_LOCATIONS, mapPointArray);
        return locationJsonObject.toString();
    }

    public String getApiKeyToken() {
        return apiKeyToken;
    }

}
