package se.kth.ict.id2203.components.rb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.kth.ict.id2203.ports.beb.BebBroadcast;
import se.kth.ict.id2203.ports.beb.BestEffortBroadcast;
import se.kth.ict.id2203.ports.rb.RbBroadcast;
import se.kth.ict.id2203.ports.rb.ReliableBroadcast;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import se.sics.kompics.Positive;
import se.sics.kompics.address.Address;

import java.util.HashSet;

public class EagerRb extends ComponentDefinition {

    private static final Logger logger = LoggerFactory.getLogger(EagerRb.class);

    private int seqNum;
    private HashSet<RbDataMessage> delivered;
    private final Address self;

    private final Positive<BestEffortBroadcast> bestEffortBroadcast = requires(BestEffortBroadcast.class);
    private final Negative<ReliableBroadcast> reliableBroadcast = provides(ReliableBroadcast.class);

    private Handler<RbBroadcast> rbBroadcastHandler = new Handler<RbBroadcast>() {
        @Override
        public void handle(RbBroadcast event) {
            seqNum++;
            trigger(new BebBroadcast(new RbDataMessage(self, event.getDeliverEvent(), seqNum)), bestEffortBroadcast);
        }
    };

    private Handler<RbDataMessage> rbDataMessageHandler = new Handler<RbDataMessage>() {
        @Override
        public void handle(RbDataMessage event) {
            if (!delivered.contains(event)) {
                delivered.add(event);
                trigger(event.getData(), reliableBroadcast);
                trigger(new BebBroadcast(event), bestEffortBroadcast);
            }
        }
    };

    public EagerRb(EagerRbInit init) {
        delivered = new HashSet<>();
        this.seqNum = 0;
        this.self = init.getSelfAddress();

        subscribe(rbBroadcastHandler, reliableBroadcast);
        subscribe(rbDataMessageHandler, bestEffortBroadcast);
    }

}
