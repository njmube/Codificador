/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nomina.entidad;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author ovante
 */
@Entity
@Table(name = "contribuyente", catalog = "sole3", schema = "")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Contribuyente.findAll", query = "SELECT c FROM Contribuyente c")
    , @NamedQuery(name = "Contribuyente.findByIdContribuyente", query = "SELECT c FROM Contribuyente c WHERE c.idContribuyente = :idContribuyente")
    , @NamedQuery(name = "Contribuyente.findByRfc", query = "SELECT c FROM Contribuyente c WHERE c.rfc = :rfc")
    , @NamedQuery(name = "Contribuyente.findByEmail", query = "SELECT c FROM Contribuyente c WHERE c.email = :email")})
public class Contribuyente implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_contribuyente")
    private Integer idContribuyente;
    @Basic(optional = false)
    @NotNull
    @Lob
    @Size(min = 1, max = 65535)
    @Column(name = "nombre")
    private String nombre;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 13)
    @Column(name = "rfc")
    private String rfc;
    // @Pattern(regexp="[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", message="Invalid email")//if the field contains email address consider using this annotation to enforce field validation
    @Size(max = 254)
    @Column(name = "email")
    private String email;
    @Basic(fetch=FetchType.LAZY)
    @Size(max = 500)
    @Column(name = "impresion")
    private String impresion;
    @Basic(fetch=FetchType.LAZY)
    @Size(max = 254)
    @Column(name = "notas")
    private String notas;
    
    @OneToMany(mappedBy = "contribuyente")
    private Collection<Empleado> empleadoCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "contribuyente")
    private Collection<EmpresaContribuyente> empresaContribuyenteCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "contribuyente")
    private Collection<Empresa> empresaCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "contribuyente")
    private Collection<ComprobanteL> comprobanteLCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "contribuyente1")
    private Collection<ComprobanteL> comprobanteLCollection1;

    public Contribuyente() {
        //this.idContribuyente=0;
    }

    public Contribuyente(Integer idContribuyente) {
        this.idContribuyente = idContribuyente;
    }

    public Contribuyente(Integer idContribuyente, String nombre, String rfc) {
        this.idContribuyente = idContribuyente;
        this.nombre = nombre;
        this.rfc = rfc;
    }

    public Integer getIdContribuyente() {
        return idContribuyente;
    }

    public void setIdContribuyente(Integer idContribuyente) {
        this.idContribuyente = idContribuyente;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre.toUpperCase();
    }

    public String getRfc() {
        return rfc;
    }

    public void setRfc(String rfc) throws Exception {
    //    if(rfc.matches("/^([A-Z,Ñ,&]{3,4}([0-9]{2})(0[1-9]|1[0-2])(0[1-9]|1[0-9]|2[0-9]|3[0-1])[A-Z|\\d]{3})$/")){
                 this.rfc = rfc.toUpperCase();
      /*  }
        else{
            throw new Exception ("Error en RFC");
        }*/
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImpresion() {
        return impresion;
    }

    public void setImpresion(String impresion) {
        this.impresion = impresion;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }

    @XmlTransient
    public Collection<Empleado> getEmpleadoCollection() {
        return empleadoCollection;
    }

    public void setEmpleadoCollection(Collection<Empleado> empleadoCollection) {
        this.empleadoCollection = empleadoCollection;
    }

    @XmlTransient
    public Collection<EmpresaContribuyente> getEmpresaContribuyenteCollection() {
        return empresaContribuyenteCollection;
    }

    public void setEmpresaContribuyenteCollection(Collection<EmpresaContribuyente> empresaContribuyenteCollection) {
        this.empresaContribuyenteCollection = empresaContribuyenteCollection;
    }

    @XmlTransient
    public Collection<Empresa> getEmpresaCollection() {
        return empresaCollection;
    }

    public void setEmpresaCollection(Collection<Empresa> empresaCollection) {
        this.empresaCollection = empresaCollection;
    }

    @XmlTransient
    public Collection<ComprobanteL> getComprobanteLCollection() {
        return comprobanteLCollection;
    }

    public void setComprobanteLCollection(Collection<ComprobanteL> comprobanteLCollection) {
        this.comprobanteLCollection = comprobanteLCollection;
    }

    @XmlTransient
    public Collection<ComprobanteL> getComprobanteLCollection1() {
        return comprobanteLCollection1;
    }

    public void setComprobanteLCollection1(Collection<ComprobanteL> comprobanteLCollection1) {
        this.comprobanteLCollection1 = comprobanteLCollection1;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idContribuyente != null ? idContribuyente.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Contribuyente)) {
            return false;
        }
        Contribuyente other = (Contribuyente) object;
        if ((this.idContribuyente == null && other.idContribuyente != null) || (this.idContribuyente != null && !this.idContribuyente.equals(other.idContribuyente))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "nomina.entidad.Contribuyente[ idContribuyente=" + idContribuyente + " ]";
    }
    
}
