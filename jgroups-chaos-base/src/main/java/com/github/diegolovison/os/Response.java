package com.github.diegolovison.os;

public class Response {

   public static final Response EMPTY_RESPONSE = new Response();

   private boolean ok;
   public String message;

   private Response() {
   }

   public Response(String message) {
      this.ok = true;
      this.message = message;
   }

   public String getMessage() {
      return this.message;
   }

   public boolean isOk() {
      return this.ok;
   }
}
