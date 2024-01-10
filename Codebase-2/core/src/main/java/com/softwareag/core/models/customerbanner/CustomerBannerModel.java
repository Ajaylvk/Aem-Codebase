package com.softwareag.core.models.customerbanner;

import com.softwareag.core.models.link.ImageLinkModel;
import com.softwareag.core.util.AttributeUtil;

import org.apache.commons.collections.CollectionUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class CustomerBannerModel {

    @ValueMapValue
    private String title;

    @ValueMapValue
    private String backgroundStyle;

    @ValueMapValue
    private String fontStyle;

    @ChildResource
    private List<ImageLinkModel> items;

    @ValueMapValue
    private String anchorName;

    /**
     * Function that checks if there is any information to be shown in the component.
     *
     * @return {@link Boolean} {@code true} if there is any information to be shown, otherwise returns {@code false}.
     */
    public Boolean hasContent() {
        return CollectionUtils.isNotEmpty(items);
    }

    /**
     * Function that returns the title configured for the component.
     *
     * @return {@link String} with the title configured for the component.
     */
    public String getTitle() {
        return  title;
    }

    public String getDataAttribValue() {
        return AttributeUtil.removeEmptyLines(getTitle());
    }

    /**
     * Function that returns the background style configured for the component.
     *
     * @return {@link String} with the title configured for the component.
     */
    public String getBackgroundStyle() {
        return  backgroundStyle;
    }

    /**
     * Function that returns the font style configured for the component.
     *
     * @return {@link String} with the font color configured for the component.
     */
    public String getFontStyle() {
        return  fontStyle;
    }

    /**
     * Function that returns a {@link List} of {@link ImageLinkModel} with the information of the customer.
     *
     * @return {@link List} of {@link ImageLinkModel} with the information of the customer.
     */
    public List<ImageLinkModel> getItems() {
        return Optional.ofNullable(items)
            .map(List::stream)
            .orElseGet(Stream::empty)
            .collect(Collectors.toList());
    }

    public String getAnchorName() {
        return anchorName;
    }

}
