package com.softwareag.core.models.xf;

import com.day.cq.commons.Externalizer;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Objects;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class XfModelTest {

    private static final String DEST_PATH = "/content/experience-fragments/softwareag/en_us/target/experience-fragment/master";
    private static final String JCR_CONTENT_PATH = DEST_PATH + "/jcr:content";
    private static final String FIRST_CONTENT_PATH = JCR_CONTENT_PATH + "/root/text";

    private final AemContext context = new AemContext();

    @Mock
    private Externalizer externalizer;
    @Mock
    private ResourceResolver resourceResolver;

    @InjectMocks
    private XfModel underTest;

    @BeforeEach
    public void setUp() {
        context.load().json("/components/xftarget.json", DEST_PATH);
        context.registerService(Externalizer.class, externalizer);
        context.addModelsForPackage("com.softwareag.core.models");

        context.currentResource(JCR_CONTENT_PATH);
    }

    @Test
    void getXfRemoteOfferLink() {

        when(externalizer.publishLink(any(ResourceResolver.class), any(String.class))).thenReturn(
            "https://www.softwareag.com" + FIRST_CONTENT_PATH);

        underTest = Objects.requireNonNull(context.currentResource()).adaptTo(XfModel.class);

        assertThat(underTest).isNotNull();
        assertThat(underTest.getXfRemoteOfferLink()).isEqualTo("https://www.softwareag.com" + FIRST_CONTENT_PATH + ".html");
    }

}
