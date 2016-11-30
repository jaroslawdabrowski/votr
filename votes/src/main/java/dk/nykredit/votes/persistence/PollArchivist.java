package dk.nykredit.votes.persistence;

import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import dk.nykredit.nic.core.logging.LogDuration;
import dk.nykredit.votes.model.Poll;

/**
 * @author Q1WS@nykredit.dk
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class PollArchivist {
    @PersistenceContext(unitName = "votesPersistenceUnit")
    private EntityManager em;

    @LogDuration(limit = 50)
    public void save(Poll poll) {
        em.persist(poll);
    }

    @LogDuration(limit = 50)
    public List<Poll> findByGroupId(String groupId) {
        TypedQuery<Poll> q = em.createQuery("select p from Poll p where p.groupId=:groupId order by p.startDate desc", Poll.class);
        q.setParameter("groupId", groupId);
        return q.getResultList();
    }

    public Optional<Poll> findById(String id) {
        TypedQuery<Poll> q = em.createQuery("select p from Poll p where p.id=:id", Poll.class);
        q.setParameter("id", id);
        return Optional.of(q.getSingleResult());
    }
}
