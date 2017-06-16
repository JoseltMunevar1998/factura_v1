/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo;

import java.io.Serializable;
import java.math.BigInteger;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Entelgy
 */
@Embeddable
public class DetalleFacturaPK implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Column(name = "NUM_FACTURA")
    private BigInteger numFactura;
    @Basic(optional = false)
    @NotNull
    @Column(name = "ID_PRODUCTO")
    private BigInteger idProducto;

    public DetalleFacturaPK() {
    }

    public DetalleFacturaPK(BigInteger numFactura, BigInteger idProducto) {
        this.numFactura = numFactura;
        this.idProducto = idProducto;
    }

    public BigInteger getNumFactura() {
        return numFactura;
    }

    public void setNumFactura(BigInteger numFactura) {
        this.numFactura = numFactura;
    }

    public BigInteger getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(BigInteger idProducto) {
        this.idProducto = idProducto;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (numFactura != null ? numFactura.hashCode() : 0);
        hash += (idProducto != null ? idProducto.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof DetalleFacturaPK)) {
            return false;
        }
        DetalleFacturaPK other = (DetalleFacturaPK) object;
        if ((this.numFactura == null && other.numFactura != null) || (this.numFactura != null && !this.numFactura.equals(other.numFactura))) {
            return false;
        }
        if ((this.idProducto == null && other.idProducto != null) || (this.idProducto != null && !this.idProducto.equals(other.idProducto))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "modelo.DetalleFacturaPK[ numFactura=" + numFactura + ", idProducto=" + idProducto + " ]";
    }
    
}
