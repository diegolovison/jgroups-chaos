package com.github.diegolovison.os;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;

public abstract class SocketServer {

   protected static Class<? extends SocketServer> instanceClass;

   private ServerSocket serverSocket;
   private Socket clientSocket;
   protected PrintWriter out;
   private BufferedReader in;
   private boolean running = false;

   protected boolean onMessage(String message) {
      boolean _super = false;
      String method = getMethod(message);
      String[] args = getArguments(message);
      if ("stop".equals(method)) {
         stop();
         _super = true;
      }
      return _super;
   }

   protected abstract void onStart(String[] args);
   protected abstract void onStop();

   protected String getMethod(String message) {
      int index = message.indexOf("[");
      String method = message;
      if (index >= 0) {
         method = message.substring(0, index);
      }
      return method;
   }

   protected String[] getArguments(String message) {
      String[] args = null;
      int begin = message.indexOf("[");
      if (begin >= 0) {
         int end = message.indexOf("]");
         if (end >= 0) {
            args = message.substring(begin + 1, end).split(",");
         } else {
            throw new IllegalStateException("Message not well formatted");
         }
      }
      return args;
   }

   public void start(String[] args) throws IOException {
      int port = Integer.valueOf(args[0]);
      serverSocket = new ServerSocket(port);
      clientSocket = serverSocket.accept();
      out = new PrintWriter(clientSocket.getOutputStream(), true);
      in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
      running = true;
      onStart(args);
      String inputLine;
      while (running && (inputLine = in.readLine()) != null) {
         onMessage(inputLine);
      }
   }

   public void stop() {
      try {
         running = false;
         in.close();
         out.close();
         clientSocket.close();
         serverSocket.close();
         onStop();
      } catch (IOException e) {
         throw new IllegalStateException(e);
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

   public static int getAvailablePort(int port) {
      // is 100 enough ?
      boolean available = false;
      for (int i = 0; i < 100; i++) {
         port = port + i;
         if (isPortAvailable(port)) {
            available = true;
            break;
         }
      }
      if (!available) {
         throw new IllegalStateException("Maybe too many tests running?");
      }
      return port;
   }

   private static boolean isPortAvailable(int port) {
      try (ServerSocket ss = new ServerSocket(port); DatagramSocket ds = new DatagramSocket(port)) {
         return true;
      } catch (IOException e) {
         return false;
      }
   }
}
