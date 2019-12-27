package com.github.diegolovison.jgroups;

import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.protocols.TP;
import org.jgroups.stack.Protocol;
import org.jgroups.stack.ProtocolStack;

import com.github.diegolovison.jgroups.protocol.ProtocolAction;
import com.github.diegolovison.os.ChaosProcess;

/**
 * Wrapper for {@link JChannel} interactions
 */
public class Node {

   private final ChaosProcess chaosProcess;

   public Node(ChaosProcess chaosProcess) {
      this.chaosProcess = chaosProcess;
   }

   public boolean isCoordinator() {
      return this.chaosProcess.isCoordinator();
   }

   public void insertProtocol(Class<? extends ProtocolAction> protocolActionClass, ProtocolStack.Position above,
                              Class<TP> tpClass, Address[] ignored) {
      this.chaosProcess.insertProtocol(protocolActionClass, above, tpClass, ignored);
   }

   public void removeProtocol(Class<? extends Protocol> protocolClass) {
      this.chaosProcess.removeProtocol(protocolClass);
   }

   public Address getAddress() {
      return this.chaosProcess.getAddress();
   }

   public void disconnect() {
      this.chaosProcess.disconnect();
   }
}
