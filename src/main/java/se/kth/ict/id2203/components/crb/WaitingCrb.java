/**
 * This file is part of the ID2203 course assignments kit.
 * <p>
 * Copyright (C) 2009-2013 KTH Royal Institute of Technology
 * <p>
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package se.kth.ict.id2203.components.crb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.kth.ict.id2203.ports.crb.CausalOrderReliableBroadcast;
import se.kth.ict.id2203.ports.crb.CrbBroadcast;
import se.kth.ict.id2203.ports.rb.RbBroadcast;
import se.kth.ict.id2203.ports.rb.ReliableBroadcast;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import se.sics.kompics.Positive;
import se.sics.kompics.address.Address;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeSet;

public class WaitingCrb extends ComponentDefinition {

    private static final Logger logger = LoggerFactory.getLogger(WaitingCrb.class);

    private ArrayList<Integer> seqNumArrayList;
    private final HashSet<Address> allAddress;
    private int seqNum;
    private TreeSet<CrbDataMessage> pending;
    private final Address self;

    private Positive<ReliableBroadcast> reliableBroadcast = requires(ReliableBroadcast.class);
    private Negative<CausalOrderReliableBroadcast> causalOrderReliableBroadcast = provides(CausalOrderReliableBroadcast.class);

    private Handler<CrbBroadcast> crbBroadcastHandler = new Handler<CrbBroadcast>() {
        @Override
        public void handle(CrbBroadcast event) {
            ArrayList<Integer> localArrayList = new ArrayList<>(seqNumArrayList);
            localArrayList.set(self.getId() - 1, seqNum);
            seqNum++;
            trigger(new RbBroadcast(new CrbDataMessage(self, seqNum, localArrayList, event.getDeliverEvent())), reliableBroadcast);
        }
    };

    private Handler<CrbDataMessage> crbDataMessageHandler = new Handler<CrbDataMessage>() {
        @Override
        public void handle(CrbDataMessage event) {
            pending.add(event);

            Iterator<CrbDataMessage> iterator = pending.iterator();
            CrbDataMessage curCrbDataMessage;

            while (iterator.hasNext()) {
                curCrbDataMessage = iterator.next();
                if (lessOrEqual(curCrbDataMessage.getSeqNumArrayList(), seqNumArrayList)) {
                    iterator.remove();
                    seqNumArrayList.set(curCrbDataMessage.getSource().getId() - 1,
                            seqNumArrayList.get(curCrbDataMessage.getSource().getId() - 1) + 1);
                    trigger(curCrbDataMessage.getData(), causalOrderReliableBroadcast);
                } else {
                    break;
                }
            }
        }
    };

    private boolean lessOrEqual(ArrayList<Integer> v, ArrayList<Integer> w) {
        boolean result = false;
        if (v.size() == w.size()) {
            for (Integer i : v) {
                if (v.get(i) <= w.get(i)) {
                    result = true;
                } else {
                    result = false;
                    break;
                }
            }
        }
        return result;
    }

    public WaitingCrb(WaitingCrbInit init) {
        allAddress = new HashSet<>(init.getAllAddresses());
        seqNumArrayList = new ArrayList<>(allAddress.size());
        seqNum = 0;
        pending = new TreeSet<>();
        self = init.getSelfAddress();

        for (int i = 0; i < allAddress.size(); i++) {
            seqNumArrayList.add(0);
        }

        subscribe(crbBroadcastHandler, causalOrderReliableBroadcast);
        subscribe(crbDataMessageHandler, reliableBroadcast);
    }
}
