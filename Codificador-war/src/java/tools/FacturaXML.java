package tools;

import catalogo.entidad.FormaPago;
import catalogo.entidad.MetodoPago;
import catalogo.entidad.UsoCfdi;
import ejb.CrearCFDILocal;
import factura.entidad.CategoriaImpuesto;
import factura.entidad.ImpuestoP;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.NoSuchAlgorithmException;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpSession;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.transform.TransformerException;
import mb.PruebaFirma;
import nomina.entidad.ComprobanteL;
import nomina.entidad.Contribuyente;
import nomina.entidad.DeduccionPercepcion;
import nomina.entidad.Empleado;
import nomina.entidad.Empresa;
import nomina.entidad.Folio;
import nomina.servicio.ComprobanteLFacadeLocal;
import nomina.servicio.FolioFacadeLocal;
import org.primefaces.context.RequestContext;
import sat.CEstado;
import sat.CMetodoPago;
import sat.CMoneda;
import sat.CTipoDeComprobante;
import sat.CTipoFactor;
import sat.CTipoNomina;
import sat.CUsoCFDI;
import sat.Comprobante;
import sat.Nomina;
import utilerias.CertificadoUsuario;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author ovante
 */
public class FacturaXML implements Serializable {

    CrearCFDILocal crearCFDI = lookupCrearCFDILocal();

    ComprobanteLFacadeLocal comprobanteLFacade = lookupComprobanteLFacadeLocal();

    FolioFacadeLocal folioFacade = lookupFolioFacadeLocal();
    

    private final List<DetalleFactura> detalles;
    private Comprobante.Emisor emisor;
    private Comprobante.Receptor receptor;

    private Contribuyente receptorCFDI;
    private final UsoCfdi uso;
    private final FormaPago formaPago;
    private final String referencia;
    private final MetodoPago metodoPago;
    private final boolean estaPagado;
    private final Empresa empresa;
    private final Date fechaIPago;
    private ComprobanteL comprobanteX;
    private Comprobante cfdi;
    private Folio folio;

    public FacturaXML(Contribuyente receptorCFDI, UsoCfdi uso, List<DetalleFactura> detalles, FormaPago formaPago, String referencia, MetodoPago metodoPago, boolean estaPagado) {
        this.detalles = detalles;
        this.receptorCFDI = receptorCFDI;
        this.uso = uso;
        this.formaPago = formaPago;
        this.referencia = referencia;
        this.metodoPago = metodoPago;
        this.estaPagado = estaPagado;

        empresa = (Empresa) this.recuperarParametroObject("empresaActual");
        Calendar calendar = Calendar.getInstance();
        fechaIPago = calendar.getTime();
    }

    private Object recuperarParametroObject(String parametro) {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) context.getExternalContext().getSession(true);
        Object retorno = session.getAttribute(parametro);
        return retorno;
    }

    public void llenarCFDI() throws DatatypeConfigurationException {
        
        Double descuentos =0.0;
        Double importes=0.0;
        Double trasladoT=0.0;
        Double retencionT=0.0;
        Map<ImpuestoP, Double> trasladoMapa = new HashMap<ImpuestoP, Double>();
        Map<ImpuestoP, Double> retencionMapa = new HashMap<ImpuestoP, Double>();

        /*  Crear xml */

        cfdi = new Comprobante();
        folio = folioFacade.getFolioEmpresa(empresa);
        cfdi.setSerie(folio.getSerie());
        cfdi.setFolio(folio.getFolio().toString());
        folioFacade.folioInc(folio);
        emisor = new Comprobante.Emisor();
        emisor.setNombre(empresa.getContribuyente().getNombre());
        emisor.setRfc(empresa.getContribuyente().getRfc());
        emisor.setRegimenFiscal(empresa.getRegimenFiscal());
        //601 Morales
        //603 
        receptor = new Comprobante.Receptor();
        //  receptor.setNombre("Pruebas y Mas S de RL MI de CV");
        receptor.setRfc(receptorCFDI.getRfc());
        receptor.setNombre(receptorCFDI.getNombre());

        // uso del cfdi
        //receptor.setUsoCFDI(CUsoCFDI.G_01); //aquieren mercancias
        receptor.setUsoCFDI(CUsoCFDI.fromValue(uso.getUsoCfdi())); // para nomina

        /*ingresar emisor y receptor */
        cfdi.setEmisor(emisor);
        cfdi.setReceptor(receptor);

        //   cfdi.setMoneda(CMoneda.);        
        cfdi.setMetodoPago(CMetodoPago.fromValue(metodoPago.getMetodoPago()));
        /*   cfdi.setF*/
        cfdi.setLugarExpedicion(empresa.getCp());//falta codigo postal
        cfdi.setVersion("3.3");

        /*
        CfdiRelacionado relacion =  new CfdiRelacionado();
        relacion.setUUID("5FB2822E-396D-4725-8521-CDC4BDD20CCF");
        cfdi.getCfdiRelacionados().setTipoRelacion("04");
        cfdi.getCfdiRelacionados().getCfdiRelacionado().add(relacion);
         */
        GregorianCalendar c = new GregorianCalendar();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, -2);
        Date time = calendar.getTime();
        c.setTime(time);
        XMLGregorianCalendar newXMLGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
        newXMLGregorianCalendar.setTimezone(DatatypeConstants.FIELD_UNDEFINED);
        newXMLGregorianCalendar.setMillisecond(DatatypeConstants.FIELD_UNDEFINED);

        cfdi.setFecha(newXMLGregorianCalendar);

        CertificadoUsuario certificadoUsuario = new CertificadoUsuario(empresa.getContribuyente().getRfc()/*"TME960709LR2"*/);

        cfdi.setCertificado(certificadoUsuario.getBase64Certificado());
        //cfdi.setNoCertificado("20001000000300022763");
        cfdi.setNoCertificado(certificadoUsuario.getCertNumber());
        cfdi.setTipoCambio(new BigDecimal(1.0));
        cfdi.setMoneda(CMoneda.MXN);
        cfdi.setTipoCambio(BigDecimal.ONE);

        cfdi.setFormaPago("01");
        cfdi.setTipoDeComprobante(CTipoDeComprobante.I);

        Comprobante.Conceptos conceptos = new Comprobante.Conceptos();

        for (DetalleFactura detalle : detalles) {
            Comprobante.Conceptos.Concepto concepto = new Comprobante.Conceptos.Concepto();

            /*agregar concepto */
            concepto.setCantidad(new BigDecimal(detalle.getCantidad()));
            concepto.setClaveProdServ(detalle.getProducto().getCategoria().getClaveProductoServicio());
            concepto.setNoIdentificacion(detalle.getProducto().getClaveIdentificacion());
            concepto.setClaveUnidad(detalle.getProducto().getCategoria().getClaveUnidad());
            concepto.setUnidad(detalle.getProducto().getCategoria().getUnidad());
            concepto.setDescripcion(detalle.getDescripcion());
            concepto.setValorUnitario(new BigDecimal(detalle.getValorUnitario()).setScale(2, RoundingMode.HALF_UP));
            concepto.setImporte(new BigDecimal(detalle.getImporte()).setScale(2, RoundingMode.HALF_UP));
            concepto.setDescuento(new BigDecimal(detalle.getCantidadDescuento()).setScale(2, RoundingMode.HALF_UP));

            /*impuestos del concepto  de translados */
            Comprobante.Conceptos.Concepto.Impuestos impuestosConcepto = new Comprobante.Conceptos.Concepto.Impuestos();
            Comprobante.Conceptos.Concepto.Impuestos.Traslados transladosConcepto = new Comprobante.Conceptos.Concepto.Impuestos.Traslados();
            /*impuestos del concepto de retenciones */
            Comprobante.Conceptos.Concepto.Impuestos.Retenciones retencionesConcepto = new Comprobante.Conceptos.Concepto.Impuestos.Retenciones();
            

            /*lista de impuestos del articulo en cuestion */
            Collection<CategoriaImpuesto> listaDeImpuestos = detalle.getProducto().getCategoria().getCategoriaImpuestoCollection();
            
        
            /*Verificar parsear lista de impuestos */
            for(CategoriaImpuesto impuestoPrd: listaDeImpuestos ){
                
                if (impuestoPrd.getTraslado()){
                    double importeTraslado;
                    Comprobante.Conceptos.Concepto.Impuestos.Traslados.Traslado translado = new Comprobante.Conceptos.Concepto.Impuestos.Traslados.Traslado();

                    translado.setImpuesto(impuestoPrd.getImpuestoP().getImpuesto());
                    if (!impuestoPrd.getImpuestoP().getPorciento().equals(0.0)) {
                        translado.setTipoFactor(CTipoFactor.TASA);
                        translado.setTasaOCuota(new BigDecimal(impuestoPrd.getImpuestoP().getPorciento()).setScale(2, RoundingMode.HALF_UP));
                        importeTraslado=detalle.getImporte() * impuestoPrd.getImpuestoP().getPorciento();
                        translado.setImporte(new BigDecimal(importeTraslado).setScale(2, RoundingMode.HALF_UP));

                    } else {
                        translado.setTipoFactor(CTipoFactor.CUOTA);
                        translado.setTasaOCuota(new BigDecimal(impuestoPrd.getImpuestoP().getCantidad()).setScale(2, RoundingMode.HALF_UP));
                          importeTraslado=detalle.getImporte() * impuestoPrd.getImpuestoP().getCantidad();
                         translado.setImporte(new BigDecimal(importeTraslado).setScale(2, RoundingMode.HALF_UP));
                    }
                    translado.setBase(new BigDecimal(detalle.getImporte()).setScale(2, RoundingMode.HALF_UP));
                    transladosConcepto.getTraslado().add(translado);
                    Double trasladoCuenta;

                    trasladoCuenta = trasladoMapa.get(impuestoPrd.getImpuestoP());

                    if (trasladoCuenta == null) {
                        trasladoCuenta = 0.0;
                    }
                    trasladoCuenta += importeTraslado;
                    trasladoMapa.put(impuestoPrd.getImpuestoP(), trasladoCuenta);
                    trasladoT += importeTraslado;


                }else
                {
                      double importeRetencion;
                    Comprobante.Conceptos.Concepto.Impuestos.Retenciones.Retencion retencion = new Comprobante.Conceptos.Concepto.Impuestos.Retenciones.Retencion();
                    retencion.setImpuesto(impuestoPrd.getImpuestoP().getImpuesto());
                    if (!impuestoPrd.getImpuestoP().getPorciento().equals(0.0)) {
                        retencion.setTipoFactor(CTipoFactor.TASA);
                        importeRetencion=detalle.getImporte() * impuestoPrd.getImpuestoP().getPorciento();
                        retencion.setTasaOCuota(new BigDecimal(importeRetencion).setScale(2, RoundingMode.HALF_UP));
                        retencion.setImporte(new BigDecimal(detalle.getImporte() * impuestoPrd.getImpuestoP().getPorciento()).setScale(2, RoundingMode.HALF_UP));

                    } else {
                        retencion.setTipoFactor(CTipoFactor.CUOTA);
                        importeRetencion=impuestoPrd.getImpuestoP().getCantidad();
                        retencion.setTasaOCuota(new BigDecimal(importeRetencion).setScale(2, RoundingMode.HALF_UP));
                         retencion.setImporte(new BigDecimal(detalle.getImporte() * impuestoPrd.getImpuestoP().getCantidad()).setScale(2, RoundingMode.HALF_UP));
                    }
                    retencion.setBase(new BigDecimal(detalle.getImporte()).setScale(2, RoundingMode.HALF_UP));
                    retencionesConcepto.getRetencion().add(retencion);
                    Double retencionCuenta;

                    retencionCuenta = retencionMapa.get(impuestoPrd.getImpuestoP());
                    if (retencionCuenta == null) {
                        retencionCuenta = 0.0;
                    }

                    retencionCuenta += importeRetencion;
                    retencionMapa.put(impuestoPrd.getImpuestoP(), retencionCuenta);
                    retencionT  += importeRetencion;

                }

            }

            /*translado.setImpuesto("002");
            translado.setTasaOCuota(new BigDecimal(0.16).setScale(2, RoundingMode.HALF_UP));
            translado.setBase(new BigDecimal(130).setScale(2, RoundingMode.HALF_UP));
            translado.setImporte(new BigDecimal(130 * 0.16).setScale(2, RoundingMode.HALF_UP));
            translado.setTipoFactor(CTipoFactor.TASA);
            transladosConcepto.getTraslado().add(translado);
             */
            impuestosConcepto.setTraslados(transladosConcepto);
            impuestosConcepto.setRetenciones(retencionesConcepto);

            /*agregar impuesto al concepto*/
            concepto.setImpuestos(impuestosConcepto);

            //sumar total de Descuentos
            descuentos += detalle.getCantidadDescuento();
            importes += detalle.getImporte();

            //agrega el concepto a la factura
            conceptos.getConcepto().add(concepto);
        }

        cfdi.setDescuento(new BigDecimal(descuentos).setScale(2, RoundingMode.HALF_UP));
        cfdi.setSubTotal(new BigDecimal(importes).setScale(2, RoundingMode.HALF_UP));

        
        /*manejo de impuestos para el comprobante */
        Comprobante.Impuestos impuestos = new Comprobante.Impuestos();
        Comprobante.Impuestos.Traslados traslados = new Comprobante.Impuestos.Traslados();

        trasladoMapa.forEach((impuesto, v) -> {
            Comprobante.Impuestos.Traslados.Traslado traslado = new Comprobante.Impuestos.Traslados.Traslado();
            if (!impuesto.getPorciento().equals(0.0)) {
                traslado.setTipoFactor(CTipoFactor.TASA);
            } else {
                traslado.setTipoFactor(CTipoFactor.CUOTA);
            }
            traslado.setTasaOCuota(new BigDecimal(impuesto.getPorciento()).setScale(2, RoundingMode.HALF_UP));
            traslado.setImporte(new BigDecimal(v).setScale(2, RoundingMode.HALF_UP));
            traslado.setImpuesto(impuesto.getImpuesto());
         
            traslados.getTraslado().add(traslado);
        });
        
        
     Comprobante.Impuestos.Retenciones retenciones = new Comprobante.Impuestos.Retenciones();
        retencionMapa.forEach((impuesto, v) -> {
            Comprobante.Impuestos.Retenciones.Retencion retencion = new Comprobante.Impuestos.Retenciones.Retencion();           
            retencion.setImporte(new BigDecimal(v).setScale(2, RoundingMode.HALF_UP));
            retencion.setImpuesto(impuesto.getImpuesto());         
            retenciones.getRetencion().add(retencion);
        });



        impuestos.setTotalImpuestosTrasladados(new BigDecimal(trasladoT).setScale(2, RoundingMode.HALF_UP));
        impuestos.setTotalImpuestosRetenidos(new BigDecimal(retencionT).setScale(2, RoundingMode.HALF_UP));

        
       
       
        cfdi.setImpuestos(impuestos);

        cfdi.setTotal(new BigDecimal(importes - descuentos - retencionT + trasladoT).setScale(2, RoundingMode.HALF_UP));

        cfdi.setConceptos(conceptos);

    }

    private FolioFacadeLocal lookupFolioFacadeLocal() {
        try {
            Context c = new InitialContext();
            return (FolioFacadeLocal) c.lookup("java:global/Codificador/Codificador-ejb/FolioFacade!nomina.servicio.FolioFacadeLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }

    private ComprobanteLFacadeLocal lookupComprobanteLFacadeLocal() {
        try {
            Context c = new InitialContext();
            return (ComprobanteLFacadeLocal) c.lookup("java:global/Codificador/Codificador-ejb/ComprobanteLFacade!nomina.servicio.ComprobanteLFacadeLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }

    private CrearCFDILocal lookupCrearCFDILocal() {
        try {
            Context c = new InitialContext();
            return (CrearCFDILocal) c.lookup("java:global/Codificador/Codificador-ejb/CrearCFDI!ejb.CrearCFDILocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }
    
    public void guardarComprobante(String tipo, int estatus) {
        ComprobanteL comprobanteX = new ComprobanteL();
        comprobanteX.setIdComprobante(0);
        comprobanteX.setFolio(folio.getFolio().toString());
        comprobanteX.setSerie(folio.getSerie());
        comprobanteX.setContribuyente(empresa.getContribuyente());
        comprobanteX.setContribuyente1(receptorCFDI);
        comprobanteX.setTipo(tipo);
        comprobanteX.setTotal(cfdi.getTotal().doubleValue());
        comprobanteX.setFecha(cfdi.getFecha().toGregorianCalendar().getTime());
        comprobanteX.setEstatus(estatus);
        comprobanteX.setImpuesto(cfdi.getImpuestos().getTotalImpuestosTrasladados().doubleValue());
        comprobanteX.setSubtotal(cfdi.getSubTotal().doubleValue());
        comprobanteX.setUuid(cfdi.getNoCertificado());
        comprobanteLFacade.create(comprobanteX);
    }
    
    public void generaCFDI(){
         try {
                        guardarComprobante("I", 1);
                        this.crearCFDI.crear(cfdi, comprobanteX);
                       RequestContext.getCurrentInstance().execute("window.open('"+"/Codificador-war/faces/descargas?serie="+cfdi.getSerie()+"&folio="+cfdi.getFolio()+"&rfc="+cfdi.getEmisor().getRfc()+"&tipo=PDF"+"','_blank')");
                        addMessage("Generando Factura");

                    } catch (FileNotFoundException | DatatypeConfigurationException | TransformerException | NoSuchAlgorithmException ex) {
                                                addMessage("Error en generar Factura");

                        Logger.getLogger(PruebaFirma.class.getName()).log(Level.SEVERE, null, ex);
                    }
    }
    
    
    

    public void addMessage(String summary) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, summary, null);
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public void addMessageError(String summary) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, summary, null);
        FacesContext.getCurrentInstance().addMessage(null, message);
    }
    
    

}