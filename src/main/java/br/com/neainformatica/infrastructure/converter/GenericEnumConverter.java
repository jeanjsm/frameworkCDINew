package br.com.neainformatica.infrastructure.converter;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

/**
 * Conversor para ser usado pelo componente nea:selectEnum
 * 
 * Converte de e para tipos enumerados (enum), uma vez que o componente nea:selectEnum usa uma lista de enum (List<TipoEnum>)
 * 
 * Baseado em: http://stackoverflow.com/questions/3822058/jsf-2-0-use-enum-in-selectmany-menu
 * 
 * @author marcos, em 11/02/2014
 *
 */
@SuppressWarnings("unused")
@FacesConverter(value="genericEnumConverter")
public class GenericEnumConverter implements Converter {

    private static final String ATTRIBUTE_ENUM_TYPE = "GenericEnumConverter.enumType";

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value instanceof Enum) {
            component.getAttributes().put(ATTRIBUTE_ENUM_TYPE, value.getClass());
            return ((Enum<?>) value).name();
        } else {
        	return null;
        	// Vamos deixar passar valor nulo, senão não é possível não selecionar um valor no <nea:selectEnum>
        	// Estudar a possibilidade de deixar fechado, obrigar a selecionar um valor 
            //throw new ConverterException(new FacesMessage("Valor não é um tipo enumerado (enum): " + value.getClass()));
        }
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        Class<Enum> enumType = (Class<Enum>) component.getAttributes().get(ATTRIBUTE_ENUM_TYPE);
        try {
            return Enum.valueOf(enumType, value);
        } catch (IllegalArgumentException|NullPointerException e) {
        	return null;
        	// Vamos deixar passar valor nulo, senão não é possível não selecionar um valor no <nea:selectEnum>
        	// Estudar a possibilidade de deixar fechado, obrigar a selecionar um valor         	
            //throw new ConverterException(new FacesMessage("Valor não é um tipo enumerado (enum) do tipo: " + enumType));
        }
    }

}