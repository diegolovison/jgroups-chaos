package com.github.diegolovison.jgroups.protocol;

import org.jgroups.JChannel;
import org.jgroups.stack.Protocol;

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

   public boolean shouldOverwriteSet() {
      return overwriteSet;
   }
}
