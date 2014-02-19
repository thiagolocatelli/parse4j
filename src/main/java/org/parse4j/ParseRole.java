package org.parse4j;

@ParseClassName("roles")
public class ParseRole extends ParseObject {

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
			throw new IllegalStateException("New roles must specify a name.");
		}
	}

}
