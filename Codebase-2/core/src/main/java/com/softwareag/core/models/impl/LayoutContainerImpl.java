package com.softwareag.core.models.impl;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.models.Component;
import com.softwareag.core.models.LayoutContainer;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Delegate;

@Data
@EqualsAndHashCode(callSuper = false)

@Model(adaptables = { SlingHttpServletRequest.class },

		adapters = { LayoutContainer.class, ComponentExporter.class },

		resourceType = { LayoutContainerImpl.RESOURCE_TYPE,LayoutContainerImpl.TEXT_MEDIA_RESOURCE_TYPE  },

		defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)

@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME,

		extensions = ExporterConstants.SLING_MODEL_EXTENSION)

public class LayoutContainerImpl implements LayoutContainer {

	public static final String RESOURCE_TYPE = "softwareag/components/content/layoutcontainer";
	public static final String TEXT_MEDIA_RESOURCE_TYPE = "softwareag/components/content/textmediacontainer";

	@Self
	@Delegate(types = Component.class)
	@Setter(AccessLevel.NONE)
	@Getter(AccessLevel.NONE)
	private Component component;

	/**
	 * 
	 * Holds the number of columns to be included in the layout container
	 * 
	 */

	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String columnCount;
	
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String backgroundColor;
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String title;
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String description;
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String noMarginLeft;

	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String animation;

}