package com.github.diegolovison.os;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class SocketClient {

   private Socket clientSocket;
   private PrintWriter out;
   private BufferedReader in;

   public void startConnection(String ip, int port) throws IOException {
      clientSocket = new Socket(ip, port);
      out = new PrintWriter(clientSocket.getOutputStream(), true);
      in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
   }

   public void startConnection(int port) throws IOException {
      this.startConnection("127.0.0.1", port);
   }

   public String sendMessage(String msg) {
      out.println(msg);
      String resp;
      try {
         resp = in.readLine();
      } catch (IOException e) {
         throw new IllegalStateException(e);
      }
      return resp;
   }

   public void stopConnection() {
      try {
         in.close();
         out.close();
         clientSocket.close();
      } catch (IOException e) {
         throw new IllegalStateException(e);
      }
   }

   public static void main(String[] args) throws IOException {
      SocketClient client = new SocketClient();
      client.startConnection("127.0.0.1", 6666);
      String response = client.sendMessage("hello server");
      System.out.println(response);
   }
}
