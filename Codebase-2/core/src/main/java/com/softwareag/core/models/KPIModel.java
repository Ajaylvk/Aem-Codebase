package com.softwareag.core.models;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Model(adaptables = { SlingHttpServletRequest.class },defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class KPIModel {

	@Inject
	private Resource resource;
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String productsCount;
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String description;
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String symbol;
	
	@Setter @Getter
	private String uniqueId;
	
	@PostConstruct
	public void init() {
	
		uniqueId =String.valueOf(Math.abs(resource.getPath().hashCode() - 1));
	}

}
