package Consultas;


import java.io.Serializable;
import java.math.BigDecimal;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import modelo.Producto;
import vista.ProductoFacade;




@Named("ConsultaProductoController")
@SessionScoped
public class ConsultaProductoController implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private vista.ProductoFacade ejbFacade;

    public ProductoFacade getFacade() {
        return ejbFacade;
    }

    public void setFacade(ProductoFacade ejbFacade) {
        this.ejbFacade = ejbFacade;
    }
    private Producto itemsP;
    private String idProducto;

    public Producto getItemsP() {
        return itemsP;
    }

    public void setItemsP(Producto itemsP) {
        this.itemsP = itemsP;
    }

    public String getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(String idProducto) {
        this.idProducto = idProducto;
    }

    public Producto action() {
        //System.out.println("entro al action");
        itemsP = null;
        BigDecimal idProductoP = new BigDecimal(idProducto);
        itemsP = getFacade().find(idProductoP);

        if (itemsP == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "El producto que busca no existe", "Contact admin."));
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Producto Encontrado", idProducto));
        }

        return itemsP;

    }

}
