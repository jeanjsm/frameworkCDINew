package br.com.neainformatica.infrastructure.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

public class FilterKaptcha implements Filter {

	public static final String _LISTA_KSK = "_lista_KSK";

	@Override
	public void init(FilterConfig config) throws ServletException {

	}

	@SuppressWarnings("unchecked")
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		List<String> listaKaptchaKey = (List<String>) ((HttpServletRequest) request).getSession().getAttribute(_LISTA_KSK);

		if (listaKaptchaKey == null) {
			listaKaptchaKey = new ArrayList<String>();
		}

		chain.doFilter(request, response);
		listaKaptchaKey.add((String) ((HttpServletRequest) request).getSession().getAttribute(com.google.code.kaptcha.Constants.KAPTCHA_SESSION_KEY));
		((HttpServletRequest) request).getSession().setAttribute(_LISTA_KSK, listaKaptchaKey);
	}

	@Override
	public void destroy() {
	}

}
