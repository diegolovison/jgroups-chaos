package com.github.diegolovison.jgroups.failure;

import java.util.List;

import org.jgroups.Address;

import com.github.diegolovison.jgroups.Node;
import com.github.diegolovison.jgroups.failure.provider.GCStopWorldFailureProvider;
import com.github.diegolovison.jgroups.failure.provider.NetworkPartitionDiscardFailureProvider;

public interface FailureProvider {

   void createFailure(Node[] nodes, List<Node> ignored);

   void solveFailure(Node... nodes);

   static FailureProvider get(Failure failure) {
      if (Failure.NetworkPartition.equals(failure)) {
         return new NetworkPartitionDiscardFailureProvider();
      } else if (Failure.GCStopWorld.equals(failure)) {
         return new GCStopWorldFailureProvider();
      }
      throw new NullPointerException(String.format("Can't find the failure provider: %s", failure));
   }

   default Address[] addressFrom(List<Node> nodes) {
      Address[] addresses = new Address[nodes.size()];
      for (int i = 0; i < nodes.size(); i++) {
         addresses[i] = nodes.get(i).getAddress();
      }
      return addresses;
   }

   void waitForFailure(Node node);

   void waitForFailureBeSolved(Node node);
}
