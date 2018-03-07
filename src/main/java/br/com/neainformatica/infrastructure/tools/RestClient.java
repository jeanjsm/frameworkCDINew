package br.com.neainformatica.infrastructure.tools;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;

/**
 * Classe para trabalhar com clientes REST
 *
 * @author elielcio.santos
 */
public class RestClient {

    SimpleModule converters = new SimpleModule();

    Object retornoServico;
    TypeRequest typeRequest;
    private String path;
    private MultivaluedMap parameter;
    private Object objectRequest;
    private Object objectResponse;
    private String typeDataRequest;
    private String typeDataResponse;
    private Class classResponse;
    private boolean imprimeJson = false;
    private int timeoutMinutes;
    private String token;

    public RestClient(String path, TypeRequest typeRequest, String token) {

        this.path = path;
        this.typeRequest = typeRequest;
        this.token = token;
        this.timeoutMinutes = 60;

        setTypeDataJSON();

        setConverterSerialize(Date.class, new JsonConverterDateTime.serialize());
        setConverterDesserialize(Date.class, new JsonConverterDateTime.deserialize());

    }

    private RestClient setTypeDataJSON() {
        this.typeDataRequest = MediaType.APPLICATION_JSON;
        this.typeDataResponse = MediaType.APPLICATION_JSON;
        return this;
    }

    /**
     * Utilize este para definir o tipo de dados a ser enviado no request. Tipo
     * padrão: JSON
     *
     * @param type
     * @return
     */
    public RestClient setTypeRequest(String type) {
        this.typeDataRequest = type;
        return this;
    }

    /**
     * Utilize este para definir o tipo de dados a ser recebido no response.
     * Tipo padrão: JSON
     *
     * @param type
     * @return
     */
    public RestClient setTypeResponse(String type) {
        this.typeDataRequest = type;
        return this;
    }

    /**
     * utilize para definir quais dados serão enviados para o serviço
     *
     * @param objectRequest
     * @return
     */
    public RestClient setObjectRequest(Object objectRequest) {
        this.objectRequest = objectRequest;
        return this;
    }

    /**
     * Utilize para definir o tipo de objeto que será retornado pelo serviço
     *
     * @param objectResponse
     * @return
     */
    public RestClient setObjectResponse(Object objectResponse) {
        this.objectResponse = objectResponse;
        return this;

    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public RestClient setConverterSerialize(Class<?> classe, JsonSerializer serializer) {

        if (this.converters == null)
            this.converters = new SimpleModule();

        this.converters.addSerializer(classe, serializer);

        return this;

    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public RestClient setConverterDesserialize(Class<?> classe, JsonDeserializer serializer) {

        if (this.converters == null)
            this.converters = new SimpleModule();

        this.converters.addDeserializer(classe, serializer);

        return this;

    }

    private String convertObjetRequestToJson(Object obj) throws JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();

        if (this.converters != null)
            mapper.registerModule(this.converters);

        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        String json = mapper.writeValueAsString(obj);
        if (imprimeJson)
            System.out.println(json);

        return json;
    }

    private Object convertJsonToObject(String json, Class<?> classe) throws JsonParseException, JsonMappingException, IOException {

        if (classe.getName().startsWith("java.lang."))
            return json;

        ObjectMapper mapper = new ObjectMapper();
        if (this.converters != null)
            mapper.registerModule(this.converters);

        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        if (json == null || json.trim().equals(""))
        	return null;
        
        Object retorno = mapper.readValue(json, classe);

        if (imprimeJson)
            System.out.println(json);

        return retorno;

    }

    public RestClient setClassResponse(Class<?> classResponse) {
        this.classResponse = classResponse;
        return this;
    }

    private void postPut(TypeRequest TypeRequest) {
        try {

            Client client = Client.create();
            client.setReadTimeout(1000 * 60 * this.timeoutMinutes);
            client.setConnectTimeout(1000 * 15);

            WebResource webResource = client.resource(this.path);
            if (this.parameter != null)
                webResource.queryParams(this.parameter);
            ClientResponse response;

            if (TypeRequest.equals(TypeRequest.POST)) {
                response = webResource.type(this.typeDataRequest)
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .post(ClientResponse.class, convertObjetRequestToJson(this.objectRequest));
            } else {
                if (TypeRequest.equals(TypeRequest.PUT)) {
                    response = webResource.type(this.typeDataRequest)
                            .header(HttpHeaders.AUTHORIZATION, token)
                            .put(ClientResponse.class, convertObjetRequestToJson(this.objectRequest));
                } else {
                    throw new RuntimeException("este método não pode receber requisições deste tipo");
                }
            }

            if (response.getStatus() >= 300) {

                if (Response.Status.UNAUTHORIZED.getStatusCode() == response.getStatus()) {
//                    AutenticarService.realizarLougout();
                    throw new RuntimeException("Erro: Usuário não autorizado para esta operação!");
                }

                String erro = "Erro: " + response.getStatus() + ". " + response.getEntity(String.class).toString();
                throw new RuntimeException(erro);
            }

            this.retornoServico = response.getEntity(String.class).toString();

        } catch (UniformInterfaceException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getCause());
        } catch (ClientHandlerException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getCause());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getCause());
        }
    }

    private void post() {
        postPut(TypeRequest.POST);
    }

    private void put() {
        postPut(TypeRequest.PUT);
    }

    private void get() {
        //TODO: Investigar Utilização de lib glassfish e jettison no RestClient
        Client client = Client.create();
        client.setReadTimeout(1000 * 60 * this.timeoutMinutes);
        client.setConnectTimeout(1000 * 15);

        WebResource resource = client.resource(this.path);
        ClientResponse response;

        response = resource.type(this.typeDataRequest)
                .header(HttpHeaders.AUTHORIZATION, token)
                .get(ClientResponse.class);

        if (response.getStatus() >= 300) {

            if (Response.Status.UNAUTHORIZED.getStatusCode() == response.getStatus()) {
                throw new RuntimeException("Erro: Usuário não autorizado para esta operação!");
            }

            String erro = "Erro: " + response.getStatus() + ". " + response.getEntity(String.class).toString();
            throw new RuntimeException(erro);
        }

        this.retornoServico = response.getEntity(String.class).toString();

    }

    private void delete() {
        // TODO: falta implementar
        Client c = Client.create();
        WebResource webResource = c.resource(this.path);
        ClientResponse response;

        response = webResource.type(this.typeDataRequest)
                .header(HttpHeaders.AUTHORIZATION, token)
                .delete(ClientResponse.class);

        if (response.getStatus() >= 300) {

            if (Response.Status.UNAUTHORIZED.getStatusCode() == response.getStatus()) {
                throw new RuntimeException("Erro: Usuário não autorizado para esta operação!");
            }

            String erro = "Erro: " + response.getStatus() + ". " + response.getEntity(String.class).toString();
            throw new RuntimeException(erro);
        }

    }

    /**
     * Utilize para chamadas sem retorno
     *
     * @return
     */
    public RestClient build() {

        if (this.retornoServico != null)
            throw new RuntimeException("este serviço ja foi executado, verifique se anteriormente não foi chamado um asList ou asObject");

        if (typeRequest.equals(TypeRequest.POST))
            post();
        else if (typeRequest.equals(TypeRequest.PUT))
            put();
        else if (typeRequest.equals(TypeRequest.GET))
            get();
        else if (typeRequest.equals(TypeRequest.DELETE))
            delete();

        return this;

    }

    public RestClient setParameters(Map parameters) {
        this.parameter = new MultivaluedHashMap(parameters);
        return this;
    }

    public RestClient setParameter(String key, String value) {
        this.parameter.add(key, value);
        return this;
    }

    /**
     * Este método pode ser utilizado para retornar os dados do JSON em formato
     * de lista, utilize quando o retorno for uma lista de itens
     *
     * @return List<?>
     */
    public List<?> asList() {

        build();

        try {
            if (!this.classResponse.isArray())
                throw new RuntimeException("O parametro classResponse não é um array, favor verificar.");

            Object[] ret = (Object[]) convertJsonToObject(this.retornoServico.toString(), this.classResponse);

            List<Object> list = new ArrayList<>();

            for (Object object : ret)
                list.add(object);

            return list;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getCause());
        }
    }

    /**
     * Este método pode ser utilizado para retornar os dados do JSON em formato
     * de Objeto, utilize onde o retorno é um único objeto
     *
     * @return List<?>
     */
    public Object asObject() {

        build();

        try {

            if (this.classResponse.isArray())
                throw new RuntimeException("O parametro classResponse é um array, favor verificar.");

            return convertJsonToObject(this.retornoServico.toString(), this.classResponse);

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getCause());
        }
    }

    /**
     * Metodo utilizado para testes do JSon Enviado do Client ao Server.
     *
     * @return
     */
    public RestClient printJson() {
        this.imprimeJson = true;
        return this;
    }

    public RestClient setTimeoutInMinutes(int timeout) {
        this.timeoutMinutes = timeout;
        return this;
    }

    public enum TypeRequest {
        POST, PUT, GET, DELETE
    }

}
