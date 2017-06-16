
package control;

import control.exceptions.NonexistentEntityException;
import control.exceptions.PreexistingEntityException;
import control.exceptions.RollbackFailureException;
import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.UserTransaction;
import modelo.DetalleFactura;
import modelo.DetalleFacturaPK;
import modelo.FacturaVenta;
import modelo.Producto;

/**
 *
 * @author Entelgy
 */
public class DetalleFacturaJpaController implements Serializable {

    public DetalleFacturaJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(DetalleFactura detalleFactura) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (detalleFactura.getDetalleFacturaPK() == null) {
            detalleFactura.setDetalleFacturaPK(new DetalleFacturaPK());
        }
        detalleFactura.getDetalleFacturaPK().setNumFactura(detalleFactura.getFacturaVenta().getNumFactura().toBigInteger());
        detalleFactura.getDetalleFacturaPK().setIdProducto(detalleFactura.getProducto().getIdProducto().toBigInteger());
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            FacturaVenta facturaVenta = detalleFactura.getFacturaVenta();
            if (facturaVenta != null) {
                facturaVenta = em.getReference(facturaVenta.getClass(), facturaVenta.getNumFactura());
                detalleFactura.setFacturaVenta(facturaVenta);
            }
            Producto producto = detalleFactura.getProducto();
            if (producto != null) {
                producto = em.getReference(producto.getClass(), producto.getIdProducto());
                detalleFactura.setProducto(producto);
            }
            em.persist(detalleFactura);
            if (facturaVenta != null) {
                facturaVenta.getDetalleFacturaCollection().add(detalleFactura);
                facturaVenta = em.merge(facturaVenta);
            }
            if (producto != null) {
                producto.getDetalleFacturaCollection().add(detalleFactura);
                producto = em.merge(producto);
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findDetalleFactura(detalleFactura.getDetalleFacturaPK()) != null) {
                throw new PreexistingEntityException("DetalleFactura " + detalleFactura + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(DetalleFactura detalleFactura) throws NonexistentEntityException, RollbackFailureException, Exception {
        detalleFactura.getDetalleFacturaPK().setNumFactura(detalleFactura.getFacturaVenta().getNumFactura().toBigInteger());
        detalleFactura.getDetalleFacturaPK().setIdProducto(detalleFactura.getProducto().getIdProducto().toBigInteger());
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            DetalleFactura persistentDetalleFactura = em.find(DetalleFactura.class, detalleFactura.getDetalleFacturaPK());
            FacturaVenta facturaVentaOld = persistentDetalleFactura.getFacturaVenta();
            FacturaVenta facturaVentaNew = detalleFactura.getFacturaVenta();
            Producto productoOld = persistentDetalleFactura.getProducto();
            Producto productoNew = detalleFactura.getProducto();
            if (facturaVentaNew != null) {
                facturaVentaNew = em.getReference(facturaVentaNew.getClass(), facturaVentaNew.getNumFactura());
                detalleFactura.setFacturaVenta(facturaVentaNew);
            }
            if (productoNew != null) {
                productoNew = em.getReference(productoNew.getClass(), productoNew.getIdProducto());
                detalleFactura.setProducto(productoNew);
            }
            detalleFactura = em.merge(detalleFactura);
            if (facturaVentaOld != null && !facturaVentaOld.equals(facturaVentaNew)) {
                facturaVentaOld.getDetalleFacturaCollection().remove(detalleFactura);
                facturaVentaOld = em.merge(facturaVentaOld);
            }
            if (facturaVentaNew != null && !facturaVentaNew.equals(facturaVentaOld)) {
                facturaVentaNew.getDetalleFacturaCollection().add(detalleFactura);
                facturaVentaNew = em.merge(facturaVentaNew);
            }
            if (productoOld != null && !productoOld.equals(productoNew)) {
                productoOld.getDetalleFacturaCollection().remove(detalleFactura);
                productoOld = em.merge(productoOld);
            }
            if (productoNew != null && !productoNew.equals(productoOld)) {
                productoNew.getDetalleFacturaCollection().add(detalleFactura);
                productoNew = em.merge(productoNew);
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                DetalleFacturaPK id = detalleFactura.getDetalleFacturaPK();
                if (findDetalleFactura(id) == null) {
                    throw new NonexistentEntityException("The detalleFactura with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(DetalleFacturaPK id) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            DetalleFactura detalleFactura;
            try {
                detalleFactura = em.getReference(DetalleFactura.class, id);
                detalleFactura.getDetalleFacturaPK();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The detalleFactura with id " + id + " no longer exists.", enfe);
            }
            FacturaVenta facturaVenta = detalleFactura.getFacturaVenta();
            if (facturaVenta != null) {
                facturaVenta.getDetalleFacturaCollection().remove(detalleFactura);
                facturaVenta = em.merge(facturaVenta);
            }
            Producto producto = detalleFactura.getProducto();
            if (producto != null) {
                producto.getDetalleFacturaCollection().remove(detalleFactura);
                producto = em.merge(producto);
            }
            em.remove(detalleFactura);
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<DetalleFactura> findDetalleFacturaEntities() {
        return findDetalleFacturaEntities(true, -1, -1);
    }

    public List<DetalleFactura> findDetalleFacturaEntities(int maxResults, int firstResult) {
        return findDetalleFacturaEntities(false, maxResults, firstResult);
    }

    private List<DetalleFactura> findDetalleFacturaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(DetalleFactura.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public DetalleFactura findDetalleFactura(DetalleFacturaPK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(DetalleFactura.class, id);
        } finally {
            em.close();
        }
    }

    public int getDetalleFacturaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<DetalleFactura> rt = cq.from(DetalleFactura.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
