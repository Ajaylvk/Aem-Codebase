package com.softwareag.core.models.filtercontainer;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Objects;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
public class FilterContainerModelTest {

    private static final String DEST_PATH = "/content/softwareag/test";
    private static final String TAGS_PATH = "/content/cq:tags/softwareag";

    private final AemContext context = new AemContext();

    private FilterContainerModel filterContainer;

    @BeforeEach
    public void setUp() {
        context.load().json("/components/filtercontainer.json", DEST_PATH);
        context.load().json("/tags/tagList.json", TAGS_PATH);
        context.currentResource(DEST_PATH + "/jcr:content/parsys/filtercontainer");
        context.addModelsForPackage("com.softwareag.core.models");

        filterContainer = Objects.requireNonNull(context.currentResource()).adaptTo(FilterContainerModel.class);
        assertThat(filterContainer).isNotNull();
    }

    @Test
    void testGetOptionTags() {
        assertEquals("industry", filterContainer.getOption1TagName());
        assertEquals("countries", filterContainer.getOption2TagName());
        assertEquals("asset-type", filterContainer.getOption3TagName());
    }

    @Test
    void testFilterOptionsExist() {
        assertTrue(filterContainer.filterOption1Exists());
        assertTrue(filterContainer.filterOption2Exists());
        assertTrue(filterContainer.filterOption3Exists());
    }

    @Test
    void testMatchesOptionTags() {
        assertFalse(filterContainer.matchesOptionTag1("softwareag:asset-type/image"));
        assertFalse(filterContainer.matchesOptionTag2("softwareag:asset-type/image"));
        assertTrue(filterContainer.matchesOptionTag3("softwareag:asset-type/image"));
    }

}
