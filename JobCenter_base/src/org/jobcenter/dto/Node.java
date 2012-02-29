package org.jobcenter.dto;

import java.util.Set;

public class Node {

	private Integer id = 0;
	private String name;
	private String description;

	private Set<NodeAccessRule> nodeAccessRuleSet;



	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Set<NodeAccessRule> getNodeAccessRuleSet() {
		return nodeAccessRuleSet;
	}
	public void setNodeAccessRuleSet(Set<NodeAccessRule> nodeAccessRuleSet) {
		this.nodeAccessRuleSet = nodeAccessRuleSet;
	}

}
