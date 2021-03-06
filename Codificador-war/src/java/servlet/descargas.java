/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.math.BigInteger;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import nomina.entidad.Archivos;
import nomina.entidad.ComprobanteL;
import nomina.servicio.ComprobanteLFacadeLocal;

/**
 *
 * @author ovante
 */
@WebServlet(name = "descargas", urlPatterns = {"/descargas"}, initParams = {
    @WebInitParam(name = "serie", value = "a")
    , @WebInitParam(name = "folio", value = "1")
    , @WebInitParam(name = "rfc", value = "aaa000000aaa")
    , @WebInitParam(name = "tipo", value = "pdf")})
public class descargas extends HttpServlet {

    @EJB
    private ComprobanteLFacadeLocal comprobanteLFacade;

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //response.setContentType("text/html;charset=UTF-8");
        String serie = request.getParameter("serie");
        String folio = request.getParameter("folio");
        String rfc = request.getParameter("rfc");
        String tipo = request.getParameter("tipo");

        ComprobanteL comprobanteBySFE = comprobanteLFacade.comprobanteBySFE(serie, folio, rfc);

        if (comprobanteBySFE.getArchivosCollection() != null) {
            Archivos archivoX = new Archivos();

            for (Archivos archivo : comprobanteBySFE.getArchivosCollection()) {
                if (archivo.getTipo().equals(tipo)) {
                    archivoX = archivo;
                }
            }
            
            
             response.setContentLength((int) archivoX.getContenido().length);
            if (tipo.equals("PDF")) {
                response.setContentType("application/pdf");
                response.setHeader("Content-Disposition", "inline; filename=\"" + archivoX.getNombre() + "\"");//fileName);
                System.out.println("es un pdf");
            } else {
                response.setContentType("application/force-download");
                response.setHeader("Content-Transfer-Encoding", "binary");
                response.setHeader("Content-Disposition", "attachment; filename=\"" + archivoX.getNombre() + "\"");//fileName);

            }
           
         
            OutputStream os = response.getOutputStream();
            try {
                os.write(archivoX.getContenido());
                os.flush();

            } catch (Exception excp) {
                excp.printStackTrace();
            } finally {
                os.close();
            }
        }

        /*
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. 
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet descargas</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet descargas at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
            out.write(archivoX.getContenido());
        }*/
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
