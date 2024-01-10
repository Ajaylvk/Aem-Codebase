package com.softwareag.core.models.navigation;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class AdditionalLinks {
	
	@Setter @Getter
	private String linkLabel;
	
	@Setter @Getter
	private String linkDestination;
	
	@Setter @Getter
	private String linkOpenIn;
	

}
