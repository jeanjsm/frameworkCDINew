/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.neainformatica.infrastructure.tools;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.html.simpleparser.HTMLWorker;
import com.lowagie.text.pdf.PdfWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author rodolpho.sotolani
 */
public class HtmlToPDF {

    /**
     * Funcao que recebe uma String
     *
     * @param textoHtml
     */
    public static void imprimirHtml(String textoHtml, String nomeRelatorio) {
        FacesContext context;
        HttpServletResponse response;
        ServletOutputStream output;

        context = FacesContext.getCurrentInstance();
        response = (HttpServletResponse) context.getExternalContext().getResponse();
        response.setContentType("application/pdf");
        response.addHeader("Content-Disposition:", "attachment; filename=" + nomeRelatorio);

        try {
            output = response.getOutputStream();
            Document document = new Document();
            PdfWriter.getInstance(document, output);
            document.open();
            
            HTMLWorker htmlWorker = new HTMLWorker(document);
            htmlWorker.parse(new StringReader(textoHtml));

            document.close();
            output.flush();
            output.close();
            context.responseComplete();

        } catch (DocumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public static void imprimirHtml(ArrayList<String> listaRelatoriosHtml, String nomeRelatorio) {
        FacesContext context;
        HttpServletResponse response;
        ServletOutputStream output;

        context = FacesContext.getCurrentInstance();
        response = (HttpServletResponse) context.getExternalContext().getResponse();
        response.setContentType("application/pdf");
        response.addHeader("Content-Disposition:", "attachment; filename=" + nomeRelatorio);

        try {
            output = response.getOutputStream();
            Document document = new Document();
            PdfWriter.getInstance(document, output);
            document.open();
            
            HTMLWorker htmlWorker = new HTMLWorker(document);
            
            for (String textoHtml : listaRelatoriosHtml) {
                htmlWorker.parse(new StringReader(textoHtml));
                document.newPage();
            }
            
            document.close();
            output.flush();
            output.close();
            context.responseComplete();

        } catch (DocumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
