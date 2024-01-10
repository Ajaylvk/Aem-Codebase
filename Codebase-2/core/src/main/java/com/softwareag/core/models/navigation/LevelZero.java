package com.softwareag.core.models.navigation;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Data
@EqualsAndHashCode(callSuper = false)
public class LevelZero {


	@Setter @Getter
	private String levelZeroTitle;

	@Setter @Getter
	private List<LevelOne> levelOne;
	
	@Setter @Getter
	private int levelOneListSize;

	@Setter @Getter
	private String overviewLinkLabel;
	
	@Setter @Getter
	private String overviewLinkDestination;
	
	@Setter @Getter
	private String levelZeroLinkDestination;
	
	@Setter @Getter
	private String levelZeroLinkOpenIn;
	
	@Setter @Getter
	private String overviewLinkOpenIn;
}
