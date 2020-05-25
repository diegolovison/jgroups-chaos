package com.github.diegolovison.os;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Spawn {

   public static Process exec(Class clazz) {
      return exec(clazz, Collections.EMPTY_LIST, Collections.EMPTY_LIST);
   }

   public static Process exec(Class clazz, List<String> args) {
      return exec(clazz, args, Collections.EMPTY_LIST);
   }

   public static Process exec(Class clazz, List<String> args, Collection<String> jvmArgs) {
      try {
         args = trim(args);
         jvmArgs = trim(jvmArgs);
         String javaHome = System.getProperty("java.home");
         String javaBin = javaHome + File.separator + "bin" + File.separator + "java";
         String classpath = System.getProperty("java.class.path");
         String className = clazz.getName();
         List<String> command = new ArrayList<>();
         command.add(javaBin);
         command.addAll(jvmArgs);
         command.add("-cp");
         command.add(classpath);
         command.add(className);
         command.addAll(args);
         ProcessBuilder builder = new ProcessBuilder(command);
         Process process = builder.inheritIO().start();
         return process;
      } catch (Exception e) {
         throw new IllegalStateException(e);
      }
   }

   // if one is empty, things goes wrong and are hard to find the root cause
   private static List<String> trim(Collection<String> args) {
      // Arrays.asList don't allow removing an item
      List<String> list = new ArrayList<>();
      if (args != null) {
         for (String str : args) {
            if (str != null && !str.trim().isEmpty()) {
               list.add(str);
            }
         }
      }
      return list;
   }
}
