package com.softwareag.core.models.navigation;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
@Data
@EqualsAndHashCode(callSuper = false)
public class SiteNavigation {

	@Setter @Getter
	private String siteName;

	@Setter @Getter
	List<LevelZero> levelZero;

	@Setter @Getter
	private int levlZeroListSize;
	
	@Setter @Getter
	List<AdditionalLinks> additionalLinks;
}
