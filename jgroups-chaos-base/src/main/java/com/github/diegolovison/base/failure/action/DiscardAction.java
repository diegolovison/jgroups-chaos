package com.github.diegolovison.base.failure.action;

import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.protocols.DISCARD;

import com.github.diegolovison.protocol.ProtocolAction;

public class DiscardAction extends ProtocolAction<DISCARD> {

   private Address[] ignored;

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
      discard.addIgnoredMembers(ignored);
   }

   @Override
   public void setIgnored(Address[] ignored) {
      this.ignored = ignored;
   }
}
