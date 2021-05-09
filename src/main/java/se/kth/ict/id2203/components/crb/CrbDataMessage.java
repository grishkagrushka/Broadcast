package se.kth.ict.id2203.components.crb;

import se.kth.ict.id2203.ports.crb.CrbDeliver;
import se.kth.ict.id2203.ports.rb.RbDeliver;
import se.sics.kompics.address.Address;

import java.util.ArrayList;

public class CrbDataMessage extends RbDeliver implements Comparable<CrbDataMessage>{

    private static final long serialVersionUID = 3236160554463192926L;

    private final int seqNum;
    private final ArrayList<Integer> seqNumArrayList;
    private final CrbDeliver data;

    public CrbDataMessage(Address source, int seqNum, ArrayList<Integer> seqNumArrayList, CrbDeliver data) {
        super(source);
        this.seqNum = seqNum;
        this.seqNumArrayList = new ArrayList<>(seqNumArrayList);
        this.data = data;
    }


    @Override
    public int compareTo(CrbDataMessage o) {
        return this.seqNum - o.seqNum;
    }

    public ArrayList<Integer> getSeqNumArrayList() {
        return seqNumArrayList;
    }

    public CrbDeliver getData() {
        return data;
    }
}
