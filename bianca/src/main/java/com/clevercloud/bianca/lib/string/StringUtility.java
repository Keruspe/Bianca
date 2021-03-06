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
 * @author Nam Nguyen
 */
package com.clevercloud.bianca.lib.string;

import com.clevercloud.bianca.BiancaModuleException;
import com.clevercloud.bianca.env.*;
import com.clevercloud.bianca.lib.ArrayModule;
import com.clevercloud.util.L10N;

import java.io.IOException;

public class StringUtility {

   private static final L10N L = new L10N(StringModule.class);

   public static Value parseStr(Env env,
                                CharSequence str,
                                ArrayValue result,
                                boolean isRef,
                                String encoding) {
      return parseStr(env, str, result, isRef, encoding,
         env.getIniBoolean("magic_quotes_gpc"),
         Env.DEFAULT_QUERY_SEPARATOR_MAP);
   }

   public static Value parseStr(Env env,
                                CharSequence str,
                                ArrayValue result,
                                boolean isRef,
                                String encoding,
                                boolean isMagicQuotes,
                                int[] querySeparatorMap) {
      try {
         int len = str.length();

         for (int i = 0; i < len; i++) {
            int ch = 0;
            StringBuilder builder = new StringBuilder();

            for (; i < len && querySeparatorMap[ch = str.charAt(i)] > 0; i++) {
            }

            for (; i < len && (ch = str.charAt(i)) != '='
               && querySeparatorMap[ch] == 0; i++) {
               i = addQueryChar(builder, str, len, i, ch);
            }

            String key = builder.toString();

            builder = new StringBuilder();

            String value;
            if (ch == '=') {
               for (i++; i < len
                  && querySeparatorMap[ch = str.charAt(i)] == 0; i++) {
                  i = addQueryChar(builder, str, len, i, ch);
               }

               value = builder.toString();
            } else {
               value = "";
            }

            if (isRef) {
               Post.addFormValue(env, result, key,
                  new String[]{value}, isMagicQuotes);
            } else {
               // If key is an exsiting array, then append
               // this value to existing array
               // Only use extract(EXTR_OVERWRITE) on non-array variables or
               // non-existing arrays
               int openBracketIndex = key.indexOf('[');
               int closeBracketIndex = key.indexOf(']');
               if (openBracketIndex > 0) {
                  String arrayName = key.substring(0, openBracketIndex);
                  arrayName = arrayName.replaceAll("\\.", "_");

                  Value v = env.getVar(arrayName).getRawValue();
                  if (v instanceof ArrayValue) {
                     //Check to make sure valid string (ie: foo[...])
                     if (closeBracketIndex < 0) {
                        env.warning(L.l("invalid array {0}", key));
                        return NullValue.NULL;
                     }

                     if (closeBracketIndex > openBracketIndex + 1) {
                        String index = key.substring(key.indexOf('[') + 1, key.indexOf(']'));
                        v.put(env.createString(index), env.createString(value));
                     } else {
                        v.put(env.createString(value));
                     }
                  } else {
                     Post.addFormValue(env, result, key,
                        new String[]{value}, isMagicQuotes);
                  }
               } else {
                  Post.addFormValue(env, result, key,
                     new String[]{value}, isMagicQuotes);
               }
            }
         }

         if (!isRef) {
            ArrayModule.extract(env, result,
               ArrayModule.EXTR_OVERWRITE,
               null);
         }

         return NullValue.NULL;
      } catch (IOException e) {
         throw new BiancaModuleException(e);
      }
   }

   protected static int addQueryChar(StringBuilder builder,
                                     CharSequence str,
                                     int len,
                                     int i,
                                     int ch)
      throws IOException {
      if (str == null) {
         str = "";
      }

      switch (ch) {
         case '+':
            builder.append(' ');
            return i;

         case '%':
            if (i + 2 < len) {
               int d1 = StringModule.hexToDigit(str.charAt(i + 1));
               int d2 = StringModule.hexToDigit(str.charAt(i + 2));

               // TODO: d1 and d2 may be -1 if not valid hex chars
               builder.append((char) (d1 * 16 + d2));

               return i + 2;
            } else {
               builder.append((char) ch);
               return i;
            }

         default:
            builder.append((char) ch);
            return i;
      }
   }

   public static void addQueryValue(Env env, ArrayValue array,
                                    String key, String valueStr) {
      if (key == null) {
         key = "";
      }

      if (valueStr == null) {
         valueStr = "";
      }

      int p;

      Value value = env.createString(valueStr);

      if ((p = key.indexOf('[')) > 0 && key.endsWith("]")) {
         String index = key.substring(p + 1, key.length() - 1);
         key = key.substring(0, p);

         Value keyValue = env.createString(key);

         Value part;

         if (array != null) {
            part = array.get(keyValue);
         } else {
            part = env.getVar(key);
         }

         if (!part.isArray()) {
            part = new ArrayValueImpl();
         }

         if (index.equals("")) {
            part.put(value);
         } else {
            part.put(env.createString(index), value);
         }

         if (array != null) {
            array.put(keyValue, part);
         } else {
            env.setVar(key, part);
         }
      } else {
         if (array != null) {
            array.put(env.createString(key), value);
         } else {
            env.setVar(key, value);
         }
      }
   }
}
