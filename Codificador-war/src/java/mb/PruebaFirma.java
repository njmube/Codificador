/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mb;

import ejb.CrearCFDILocal;
import ejb.FirmaLocal;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.transform.TransformerException;
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

/**
 *
 * @author franciscogutierrez
 */
@Named(value = "pruebaFirma")
@SessionScoped
public class PruebaFirma implements Serializable {

    @EJB
    private CrearCFDILocal crearCFDI;

    @EJB
    private FirmaLocal firma;
    
    private Comprobante cfdi;
    private boolean activoNomina = true;
     
 
    /**
     * Creates a new instance of PruebaFirma
     */
    public PruebaFirma() {
        
    }
    
    public String getFirma(){
        try {
            llenarCFDI();
        } catch (DatatypeConfigurationException ex) {
            Logger.getLogger(PruebaFirma.class.getName()).log(Level.SEVERE, null, ex);
        }
     
        return "OK";
    }   
       
   
     
    public void addMessage(String summary) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, summary,  null);
        FacesContext.getCurrentInstance().addMessage(null, message);
    }
    public void generaCFDI(ActionEvent e){
        
        addMessage("Generando CFDI");
          // String firmar = firma.firmar("VivaMexico","TME960709LR2");
     //   try {
          //  this.crearCFDI.crear(cfdi,comprobanteX);
    //    } catch (FileNotFoundException | DatatypeConfigurationException | TransformerException | NoSuchAlgorithmException ex) {
    //        Logger.getLogger(PruebaFirma.class.getName()).log(Level.SEVERE, null, ex);
    //    }
    }
    
    public void llenarCFDI() throws DatatypeConfigurationException{
          // TODO code application logic here
        /*  Crear xml */

        cfdi = new Comprobante();
        cfdi.setSerie("A");
        cfdi.setFolio("01");
        Comprobante.Emisor emisor = new Comprobante.Emisor();
        emisor.setNombre("Pruebas SA ");
        emisor.setRfc("TME960709LR2");
        emisor.setRegimenFiscal("601");
        //601 Morales
        //603 
        Comprobante.Receptor receptor = new Comprobante.Receptor();
        //  receptor.setNombre("Pruebas y Mas S de RL MI de CV");
        receptor.setRfc("GUNF7505112Q3");

        // uso del cfdi
        //receptor.setUsoCFDI(CUsoCFDI.G_01); //aquieren mercancias
        receptor.setUsoCFDI(CUsoCFDI.P_01); // para nomina

        /*ingresar emisor y receptor */
        cfdi.setEmisor(emisor);
        cfdi.setReceptor(receptor);

        //   cfdi.setMoneda(CMoneda.);        
        cfdi.setMetodoPago(CMetodoPago.PUE);
        /*   cfdi.setF*/
        cfdi.setLugarExpedicion("20140");
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
        
        c.setTime(calendar.getTime());
          XMLGregorianCalendar fechaPago = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
        fechaPago.setTimezone(DatatypeConstants.FIELD_UNDEFINED);
        fechaPago.setMillisecond(DatatypeConstants.FIELD_UNDEFINED);
        fechaPago.setTime(DatatypeConstants.FIELD_UNDEFINED,DatatypeConstants.FIELD_UNDEFINED,DatatypeConstants.FIELD_UNDEFINED);
        
        
        calendar.add(Calendar.MONTH, -1);         
        c.setTime(calendar.getTime());
          XMLGregorianCalendar fechaIPapgo = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
        fechaIPapgo.setTimezone(DatatypeConstants.FIELD_UNDEFINED);
        fechaIPapgo.setMillisecond(DatatypeConstants.FIELD_UNDEFINED);
        fechaIPapgo.setTime(DatatypeConstants.FIELD_UNDEFINED,DatatypeConstants.FIELD_UNDEFINED,DatatypeConstants.FIELD_UNDEFINED);

        calendar.add(Calendar.YEAR, -1);         
        c.setTime(calendar.getTime());
          XMLGregorianCalendar fechaInicio = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
        fechaInicio.setTimezone(DatatypeConstants.FIELD_UNDEFINED);
        fechaInicio.setMillisecond(DatatypeConstants.FIELD_UNDEFINED);
        fechaInicio.setTime(DatatypeConstants.FIELD_UNDEFINED,DatatypeConstants.FIELD_UNDEFINED,DatatypeConstants.FIELD_UNDEFINED);
        
 
        
        
        cfdi.setFecha(newXMLGregorianCalendar);

        CertificadoUsuario certificadoUsuario = new CertificadoUsuario("TME960709LR2");

        cfdi.setCertificado(certificadoUsuario.getBase64Certificado());
        //cfdi.setNoCertificado("20001000000300022763");
        cfdi.setNoCertificado(certificadoUsuario.getCertNumber());
        cfdi.setTipoCambio(new BigDecimal(1.0));
        cfdi.setMoneda(CMoneda.MXN);

       

        Comprobante.Conceptos.Concepto concepto = new Comprobante.Conceptos.Concepto();
        if (this.activoNomina) {
            cfdi.setFormaPago("PAGO EN UNA SOLA EXHIBICION");
            //        <cfdi:Concepto cantidad="1" unidad="ACT" descripcion="Pago de nómina" valorUnitario="7500.05" importe="7500.05" />
            /*agregar concepto */
            concepto.setCantidad(new BigDecimal(1));
            /* concepto.setClaveProdServ("01010101");
        concepto.setNoIdentificacion("001-002");
        concepto.setClaveUnidad("KGM");*/
            concepto.setClaveUnidad("ACT");
            concepto.setClaveProdServ("84111505");
            // concepto.setUnidad("ACT");
            concepto.setDescripcion("Pago de nómina");
            concepto.setValorUnitario(new BigDecimal(625.0 + 625.0 + 6250.05).setScale(2, RoundingMode.HALF_UP));
            concepto.setImporte(new BigDecimal(625.0 + 625.0 + 6250.05).setScale(2, RoundingMode.HALF_UP));
            concepto.setDescuento(new BigDecimal(1054.75+179.34).setScale(2, RoundingMode.HALF_UP));

            //subTotal="7500.05" descuento="1234.09" Moneda="MXN" TipoCambio="1" total="6265.96" 
            cfdi.setSubTotal(new BigDecimal(7500.05).setScale(2, RoundingMode.HALF_UP));
            cfdi.setDescuento(new BigDecimal(1234.09).setScale(2, RoundingMode.HALF_UP));
            cfdi.setTotal(new BigDecimal(6265.96).setScale(2, RoundingMode.HALF_UP));

            cfdi.setTipoDeComprobante(CTipoDeComprobante.N);

            /*Complemento de nomina */
            Nomina nomina = new Nomina();
            nomina.setVersion("1.2");
            nomina.setTipoNomina(CTipoNomina.O);
            nomina.setFechaPago(fechaPago);
            
            nomina.setFechaInicialPago(fechaIPapgo);
            nomina.setFechaFinalPago(fechaPago);
            nomina.setNumDiasPagados(new BigDecimal(14.5).setScale(3, RoundingMode.HALF_UP));
            Nomina.Emisor emisorNomina = new Nomina.Emisor();
        //    emisorNomina.setRfcPatronOrigen(emisor.getRfc());
            emisorNomina.setRegistroPatronal("5525665412");
            
            nomina.setEmisor(emisorNomina);
            
            Nomina.Receptor empleado = new Nomina.Receptor();
            empleado.setCurp("GUNF750511HASTJR05");
            empleado.setNumSeguridadSocial("04078873454");
            empleado.setTipoContrato("01");
            empleado.setFechaInicioRelLaboral(fechaInicio);
            empleado.setAntigüedad("P21W");
            empleado.setClaveEntFed(CEstado.AGU);
            empleado.setTipoRegimen("02"); //02-Sueldos,03 Jubilados, 04 Pensionados, 09 Asimilados Honorarios
            empleado.setNumEmpleado("001");
            empleado.setPeriodicidadPago("06"); //ver hoja 34  
            empleado.setRiesgoPuesto("2");
            empleado.setSalarioDiarioIntegrado(new BigDecimal(435.50).setScale(2, RoundingMode.HALF_UP));
            nomina.setReceptor(empleado);
            Nomina.Percepciones percepciones = new Nomina.Percepciones();
            Nomina.Percepciones.Percepcion salario = new Nomina.Percepciones.Percepcion();
            salario.setTipoPercepcion("001");
            salario.setClave("001");
            salario.setConcepto("Sueldos, Salarios Rayas y Jornales");
            salario.setImporteExento(new BigDecimal(0.0).setScale(2, RoundingMode.HALF_UP));
            salario.setImporteGravado(new BigDecimal(6250.05).setScale(2, RoundingMode.HALF_UP));
            Nomina.Percepciones.Percepcion asistencia = new Nomina.Percepciones.Percepcion();
            asistencia.setTipoPercepcion("049");
            asistencia.setClave("014");
            asistencia.setConcepto("Premios de asistencia");
            asistencia.setImporteExento(new BigDecimal(0.0).setScale(2, RoundingMode.HALF_UP));
            asistencia.setImporteGravado(new BigDecimal(625.0).setScale(2, RoundingMode.HALF_UP));
            Nomina.Percepciones.Percepcion puntualidad = new Nomina.Percepciones.Percepcion();
            puntualidad.setTipoPercepcion("010");
            puntualidad.setClave("013");
            puntualidad.setConcepto("Premios por puntualidad");
            puntualidad.setImporteExento(new BigDecimal(0.0).setScale(2, RoundingMode.HALF_UP));
            puntualidad.setImporteGravado(new BigDecimal(625.0).setScale(2, RoundingMode.HALF_UP));

            percepciones.setTotalExento(new BigDecimal(0.0).setScale(2, RoundingMode.HALF_UP)); //suma de percepciones 
            percepciones.setTotalGravado(new BigDecimal(625.0 + 625.0 + 6250.05).setScale(2, RoundingMode.HALF_UP)); //suma de percepciones 
            percepciones.setTotalSueldos(new BigDecimal(625.0 + 625.0 + 6250.05).setScale(2, RoundingMode.HALF_UP)); //suma de percepciones 

            percepciones.getPercepcion().add(salario);
            percepciones.getPercepcion().add(asistencia);
            percepciones.getPercepcion().add(puntualidad);

            Nomina.Deducciones deducciones = new Nomina.Deducciones();
            Nomina.Deducciones.Deduccion ISR = new Nomina.Deducciones.Deduccion();
            ISR.setTipoDeduccion("002");
            ISR.setClave("001");
            ISR.setConcepto("ISR");
            ISR.setImporte(new BigDecimal(1054.75).setScale(2, RoundingMode.HALF_UP));

            Nomina.Deducciones.Deduccion seguro = new Nomina.Deducciones.Deduccion();
            seguro.setTipoDeduccion("001");
            seguro.setClave("012");
            seguro.setConcepto("Seguridad social");
            seguro.setImporte(new BigDecimal(179.34).setScale(2, RoundingMode.HALF_UP));

            deducciones.setTotalImpuestosRetenidos(new BigDecimal(1054.75).setScale(2, RoundingMode.HALF_UP)); //el isr es retenido
            deducciones.setTotalOtrasDeducciones(new BigDecimal(179.34).setScale(2, RoundingMode.HALF_UP)); //el isr es retenido

            deducciones.getDeduccion().add(ISR);
            deducciones.getDeduccion().add(seguro);

            nomina.setPercepciones(percepciones);
            nomina.setDeducciones(deducciones);
            
            //nomina con calculos TotalDeducciones="1234.09" TotalOtrosPagos="0.0" TotalPercepciones="7500.05"
            nomina.setTotalOtrosPagos(new BigDecimal(0.0).setScale(2, RoundingMode.HALF_UP));
            nomina.setTotalPercepciones(new BigDecimal(625.0 + 625.0 + 6250.05).setScale(2, RoundingMode.HALF_UP));
            nomina.setTotalDeducciones(new BigDecimal(1054.75+179.34).setScale(2, RoundingMode.HALF_UP));
            

            Comprobante.Complemento complemento = new Comprobante.Complemento();
            complemento.getAny().add(nomina);
            cfdi.getComplemento().add(complemento);

             cfdi.setFormaPago("99");
        } else {
            
            cfdi.setTipoCambio(BigDecimal.ONE);
            
            cfdi.setFormaPago("01");
            cfdi.setTipoDeComprobante(CTipoDeComprobante.I);

            /*agregar concepto */
            concepto.setCantidad(new BigDecimal(1));
            concepto.setClaveProdServ("01010101");
            concepto.setNoIdentificacion("001-002");
            concepto.setClaveUnidad("KGM");
            concepto.setUnidad("Pieza");
            concepto.setDescripcion("Pieza de prueba");
            concepto.setValorUnitario(new BigDecimal(130).setScale(2));
            concepto.setImporte(new BigDecimal(130).setScale(2));
            concepto.setDescuento(BigDecimal.ZERO);

            /*impuestos del concepto */
            Comprobante.Conceptos.Concepto.Impuestos impuestosConcepto = new Comprobante.Conceptos.Concepto.Impuestos();
            Comprobante.Conceptos.Concepto.Impuestos.Traslados transladosConcepto = new Comprobante.Conceptos.Concepto.Impuestos.Traslados();
            Comprobante.Conceptos.Concepto.Impuestos.Traslados.Traslado translado = new Comprobante.Conceptos.Concepto.Impuestos.Traslados.Traslado();
            translado.setImpuesto("002");
            translado.setTasaOCuota(new BigDecimal(0.16).setScale(2, RoundingMode.HALF_UP));
            translado.setBase(new BigDecimal(130).setScale(2));
            translado.setImporte(new BigDecimal(130 * 0.16).setScale(2, RoundingMode.HALF_UP));
            translado.setTipoFactor(CTipoFactor.TASA);
            transladosConcepto.getTraslado().add(translado);
            impuestosConcepto.setTraslados(transladosConcepto);

            /*agregar impuesto al concepto*/
            concepto.setImpuestos(impuestosConcepto);

            cfdi.setDescuento(BigDecimal.ZERO);
            cfdi.setSubTotal(new BigDecimal(130).setScale(2));

            Comprobante.Impuestos impuestos = new Comprobante.Impuestos();
            impuestos.setTotalImpuestosTrasladados(new BigDecimal(130 * 0.16).setScale(2, RoundingMode.HALF_UP));
            Comprobante.Impuestos.Traslados translados = new Comprobante.Impuestos.Traslados();
            Comprobante.Impuestos.Traslados.Traslado transladoIVA = new Comprobante.Impuestos.Traslados.Traslado();
            transladoIVA.setImporte(new BigDecimal(130 * .16).setScale(2, RoundingMode.HALF_UP));
            transladoIVA.setImpuesto("002");
            //001 isr
            //002 iva
            //003 ieps
            transladoIVA.setTasaOCuota(new BigDecimal(16.0 / 100).setScale(2, RoundingMode.HALF_UP));
            transladoIVA.setTipoFactor(CTipoFactor.TASA);

            translados.getTraslado().add(transladoIVA);
            impuestos.setTraslados(translados);
            cfdi.setImpuestos(impuestos);

            cfdi.setTotal(new BigDecimal(130 * 1.16).setScale(2, RoundingMode.HALF_UP));

        }
        Comprobante.Conceptos conceptos = new Comprobante.Conceptos();
        conceptos.getConcepto().add(concepto);
        cfdi.setConceptos(conceptos);

    }
    
}
