package Consultas;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import modelo.Cliente;
import vista.ClienteFacade;



@Named("ConsultaClienteController")
@SessionScoped
public class ConsultaClienteController implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private vista.ClienteFacade ejbFacade;

    public ClienteFacade getFacade() {
        return ejbFacade;
    }

    public void setFacade(ClienteFacade ejbFacade) {
        this.ejbFacade = ejbFacade;
    }
    private Cliente item;
    private String idCliente;

    public Cliente getItem() {
        return item;
    }

    public void setItem(Cliente item) {
        this.item = item;
    }

    public String getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(String idCliente) {
        this.idCliente = idCliente;
    }

    public Cliente action() {
        //System.out.println("entro al action");
        item = null;
        BigDecimal idClienteN = new BigDecimal(idCliente);
        item = getFacade().find(idClienteN);

        if (item == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "El cliente que busca no existe", "Contact admin."));
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Cliente encontrado", idCliente));
        }

        return item;

    }

}
