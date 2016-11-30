package dk.nykredit.bank.account.model;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import dk.nykredit.nic.persistence.jpa.AbstractAuditable;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Very basic modelling of account concept to show the basic use of JPA for persistence handling.
 */
@Entity
@Table(name = "POLL")
public class Poll extends AbstractAuditable {
    /**
     * TID - the technical unique identifier for instance, i.e., primary key. This should NEVER EVER be
     * exposed out side the service since it is a key very internal to this service.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID", nullable = false)
    private int id;

    /**
     * Very very rudimentary modelling of account only used for the sake of the example.
     */
    @Column(name = "GROUP_ID", length = 40, nullable = false)
    private String groupId;

    /**
     * Very very rudimentary modelling of account only used for the sake of the example.
     */
    @Column(name = "ACCOUNT_NO", length = 12, nullable = false)
    private String accountNo;

    /**
     * Very very rudimentary modelling of account only used for the sake of the example.
     */
    @Column(name = "NAME", length = 40, nullable = false)
    private String name;


    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Vote> votes;

    protected Poll() {
        // Required by JPA
    }

    public Poll(String groupId, String accountNo, String name) {
        this.groupId = groupId;
        this.accountNo = accountNo;
        this.name = name;
        votes = new HashSet<>();
    }

    public String getGroupId() {
        return groupId;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Vote> getVotes() {
        return Collections.unmodifiableSet(votes);
    }

    public void addTransaction(String description, BigDecimal amount) {
        votes.add(new Vote(this, amount, description));
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("groupId", groupId)
                .append("accountNo", accountNo)
                .append("name", name)
                .toString();
    }

}
