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
 * @author Sam
 */
package com.clevercloud.bianca.program;

import com.clevercloud.bianca.env.*;

/**
 * A delegate that performs Array operations for Bianca objects.
 */
public class FunctionArrayDelegate implements ArrayDelegate {

   private JavaInvoker _arrayGet;
   private JavaInvoker _arrayPut;
   private JavaInvoker _arrayCount;

   public FunctionArrayDelegate() {
   }

   /**
    * Sets the custom function for the array get.
    */
   public void setArrayGet(JavaInvoker arrayGet) {
      _arrayGet = arrayGet;
   }

   /**
    * Sets the custom function for the array set.
    */
   public void setArrayPut(JavaInvoker arrayPut) {
      _arrayPut = arrayPut;
   }

   /**
    * Sets the custom function for the array set.
    */
   public void setArrayCount(JavaInvoker arrayCount) {
      _arrayCount = arrayCount;
   }

   /**
    * Returns the value for the specified key.
    */
   @Override
   public Value get(ObjectValue qThis, Value key) {
      if (_arrayGet != null) {
         return _arrayGet.callMethod(Env.getInstance(),
            _arrayGet.getBiancaClass(),
            qThis,
            new Value[]{key});
      } else {
         return UnsetValue.UNSET;
      }
   }

   /**
    * Sets the value for the spoecified key.
    */
   @Override
   public Value put(ObjectValue qThis, Value key, Value value) {
      if (_arrayPut != null) {
         return _arrayPut.callMethod(Env.getInstance(),
            _arrayPut.getBiancaClass(),
            qThis, key, value);
      } else {
         return UnsetValue.UNSET;
      }
   }

   /**
    * Appends a value.
    */
   @Override
   public Value put(ObjectValue qThis, Value value) {
      if (_arrayPut != null) {
         return _arrayPut.callMethod(Env.getInstance(),
            _arrayPut.getBiancaClass(),
            qThis, value);
      } else {
         return UnsetValue.UNSET;
      }
   }

   /**
    * Returns true if the value is set
    */
   @Override
   public boolean isset(ObjectValue qThis, Value key) {
      return get(qThis, key).isset();
   }

   /**
    * Removes the value at the speified key.
    */
   @Override
   public Value unset(ObjectValue qThis, Value key) {
      return UnsetValue.UNSET;
   }

   /**
    * Returns the value for the specified key.
    */
   @Override
   public long count(ObjectValue qThis) {
      if (_arrayCount != null) {
         return _arrayCount.callMethod(Env.getInstance(),
            _arrayGet.getBiancaClass(),
            qThis).toLong();
      } else {
         return 1;
      }
   }
}
