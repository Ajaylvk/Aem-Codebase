package com.softwareag.core.util;

import com.day.cq.wcm.api.Page;
import com.softwareag.core.models.link.LinkModel;
import com.softwareag.core.models.link.LinkModelBuilder;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.ValueMap;

/**
 * the AbstractNavigationLinkPage provides common functionality to pages which function as navigation items
 * for parent pages, such as @EventItem.
 *
 * The class ensures that redirects set on the page are handled as links rather than as redirects, and that
 * these "redirect links" have a target property.
 *
 * Any configured redirect is considered to be an external link. No attempt is made to identify internal and external
 * redirect URIs.  If a navigation link page has a redirect configured - open in new window.
 */
public abstract class AbstractNavigationLinkPage {

    private static final String PN_REDIRECT_TARGET = "cq:redirectTarget";
    private static final String LINK_TARGET_BLANK = "_blank";
    private static final String LINK_TARGET_SELF = "_self";

    private String externalLinkTarget;

    protected String title;

    private LinkModel link;

    protected abstract String getPath();

    protected void initRedirectNav(Page currentPage, String externalEventLink) {
        this.externalLinkTarget = externalEventLink;
        title = PageUtil.getPageTitle(currentPage);
        createLinkModel();
    }

    protected void initRedirectNav(Page currentPage) {
        if (currentPage != null) {
            ValueMap pageValueMap = currentPage.getProperties();
            if (pageValueMap != null) {
                this.externalLinkTarget = pageValueMap.get(PN_REDIRECT_TARGET, String.class);
            }
            title = PageUtil.getPageTitle(currentPage);
            createLinkModel();
        }
    }

    private void createLinkModel() {

        this.link = new LinkModelBuilder()
            .with(builder -> {
                builder.setText(title);
                builder.setTarget(this.getHrefTarget());
                builder.setHref(this.getItemURL());
            })
            .createLinkModel();
    }

    private String getItemURL() {
        return StringUtils.isNotBlank(externalLinkTarget) ? externalLinkTarget : getPath();
    }

    private String getHrefTarget() { return StringUtils.isNotBlank(externalLinkTarget) ? LINK_TARGET_BLANK : LINK_TARGET_SELF; }

    public LinkModel getLink() {
        return link;
    }
}
