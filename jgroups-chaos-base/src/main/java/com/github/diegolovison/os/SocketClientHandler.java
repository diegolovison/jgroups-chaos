package com.github.diegolovison.os;

import static com.github.diegolovison.os.ClosableUtil.closeSilent;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class SocketClientHandler extends Thread implements Closeable {

   private static final Response STOP_RESPONSE = new Response("___stop___");
   private static final Response ERROR_RESPONSE = new Response("___error___");


   private final BufferedReader in;
   private final PrintWriter out;
   private final Socket s;
   private Runnable stopListener;

   public SocketClientHandler(Socket s) throws IOException {
      this.s = s;
      this.in = new BufferedReader(new InputStreamReader(s.getInputStream()));
      this.out = new PrintWriter(s.getOutputStream(), true);
   }

   public void addStopListener(Runnable stopListener) {
      this.stopListener = stopListener;
   }

   @Override
   public void run() {
      try {
         while (true) {
            boolean stop = false;
            String request;
            try {
               request = in.readLine();
            } catch (IOException e) {
               // silent in.readLine() can be closed
               break;
            }
            if (request != null) {
               String method = getMethod(request);
               String[] args = getArguments(request);

               Response response;
               try {
                  response = doRequest(method, args);
               } catch (Exception e) {
                  e.printStackTrace();
                  response = ERROR_RESPONSE;
               }

               out.println(response.getMessage());

               if (STOP_RESPONSE.equals(response)) {
                  stop = true;
               }
            } else {
               stop = true;
            }

            if (stop) {
               break;
            }
         }
      } finally {
         this.close();
      }
   }

   protected Response doRequest(String method, String[] args) {
      if ("stop".equals(method)) {
         close();
         return STOP_RESPONSE;
      } else if ("ping".equals(method)) {
         return new Response(ping());
      } else if ("killServer".equals(method)) {
         close();
         stopListener.run();
         return STOP_RESPONSE;
      } else {
         return Response.EMPTY_RESPONSE;
      }
   }

   @Override
   public void close() {
      closeSilent(s, in, out);
   }

   protected String ping() {
      return "pong";
   }

   protected String getMethod(String message) {
      int index = message.indexOf("[");
      String method = message;
      if (index >= 0) {
         method = message.substring(0, index);
      }
      return method.trim();
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
      if (args != null) {
         for (int i = 0; i < args.length; i++) {
            args[i] = args[i].trim();
         }
      }
      return args;
   }
}
