package com.softwareag.core.models.reference;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.softwareag.core.models.filtercontainer.FilterContainerModel;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.util.Objects;


@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ReferenceModel {

    private static final Logger LOG = LoggerFactory.getLogger(ReferenceModel.class);

    public static final String REFERENCE_COMPONENT_RESOURCE_TYPE = "softwareag/components/content/reference";
    public static final String COMPONENT_TAGS = "tags";
    public static final String PATH_LANGUAGE_MASTER = "language_master";
    public static final String PATH_CORPORATE = "corporate";
    public static final String PATH_XF = "/content/experience-fragments";
    public static final String PATH_TYPE_XF = "experienceFragment";
    public static final String PATH_TYPE_PARTNER = "partner";
    public static final String PATH_TYPE_ASSET = "asset";

    @Self
    private Resource resource;

    @SlingObject
    private ResourceResolver resourceResolver;

    @ValueMapValue
    private String anchorName;

    @ValueMapValue
    private String path;

    @ValueMapValue
    private String pathType;

    @ValueMapValue(name = COMPONENT_TAGS)
    private String[] tagPaths;

    @PostConstruct
    void init() {
        if (path != null && path.contains(PATH_XF)) {
            pathType = PATH_TYPE_XF;
            populateXFDetails();
        }else if (path != null && path.contains(PATH_TYPE_PARTNER)) {
            pathType = PATH_TYPE_PARTNER;
        } else if (path != null && path.contains(PATH_TYPE_ASSET)) {
            pathType = PATH_TYPE_ASSET;
        }
    }

    private void populateXFDetails() {
        PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
        Page currentPage = Objects.requireNonNull(pageManager).getContainingPage(resource);
        String pagePath = currentPage.getPath();
        String currentPageLocale = currentPage.getLanguage().toString().toLowerCase();
        String fragmentPath = path;

        if (pagePath.contains(PATH_CORPORATE)) {
            /*
              if page is under corporate, simple replace 'en' in xf path with 'corporate'
             */
            fragmentPath = fragmentPath.replace(PATH_LANGUAGE_MASTER, PATH_CORPORATE);
        } else if(resource.getResourceResolver().getResource(fragmentPath) != null) {
                Page expFragment = pageManager.getPage(fragmentPath);
                String xfLocale = expFragment.getLanguage().toString().toLowerCase();
                if(!currentPageLocale.equals(xfLocale)){
                    fragmentPath = fragmentPath.replaceAll("\\b"+xfLocale+"\\b", currentPageLocale);
                }
        }
        Resource fragmentResource = resource.getResourceResolver().getResource(fragmentPath);
        if (fragmentResource == null) {
            if (pagePath.contains(PATH_CORPORATE)) {
                fragmentPath = fragmentPath.replace(PATH_CORPORATE, PATH_LANGUAGE_MASTER);
            } else {
                fragmentPath = fragmentPath.replaceAll("\\b" + currentPageLocale + "\\b", "en");
            }
        }
        path = checkForXFResourceType(fragmentPath);
        setReferenceNodeProps(path);
    }

    private String checkForXFResourceType(String fragmentPath){
        Resource fragmentResource = resource.getResourceResolver().getResource(fragmentPath);
        Page fragmentPage = fragmentResource != null ? fragmentResource.adaptTo(Page.class) : null;
        if (fragmentPage != null && !fragmentPage.getContentResource().getResourceType().contains("xfpage")) {
            fragmentPath = fragmentPath.concat("/master");
        }
        return fragmentPath;
    }
    private void setReferenceNodeProps(String path) {
        Node referenceNode = resource.adaptTo(Node.class);
        try {
            Objects.requireNonNull(referenceNode).setProperty("path", path);
            referenceNode.getSession().save();
        } catch (RepositoryException e) {
            LOG.error("An error ocurred while setting fragment path.", e);
        }
    }

    /**
     * Checks if tagPaths are empty or all of the chosen ones are matching childTags.
     *
     * @param childTagPaths
     *     tagPaths assigned to a child component
     * @return true if tagPaths are empty or all of the chosen ones are matching childTags, otherwise false.
     */
    public boolean matchesTag(final String[] childTagPaths) {
        if (ArrayUtils.isEmpty(tagPaths)) {
            return true;
        }

        for (final String tagPath : tagPaths) {
            if (!matchesAnyChildTag(tagPath, childTagPaths)) {
                return false;
            }
        }

        return true;
    }

    private boolean matchesAnyChildTag(final String tagPath, final String[] childTagPaths) {
        if (StringUtils.isBlank(tagPath) || ArrayUtils.isEmpty(childTagPaths)) {
            return false;
        }

        for (final String childTagPath : childTagPaths) {
            if (FilterContainerModel.matchesOptionTag(tagPath, childTagPath)) {
                return true;
            }
        }
        return false;
    }

    public String getAnchorName() {
        return anchorName;
    }

    public String getPath() {
        return path;
    }

    public String getPathType() {
        return pathType;
    }
}
