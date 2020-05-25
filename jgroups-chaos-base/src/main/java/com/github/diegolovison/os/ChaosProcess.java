package com.github.diegolovison.os;

import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.TimeoutException;

import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.protocols.TP;
import org.jgroups.stack.Protocol;
import org.jgroups.stack.ProtocolStack;
import org.zeroturnaround.exec.ProcessExecutor;

import com.github.diegolovison.protocol.ProtocolAction;

public abstract class ChaosProcess<T> {

   private Collection<String> jvmStartupArgs;

   // abstract
   public abstract ChaosProcess run(T supplier);
   public abstract int getNumberOfMembers();
   public abstract String getClusterName();
   public abstract boolean isRunning();
   public abstract boolean isCoordinator();
   public abstract void removeProtocol(Class<? extends Protocol> protocolClass);
   public abstract void disconnect();
   public abstract Address getAddress();
   public abstract JChannel getJChannel();

   public void insertProtocol(Class<? extends ProtocolAction> protocolActionClass, ProtocolStack.Position above,
                              Class<TP> tpClass, Address[] ignored) {
      ProtocolAction.insert(getJChannel(), protocolActionClass, above, tpClass, ignored);
   }

   public Collection<String> getJvmStartupArgs() {
      return jvmStartupArgs;
   }

   public void setJvmStartupArgs(Collection<String> jvmStartupArgs) {
      this.jvmStartupArgs = jvmStartupArgs;
   }

   public abstract long getPid();

   public void pause() {
      if (ProcessHandle.current().pid() == getPid()) {
         throw new IllegalStateException("You can't pause the same process");
      }
      try {
         new ProcessExecutor().command("kill", "-STOP", String.valueOf(getPid())).readOutput(true).exitValues(0).execute().outputUTF8();
      } catch (IOException | InterruptedException | TimeoutException e) {
         throw new IllegalStateException(e);
      }
   }

   public void resume() {
      if (ProcessHandle.current().pid() == getPid()) {
         throw new IllegalStateException("You can't resume the same process");
      }
      try {
         new ProcessExecutor().command("kill", "-CONT", String.valueOf(getPid())).readOutput(true).exitValues(0).execute().outputUTF8();
      } catch (IOException | InterruptedException | TimeoutException e) {
         throw new IllegalStateException(e);
      }
   }

   public String getPidStatus() {
      String output;
      try {
         output = new ProcessExecutor().command("ps", "-o", "state=", "-p", String.valueOf(getPid())).readOutput(true).exitValues(0).execute().outputUTF8();
      } catch (IOException | InterruptedException | TimeoutException e) {
         throw new IllegalStateException(e);
      }
      return output.split(" ")[0].trim();
   }
}
