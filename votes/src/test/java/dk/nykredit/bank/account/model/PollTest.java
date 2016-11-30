package dk.nykredit.bank.account.model;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.junit.Test;

public class PollTest {

    @Test
    public void testAddTransaction() {
        Poll poll = new Poll("5479", "123456", "Savings poll");
        poll.addTransaction("description", new BigDecimal("1234.42"));

        assertEquals(1, poll.getVotes().size());
        Vote vote = poll.getVotes().iterator().next();
        assertEquals("description", vote.getDescription());
        assertEquals(new BigDecimal("1234.42"), vote.getAmount());
    }
}
