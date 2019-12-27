package com.github.diegolovison.jgroups;

import org.jgroups.Address;
import org.jgroups.protocols.TP;
import org.jgroups.stack.ProtocolStack;
import org.jgroups.util.UUID;

import com.github.diegolovison.jgroups.protocol.ProtocolAction;
import com.github.diegolovison.os.ChaosProcess;
import com.github.diegolovison.os.SocketClient;

public class ProcessSpawn {

   public static void insertProtocol(SocketClient client, Class<? extends ProtocolAction> protocolActionClass, ProtocolStack.Position above,
                               Class<TP> tpClass, Address[] ignored) {
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < ignored.length; i++) {
         Address a = ignored[i];
         if (a instanceof UUID) {
            sb.append(((UUID) a).toStringLong());
         } else {
            throw new UnsupportedOperationException("Only UUID was implemented");
         }
         if (i+1 < ignored.length) {
            sb.append(", ");
         }
      }
      client.sendMessage("insertProtocol[" +
            protocolActionClass.getName() + ", " +
            above.toString() + ", " +
            tpClass.getName() + ", " +
            sb.toString() +
            "]");
   }

   public static String insertProtocol(ChaosProcess chaosProcess, String[] args) {
      try {
         Class<? extends ProtocolAction> protocolActionClass = (Class<? extends ProtocolAction>) Class.forName(args[0]);
         ProtocolStack.Position above = ProtocolStack.Position.valueOf(args[1]);
         Class<TP> tpClass = (Class<TP>) Class.forName(args[2]);
         int ignoredIndex = 3;
         Address[] ignored = new Address[args.length - ignoredIndex];
         if (ignored.length > 0) {
            for (int i = ignoredIndex; i < args.length; i++) {
               ignored[i - ignoredIndex] = UUID.fromString(args[i]);
            }
         }
         chaosProcess.insertProtocol(protocolActionClass, above, tpClass, ignored);
         return null;
      } catch (ClassNotFoundException e) {
         throw new IllegalStateException(e);
      }
   }
}
