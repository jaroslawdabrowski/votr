package dk.nykredit.votes.persistence;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import dk.nykredit.nic.core.logging.LogDuration;
import dk.nykredit.votes.model.Vote;

/**
 * @author Q1WS@nykredit.dk
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class VoteArchivist {
    @PersistenceContext(unitName = "votesPersistenceUnit")
    private EntityManager em;

    @LogDuration(limit = 50)
    public void save(Vote vote) {
        em.persist(vote);
    }
}
