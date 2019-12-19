package com.github.diegolovison.jgroups;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.jgroups.Address;

public class FakeAddress implements Address {

   public FakeAddress(String data) {

   }

   @Override
   public int compareTo(Address o) {
      return 0;
   }

   @Override
   public int serializedSize() {
      return 0;
   }

   @Override
   public void writeTo(DataOutput out) throws IOException {

   }

   @Override
   public void readFrom(DataInput in) throws IOException, ClassNotFoundException {

   }
}
