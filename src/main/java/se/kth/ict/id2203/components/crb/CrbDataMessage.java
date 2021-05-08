package se.kth.ict.id2203.components.crb;

import se.kth.ict.id2203.ports.rb.RbDeliver;
import se.sics.kompics.address.Address;

public class CrbDataMessage extends RbDeliver {

    private static final long serialVersionUID = 3236160554463192926L;

    public CrbDataMessage(Address source) {
        super(source);
    }
}
