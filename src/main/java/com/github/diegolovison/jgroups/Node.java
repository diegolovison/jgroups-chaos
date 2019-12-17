package com.github.diegolovison.jgroups;

import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.protocols.DISCARD;
import org.jgroups.protocols.TP;
import org.jgroups.stack.Protocol;
import org.jgroups.stack.ProtocolStack;

import com.github.diegolovison.jgroups.protocol.ProtocolAction;

/**
 * Wrapper for {@link JChannel} interactions
 */
public class Node {

   private final int nodeIndex;
   private final NodeConfig nodeConfig;

   public Node(int nodeIndex, NodeConfig nodeConfig) {
      this.nodeIndex = nodeIndex;
      this.nodeConfig = nodeConfig;
   }

   int getMembersSize() {
      return this.getChannel().getView().getMembers().size();
   }

   void connect() {
      try {
         this.getChannel().connect(this.nodeConfig.getClusterName());
      } catch (Exception e) {
         throw new IllegalStateException(e);
      }
   }

   boolean isRunning() {
      return this.getChannel().isConnected();
   }

   void close() {
      this.getChannel().close();
   }

   private JChannel getChannel() {
      return this.nodeConfig.getChannel();
   }

   public boolean isCoordinator() {
      // the first member of a view is the coordinator
      JChannel channel = getChannel();
      Address address = channel.getAddress();
      return channel.getView().getMembers().get(0).equals(address);
   }

   public Address getAddress() {
      return getChannel().getAddress();
   }

   public void insertProtocol(ProtocolAction protocolAction, ProtocolStack.Position above, Class<TP> tpClass) {
      JChannel channel = getChannel();
      try {
         Protocol protocol = protocolAction.find(channel);
         if (protocol == null) {
            protocol = protocolAction.create();
         }
         if (protocolAction.shouldOverwriteSet()) {
            protocolAction.set(protocol);
         }
         channel.getProtocolStack().insertProtocol(protocol, above, tpClass);
      } catch (Exception e) {
         throw new IllegalStateException("Cannot insert protocol", e);
      }
   }

   @Override
   public String toString() {
      return "Node{" +
            "nodeIndex=" + nodeIndex +
            '}';
   }
}
