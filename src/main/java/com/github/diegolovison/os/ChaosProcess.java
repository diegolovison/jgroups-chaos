package com.github.diegolovison.os;

import org.jgroups.Address;
import org.jgroups.protocols.TP;
import org.jgroups.stack.Protocol;
import org.jgroups.stack.ProtocolStack;

import com.github.diegolovison.jgroups.protocol.ProtocolAction;

public abstract class ChaosProcess<T> {

   // attr
   private ChaosProcessStatus status = ChaosProcessStatus.STOPPED;

   // abstract
   public abstract ChaosProcess run(T supplier);
   public abstract int getNumberOfMembers();
   public abstract String getClusterName();

   // public
   public void setStatus(ChaosProcessStatus status) {
      this.status = status;
   }

   public boolean isRunning() {
      return ChaosProcessStatus.RUNNING.equals(this.status);
   }

   public abstract boolean isCoordinator();
   public abstract void insertProtocol(ProtocolAction protocolAction, ProtocolStack.Position above, Class<TP> tpClass);
   public abstract void removeProtocol(Class<? extends Protocol> protocolClass);
   public abstract void disconnect();
   public abstract Address getAddress();

   // static
   public enum ChaosProcessStatus {
      STOPPED, RUNNING
   }
}
