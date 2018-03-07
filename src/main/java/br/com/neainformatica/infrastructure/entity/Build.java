package br.com.neainformatica.infrastructure.entity;

import java.io.Serializable;
import java.util.Collection;

public class Build implements Serializable  {
	private static final long serialVersionUID = 1L;

	private String name;
	private Collection<Implementation> implementations;
	
	public Build() {
		super();
	}
	
	public Build(String name, Collection<Implementation> implementations) {
		super();
		this.name = name;
		this.implementations = implementations;
	}

	public Build(String name) {
		super();
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Collection<Implementation> getImplementations() {
		return implementations;
	}

	public void setImplementations(Collection<Implementation> implementations) {
		this.implementations = implementations;
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
		if (!(obj instanceof Build))
			return false;
		Build other = (Build) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	
	
}
