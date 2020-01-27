package com.github.diegolovison.os;

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
