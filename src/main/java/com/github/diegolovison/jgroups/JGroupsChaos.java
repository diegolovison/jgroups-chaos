package com.github.diegolovison.jgroups;

public class JGroupsChaos {

   public static void main(String[] args) {

      String configFile = null;
      String clusterName = null;
      
      if (args.length == 0) {
         help();
      } else {
         for(int i=0; i < args.length; i++) {
            if(args[i].equals("-config")) {
               configFile=args[++i];
               continue;
            }
            if(args[i].equals("-name")) {
               clusterName=args[++i];
               continue;
            }
         }  
      }
   }

   private static void help() {
      System.out.println("JGroupsChaos [-config XML] [-name name]");
   }
}
