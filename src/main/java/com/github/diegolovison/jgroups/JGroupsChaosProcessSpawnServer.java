package com.github.diegolovison.jgroups;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.jgroups.protocols.TP;
import org.jgroups.stack.Protocol;
import org.jgroups.stack.ProtocolStack;

import com.github.diegolovison.jgroups.protocol.ProtocolAction;
import com.github.diegolovison.os.SocketServer;

public class JGroupsChaosProcessSpawnServer extends SocketServer {

   static {
      instanceClass = JGroupsChaosProcessSpawnServer.class;
   }

   private JGroupsChaosProcessSameVM vm;

   @Override
   protected boolean onMessage(String message) {
      boolean _super = super.onMessage(message);
      if (!_super) {
         String method = getMethod(message);
         String[] args = getArguments(message);
         String response = null;
         if ("getNumberOfMembers".equals(method)) {
            response = getNumberOfMembers(args);
            _super = true;
         } else if ("getClusterName".equals(method)) {
            response = getClusterName(args);
            _super = true;
         } else if ("isCoordinator".equals(method)) {
            response = isCoordinator(args);
            _super = true;
         } else if ("insertProtocol".equals(method)) {
            response = insertProtocol(args);
            _super = true;
         } else if ("removeProtocol".equals(method)) {
            response = removeProtocol(args);
            _super = true;
         } else if ("disconnect".equals(method)) {
            response = disconnect(args);
            _super = true;
         } else if ("getAddress".equals(method)) {
            response = getAddress(args);
            _super = true;
         } else if ("waitForClusterToForm".equals(method)) {
            response = waitForClusterToForm(args);
            _super = true;
         } else if ("isRunning".equals(method)) {
            response = isRunning(args);
            _super = true;
         }
         if (_super) {
            out.println(response);
         }
      }
      return _super;
   }

   @Override
   protected void onStart(String[] args) {
      File configFile = new File(args[1]);
      JGroupsChaosConfig config;
      try (FileInputStream inputStream = new FileInputStream(configFile)) {
         config = JGroupsChaosConfig.JGroupsChaosConfigMarshaller.fromStream(inputStream);

      } catch (FileNotFoundException e) {
         throw new IllegalStateException(e);
      } catch (IOException e) {
         throw new IllegalStateException(e);
      }
      vm = new JGroupsChaosProcessSameVM();
      vm.run(config);
   }

   @Override
   protected void onStop() {
      vm.disconnect();
   }

   private String waitForClusterToForm(String[] args) {
      vm.waitForClusterToForm(Integer.valueOf(args[0]));
      return null;
   }

   private String getAddress(String[] args) {
      return vm.getAddress().toString();
   }

   private String disconnect(String[] args) {
      vm.disconnect();
      return null;
   }

   private String isRunning(String[] args) {
      return String.valueOf(vm.isRunning());
   }

   private String removeProtocol(String[] args) {
      try {
         vm.removeProtocol((Class<? extends Protocol>) Class.forName(args[0]));
      } catch (ClassNotFoundException e) {
         throw new IllegalStateException(e);
      }
      return null;
   }

   private String insertProtocol(String[] args) {
      ProtocolAction action = null;
      ProtocolStack.Position position = null;
      Class<TP> clazz = null;
      vm.insertProtocol(action, position, clazz);
      return null;
   }

   private String isCoordinator(String[] args) {
      return String.valueOf(vm.isCoordinator());
   }

   private String getClusterName(String[] args) {
      return vm.getClusterName();
   }

   private String getNumberOfMembers(String[] args) {
      return String.valueOf(vm.getNumberOfMembers());
   }


}
