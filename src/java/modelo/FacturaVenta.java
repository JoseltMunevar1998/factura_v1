/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
 * @author Entelgy
 */
@Entity
@Table(name = "FACTURA_VENTA")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "FacturaVenta.findAll", query = "SELECT f FROM FacturaVenta f")
    , @NamedQuery(name = "FacturaVenta.findByNumFactura", query = "SELECT f FROM FacturaVenta f WHERE f.numFactura = :numFactura")
    , @NamedQuery(name = "FacturaVenta.findByPorcentajeDescuento", query = "SELECT f FROM FacturaVenta f WHERE f.porcentajeDescuento = :porcentajeDescuento")
    , @NamedQuery(name = "FacturaVenta.findByPrecioBruto", query = "SELECT f FROM FacturaVenta f WHERE f.precioBruto = :precioBruto")
    , @NamedQuery(name = "FacturaVenta.findByPrecioNeto", query = "SELECT f FROM FacturaVenta f WHERE f.precioNeto = :precioNeto")
    , @NamedQuery(name = "FacturaVenta.findByIva", query = "SELECT f FROM FacturaVenta f WHERE f.iva = :iva")
    , @NamedQuery(name = "FacturaVenta.findByPrecioTotal", query = "SELECT f FROM FacturaVenta f WHERE f.precioTotal = :precioTotal")})
public class FacturaVenta implements Serializable {

    private static final long serialVersionUID = 1L;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "NUM_FACTURA")
    private BigDecimal numFactura;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 5)
    @Column(name = "PORCENTAJE_DESCUENTO")
    private String porcentajeDescuento;
    @Basic(optional = false)
    @NotNull
    @Column(name = "PRECIO_BRUTO")
    private BigInteger precioBruto;
    @Basic(optional = false)
    @NotNull
    @Column(name = "PRECIO_NETO")
    private BigInteger precioNeto;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 5)
    @Column(name = "IVA")
    private String iva;
    @Basic(optional = false)
    @NotNull
    @Column(name = "PRECIO_TOTAL")
    private BigInteger precioTotal;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "facturaVenta")
    private Collection<DetalleFactura> detalleFacturaCollection;
    @JoinColumn(name = "CLIENTE", referencedColumnName = "ID_CLIENTE")
    @ManyToOne
    private Cliente cliente;

    public FacturaVenta() {
    }

    public FacturaVenta(BigDecimal numFactura) {
        this.numFactura = numFactura;
    }

    public FacturaVenta(BigDecimal numFactura, String porcentajeDescuento, BigInteger precioBruto, BigInteger precioNeto, String iva, BigInteger precioTotal) {
        this.numFactura = numFactura;
        this.porcentajeDescuento = porcentajeDescuento;
        this.precioBruto = precioBruto;
        this.precioNeto = precioNeto;
        this.iva = iva;
        this.precioTotal = precioTotal;
    }

    public BigDecimal getNumFactura() {
        return numFactura;
    }

    public void setNumFactura(BigDecimal numFactura) {
        this.numFactura = numFactura;
    }

    public String getPorcentajeDescuento() {
        return porcentajeDescuento;
    }

    public void setPorcentajeDescuento(String porcentajeDescuento) {
        this.porcentajeDescuento = porcentajeDescuento;
    }

    public BigInteger getPrecioBruto() {
        return precioBruto;
    }

    public void setPrecioBruto(BigInteger precioBruto) {
        this.precioBruto = precioBruto;
    }

    public BigInteger getPrecioNeto() {
        return precioNeto;
    }

    public void setPrecioNeto(BigInteger precioNeto) {
        this.precioNeto = precioNeto;
    }

    public String getIva() {
        return iva;
    }

    public void setIva(String iva) {
        this.iva = iva;
    }

    public BigInteger getPrecioTotal() {
        return precioTotal;
    }

    public void setPrecioTotal(BigInteger precioTotal) {
        this.precioTotal = precioTotal;
    }

    @XmlTransient
    public Collection<DetalleFactura> getDetalleFacturaCollection() {
        return detalleFacturaCollection;
    }

    public void setDetalleFacturaCollection(Collection<DetalleFactura> detalleFacturaCollection) {
        this.detalleFacturaCollection = detalleFacturaCollection;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (numFactura != null ? numFactura.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof FacturaVenta)) {
            return false;
        }
        FacturaVenta other = (FacturaVenta) object;
        if ((this.numFactura == null && other.numFactura != null) || (this.numFactura != null && !this.numFactura.equals(other.numFactura))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "modelo.FacturaVenta[ numFactura=" + numFactura + " ]";
    }
    
}
