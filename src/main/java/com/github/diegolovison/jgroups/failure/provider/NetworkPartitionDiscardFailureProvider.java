package com.github.diegolovison.jgroups.failure.provider;

import static com.github.diegolovison.jgroups.Sleep.sleep;

import java.util.List;

import org.jgroups.JChannel;
import org.jgroups.protocols.DISCARD;
import org.jgroups.protocols.TP;
import org.jgroups.stack.ProtocolStack;

import com.github.diegolovison.jgroups.Node;
import com.github.diegolovison.jgroups.failure.FailureProvider;
import com.github.diegolovison.jgroups.protocol.ProtocolAction;

public class NetworkPartitionDiscardFailureProvider implements FailureProvider {
   @Override
   public void createFailure(Node[] nodes, List<Node> ignored) {
      for (Node node : nodes) {
         ProtocolAction protocolAction = new ProtocolAction<DISCARD>() {
            @Override
            public DISCARD create() {
               return new DISCARD();
            }

            @Override
            public DISCARD find(JChannel channel) {
               return channel.getProtocolStack().findProtocol(DISCARD.class);
            }

            @Override
            public void set(DISCARD discard) {
               discard.addIgnoredMembers(addressFrom(ignored));
            }
         };
         node.insertProtocol(protocolAction, ProtocolStack.Position.ABOVE, TP.class);
      }
   }

   @Override
   public void solveFailure(Node... nodes) {
      for (Node node : nodes) {
         node.removeProtocol(DISCARD.class);
      }
   }

   @Override
   public void waitForFailure() {
      // TODO Find out how to detect the failure
      sleep(60 * 1_000);
   }

   @Override
   public void waitForFailureBeSolved() {
      // TODO Find out how to detect that the failure was solved
      sleep(60 * 1_000);
   }
}
