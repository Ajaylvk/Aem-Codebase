package com.softwareag.core.models.marketoform;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.softwareag.core.models.marketoform.MarketoFormModel;

import java.util.Objects;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(AemContextExtension.class)
public class MarketoFormModelTest {

	public final AemContext context = new AemContext(ResourceResolverType.RESOURCERESOLVER_MOCK);

	private MarketoFormModel marketoFormModel;

	@BeforeEach
	public void setUp() {
		context.load().json("/components/marketoForm.json", "/content/softwareag");
		context.currentResource("/content/softwareag/test/jcr:content/parsys/marketo-form");

		marketoFormModel = Objects.requireNonNull(context.request()).adaptTo(MarketoFormModel.class);
		assertThat(marketoFormModel).isNotNull();
		
		assertThat(marketoFormModel.getMarketoJSUrl()).isEqualTo("http://app-sj28.marketo.com/js/forms2/js/forms2.min.js");
	}

	@Test
	public void testGetConfigurationPath() {
		assertThat(marketoFormModel.getConfigurationPath())
				.isEqualTo("/content/softwareag/config-page/jcr:content/parsys/marketo-form");
	}
}
