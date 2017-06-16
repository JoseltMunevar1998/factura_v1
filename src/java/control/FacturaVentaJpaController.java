/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package control;

import control.exceptions.IllegalOrphanException;
import control.exceptions.NonexistentEntityException;
import control.exceptions.PreexistingEntityException;
import control.exceptions.RollbackFailureException;
import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import modelo.Cliente;
import modelo.DetalleFactura;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;
import modelo.FacturaVenta;

/**
 *
 * @author Entelgy
 */
public class FacturaVentaJpaController implements Serializable {

    public FacturaVentaJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(FacturaVenta facturaVenta) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (facturaVenta.getDetalleFacturaCollection() == null) {
            facturaVenta.setDetalleFacturaCollection(new ArrayList<DetalleFactura>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Cliente cliente = facturaVenta.getCliente();
            if (cliente != null) {
                cliente = em.getReference(cliente.getClass(), cliente.getIdCliente());
                facturaVenta.setCliente(cliente);
            }
            Collection<DetalleFactura> attachedDetalleFacturaCollection = new ArrayList<DetalleFactura>();
            for (DetalleFactura detalleFacturaCollectionDetalleFacturaToAttach : facturaVenta.getDetalleFacturaCollection()) {
                detalleFacturaCollectionDetalleFacturaToAttach = em.getReference(detalleFacturaCollectionDetalleFacturaToAttach.getClass(), detalleFacturaCollectionDetalleFacturaToAttach.getDetalleFacturaPK());
                attachedDetalleFacturaCollection.add(detalleFacturaCollectionDetalleFacturaToAttach);
            }
            facturaVenta.setDetalleFacturaCollection(attachedDetalleFacturaCollection);
            em.persist(facturaVenta);
            if (cliente != null) {
                cliente.getFacturaVentaCollection().add(facturaVenta);
                cliente = em.merge(cliente);
            }
            for (DetalleFactura detalleFacturaCollectionDetalleFactura : facturaVenta.getDetalleFacturaCollection()) {
                FacturaVenta oldFacturaVentaOfDetalleFacturaCollectionDetalleFactura = detalleFacturaCollectionDetalleFactura.getFacturaVenta();
                detalleFacturaCollectionDetalleFactura.setFacturaVenta(facturaVenta);
                detalleFacturaCollectionDetalleFactura = em.merge(detalleFacturaCollectionDetalleFactura);
                if (oldFacturaVentaOfDetalleFacturaCollectionDetalleFactura != null) {
                    oldFacturaVentaOfDetalleFacturaCollectionDetalleFactura.getDetalleFacturaCollection().remove(detalleFacturaCollectionDetalleFactura);
                    oldFacturaVentaOfDetalleFacturaCollectionDetalleFactura = em.merge(oldFacturaVentaOfDetalleFacturaCollectionDetalleFactura);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findFacturaVenta(facturaVenta.getNumFactura()) != null) {
                throw new PreexistingEntityException("FacturaVenta " + facturaVenta + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(FacturaVenta facturaVenta) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            FacturaVenta persistentFacturaVenta = em.find(FacturaVenta.class, facturaVenta.getNumFactura());
            Cliente clienteOld = persistentFacturaVenta.getCliente();
            Cliente clienteNew = facturaVenta.getCliente();
            Collection<DetalleFactura> detalleFacturaCollectionOld = persistentFacturaVenta.getDetalleFacturaCollection();
            Collection<DetalleFactura> detalleFacturaCollectionNew = facturaVenta.getDetalleFacturaCollection();
            List<String> illegalOrphanMessages = null;
            for (DetalleFactura detalleFacturaCollectionOldDetalleFactura : detalleFacturaCollectionOld) {
                if (!detalleFacturaCollectionNew.contains(detalleFacturaCollectionOldDetalleFactura)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain DetalleFactura " + detalleFacturaCollectionOldDetalleFactura + " since its facturaVenta field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (clienteNew != null) {
                clienteNew = em.getReference(clienteNew.getClass(), clienteNew.getIdCliente());
                facturaVenta.setCliente(clienteNew);
            }
            Collection<DetalleFactura> attachedDetalleFacturaCollectionNew = new ArrayList<DetalleFactura>();
            for (DetalleFactura detalleFacturaCollectionNewDetalleFacturaToAttach : detalleFacturaCollectionNew) {
                detalleFacturaCollectionNewDetalleFacturaToAttach = em.getReference(detalleFacturaCollectionNewDetalleFacturaToAttach.getClass(), detalleFacturaCollectionNewDetalleFacturaToAttach.getDetalleFacturaPK());
                attachedDetalleFacturaCollectionNew.add(detalleFacturaCollectionNewDetalleFacturaToAttach);
            }
            detalleFacturaCollectionNew = attachedDetalleFacturaCollectionNew;
            facturaVenta.setDetalleFacturaCollection(detalleFacturaCollectionNew);
            facturaVenta = em.merge(facturaVenta);
            if (clienteOld != null && !clienteOld.equals(clienteNew)) {
                clienteOld.getFacturaVentaCollection().remove(facturaVenta);
                clienteOld = em.merge(clienteOld);
            }
            if (clienteNew != null && !clienteNew.equals(clienteOld)) {
                clienteNew.getFacturaVentaCollection().add(facturaVenta);
                clienteNew = em.merge(clienteNew);
            }
            for (DetalleFactura detalleFacturaCollectionNewDetalleFactura : detalleFacturaCollectionNew) {
                if (!detalleFacturaCollectionOld.contains(detalleFacturaCollectionNewDetalleFactura)) {
                    FacturaVenta oldFacturaVentaOfDetalleFacturaCollectionNewDetalleFactura = detalleFacturaCollectionNewDetalleFactura.getFacturaVenta();
                    detalleFacturaCollectionNewDetalleFactura.setFacturaVenta(facturaVenta);
                    detalleFacturaCollectionNewDetalleFactura = em.merge(detalleFacturaCollectionNewDetalleFactura);
                    if (oldFacturaVentaOfDetalleFacturaCollectionNewDetalleFactura != null && !oldFacturaVentaOfDetalleFacturaCollectionNewDetalleFactura.equals(facturaVenta)) {
                        oldFacturaVentaOfDetalleFacturaCollectionNewDetalleFactura.getDetalleFacturaCollection().remove(detalleFacturaCollectionNewDetalleFactura);
                        oldFacturaVentaOfDetalleFacturaCollectionNewDetalleFactura = em.merge(oldFacturaVentaOfDetalleFacturaCollectionNewDetalleFactura);
                    }
                }
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
                BigDecimal id = facturaVenta.getNumFactura();
                if (findFacturaVenta(id) == null) {
                    throw new NonexistentEntityException("The facturaVenta with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(BigDecimal id) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            FacturaVenta facturaVenta;
            try {
                facturaVenta = em.getReference(FacturaVenta.class, id);
                facturaVenta.getNumFactura();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The facturaVenta with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<DetalleFactura> detalleFacturaCollectionOrphanCheck = facturaVenta.getDetalleFacturaCollection();
            for (DetalleFactura detalleFacturaCollectionOrphanCheckDetalleFactura : detalleFacturaCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This FacturaVenta (" + facturaVenta + ") cannot be destroyed since the DetalleFactura " + detalleFacturaCollectionOrphanCheckDetalleFactura + " in its detalleFacturaCollection field has a non-nullable facturaVenta field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Cliente cliente = facturaVenta.getCliente();
            if (cliente != null) {
                cliente.getFacturaVentaCollection().remove(facturaVenta);
                cliente = em.merge(cliente);
            }
            em.remove(facturaVenta);
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

    public List<FacturaVenta> findFacturaVentaEntities() {
        return findFacturaVentaEntities(true, -1, -1);
    }

    public List<FacturaVenta> findFacturaVentaEntities(int maxResults, int firstResult) {
        return findFacturaVentaEntities(false, maxResults, firstResult);
    }

    private List<FacturaVenta> findFacturaVentaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(FacturaVenta.class));
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

    public FacturaVenta findFacturaVenta(BigDecimal id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(FacturaVenta.class, id);
        } finally {
            em.close();
        }
    }

    public int getFacturaVentaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<FacturaVenta> rt = cq.from(FacturaVenta.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
