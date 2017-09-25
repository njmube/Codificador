/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nomina.servicio;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import nomina.entidad.Comprobante;

/**
 *
 * @author ovante
 */
@Stateless
public class ComprobanteFacade extends AbstractFacade<Comprobante> implements ComprobanteFacadeLocal {

    @PersistenceContext(unitName = "Codificador-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ComprobanteFacade() {
        super(Comprobante.class);
    }
    
}
