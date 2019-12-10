package com.github.diegolovison.jgroups;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

public class Sleep {

   /**
    * Sleep without blocking the current thread execution
    */
   public static void sleep(long ms) {
      LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(ms));
   }
}
