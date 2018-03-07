package br.com.neainformatica.infrastructure.controller;

import br.com.neainformatica.infrastructure.entity.NaAgendamento;
import br.com.neainformatica.infrastructure.services.GenericService;
import br.com.neainformatica.infrastructure.services.NaAgendamentoService;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;

/**
 * Created by rodolpho.sotolani on 11/04/2017.
 */
@Named
@ConversationScoped
public class NaAgendamentoController extends GenericController<NaAgendamento> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private NaAgendamentoService service;

    @PostConstruct
    public void createDynamicColumns() {

    }

    @Override
    public void setService(GenericService<NaAgendamento> service) {
        super.setService(this.service);
    }

    @Override
    public NaAgendamentoService getService() {
        return this.service;
    }
}
