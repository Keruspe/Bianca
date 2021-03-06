/*
 * Copyright (c) 1998-2010 Caucho Technology -- all rights reserved
 * Copyright (c) 2011-2012 Clever Cloud SAS -- all rights reserved
 *
 * This file is part of Bianca(R) Open Source
 *
 * Each copy or derived work must preserve the copyright notice and this
 * notice unmodified.
 *
 * Bianca Open Source is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * Bianca Open Source is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, or any warranty
 * of NON-INFRINGEMENT.  See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Bianca Open Source; if not, write to the
 *
 *   Free Software Foundation, Inc.
 *   59 Temple Place, Suite 330
 *   Boston, MA 02111-1307  USA
 *
 * @author Scott Ferguson
 */
package com.clevercloud.bianca.env;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Represents the server
 */
public class GlobalArrayValue extends ArrayValueImpl {

   private final Env _env;

   public GlobalArrayValue(Env env) {
      _env = env;
   }

   /**
    * Converts to an object.
    */
   @Override
   public Object toObject() {
      return null;
   }

   @Override
   public boolean toBoolean() {
      return true;
   }

   /**
    * Adds a new value.
    */
   @Override
   public ArrayValue append(Value key, Value value) {
      _env.setGlobalValue(key.toStringValue(), value);

      return this;
   }

   /**
    * Gets a new value.
    */
   @Override
   public Value get(Value key) {
      return _env.getGlobalValue(key.toStringValue());
   }

   /**
    * Returns the array ref.
    */
   @Override
   public Var getVar(Value key) {
      // return _env.getGlobalRef(key.toStringValue());

      return _env.getGlobalVar(key.toStringValue());
   }

   /**
    * Returns the value as an argument which may be a reference.
    */
   @Override
   public Value getArg(Value index, boolean isTop) {
      return getVar(index);
   }

   /**
    * Returns the value as an array.
    */
   @Override
   public Value getArray(Value index) {
      Value array = getVar(index).toAutoArray();

      return array;
   }

   /**
    * Unsets a value.
    */
   @Override
   public Value remove(Value key) {
      return _env.unsetGlobalVar(key.toStringValue());
   }

   @Override
   public void clear() {
   }

   /**
    * Copy for assignment.
    */
   @Override
   public Value copy() {
      return this;
   }

   /*
    * Returns the size.
    */
   @Override
   public int getSize() {
      return _env.getGlobalEnv().size();
   }

   /**
    * Gets a new value.
    */
   @Override
   public Value containsKey(Value key) {
      EnvVar var = _env.getGlobalEnv().get(key.toStringValue());

      if (var != null) {
         return var.get();
      } else {
         return null;
      }
   }

   /**
    * Returns true if the index isset().
    */
   @Override
   public boolean isset(Value key) {
      return get(key).isset();
   }

   /**
    * Returns true if the key exists in the array.
    */
   @Override
   public boolean keyExists(Value key) {
      EnvVar var = _env.getGlobalEnv().get(key.toStringValue());

      return var != null;
   }

   /**
    * Prints the value.
    *
    * @param env
    */
   @Override
   public void print(Env env) {
      env.print("Array");
   }

   /**
    * Returns the array keys.
    */
   @Override
   public Value getKeys() {
      return createAndFillArray().getKeys();
   }

   /**
    * Returns an iterator of the entries.
    */
   @Override
   public Set<Map.Entry<Value, Value>> entrySet() {
      return createAndFillArray().entrySet();
   }

   @Override
   public Iterator<Map.Entry<Value, Value>> getIterator(Env env) {
      return createAndFillArray().getIterator(env);
   }

   @Override
   public Iterator<Value> getKeyIterator(Env env) {
      return createAndFillArray().getKeyIterator(env);
   }

   @Override
   public Iterator<Value> getValueIterator(Env env) {
      return createAndFillArray().getValueIterator(env);
   }

   private ArrayValue createAndFillArray() {
      ArrayValue array = new ArrayValueImpl();

      for (Map.Entry<StringValue, EnvVar> entry : _env.getGlobalEnv().entrySet()) {
         Value key = entry.getKey();
         Value val = entry.getValue().get();

         array.put(key, val);
      }

      return array;
   }
}
