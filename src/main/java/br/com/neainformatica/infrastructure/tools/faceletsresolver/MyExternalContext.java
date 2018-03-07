package br.com.neainformatica.infrastructure.tools.faceletsresolver;

import java.net.MalformedURLException;
import java.net.URL;

import javax.faces.context.ExternalContext;
import javax.faces.context.ExternalContextWrapper;

public class MyExternalContext extends ExternalContextWrapper {
    private ExternalContext wrapped;
    public MyExternalContext(ExternalContext wrapped) {
        this.wrapped = wrapped;
    }
    public URL getResource(String path) throws MalformedURLException {
        //log.debug("Looking for " + path);
        URL url = Thread.currentThread().getContextClassLoader().getResource(path.substring(1));
        if (url == null) {
            url = getWrapped().getResource(path);
        }
        return url;
    }
    @Override
    public ExternalContext getWrapped() {
        return wrapped;
    }
}