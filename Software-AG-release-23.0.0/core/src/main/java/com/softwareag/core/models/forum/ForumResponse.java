package com.softwareag.core.models.forum;

import java.util.List;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class ForumResponse {
	
	@Setter @Getter
	private List<ForumBean> forumBean;
	
	@Setter @Getter
	private String error_type;
	@Setter @Getter
	private String status;
}
