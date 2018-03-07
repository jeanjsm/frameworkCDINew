package br.com.neainformatica.infrastructure.tools.faceletsresolver;

import java.net.URL;

import javax.faces.view.facelets.ResourceResolver;

public class MyResourceResolver extends ResourceResolver {
	private ResourceResolver parent;

	public MyResourceResolver(ResourceResolver parent) {
		this.parent = parent;
	}

	@Override
	public URL resolveUrl(String path) {
		URL url = null;
		try {
			url = url = Thread.currentThread().getContextClassLoader().getResource(path.substring(1));
			if (url == null) {
				url = parent.resolveUrl(path);
			}
		} catch (Exception e) {
			e.printStackTrace(); // To change body of catch statement use File |
									// Settings | File Templates.
		}
		return url;
	}
}