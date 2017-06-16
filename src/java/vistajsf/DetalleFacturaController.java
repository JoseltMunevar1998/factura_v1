package vistajsf;

import modelo.DetalleFactura;
import vistajsf.util.JsfUtil;
import vistajsf.util.JsfUtil.PersistAction;
import vista.DetalleFacturaFacade;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@Named("detalleFacturaController")
@SessionScoped
public class DetalleFacturaController implements Serializable {

    @EJB
    private vista.DetalleFacturaFacade ejbFacade;
    private List<DetalleFactura> items = null;
    private DetalleFactura selected;

    public DetalleFacturaController() {
    }

    public DetalleFactura getSelected() {
        return selected;
    }

    public void setSelected(DetalleFactura selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
        selected.getDetalleFacturaPK().setNumFactura(selected.getFacturaVenta().getNumFactura().toBigInteger());
        selected.getDetalleFacturaPK().setIdProducto(selected.getProducto().getIdProducto().toBigInteger());
    }

    protected void initializeEmbeddableKey() {
        selected.setDetalleFacturaPK(new modelo.DetalleFacturaPK());
    }

    private DetalleFacturaFacade getFacade() {
        return ejbFacade;
    }

    public DetalleFactura prepareCreate() {
        selected = new DetalleFactura();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("DetalleFacturaCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("DetalleFacturaUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("DetalleFacturaDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<DetalleFactura> getItems() {
        if (items == null) {
            items = getFacade().findAll();
        }
        return items;
    }

    private void persist(PersistAction persistAction, String successMessage) {
        if (selected != null) {
            setEmbeddableKeys();
            try {
                if (persistAction != PersistAction.DELETE) {
                    getFacade().edit(selected);
                } else {
                    getFacade().remove(selected);
                }
                JsfUtil.addSuccessMessage(successMessage);
            } catch (EJBException ex) {
                String msg = "";
                Throwable cause = ex.getCause();
                if (cause != null) {
                    msg = cause.getLocalizedMessage();
                }
                if (msg.length() > 0) {
                    JsfUtil.addErrorMessage(msg);
                } else {
                    JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
                }
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            }
        }
    }

    public DetalleFactura getDetalleFactura(modelo.DetalleFacturaPK id) {
        return getFacade().find(id);
    }

    public List<DetalleFactura> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<DetalleFactura> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = DetalleFactura.class)
    public static class DetalleFacturaControllerConverter implements Converter {

        private static final String SEPARATOR = "#";
        private static final String SEPARATOR_ESCAPED = "\\#";

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            DetalleFacturaController controller = (DetalleFacturaController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "detalleFacturaController");
            return controller.getDetalleFactura(getKey(value));
        }

        modelo.DetalleFacturaPK getKey(String value) {
            modelo.DetalleFacturaPK key;
            String values[] = value.split(SEPARATOR_ESCAPED);
            key = new modelo.DetalleFacturaPK();
            key.setNumFactura(BigInteger.valueOf(Long.parseLong(values[0])));
            key.setIdProducto(BigInteger.valueOf(Long.parseLong(values[1])));
            return key;
        }

        String getStringKey(modelo.DetalleFacturaPK value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value.getNumFactura());
            sb.append(SEPARATOR);
            sb.append(value.getIdProducto());
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof DetalleFactura) {
                DetalleFactura o = (DetalleFactura) object;
                return getStringKey(o.getDetalleFacturaPK());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), DetalleFactura.class.getName()});
                return null;
            }
        }

    }

}
