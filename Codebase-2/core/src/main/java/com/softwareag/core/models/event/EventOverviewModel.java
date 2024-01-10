package com.softwareag.core.models.event;

import com.day.cq.wcm.api.Page;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.softwareag.core.configuration.ConfigurationFinder;
import com.softwareag.core.models.GeneralConfigModel;
import com.softwareag.core.models.label.LabelConfigModel;
import com.softwareag.core.services.query.QueryConfigService;

import lombok.Getter;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Required;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;


@Model(adaptables = {SlingHttpServletRequest.class, Resource.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class EventOverviewModel {

    private static final Logger LOG = LoggerFactory.getLogger(EventOverviewModel.class);

    private static final String PN_HIDE_IN_OVERVIEW = "hideEventInEventOverview";
    private static final String PN_EVENT_START_DATE = "eventStartDate";
    private static final String PN_EVENT_END_DATE = "eventEndDate";
    private static final String NN_EVENT_COMPONENT = "eventitem";

    public static final String COUNTRY_TAG_PATH = "softwareag:countries/";
    public static final String EVENT_TYPE_TAG_PATH = "softwareag:event-type/";

    @Inject
    private QueryConfigService queryConfigService;

    @SlingObject
    private ResourceResolver resourceResolver;

    @Required
    @ScriptVariable
    private Page currentPage;

    @ValueMapValue
    private String title;

    @ValueMapValue
    private String copyText;

    @ValueMapValue
    private long amountOfListedEventItems;

    @ValueMapValue
    private long amountOfMoreLoadedEventItems;

    private List<EventItemModel> eventItems = new ArrayList<>();

    private final Set<String> eventCountryTags = new TreeSet<>();

    private final Set<String> eventTypeTags = new TreeSet<>();

    @Getter
    private Map<String,String> eventSchema=new HashMap();

    private LabelConfigModel labelConfigModel;
    private GeneralConfigModel generalConfigModel;

    private int limit = 1000;

    @PostConstruct
    private void init() {

        labelConfigModel = ConfigurationFinder.findLabelConfigComponent(this.currentPage).orElse(null);
        generalConfigModel = ConfigurationFinder.findGeneralConfigComponent(this.currentPage).orElse(null);

        if (queryConfigService != null && queryConfigService.getEventQueryLimit() > 0) {
            limit = queryConfigService.getEventQueryLimit();
        }

        eventItems = searchForEventItems(this.currentPage);

        // generates a set of relevant event country and type tags that are used to filter the event items
        for (final EventItemModel eventItem : eventItems) {
            eventCountryTags.add(eventItem.getEventCountry());
            eventTypeTags.add(eventItem.getEventType());
        }
    }

    /**
     * returns event items below the given {@link Page}.
     *
     * @return {@link List} of {@link EventItemModel} or an empty list if no item was found.
     */
    public List<EventItemModel> getEventItems() {
        return new ArrayList<>(this.eventItems);
    }

    /**
     * Gets a list of country tags that will be used to filter the event overview item list.
     *
     * @return {@link List} of {@link String}.
     */
    public List<String> getCountryTagList() {
        return new ArrayList<>(this.eventCountryTags);
    }

    /**
     * Gets a list of event type tags that will be used to filter the event overview item list.
     *
     * @return {@link List} of {@link String}.
     */
    public List<String> getEventTypeTagList() {
        return new ArrayList<>(this.eventTypeTags);
    }

    public String getTitle() {
        return title;
    }

    public String getCopyText() {
        return copyText;
    }

    public long getAmountOfListedEventItems() {
        return amountOfListedEventItems;
    }

    public long getAmountOfMoreLoadedEventItems() {
        return amountOfMoreLoadedEventItems;
    }

    public String getLabelLoadMore() {
        return this.labelConfigModel != null ? this.labelConfigModel.getLabelLoadMore() : "";
    }

    public String getLabelEventFilterOptionOne() {
        return this.labelConfigModel != null ? this.labelConfigModel.getLabelEventFilterOptionOne() : "";
    }

    public String getLabelEventFilterOptionTwo() {
        return this.labelConfigModel != null ? this.labelConfigModel.getLabelEventFilterOptionTwo() : "";
    }

    public String getGeneralConfigDateFormat() {
        return this.generalConfigModel != null ? this.generalConfigModel.getDateFormat() : "MM/dd/yyyy";
    }
    JSONArray EventsArray = new JSONArray();

    @Getter
    String JsonString;

    private List<EventItemModel> searchForEventItems(Page currentPage) {

        List<EventItemModel> eventItemModelList = new ArrayList<>();
        Session session = resourceResolver.adaptTo(Session.class);
        try {
            QueryManager queryManager = Objects.requireNonNull(session).getWorkspace().getQueryManager();
            Query query = queryManager.createQuery(createSql2Query(currentPage.getPath()), Query.JCR_SQL2);
            query.setLimit(limit);
            QueryResult result = query.execute();
            NodeIterator nodes = result.getNodes();
            while (nodes.hasNext()) {
                LinkedHashMap<String,String> obj = new LinkedHashMap<>();
                Node node = nodes.nextNode();
                Resource eventItemResource = resourceResolver.getResource(node.getPath());
                if (eventItemResource != null) {
                    eventItemModelList.add(eventItemResource.adaptTo(EventItemModel.class));
                    Node eventitem = eventItemResource.adaptTo(Node.class);
                    obj.put("@type", "Event");
                    obj.put("name", eventitem.getProperty("eventTitle").getString());
                    if (eventitem.hasProperty("eventDescription"))
                        obj.put("description", eventitem.getProperty("eventDescription").getString());
                    obj.put("startDate", eventitem.getProperty("eventStartDate").getString());
                    if (eventitem.hasProperty("eventEndDate"))
                        obj.put("endDate", eventitem.getProperty("eventEndDate").getString());
                    obj.put("eventStatus", "https://schema.org/EventScheduled");
                    if (eventitem.getProperty("eventCountry").toString().endsWith("online")) {
                        obj.put("eventAttendanceMode", "https://schema.org/OnlineEventAttendanceMode");
                        HashMap<String,String> locationObj = new HashMap<>();
                        locationObj.put("@type", "Place");
                        locationObj.put("url", eventitem.getProperty("externalEventLink").getString());
                    } else {
                        LinkedHashMap<String,String> locationObj = new LinkedHashMap<>();
                        locationObj.put("@type", "Place");
                        locationObj.put("name", eventitem.getProperty("eventCountry").getString().substring(eventitem.getProperty("eventCountry").getString().lastIndexOf("/")).split("/")[1]);
                        locationObj.put("url", eventitem.getProperty("externalEventLink").getString());
                        obj.put("eventAttendanceMode", "https://schema.org/OfflineEventAttendanceMode");
                        obj.put("location",locationObj.toString());
                    }
                    obj.put("eventStatus", "https://schema.org/EventScheduled");
                    obj.put("@context", "https://schema.org");
                    EventsArray.put(obj);
                }
            }
            JsonString=EventsArray.toString();
        } catch (RepositoryException re) {
            LOG.error("event overview component at {} could not query the event items", currentPage.getPath(), re);
        }
        return eventItemModelList;
    }

    private String createSql2Query(String currentPagePath) {

        final LocalDate localDate = LocalDate.now();
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        final String stringDate = formatter.format(localDate);

        return "SELECT * FROM [nt:unstructured] AS comp WHERE ISDESCENDANTNODE(comp, '" + currentPagePath + "')" +
            " AND [sling:resourceType] = '" + EventItemModel.PN_RESOURCE_TYPE + "'" +
            " AND NAME() = '" + NN_EVENT_COMPONENT + "'" +
            " AND [" + PN_HIDE_IN_OVERVIEW + "] = 'false'" +
            " AND (([" + PN_EVENT_END_DATE + "] IS NULL AND [" + PN_EVENT_START_DATE + "] >= '" + stringDate + "') OR [" + PN_EVENT_END_DATE + "] >= '" + stringDate + "')" +
            " ORDER BY [" + PN_EVENT_START_DATE + "] ASC";
    }
}
