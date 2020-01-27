package com.github.diegolovison.os;

import static com.github.diegolovison.os.ClosableUtil.closeSilent;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public abstract class SocketServer {

   protected static Class<? extends SocketServer> instanceClass;

   private ServerSocket ss;
   private List<SocketClientHandler> socketClientHandlers;

   protected abstract void onStart(String[] args);

   public void start(String[] args) throws IOException {
      this.socketClientHandlers = new ArrayList<>();
      try {
         this.ss = new ServerSocket(Integer.valueOf(args[0]));
         onStart(args);

         while (true) {
            try {
               Socket s = ss.accept();
               SocketClientHandler socketClientHandler = createSocketClientHandler(s);
               socketClientHandler.start();
               this.socketClientHandlers.add(socketClientHandler);
            } catch (IOException e) {
               // silent ss.accept() can be closed
               break;
            }
         }
      } finally {
         closeSilent(this.ss);
         this.socketClientHandlers.forEach(c -> closeSilent(c));
         this.socketClientHandlers.clear();
      }
   }

   protected abstract SocketClientHandler createSocketClientHandler(Socket s) throws IOException;

   public void stop() {
      if (this.socketClientHandlers != null) {
         for (SocketClientHandler handler : this.socketClientHandlers) {
            handler.close();
         }
         this.socketClientHandlers.clear();
      }
      if (this.ss != null) {
         try {
            this.ss.close();
         } catch (IOException e) {
            // silent
         }
      }
   }

   public static void main(String[] args) throws IOException {
      SocketServer server;
      try {
         server = instanceClass.getDeclaredConstructor().newInstance();
      } catch (InstantiationException e) {
         throw new IllegalStateException(e);
      } catch (IllegalAccessException e) {
         throw new IllegalStateException(e);
      } catch (InvocationTargetException e) {
         throw new IllegalStateException(e);
      } catch (NoSuchMethodException e) {
         throw new IllegalStateException(e);
      }
      server.start(args);
   }
}
