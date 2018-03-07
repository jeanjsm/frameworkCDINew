package br.com.neainformatica.infrastructure.entity;

import java.io.Serializable;
import java.util.Collection;

public class About implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String name;
	private Collection<Version> versions; 

	public About() {
		super();
	}

	public About(String name) {
		super();
		this.name = name;
	}

	public About(String name, Collection<Version> versions) {
		super();
		this.name = name;
		this.setVersions(versions);
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Collection<Version> getVersions() {
		return versions;
	}

	public void setVersions(Collection<Version> versions) {
		this.versions = versions;
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
		if (!(obj instanceof About))
			return false;
		About other = (About) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return name;
	}

}
