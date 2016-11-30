package dk.nykredit.votes.persistence;

import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import dk.nykredit.votes.model.Poll;
import dk.nykredit.votes.model.Vote;
import dk.nykredit.nic.core.logging.LogDuration;

/**
 * Handles archiving (persistence) tasks for the account domain model.
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class AccountArchivist {
    @PersistenceContext(unitName = "accountPersistenceUnit")
    private EntityManager em;

    @LogDuration(limit = 50)
    public List<Poll> listAccounts() {
        TypedQuery<Poll> q = em.createQuery("select a from Account a", Poll.class);
        return q.getResultList();
    }

    /**
     * Find account by its primary key. Note this will throw {@link NoResultException} which will roll back the
     * transaction if the account is not found - if this is a problem consider using {@link #findAccount(String, String)}.
     */
    @LogDuration(limit = 50)
    public Poll getAccount(String regNo, String accountNo) {
        TypedQuery<Poll> q = em.createQuery("select a from Account a where a.regNo=:regNo and a.accountNo=:accountNo", Poll.class);
        q.setParameter("regNo", regNo);
        q.setParameter("accountNo", accountNo);
        return q.getSingleResult();
    }

    @LogDuration(limit = 50)
    public Optional<Poll> findAccount(String regNo, String accountNo) {
        try {
            return Optional.of(getAccount(regNo, accountNo));
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @LogDuration(limit = 50)
    public void save(Poll poll) {
        em.persist(poll);
    }

    @LogDuration(limit = 50)
    public Vote getTransaction(String regNo, String accountNo, String id) {
        TypedQuery<Vote> q = em.createQuery("select t from Transaction t " +
            "where t.account.regNo=:regNo and t.account.accountNo=:accountNo and t.id=:id", Vote.class);
        q.setParameter("regNo", regNo);
        q.setParameter("accountNo", accountNo);
        q.setParameter("id", id);
        return q.getSingleResult();
    }
}
