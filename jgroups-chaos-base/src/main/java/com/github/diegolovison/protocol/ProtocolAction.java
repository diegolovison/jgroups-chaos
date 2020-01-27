package com.github.diegolovison.protocol;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.protocols.TP;
import org.jgroups.stack.Protocol;
import org.jgroups.stack.ProtocolStack;

public abstract class ProtocolAction<P extends Protocol> {

   private final boolean overwriteSet;

   public ProtocolAction() {
      this(true);
   }

   public ProtocolAction(boolean overwriteSet) {
      this.overwriteSet = overwriteSet;
   }

   public abstract P create();

   public abstract P find(JChannel channel);

   public abstract void set(P protocol);

   public abstract void setIgnored(Address[] addresses);

   public boolean shouldOverwriteSet() {
      return overwriteSet;
   }

   public static void insert(JChannel channel, Class<? extends ProtocolAction> protocolActionClass, ProtocolStack.Position above,
                                       Class<TP> tpClass, Address[] ignored) {
      ProtocolAction protocolAction;
      try {
         protocolAction = protocolActionClass.newInstance();
         Method method = protocolAction.getClass().getDeclaredMethod("setIgnored", Address[].class);
         method.invoke(protocolAction, new Object[]{ignored});
      } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
         throw new IllegalStateException(e);
      }
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
}
