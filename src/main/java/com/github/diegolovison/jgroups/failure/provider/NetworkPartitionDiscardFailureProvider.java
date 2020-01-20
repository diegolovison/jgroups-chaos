package com.github.diegolovison.jgroups.failure.provider;

import java.util.List;

import org.jgroups.protocols.DISCARD;
import org.jgroups.protocols.TP;
import org.jgroups.stack.ProtocolStack;

import com.github.diegolovison.jgroups.Node;
import com.github.diegolovison.jgroups.failure.FailureProvider;
import com.github.diegolovison.jgroups.failure.action.DiscardAction;

public class NetworkPartitionDiscardFailureProvider implements FailureProvider {
   @Override
   public void createFailure(Node[] nodes, List<Node> ignored) {
      if (nodes.length > 0) {
         for (Node node : nodes) {
            node.insertProtocol(DiscardAction.class, ProtocolStack.Position.ABOVE, TP.class, addressFrom(ignored));
         }
         waitForFailure(nodes);
      }
   }

   @Override
   public void solveFailure(Node... nodes) {
      if (nodes.length > 0) {
         for (Node node : nodes) {
            node.removeProtocol(DISCARD.class);
         }
         waitForFailureBeSolved(nodes);
      }
   }

   @Override
   public void waitForFailure(Node... nodes) {
      waitFor(nodes);
   }

   @Override
   public void waitForFailureBeSolved(Node... nodes) {
      waitFor(nodes);
   }

   private void waitFor(Node... nodes) {
      long begin = System.currentTimeMillis();
      long maxMs = 60_000; // 1 minute
      boolean done = false;
      while (System.currentTimeMillis() - begin < maxMs) {
         try {
            Thread.sleep(1_000);
         } catch (InterruptedException e) {
            throw new IllegalStateException(e);
         }
         if (nodes.length == nodes[0].getNumberOfMembers()) {
            done = true;
            break;
         }
      }
      if (!done) {
         throw new IllegalStateException(String.format("Maybe more than %d seconds is required", maxMs / 1000));
      }
   }
}
