package com.softwareag.core.models.forum;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class ForumBean {

	@Setter @Getter
	private String title;
	
	@Setter @Getter
	private String last_posted_at;
	
	@Setter @Getter
	private String url;
	
	@Setter @Getter
	private String posts_count;
	
}
