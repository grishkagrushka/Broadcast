package se.kth.ict.id2203.components.rb;

import se.kth.ict.id2203.ports.beb.BebDeliver;
import se.sics.kompics.address.Address;

public class RbDataMessage extends BebDeliver {

    private static final long serialVersionUID = -918178206771289595L;

    public RbDataMessage(Address source) {
        super(source);
    }
}
