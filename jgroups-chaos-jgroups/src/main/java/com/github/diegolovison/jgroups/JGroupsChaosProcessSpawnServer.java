package com.github.diegolovison.jgroups;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;

import org.jgroups.stack.Protocol;
import org.jgroups.util.UUID;

import com.github.diegolovison.base.ChaosConfig;
import com.github.diegolovison.base.ProcessSpawn;
import com.github.diegolovison.os.Response;
import com.github.diegolovison.os.SocketClientHandler;
import com.github.diegolovison.os.SocketServer;

public class JGroupsChaosProcessSpawnServer extends SocketServer {

   static {
      instanceClass = JGroupsChaosProcessSpawnServer.class;
   }

   private JGroupsChaosProcessSameVM vm;

   public JGroupsChaosProcessSpawnServer() {
      this.vm = new JGroupsChaosProcessSameVM();
   }

   @Override
   protected void onStart(String[] args) {
      File configFile = new File(args[1]);
      JGroupsChaosConfig config;
      try (FileInputStream inputStream = new FileInputStream(configFile)) {
         config = ChaosConfig.ChaosConfigMarshaller.fromStream(inputStream);
      } catch (FileNotFoundException e) {
         throw new IllegalStateException(e);
      } catch (IOException e) {
         throw new IllegalStateException(e);
      }
      this.vm.run(config);
   }

   @Override
   protected SocketClientHandler createSocketClientHandler(Socket s) throws IOException {
      return new SocketClientHandler(s) {
         @Override
         protected Response doRequest(String method, String[] args) {
            Response response = super.doRequest(method, args);
            if (Response.EMPTY_RESPONSE.equals(response)) {
               if ("getNumberOfMembers".equals(method)) {
                  return new Response(getNumberOfMembers(args));
               } else if ("getClusterName".equals(method)) {
                  return new Response(getClusterName(args));
               } else if ("isCoordinator".equals(method)) {
                  return new Response(isCoordinator(args));
               } else if ("insertProtocol".equals(method)) {
                  return new Response(insertProtocol(args));
               } else if ("removeProtocol".equals(method)) {
                  return new Response(removeProtocol(args));
               } else if ("disconnect".equals(method)) {
                  return new Response(disconnect(args));
               } else if ("getAddress".equals(method)) {
                  return new Response(getAddress(args));
               } else if ("waitForClusterToForm".equals(method)) {
                  return new Response(waitForClusterToForm(args));
               } else if ("isRunning".equals(method)) {
                  return new Response(isRunning(args));
               }
            }
            return response;
         }
      };
   }

   private String waitForClusterToForm(String[] args) {
      this.vm.waitForClusterToForm(Integer.valueOf(args[0]));
      return null;
   }

   private String getAddress(String[] args) {
      return ((UUID)vm.getAddress()).toStringLong();
   }

   private String disconnect(String[] args) {
      this.vm.disconnect();
      this.stop();
      return null;
   }

   private String isRunning(String[] args) {
      return String.valueOf(this.vm.isRunning());
   }

   private String removeProtocol(String[] args) {
      try {
         this.vm.removeProtocol((Class<? extends Protocol>) Class.forName(args[0]));
      } catch (ClassNotFoundException e) {
         throw new IllegalStateException(e);
      }
      return null;
   }

   private String insertProtocol(String[] args) {
      return ProcessSpawn.insertProtocol(this.vm, args);
   }

   private String isCoordinator(String[] args) {
      return String.valueOf(this.vm.isCoordinator());
   }

   private String getClusterName(String[] args) {
      return this.vm.getClusterName();
   }

   private String getNumberOfMembers(String[] args) {
      return String.valueOf(this.vm.getNumberOfMembers());
   }


}
