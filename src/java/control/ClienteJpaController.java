/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package control;

import control.exceptions.NonexistentEntityException;
import control.exceptions.PreexistingEntityException;
import control.exceptions.RollbackFailureException;
import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import modelo.FacturaVenta;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;
import modelo.Cliente;

/**
 *
 * @author Entelgy
 */
public class ClienteJpaController implements Serializable {

    public ClienteJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Cliente cliente) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (cliente.getFacturaVentaCollection() == null) {
            cliente.setFacturaVentaCollection(new ArrayList<FacturaVenta>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Collection<FacturaVenta> attachedFacturaVentaCollection = new ArrayList<FacturaVenta>();
            for (FacturaVenta facturaVentaCollectionFacturaVentaToAttach : cliente.getFacturaVentaCollection()) {
                facturaVentaCollectionFacturaVentaToAttach = em.getReference(facturaVentaCollectionFacturaVentaToAttach.getClass(), facturaVentaCollectionFacturaVentaToAttach.getNumFactura());
                attachedFacturaVentaCollection.add(facturaVentaCollectionFacturaVentaToAttach);
            }
            cliente.setFacturaVentaCollection(attachedFacturaVentaCollection);
            em.persist(cliente);
            for (FacturaVenta facturaVentaCollectionFacturaVenta : cliente.getFacturaVentaCollection()) {
                Cliente oldClienteOfFacturaVentaCollectionFacturaVenta = facturaVentaCollectionFacturaVenta.getCliente();
                facturaVentaCollectionFacturaVenta.setCliente(cliente);
                facturaVentaCollectionFacturaVenta = em.merge(facturaVentaCollectionFacturaVenta);
                if (oldClienteOfFacturaVentaCollectionFacturaVenta != null) {
                    oldClienteOfFacturaVentaCollectionFacturaVenta.getFacturaVentaCollection().remove(facturaVentaCollectionFacturaVenta);
                    oldClienteOfFacturaVentaCollectionFacturaVenta = em.merge(oldClienteOfFacturaVentaCollectionFacturaVenta);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findCliente(cliente.getIdCliente()) != null) {
                throw new PreexistingEntityException("Cliente " + cliente + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Cliente cliente) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Cliente persistentCliente = em.find(Cliente.class, cliente.getIdCliente());
            Collection<FacturaVenta> facturaVentaCollectionOld = persistentCliente.getFacturaVentaCollection();
            Collection<FacturaVenta> facturaVentaCollectionNew = cliente.getFacturaVentaCollection();
            Collection<FacturaVenta> attachedFacturaVentaCollectionNew = new ArrayList<FacturaVenta>();
            for (FacturaVenta facturaVentaCollectionNewFacturaVentaToAttach : facturaVentaCollectionNew) {
                facturaVentaCollectionNewFacturaVentaToAttach = em.getReference(facturaVentaCollectionNewFacturaVentaToAttach.getClass(), facturaVentaCollectionNewFacturaVentaToAttach.getNumFactura());
                attachedFacturaVentaCollectionNew.add(facturaVentaCollectionNewFacturaVentaToAttach);
            }
            facturaVentaCollectionNew = attachedFacturaVentaCollectionNew;
            cliente.setFacturaVentaCollection(facturaVentaCollectionNew);
            cliente = em.merge(cliente);
            for (FacturaVenta facturaVentaCollectionOldFacturaVenta : facturaVentaCollectionOld) {
                if (!facturaVentaCollectionNew.contains(facturaVentaCollectionOldFacturaVenta)) {
                    facturaVentaCollectionOldFacturaVenta.setCliente(null);
                    facturaVentaCollectionOldFacturaVenta = em.merge(facturaVentaCollectionOldFacturaVenta);
                }
            }
            for (FacturaVenta facturaVentaCollectionNewFacturaVenta : facturaVentaCollectionNew) {
                if (!facturaVentaCollectionOld.contains(facturaVentaCollectionNewFacturaVenta)) {
                    Cliente oldClienteOfFacturaVentaCollectionNewFacturaVenta = facturaVentaCollectionNewFacturaVenta.getCliente();
                    facturaVentaCollectionNewFacturaVenta.setCliente(cliente);
                    facturaVentaCollectionNewFacturaVenta = em.merge(facturaVentaCollectionNewFacturaVenta);
                    if (oldClienteOfFacturaVentaCollectionNewFacturaVenta != null && !oldClienteOfFacturaVentaCollectionNewFacturaVenta.equals(cliente)) {
                        oldClienteOfFacturaVentaCollectionNewFacturaVenta.getFacturaVentaCollection().remove(facturaVentaCollectionNewFacturaVenta);
                        oldClienteOfFacturaVentaCollectionNewFacturaVenta = em.merge(oldClienteOfFacturaVentaCollectionNewFacturaVenta);
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
                BigDecimal id = cliente.getIdCliente();
                if (findCliente(id) == null) {
                    throw new NonexistentEntityException("The cliente with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(BigDecimal id) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Cliente cliente;
            try {
                cliente = em.getReference(Cliente.class, id);
                cliente.getIdCliente();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The cliente with id " + id + " no longer exists.", enfe);
            }
            Collection<FacturaVenta> facturaVentaCollection = cliente.getFacturaVentaCollection();
            for (FacturaVenta facturaVentaCollectionFacturaVenta : facturaVentaCollection) {
                facturaVentaCollectionFacturaVenta.setCliente(null);
                facturaVentaCollectionFacturaVenta = em.merge(facturaVentaCollectionFacturaVenta);
            }
            em.remove(cliente);
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

    public List<Cliente> findClienteEntities() {
        return findClienteEntities(true, -1, -1);
    }

    public List<Cliente> findClienteEntities(int maxResults, int firstResult) {
        return findClienteEntities(false, maxResults, firstResult);
    }

    private List<Cliente> findClienteEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Cliente.class));
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

    public Cliente findCliente(BigDecimal id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Cliente.class, id);
        } finally {
            em.close();
        }
    }

    public int getClienteCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Cliente> rt = cq.from(Cliente.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
