package br.com.neainformatica.infrastructure.service.pesquisa;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.hibernate.internal.CriteriaImpl;
import org.hibernate.internal.CriteriaImpl.Subcriteria;


@SuppressWarnings({"unchecked", "rawtypes"})
public class CriteriaSearchParameters{
	
	public Date dataBanco(String data){
		String dia = data.substring(0, 2);
		String mes = data.substring(3, 5);
		String ano = data.substring(6, 10);
		String retorno = ano + "-" + mes + "-" + dia + " 00:00:00";
			
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 00:00:00");   
		Date date = null;
		try {
			date = (Date)formatter.parse(retorno);
		} catch (ParseException e) {
			e.printStackTrace();
		} 
		return date;
	}
	
	
	
	
	public Criteria field(Criteria consult, List<SearchParameter> parameters) {

		if (parameters != null) {

			for (SearchParameter searchParameter : parameters) {
				
				if (searchParameter.getOperation() == null || (
						(!searchParameter.getOperation().equals(OperationEnum.ISNULL)
								&&!searchParameter.getOperation().equals(OperationEnum.ISNOTNULL)
						)
						&& searchParameter.getInitialValue() == null && searchParameter.getFinalValue() == null)){
					continue;
				}
				
				if(searchParameter == null ||(
						(!searchParameter.getOperation().equals(OperationEnum.ISNULL)
								&&!searchParameter.getOperation().equals(OperationEnum.ISNOTNULL)
						)
						&&(searchParameter.getInitialValue() == null ||
						searchParameter.getInitialValue().toString().equals("NaN")))){
					continue;
				}
				else if (searchParameter.getJavaType().toString().equals("ENUM")) {

					searchParameter.setOperation(OperationEnum.EQUALS);
					
					if(searchParameter.getInitialValue().getClass().toString().contains("String")){						
								
						CriteriaImpl criteriaImpl = (CriteriaImpl) consult;
	
						Class className = null;
						Field field = null;
	
						Method findMethod = null;
	
						try {
							className = Class.forName(criteriaImpl.getEntityOrClassName());
	
							if (searchParameter.getField().indexOf(".") + 1 > 0) {
								if(criteriaImpl.iterateSubcriteria().hasNext()){
									for (Iterator i = criteriaImpl.iterateSubcriteria(); i.hasNext();) {
										Subcriteria sub = (Subcriteria) i.next();
										if (searchParameter.getField().startsWith(sub.getAlias() + ".")) {
											field = className.getDeclaredField(sub.getPath());
											Class cn = field.getType();
											field = cn.getDeclaredField(searchParameter.getField().substring(searchParameter.getField().indexOf(".") + 1));
											break;
										}
									}
								}
								else{
									throw new RuntimeException("Necessarios joins para esta consulta: " + searchParameter.getField().substring(0, searchParameter.getField().indexOf(".")));
								}
							} else {
								field = className.getDeclaredField(searchParameter.getField());
							}
							
							className = field.getType(); // aqui pega o nome do ENUM
	
							Class[] args1 = new Class[1];
							args1[0] = String.class;
	
							findMethod = className.getDeclaredMethod("find", args1);
	
							List enuns = (List) findMethod.invoke(className, new Object[] { searchParameter.getInitialValue() });
							
							if(enuns.size()==0){
	
								consult = consult.add(Restrictions.isNull((String) searchParameter.getField()));
								consult = consult.add(Restrictions.isNotNull((String) searchParameter.getField()));
							}
							else{
								Disjunction disjunction = Restrictions.disjunction();
							
								for (Object object : enuns) {
									disjunction.add(Restrictions.eq(((String) searchParameter.getField()), object));
								}
								consult = consult.add(disjunction);
							}
	
	
						} catch (ClassNotFoundException e) {
							e.printStackTrace();
						} catch (SecurityException e) {
							e.printStackTrace();
						} catch (NoSuchFieldException e) {
							e.printStackTrace();
						} catch (NoSuchMethodException e) {
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						} catch (IllegalArgumentException e) {
							e.printStackTrace();
						} catch (InvocationTargetException e) {
							e.printStackTrace();
						}
					}

					//caso for um enum
					else{
						consult.add(Restrictions.eq(((String) searchParameter.getField()), searchParameter.getInitialValue()));
					}

				}

				else if (searchParameter.getOperation().equals(OperationEnum.EQUALS)) {
					if (searchParameter.getJavaType().toString().isEmpty()) {
						consult = consult.add(Restrictions.eq(((String) searchParameter.getField()),searchParameter.getInitialValue()));
					} else if (searchParameter.getJavaType().toString().equals("LONG")) {
						consult = consult.add(Restrictions.eq(((String) searchParameter.getField()), Long.parseLong(searchParameter.getInitialValue().toString())));
					} else if (searchParameter.getJavaType().toString().equals("DATE")) {
						Date data = dataBanco(searchParameter.getInitialValue().toString());

						consult = consult.add(Restrictions.eq(((String) searchParameter.getField()), data));
					} else if (searchParameter.getJavaType().toString().equals("STRING")) {
						consult = consult.add(Restrictions.eq(((String) searchParameter.getField()),searchParameter.getInitialValue()).ignoreCase());
					} else if (searchParameter.getJavaType().toString().equals("INTEGER")) {
						consult = consult.add(Restrictions.eq(((String) searchParameter.getField()), Integer.parseInt(searchParameter.getInitialValue().toString())));
					} else {
						consult = consult.add(Restrictions.eq(((String) searchParameter.getField()),searchParameter.getInitialValue()));
					}
				} else if (searchParameter.getOperation().equals(
						OperationEnum.LIKE)) {

					if (searchParameter.getJavaType().toString().equals("LONG")) {
						consult = consult.add(Restrictions.like(((String) searchParameter.getField()),Long.parseLong(searchParameter.getInitialValue().toString())));
					} else if (searchParameter.getJavaType().toString().equals("DATE")) {

						Date data = dataBanco(searchParameter.getInitialValue().toString());

						consult = consult.add(Restrictions.like(((String) searchParameter.getField()), data));
					} else {
						consult = consult.add(Restrictions.ilike(((String) searchParameter.getField()),searchParameter.getInitialValue().toString().trim(), MatchMode.ANYWHERE));
					}

				} else if (searchParameter.getOperation().equals(
						OperationEnum.GREATER_THAN)) {

					if (searchParameter.getJavaType().toString().equals("LONG")) {
						consult = consult.add(Restrictions.gt(((String) searchParameter.getField()), Long.parseLong(searchParameter.getInitialValue().toString())));
					} else if (searchParameter.getJavaType().toString().equals("DATE")) {

						Date data = dataBanco(searchParameter.getInitialValue().toString());
						consult = consult.add(Restrictions.gt(((String) searchParameter.getField()), data));
					} else {
						consult = consult.add(Restrictions.gt(((String) searchParameter.getField()),searchParameter.getInitialValue()));
					}
				} else if (searchParameter.getOperation().equals(
						OperationEnum.LESS_THAN)) {

					if (searchParameter.getJavaType().toString().equals("LONG")) {
						consult = consult.add(Restrictions.lt(((String) searchParameter.getField()), Long.parseLong(searchParameter.getInitialValue().toString())));
					} else if (searchParameter.getJavaType().toString().equals("DATE")) {
						Date data = dataBanco(searchParameter.getInitialValue().toString());

						consult = consult.add(Restrictions.lt(((String) searchParameter.getField()), data));
					} else {
						consult = consult.add(Restrictions.lt(((String) searchParameter.getField()),searchParameter.getInitialValue()));
					}
				} else if (searchParameter.getOperation().equals(OperationEnum.STARTING)) {

					if (searchParameter.getJavaType().toString().equals("LONG")) {
						consult = consult.add(Restrictions.ge(((String) searchParameter.getField()), Long.parseLong(searchParameter.getInitialValue().toString())));
					} else if (searchParameter.getJavaType().toString().equals("DATE")) {

						Date data = dataBanco(searchParameter.getInitialValue().toString());
						consult = consult.add(Restrictions.ge(((String) searchParameter.getField()), data));
					} else if (searchParameter.getJavaType().toString().equals("STRING")) {

						consult = consult.add(Restrictions.ilike(((String) searchParameter.getField()),searchParameter.getInitialValue().toString()+ "%"));
					} else if (searchParameter.getJavaType().toString().equals("INTEGER")) {
						consult = consult.add(Restrictions.ge(((String) searchParameter.getField()), Integer.parseInt(searchParameter.getInitialValue().toString())));
					} else {
						consult = consult.add(Restrictions.ge(((String) searchParameter.getField()),searchParameter.getInitialValue()));
					}
				} else if (searchParameter.getOperation().equals(OperationEnum.ENDING)) {

					if (searchParameter.getJavaType().toString().equals("LONG")) {
						consult = consult.add(Restrictions.le(((String) searchParameter.getField()), Long.parseLong(searchParameter.getInitialValue().toString())));
					} else if (searchParameter.getJavaType().toString().equals("DATE")) {

						Date data = dataBanco(searchParameter.getInitialValue().toString());
						consult = consult.add(Restrictions.le(((String) searchParameter.getField()), data));
					} else if (searchParameter.getJavaType().toString().equals("STRING")) {

						consult = consult.add(Restrictions.ilike(((String) searchParameter.getField()), "%"+ searchParameter.getInitialValue().toString()));

					} else {
						consult = consult.add(Restrictions.le(((String) searchParameter.getField()),searchParameter.getInitialValue()));
					}
				} else if (searchParameter.getOperation().equals(
						OperationEnum.BETWEEN)) {

					if (searchParameter.getJavaType().toString().equals("LONG")) {
						consult = consult.add(Restrictions.between(((String) searchParameter.getField()), Long.parseLong(searchParameter.getInitialValue().toString()),Long.parseLong(searchParameter.getFinalValue().toString())));
					} else if (searchParameter.getJavaType().toString().equals("DATE")) {

						Date dateinicial = dataBanco(searchParameter.getInitialValue().toString());
						Date datefinal = dataBanco(searchParameter.getFinalValue().toString());

						consult = consult.add(Restrictions.between(((String) searchParameter.getField()),dateinicial, datefinal));
					} else {
						consult = consult.add(Restrictions.between(((String) searchParameter.getField()),searchParameter.getInitialValue(),searchParameter.getFinalValue()));
					}
				} 
				
				else if (searchParameter.getOperation().equals(OperationEnum.DIFERENTE)) {
					if (searchParameter.getJavaType().toString().isEmpty()) {
						consult = consult.add(Restrictions.ne(((String) searchParameter.getField()),searchParameter.getInitialValue()));
					} else if (searchParameter.getJavaType().toString().equals("LONG")) {
						consult = consult.add(Restrictions.ne(((String) searchParameter.getField()), Long.parseLong(searchParameter.getInitialValue().toString())));
					} else if (searchParameter.getJavaType().toString().equals("DATE")) {
						Date data = dataBanco(searchParameter.getInitialValue().toString());

						consult = consult.add(Restrictions.ne(((String) searchParameter.getField()), data));
					} else if (searchParameter.getJavaType().toString().equals("STRING")) {
						consult = consult.add(Restrictions.ne(((String) searchParameter.getField()),searchParameter.getInitialValue()).ignoreCase());
					} else if (searchParameter.getJavaType().toString().equals("INTEGER")) {
						consult = consult.add(Restrictions.ne(((String) searchParameter.getField()), Integer.parseInt(searchParameter.getInitialValue().toString())));
					} else {
						consult = consult.add(Restrictions.ne(((String) searchParameter.getField()),searchParameter.getInitialValue()));
					}
				} 
				else if (searchParameter.getOperation().equals(OperationEnum.ISNULL)) {

					if (searchParameter.getField() != null) {
						consult = consult.add(Restrictions.isNull((String) searchParameter.getField()));
					}
				}
				else if (searchParameter.getOperation().equals(OperationEnum.ISNOTNULL)) {

					if (searchParameter.getField() != null) {
						consult = consult.add(Restrictions.isNotNull((String) searchParameter.getField()));
					}
				}
				else if (searchParameter.getOperation().equals(OperationEnum.IN)) {

					if (searchParameter.getField() != null) {
						consult = consult.add(Restrictions.in(((String) searchParameter.getField()),((ArrayList) searchParameter.getInitialValue())));
					}
				}

			}

			return consult;
		}

		return consult;
	}
	
	
	public DetachedCriteria field(DetachedCriteria consult, List<SearchParameter> parameters){
		
		if(parameters != null){
		
			
			for (SearchParameter searchParameter : parameters) {
				
				
				if(searchParameter.getOperation().equals(OperationEnum.EQUALS)){
	
					if(searchParameter.getJavaType().toString().isEmpty()){
						consult = consult.add(Restrictions.eq(((String)searchParameter.getField()),searchParameter.getInitialValue()));
					}
					else if(searchParameter.getJavaType().toString() == "LONG"){
						consult = consult.add(Restrictions.eq(((String)searchParameter.getField()),Long.parseLong(searchParameter.getInitialValue().toString())));
					}
					else if(searchParameter.getJavaType().toString() == "DATE"){
						Date data = dataBanco(searchParameter.getInitialValue().toString());
						
						consult = consult.add(Restrictions.eq(((String)searchParameter.getField()),data));
					}
					else{					
						consult = consult.add(Restrictions.eq(((String)searchParameter.getField()),searchParameter.getInitialValue()));
					}
				}
				else if(searchParameter.getOperation().equals(OperationEnum.LIKE)){

					if(searchParameter.getJavaType().toString() == "LONG"){
						consult = consult.add(Restrictions.like(((String)searchParameter.getField()),Long.parseLong(searchParameter.getInitialValue().toString())));
					}
					else if(searchParameter.getJavaType().toString() == "DATE"){
						
						Date data = dataBanco(searchParameter.getInitialValue().toString());
						
						consult = consult.add(Restrictions.eq(((String)searchParameter.getField()),data));
					}
					else{	
						consult = consult.add(Restrictions.ilike(((String)searchParameter.getField()),"%"+searchParameter.getInitialValue().toString()+"%"));
					}
					
					
					
				}
				else if(searchParameter.getOperation().equals(OperationEnum.GREATER_THAN)){
	
					if(searchParameter.getJavaType().toString() == "LONG"){
						consult = consult.add(Restrictions.gt(((String)searchParameter.getField()),Long.parseLong(searchParameter.getInitialValue().toString())));
					}
					else if(searchParameter.getJavaType().toString() == "DATE"){

						Date data = dataBanco(searchParameter.getInitialValue().toString());
						consult = consult.add(Restrictions.gt(((String)searchParameter.getField()),data));
					}
					else{	
						consult = consult.add(Restrictions.gt(((String)searchParameter.getField()),searchParameter.getInitialValue()));
					}
				}
				else if(searchParameter.getOperation().equals(OperationEnum.LESS_THAN)){
					
					if(searchParameter.getJavaType().toString() == "LONG"){
						consult = consult.add(Restrictions.lt(((String)searchParameter.getField()),Long.parseLong(searchParameter.getInitialValue().toString())));
					}
					else if(searchParameter.getJavaType().toString() == "DATE"){
						Date data = dataBanco(searchParameter.getInitialValue().toString());
						
						consult = consult.add(Restrictions.lt(((String)searchParameter.getField()),data));
					}
					else{	
						consult = consult.add(Restrictions.lt(((String)searchParameter.getField()),searchParameter.getInitialValue()));
					}
				}
				else if(searchParameter.getOperation().equals(OperationEnum.STARTING)){
					
					if(searchParameter.getJavaType().toString() == "LONG"){
						consult = consult.add(Restrictions.ge(((String)searchParameter.getField()),Long.parseLong(searchParameter.getInitialValue().toString())));
					}
					else if(searchParameter.getJavaType().toString() == "DATE"){

						Date data = dataBanco(searchParameter.getInitialValue().toString());
						consult = consult.add(Restrictions.ge(((String)searchParameter.getField()),data));
					}
					else if(searchParameter.getJavaType().toString() == "STRING"){

						consult = consult.add(Restrictions.ilike(((String)searchParameter.getField()),searchParameter.getInitialValue().toString()+"%"));

					}
					else{						
						consult = consult.add(Restrictions.ge(((String)searchParameter.getField()),searchParameter.getInitialValue()));
					}
				}
				else if(searchParameter.getOperation().equals(OperationEnum.ENDING)){

					if(searchParameter.getJavaType().toString() == "LONG"){
						consult = consult.add(Restrictions.le(((String)searchParameter.getField()),Long.parseLong(searchParameter.getInitialValue().toString())));
					}
					else if(searchParameter.getJavaType().toString() == "DATE"){

						Date data = dataBanco(searchParameter.getInitialValue().toString());
						consult = consult.add(Restrictions.le(((String)searchParameter.getField()),data));
					}
					else if(searchParameter.getJavaType().toString() == "STRING"){

						consult = consult.add(Restrictions.ilike(((String)searchParameter.getField()),searchParameter.getInitialValue().toString()+"%"));

					}
					else{						
						consult = consult.add(Restrictions.le(((String)searchParameter.getField()),searchParameter.getInitialValue()));
					}
				}
				else if(searchParameter.getOperation().equals(OperationEnum.BETWEEN)){
					

					if(searchParameter.getJavaType().toString() == "LONG"){
						consult = consult.add(Restrictions.between(((String)searchParameter.getField()),Long.parseLong(searchParameter.getInitialValue().toString()),Long.parseLong(searchParameter.getFinalValue().toString())));
					}
					else if(searchParameter.getJavaType().toString().equals("DATE")){

						Date dateinicial = dataBanco(searchParameter.getInitialValue().toString());
						Date datefinal = dataBanco(searchParameter.getFinalValue().toString());
						
						consult = consult.add(Restrictions.between(((String)searchParameter.getField()),dateinicial,datefinal));
					}
					else{	
						consult = consult.add(Restrictions.between(((String)searchParameter.getField()),searchParameter.getInitialValue(),searchParameter.getFinalValue()));
					}
				}else if(searchParameter.getOperation().equals(OperationEnum.ISNULL)){					

					if(searchParameter.getField() != null){
						consult = consult.add(Restrictions.isNull((String)searchParameter.getField()));
					}
				}
					
				
			}
			
			
			return consult;
		}

		return consult;
	}
}
