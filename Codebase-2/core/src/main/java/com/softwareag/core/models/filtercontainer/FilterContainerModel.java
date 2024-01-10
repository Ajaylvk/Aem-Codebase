package com.softwareag.core.models.filtercontainer;

import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.softwareag.core.util.AttributeUtil;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;


@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class FilterContainerModel {

    public static final String FILTER_CONTAINER_RESOURCE_TYPE = "softwareag/components/content/filtercontainer";
    public static final String FILTER_OPTION_ONE = "filterOptionOne";
    public static final String FILTER_OPTION_TWO = "filterOptionTwo";
    public static final String FILTER_OPTION_THREE = "filterOptionThree";

    @Self
    private Resource resource;

    @SlingObject
    private ResourceResolver resourceResolver;

    @ValueMapValue
    private String title;

    @ValueMapValue
    private String copyText;

    @ValueMapValue(name = FILTER_OPTION_ONE)
    private String option1TagPath;
    @ValueMapValue(name = FILTER_OPTION_TWO)
    private String option2TagPath;
    @ValueMapValue(name = FILTER_OPTION_THREE)
    private String option3TagPath;

    private Tag option1Tag;
    private Tag option2Tag;
    private Tag option3Tag;

    private List<String> firstOptionList = new ArrayList<>();
    private List<String> secondOptionList = new ArrayList<>();
    private List<String> thirdOptionList = new ArrayList<>();

    @PostConstruct
    private void init() {
        populateOptionTags();
        firstOptionList = getTags(option1Tag);
        secondOptionList = getTags(option2Tag);
        thirdOptionList = getTags(option3Tag);
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

    public boolean hasContent() {
        return StringUtils.isNotBlank(title) && StringUtils.isNotBlank(copyText);
    }

    public List<String> getFirstOptionList() {
        if (CollectionUtils.isEmpty(firstOptionList)) {
            return Collections.emptyList();
        } else {
            return Collections.unmodifiableList(firstOptionList);
        }
    }

    public List<String> getSecondOptionList() {
        if (CollectionUtils.isEmpty(secondOptionList)) {
            return Collections.emptyList();
        } else {
            return Collections.unmodifiableList(secondOptionList);
        }
    }

    public List<String> getThirdOptionList() {
        if (CollectionUtils.isEmpty(thirdOptionList)) {
            return Collections.emptyList();
        } else {
            return Collections.unmodifiableList(thirdOptionList);
        }
    }

    public Boolean filterOption1Exists() {
        final TagManager tagManager = resourceResolver.adaptTo(TagManager.class);
        Tag option1TagPathExists = Objects.requireNonNull(tagManager).resolve(option1TagPath);
        return option1TagPathExists != null;
    }

    public Boolean filterOption2Exists() {
        final TagManager tagManager = resourceResolver.adaptTo(TagManager.class);
        Tag option2TagPathExists = Objects.requireNonNull(tagManager).resolve(option2TagPath);
        return option2TagPathExists != null;
    }

    public Boolean filterOption3Exists() {
        final TagManager tagManager = resourceResolver.adaptTo(TagManager.class);
        Tag option3TagPathExists = Objects.requireNonNull(tagManager).resolve(option3TagPath);
        return option3TagPathExists != null;
    }

    public String getOption1TagName() {
        return option1Tag != null ? option1Tag.getName() : "";
    }

    public String getOption2TagName() {
        return option2Tag != null ? option2Tag.getName() : "";
    }

    public String getOption3TagName() {
        return option3Tag != null ? option3Tag.getName() : "";
    }

    public String getOption1TagTitle() {
        return option1Tag != null ? option1Tag.getTitle() : "";
    }

    public String getOption2TagTitle() {
        return option2Tag != null ? option2Tag.getTitle() : "";
    }

    public String getOption3TagTitle() {
        return option3Tag != null ? option3Tag.getTitle() : "";
    }

    /**
     * Checks if the given childTagPath matches with the option1TagPath.
     *
     * @param childTagPath
     *     Child tag path to be checked against option1TagPath
     * @return true, if the provided child tag path matches with the option1TagPath.
     */
    public boolean matchesOptionTag1(final String childTagPath) {
        return matchesOptionTag(option1TagPath, childTagPath);
    }

    /**
     * Checks if the given childTagPath matches with the option2TagPath.
     *
     * @param childTagPath
     *     Child tag path to be checked against option2TagPath
     * @return true, if the provided child tag path matches with the option2TagPath.
     */
    public boolean matchesOptionTag2(final String childTagPath) {
        return matchesOptionTag(option2TagPath, childTagPath);
    }

    /**
     * Checks if the given childTagPath matches with the option3TagPath.
     *
     * @param childTagPath
     *     Child tag path to be checked against option3TagPath
     * @return true, if the provided child tag path matches with the option3TagPath.
     */
    public boolean matchesOptionTag3(final String childTagPath) {
        return matchesOptionTag(option3TagPath, childTagPath);
    }

    /**
     * Populates the contentItemTags with the tags specified on the children resources / content items.
     */
    private void populateOptionTags() {
        final TagManager tagManager = resourceResolver.adaptTo(TagManager.class);
        option1Tag = Objects.requireNonNull(tagManager).resolve(option1TagPath);
        option2Tag = Objects.requireNonNull(tagManager).resolve(option2TagPath);
        option3Tag = Objects.requireNonNull(tagManager).resolve(option3TagPath);
    }

    /**
     * Checks if the given childTagPath is matching with the optionTagPath or a child of it.
     *
     * @param optionTagPath
     *     Option tag path.
     * @param childTagPath
     *     Tag path
     * @return true, if the childTagPath equals to the optionTagPath or is a child of it. Otherwise, false.
     */
    public static boolean matchesOptionTag(final String optionTagPath, final String childTagPath) {
        if (StringUtils.isBlank(optionTagPath) || StringUtils.isBlank(childTagPath)) {
            return false;
        }
        return StringUtils.equals(optionTagPath, childTagPath) || childTagPath.startsWith(optionTagPath + "/");
    }

    private List<String> getTags(final Tag tag) {
        List<String> tagList = new ArrayList<>();
        if (tag != null) {
            tagList.add(tag.getName());
            Iterator<Tag> children = tag.listChildren();
            while (children.hasNext()) {
                Tag next = children.next();
                tagList.add(next.getName());
            }
        }
        return tagList;
    }
}
