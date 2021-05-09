package se.kth.ict.id2203.components.rb;

import se.kth.ict.id2203.ports.beb.BebDeliver;
import se.kth.ict.id2203.ports.rb.RbDeliver;
import se.sics.kompics.address.Address;

import java.util.Objects;

public class RbDataMessage extends BebDeliver {

    private static final long serialVersionUID = -918178206771289595L;
    private final RbDeliver data;
    private final int seqNum;

    public RbDataMessage(Address source, RbDeliver data, int seqNum) {
        super(source);
        this.data = data;
        this.seqNum = seqNum;
    }

    public RbDeliver getData() {
        return data;
    }

    public boolean equals(Object o) {
        return Objects.equals(this.data, ((RbDataMessage) o).data) && Objects.equals(this.seqNum, ((RbDataMessage)o).seqNum);
    }

    public int hashCode() {
        return Objects.hashCode(this.data) + Objects.hashCode(this.seqNum);
    }
}
