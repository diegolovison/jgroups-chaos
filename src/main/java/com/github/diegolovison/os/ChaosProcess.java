package com.github.diegolovison.os;

import org.jgroups.Address;
import org.jgroups.protocols.TP;
import org.jgroups.stack.Protocol;
import org.jgroups.stack.ProtocolStack;

import com.github.diegolovison.jgroups.protocol.ProtocolAction;

public abstract class ChaosProcess<T> {

   // abstract
   public abstract ChaosProcess run(T supplier);
   public abstract int getNumberOfMembers();
   public abstract String getClusterName();
   public abstract boolean isRunning();
   public abstract boolean isCoordinator();
   public abstract void insertProtocol(ProtocolAction protocolAction, ProtocolStack.Position above, Class<TP> tpClass);
   public abstract void removeProtocol(Class<? extends Protocol> protocolClass);
   public abstract void disconnect();
   public abstract Address getAddress();
}
