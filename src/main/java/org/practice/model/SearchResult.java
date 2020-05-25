package org.practice.model;

import org.neo4j.driver.types.Node;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchResult {

	private String id;
	private String type;
	
	public static SearchResult from(Node node) {
	    return new SearchResult(node.get("id").asString(),
	    		node.get("type").asString());
	}
	
}
