package com.softwareag.core.models.media;

import com.softwareag.core.services.media.MediaConfigService;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Objects;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(AemContextExtension.class)
@ExtendWith(MockitoExtension.class)
class MediaConfigModelTest {

    public final AemContext context = new AemContext();

    @Mock
    MediaConfigService mediaConfigService;

    private MediaConfigModel mediaConfigModel;

    @BeforeEach
    public void setUp() {
        context.load().json("/components/mediaconfigmodel.json", "/content/softwareag/test/components/media");
        context.currentResource("/content/softwareag/test/components/media/jcr:content/parsys/media");
        context.registerService(MediaConfigService.class, mediaConfigService);
    }

    @Test
    void testGetBreakpoints_whenBreakpoints_AreValid() {
        when(mediaConfigService.getDmBreakpoints()).thenReturn("5");
        mediaConfigModel = Objects.requireNonNull(context.currentResource()).adaptTo(MediaConfigModel.class);
        assertThat(Objects.requireNonNull(mediaConfigModel).getBreakpoints()).isEqualTo("5");
    }

    @Test
    void testGetBreakpoints_whenBreakpoints_DoesNotMatchThePattern() {
        when(mediaConfigService.getDmBreakpoints()).thenReturn("asdf");
        mediaConfigModel = Objects.requireNonNull(context.currentResource()).adaptTo(MediaConfigModel.class);
        assertThat(Objects.requireNonNull(mediaConfigModel).getBreakpoints()).isNull();
    }

    @Test
    void testGetDisableBreakpoints() {
        mediaConfigModel = Objects.requireNonNull(context.currentResource()).adaptTo(MediaConfigModel.class);
        assertThat(Objects.requireNonNull(mediaConfigModel).getDisableBreakpoints()).isEqualTo("disableBreakpoints");
    }
}
