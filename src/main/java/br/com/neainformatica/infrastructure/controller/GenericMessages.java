package br.com.neainformatica.infrastructure.controller;

import java.io.Serializable;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.FacesContext;

public class GenericMessages implements Serializable {
	private static final long serialVersionUID = 1L;

	/* -------- Mensagens de ERROR -------- */
	public void addErrorMessage(String componentId, String errorMessage) {
		addMessage(componentId, errorMessage, FacesMessage.SEVERITY_ERROR);
	}

	public void addErrorMessage(String componentId, String summary, String detail) {
		addMessage(componentId, summary, detail, FacesMessage.SEVERITY_ERROR);
	}

	public void addErrorMessage(String errorMessage) {
		addErrorMessage(null, errorMessage);
	}

	/* -------- Mensagens de INFO -------- */
	public void addInfoMessage(String componentId, String infoMessage) {
		addMessage(componentId, infoMessage, FacesMessage.SEVERITY_INFO);
	}

	public void addInfoMessage(String componentId, String summary, String detail) {
		addMessage(componentId, summary, detail, FacesMessage.SEVERITY_INFO);
	}

	public void addInfoMessage(String infoMessage) {
		addInfoMessage(null, infoMessage);
	}

	/* -------- Mensagens de FATAL -------- */
	public void addFatalMessage(String componentId, String message) {
		addMessage(componentId, message, FacesMessage.SEVERITY_FATAL);
	}

	public void addFatalMessage(String componentId, String summary, String detail) {
		addMessage(componentId, summary, detail, FacesMessage.SEVERITY_FATAL);
	}

	public void addFatalMessage(String message) {
		addFatalMessage(null, message);
	}

	/* -------- Mensagens de WARN -------- */
	public void addWarnMessage(String componentId, String message) {
		addMessage(componentId, message, FacesMessage.SEVERITY_WARN);
	}

	public void addWarnMessage(String componentId, String summary, String detail) {
		addMessage(componentId, summary, detail, FacesMessage.SEVERITY_WARN);
	}

	public void addWarnMessage(String message) {
		addWarnMessage(null, message);
	}

	private void addMessage(String componentId, String errorMessage, Severity severity) {

		FacesContext context = FacesContext.getCurrentInstance();

		FacesMessage message = new FacesMessage(errorMessage);
		message.setSeverity(severity);

		context.addMessage(componentId, message);
	}

	private void addMessage(String componentId, String summary, String detail, Severity severity) {
		FacesContext context = FacesContext.getCurrentInstance();
		FacesMessage message = new FacesMessage(severity, summary, detail);
		context.addMessage(componentId, message);
	}

}
