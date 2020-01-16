package com.github.diegolovison.jgroups.failure.provider;

import static com.github.diegolovison.jgroups.Sleep.sleep;

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
      for (Node node : nodes) {
         node.insertProtocol(DiscardAction.class, ProtocolStack.Position.ABOVE, TP.class, addressFrom(ignored));
      }
      waitForFailure(null);
   }

   @Override
   public void solveFailure(Node... nodes) {
      for (Node node : nodes) {
         node.removeProtocol(DISCARD.class);
      }
      waitForFailureBeSolved(null);
   }

   @Override
   public void waitForFailure(Node node) {
      // TODO Find out how to detect the failure
      sleep(60 * 1_000);
   }

   @Override
   public void waitForFailureBeSolved(Node node) {
      // TODO Find out how to detect that the failure was solved
      sleep(60 * 1_000);
   }
}
