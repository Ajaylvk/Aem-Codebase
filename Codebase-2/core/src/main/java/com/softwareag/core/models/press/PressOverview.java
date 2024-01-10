package com.softwareag.core.models.press;

import com.day.cq.wcm.api.Page;
import com.softwareag.core.configuration.ConfigurationFinder;
import com.softwareag.core.models.GeneralConfigModel;
import com.softwareag.core.models.label.LabelConfigModel;
import com.softwareag.core.services.query.QueryConfigService;
import com.softwareag.core.util.AttributeUtil;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Required;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Model(adaptables = {Resource.class, SlingHttpServletRequest.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class PressOverview {

    private static final Logger LOG = LoggerFactory.getLogger(PressOverview.class);

    private static final String PN_COMPONENT_PRESSITEM_RESOURCE_TYPE = "softwareag/components/content/pressitem";
    private static final String PN_HIDE_IN_OVERVIEW = "hidePressInPressOverview";
    private static final String PN_PRESS_RELEASE_DATE = "pressReleaseDate";
    private static final String NN_PRESS_COMPONENT = "pressitem";

    @OSGiService
    private QueryConfigService queryConfigService;

    @Required
    @ScriptVariable
    private Page currentPage;

    @Inject
    private ResourceResolver resourceResolver;

    @ValueMapValue
    private String title;

    @ValueMapValue
    private String copyText;

    @ValueMapValue
    private long amountOfListedPressItems;

    @ValueMapValue
    private long amountOfMoreLoadedPressItems;

    private LabelConfigModel labelConfigModel;
    private GeneralConfigModel generalConfigModel;
    private int limit = 2000;

    @PostConstruct
    private void init() {
        labelConfigModel = ConfigurationFinder.findLabelConfigComponent(this.currentPage).orElse(null);
        generalConfigModel = ConfigurationFinder.findGeneralConfigComponent(this.currentPage).orElse(null);
        if (queryConfigService != null && queryConfigService.getPressQueryLimit() > 0) {
            limit = queryConfigService.getPressQueryLimit();
        }
    }

    /**
     * Function that searches for press items in a given path.
     *
     * @return {@link List} of {@link PressItem} with all the information returned by the search
     */
    public List<PressItem> getSearchResults() {
        List<PressItem> pressItemList = new ArrayList<>();
        Session session = resourceResolver.adaptTo(Session.class);
        try {
            QueryManager queryManager = Objects.requireNonNull(session).getWorkspace().getQueryManager();
            Query query = queryManager.createQuery(createSql2Query(currentPage.getPath()), Query.JCR_SQL2);
            query.setLimit(limit);
            QueryResult result = query.execute();
            NodeIterator nodes = result.getNodes();
            while (nodes.hasNext()) {
                Node node = nodes.nextNode();
                Resource pressItemResource = resourceResolver.getResource(node.getPath());
                if (pressItemResource != null) {
                    pressItemList.add(pressItemResource.adaptTo(PressItem.class));
                }
            }
        } catch (RepositoryException re) {
            LOG.error("press overview component at {} could not query the press items", currentPage.getPath(), re);
        }
        return pressItemList;
    }

    public String getTitle() {
        return title;
    }

    public String getDataAttribValue() {
        return AttributeUtil.removeEmptyLines(getTitle());
    }

    public String getCopyText() {
        return copyText;
    }

    public long getAmountOfListedPressItems() {
        return amountOfListedPressItems;
    }

    public long getAmountOfMoreLoadedPressItems() {
        return amountOfMoreLoadedPressItems;
    }

    public String getLabelLoadMore() {
        return labelConfigModel != null ? labelConfigModel.getLabelLoadMore() : "";
    }

    public String getLabelLearnMore() {
        return labelConfigModel != null ? labelConfigModel.getLabelLearnMore() : "";
    }

    public String getGeneralConfigDateFormat() {
        return this.generalConfigModel != null ? this.generalConfigModel.getDateFormat() : "MM/dd/yyyy";
    }

    private String createSql2Query(final String currentPagePath) {
        return "SELECT * FROM [nt:unstructured] AS comp WHERE ISDESCENDANTNODE(comp, '" + currentPagePath + "') " +
            "AND [sling:resourceType] = '" + PN_COMPONENT_PRESSITEM_RESOURCE_TYPE + "' " +
            "AND NAME() = '" + NN_PRESS_COMPONENT + "'" +
            "AND [" + PN_HIDE_IN_OVERVIEW + "] = 'false' ORDER BY [" + PN_PRESS_RELEASE_DATE + "] DESC";
    }

}
