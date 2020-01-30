package com.github.diegolovison.os;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class SocketClient extends Thread {

   private Socket clientSocket;
   private PrintWriter out;
   private BufferedReader in;

   public void startConnection(InetAddress ip, int port) throws IOException {
      clientSocket = new Socket(ip, port);
      out = new PrintWriter(clientSocket.getOutputStream(), true);
      in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
   }

   public void startConnection(int port) throws IOException {
      InetAddress ip = InetAddress.getByName("localhost");
      this.startConnection(ip, port);
   }

   public String sendMessage(String msg) {
      out.println(msg);
      try {
         String resp = null;
         if (!clientSocket.isClosed()) {
            resp = in.readLine();
         }
         // TODO :)
         if ("null".equals(resp)) {
            return null;
         } else {
            return resp;
         }
      } catch (IOException e) {
         throw new IllegalStateException(e);
      }
   }

   public void stopConnection() {
      try {
         if (in != null) {
            in.close();
         }
         if (out != null) {
            out.close();
         }
         if (clientSocket != null) {
            clientSocket.close();
         }
      } catch (IOException e) {
         // shh
      }
   }

   public void waitForTheServer(Integer availablePort) {
      // is 10 enough ?
      Exception exception = null;
      for (int i = 0; i < 60; i++) {
         try {
            this.startConnection(availablePort);
            exception = null;
            break;
         } catch (IOException e) {
            try {
               Thread.sleep(1_000);
               exception = e;
            } catch (InterruptedException ex) {
               exception = e;
            }
         }
      }
      if (exception != null) {
         throw new IllegalStateException("The server is not running. The client was not able to connect.", exception);
      }
   }
}
