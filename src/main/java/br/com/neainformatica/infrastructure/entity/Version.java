package br.com.neainformatica.infrastructure.entity;

import java.io.Serializable;
import java.util.Collection;

public class Version implements Serializable {
	private static final long serialVersionUID = 1L;

	private String name;
	private Collection<Build> builds;

	public Version() {
		super();
	}

	public Version(String name, Collection<Build> builds) {
		super();
		this.name = name;
		this.builds = builds;
	}

	public Version(String name) {
		super();
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Collection<Build> getBuilds() {
		return builds;
	}

	public void setBuilds(Collection<Build> builds) {
		this.builds = builds;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Version))
			return false;
		Version other = (Version) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	
	
}
