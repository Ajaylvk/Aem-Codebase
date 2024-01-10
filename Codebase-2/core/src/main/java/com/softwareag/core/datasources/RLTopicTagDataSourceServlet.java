package com.softwareag.core.datasources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.servlet.Servlet;

import org.apache.commons.collections4.iterators.TransformIterator;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceMetadata;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.api.wrappers.ValueMapDecorator;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.adobe.granite.ui.components.ds.ValueMapResource;
import com.day.cq.tagging.Tag;
import com.day.crx.JcrConstants;

@Component(service = Servlet.class, property = {
		Constants.SERVICE_DESCRIPTION + "= DataSource Servlet for Timezone Dropdowns",
		"sling.servlet.resourceTypes=" + RLTopicTagDataSourceServlet.RESOURCE_TYPE, "sling.servlet.methods=GET",
		"sling.servlet.extensions=html" })
public class RLTopicTagDataSourceServlet extends SlingSafeMethodsServlet {

	public static final String RESOURCE_TYPE = "softwareag/datasources/topictags";
	public static final String TOPIC = "topic";
	private static final Logger LOG = LoggerFactory.getLogger(RLTopicTagDataSourceServlet.class);

	@SuppressWarnings("unused")
	protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) {
		LOG.info("RLTagDataSourceServlet start:::");
		String tagPath = "/content/cq:tags/softwareag";
		ResourceResolver resourceResolver = request.getResourceResolver();
		Resource tagResource = resourceResolver.getResource(tagPath);
		/*
		 * Get Locale - required to get Locale based language tag values
		 */
		Locale locale = request.getLocale();
		String languageTag = locale.toLanguageTag();

		Resource contentTagResource = tagResource.getChild(TOPIC);
		// LOG.info("contentTagResource::::"+contentTagResource.getName()+":::contentResource:::"+contentTagResource.getPath());
		List<KeyValue> contentTagsList = new ArrayList<>();
		Iterator<Resource> childTags = contentTagResource.listChildren();
		while (childTags.hasNext()) {
			final Resource item = childTags.next();
			KeyValue tempMap = new KeyValue(tagPath, tagPath);
			// LOG.info("Resource Tag ::::"+item.getName());
			final Tag tag = item.adaptTo(Tag.class);
			String key = tag.getName();
			String value = tag.getTagID();
			if (StringUtils.isNotBlank(tag.getName()) && StringUtils.isNotBlank(tag.getTagID())) {
				if (!languageTag.equals("en")) {
					String localizedTagName = tag.getLocalizedTitle(locale);
					KeyValue kvLocale = new KeyValue(localizedTagName, value);
					contentTagsList.add(kvLocale);
					System.out.println("Line 83 - Inside If() - Tag Name: " + localizedTagName);
				} else {
					KeyValue kv = new KeyValue(key, value);
					contentTagsList.add(kv);
					System.out.println("Line 87 - Inside Else - Tag Name: " + key);
				}
			}
		}
		@SuppressWarnings("unchecked")
		DataSource ds = new SimpleDataSource(new TransformIterator(contentTagsList.iterator(), input -> {
			KeyValue keyValue = (KeyValue) input;
			ValueMap vm = new ValueMapDecorator(new HashMap<String, Object>());
			vm.put("value", keyValue.value);
			vm.put("icon", "");
			vm.put("text", keyValue.key);
			return new ValueMapResource(resourceResolver, new ResourceMetadata(), JcrConstants.NT_UNSTRUCTURED, vm);
		}));
		request.setAttribute(DataSource.class.getName(), ds);

	}

	private static class KeyValue {

		private String key;
		private String value;

		private KeyValue(final String newKey, final String newValue) {
			this.key = newKey;
			this.value = newValue;
		}
	}

}
