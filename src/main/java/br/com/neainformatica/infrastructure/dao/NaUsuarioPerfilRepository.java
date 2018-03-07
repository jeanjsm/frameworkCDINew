package br.com.neainformatica.infrastructure.dao;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Query;

import br.com.neainformatica.infrastructure.controller.InfrastructureController;
import br.com.neainformatica.infrastructure.entity.NaPerfil;
import br.com.neainformatica.infrastructure.entity.NaUsuario;
import br.com.neainformatica.infrastructure.entity.NaUsuarioPerfil;

public class NaUsuarioPerfilRepository extends GenericRepository<NaUsuarioPerfil> implements Serializable {

	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unchecked")
	public List<NaUsuarioPerfil> buscaUsuarioPerfil(NaUsuario usuario) {

		Query query = getSession().createQuery("select up from NaUsuarioPerfil up join fetch up.perfil p where up.usuario = :usuario and p.sistema.id = :sistemaId");
		query.setParameter("usuario", usuario);
		query.setParameter("sistemaId", InfrastructureController.getNeaInfrastructureSistemaId());

		return query.list();

	}

	@SuppressWarnings("unchecked")
	public List<NaUsuarioPerfil> buscaUsuarioPerfilPorPerfil(NaPerfil naPerfil) {

		Query query = getSession().createQuery("select up from NaUsuarioPerfil up join up.perfil p join fetch up.usuario u  where up.perfil = :perfil and p.sistema.id = :sistemaId");
		query.setParameter("perfil", naPerfil);
		query.setParameter("sistemaId", InfrastructureController.getNeaInfrastructureSistemaId());

		return query.list();
	}
}
