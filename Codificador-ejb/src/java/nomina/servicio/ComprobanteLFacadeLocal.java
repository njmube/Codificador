/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nomina.servicio;

import java.util.List;
import javax.ejb.Local;
import nomina.entidad.ComprobanteL;
import nomina.entidad.Empleado;
import nomina.entidad.Empresa;

/**
 *
 * @author ovante
 */
@Local
public interface ComprobanteLFacadeLocal {

    void create(ComprobanteL comprobante);

    void edit(ComprobanteL comprobante);

    void remove(ComprobanteL comprobante);

    ComprobanteL find(Object id);

    List<ComprobanteL> findAll();

    List<ComprobanteL> findRange(int[] range);

    int count();

    public ComprobanteL comprobanteBySFE(String serie, String folio, String rfc);

    public List<ComprobanteL> findComprobanteEmpleadoEmpresa(Empleado empleado, Empresa empresa);
    
}
