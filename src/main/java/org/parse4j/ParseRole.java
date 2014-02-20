package org.parse4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ParseClassName("roles")
public class ParseRole extends ParseObject {
	
	private static Logger LOGGER = LoggerFactory.getLogger(ParseRole.class);

	public ParseRole() {
	}

	public ParseRole(String name) {
		this();
		setName(name);
	}

	public void setName(String name) {
		put("name", name);
	}

	public String getName() {
		return getString("name");
	}

	@Override
	void validateSave() {
		if ((getObjectId() == null) && (getName() == null)) {
			LOGGER.error("New roles must specify a name.");
			throw new IllegalStateException("New roles must specify a name.");
		}
	}

}
