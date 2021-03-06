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
 * @author Marc-Antoine Perennou <Marc-Antoine@Perennou.com>
 */
package com.clevercloud.bianca.env;

import com.clevercloud.bianca.BiancaException;
import com.clevercloud.bianca.annotation.Name;
import com.clevercloud.bianca.module.ModuleContext;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Represents a function created from a java method.
 */
public class JavaMethod extends JavaInvoker {

   private final Method _method;

   /**
    * Creates a function from an introspected java method.
    *
    * @param method the introspected method.
    */
   public JavaMethod(ModuleContext moduleContext, Method method) {
      super(moduleContext,
         getName(method),
         method.getParameterTypes(),
         method.getParameterAnnotations(),
         method.getAnnotations(),
         method.getReturnType());

      _method = method;
      _isStatic = Modifier.isStatic(method.getModifiers());

      // php/069a
      // Java 6 fixes the need to do this for methods of inner classes
      _method.setAccessible(true);
   }

   private static String getName(Method method) {
      String name;

      Name nameAnn = method.getAnnotation(Name.class);

      if (nameAnn != null) {
         name = nameAnn.value();
      } else {
         name = method.getName();
      }

      return name;
   }

   @Override
   public String getDeclaringClassName() {
      return _method.getDeclaringClass().getSimpleName();
   }

   /**
    * Returns the function's method.
    *
    * @return the reflection method.
    */
   public Method getMethod() {
      return _method;
   }

   @Override
   public Class[] getJavaParameterTypes() {
      return _method.getParameterTypes();
   }

   @Override
   public Class getJavaDeclaringClass() {
      return _method.getDeclaringClass();
   }

   @Override
   public Object invoke(Object obj, Object[] args) {
      try {
         return _method.invoke(obj, args);
      } catch (InvocationTargetException e) {
         Throwable e1 = e.getCause();

         // php/0g0h
         if (e1 instanceof BiancaException) {
            throw (BiancaException) e1;
         }

         if (e1 instanceof BiancaException) {
            throw (BiancaException) e1;
         }

         String methodName = (_method.getDeclaringClass().getName() + "."
            + _method.getName());

         throw new BiancaException(methodName + ": " + e1.getMessage(), e1);
      } catch (Exception e) {
         String methodName = (_method.getDeclaringClass().getName() + "."
            + _method.getName());

         throw new BiancaException(methodName + ": " + e.getMessage(), e);
      }
   }

   @Override
   public String toString() {
      return "JavaMethod[" + _method + "]";
   }
}
