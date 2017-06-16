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
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Entelgy
 */
@Entity
@Table(name = "DETALLE_FACTURA")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "DetalleFactura.findAll", query = "SELECT d FROM DetalleFactura d")
    , @NamedQuery(name = "DetalleFactura.findByNumFactura", query = "SELECT d FROM DetalleFactura d WHERE d.detalleFacturaPK.numFactura = :numFactura")
    , @NamedQuery(name = "DetalleFactura.findByIdProducto", query = "SELECT d FROM DetalleFactura d WHERE d.detalleFacturaPK.idProducto = :idProducto")
    , @NamedQuery(name = "DetalleFactura.findByPorcentajeDescuento", query = "SELECT d FROM DetalleFactura d WHERE d.porcentajeDescuento = :porcentajeDescuento")
    , @NamedQuery(name = "DetalleFactura.findByPrecioBruto", query = "SELECT d FROM DetalleFactura d WHERE d.precioBruto = :precioBruto")
    , @NamedQuery(name = "DetalleFactura.findByPrecioNeto", query = "SELECT d FROM DetalleFactura d WHERE d.precioNeto = :precioNeto")
    , @NamedQuery(name = "DetalleFactura.findByIva", query = "SELECT d FROM DetalleFactura d WHERE d.iva = :iva")
    , @NamedQuery(name = "DetalleFactura.findByPrecioTotal", query = "SELECT d FROM DetalleFactura d WHERE d.precioTotal = :precioTotal")})
public class DetalleFactura implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected DetalleFacturaPK detalleFacturaPK;
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
    @JoinColumn(name = "NUM_FACTURA", referencedColumnName = "NUM_FACTURA", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private FacturaVenta facturaVenta;
    @JoinColumn(name = "ID_PRODUCTO", referencedColumnName = "ID_PRODUCTO", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Producto producto;

    public DetalleFactura() {
    }

    public DetalleFactura(DetalleFacturaPK detalleFacturaPK) {
        this.detalleFacturaPK = detalleFacturaPK;
    }

    public DetalleFactura(DetalleFacturaPK detalleFacturaPK, String porcentajeDescuento, BigInteger precioBruto, BigInteger precioNeto, String iva, BigInteger precioTotal) {
        this.detalleFacturaPK = detalleFacturaPK;
        this.porcentajeDescuento = porcentajeDescuento;
        this.precioBruto = precioBruto;
        this.precioNeto = precioNeto;
        this.iva = iva;
        this.precioTotal = precioTotal;
    }

    public DetalleFactura(BigInteger numFactura, BigInteger idProducto) {
        this.detalleFacturaPK = new DetalleFacturaPK(numFactura, idProducto);
    }

    public DetalleFacturaPK getDetalleFacturaPK() {
        return detalleFacturaPK;
    }

    public void setDetalleFacturaPK(DetalleFacturaPK detalleFacturaPK) {
        this.detalleFacturaPK = detalleFacturaPK;
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

    public FacturaVenta getFacturaVenta() {
        return facturaVenta;
    }

    public void setFacturaVenta(FacturaVenta facturaVenta) {
        this.facturaVenta = facturaVenta;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (detalleFacturaPK != null ? detalleFacturaPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof DetalleFactura)) {
            return false;
        }
        DetalleFactura other = (DetalleFactura) object;
        if ((this.detalleFacturaPK == null && other.detalleFacturaPK != null) || (this.detalleFacturaPK != null && !this.detalleFacturaPK.equals(other.detalleFacturaPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "modelo.DetalleFactura[ detalleFacturaPK=" + detalleFacturaPK + " ]";
    }
    
}
