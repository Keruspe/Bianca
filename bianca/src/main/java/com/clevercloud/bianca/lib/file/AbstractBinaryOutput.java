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
package com.clevercloud.bianca.lib.file;

import com.clevercloud.bianca.BiancaModuleException;
import com.clevercloud.bianca.env.BooleanValue;
import com.clevercloud.bianca.env.Value;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Represents a PHP open file
 */
abstract public class AbstractBinaryOutput
   extends OutputStream
   implements BinaryOutput {

   private int lockedShared = 0;
   private boolean lockedExclusive = false;

   /**
    * Returns self as the output stream.
    */
   @Override
   public OutputStream getOutputStream() {
      return this;
   }

   /**
    * Writes to a stream.
    */
   @Override
   public int write(InputStream is, int length) {
      int writeLength = 0;

      byte[] buffer = new byte[8192];

      try {
         while (length > 0) {
            int sublen;

            if (length < buffer.length) {
               sublen = length;
            } else {
               sublen = buffer.length;
            }

            sublen = is.read(buffer, 0, sublen);

            if (sublen < 0) {
               break;
            }

            write(buffer, 0, sublen);

            writeLength += sublen;
            length -= sublen;
         }

         return writeLength;
      } catch (IOException e) {
         throw new BiancaModuleException(e);
      }
   }

   /**
    * Prints a string to a file.
    */
   @Override
   public void print(char v)
      throws IOException {
      write((byte) v);
   }

   /**
    * Prints a string to a file.
    */
   @Override
   public void print(String v)
      throws IOException {
      for (int i = 0; i < v.length(); i++) {
         write(v.charAt(i));
      }
   }

   /**
    * Flushes the output.
    */
   @Override
   public void flush()
      throws IOException {
   }

   /**
    * Closes the file.
    */
   @Override
   public void closeWrite() {
      close();
   }

   /**
    * Closes the stream.
    */
   @Override
   public void close() {
   }

   /**
    * Returns false always for output streams
    */
   @Override
   public boolean isEOF() {
      return false;
   }

   /**
    * Tells the position in the stream
    */
   @Override
   public long getPosition() {
      return 0;
   }

   /**
    * Sets the position.
    */
   @Override
   public boolean setPosition(long offset) {
      return false;
   }

   @Override
   public long seek(long offset, int whence) {
      long position;

      switch (whence) {
         case BinaryStream.SEEK_CUR:
            position = getPosition() + offset;
            break;
         case BinaryStream.SEEK_END:
            // don't necessarily have an end
            position = getPosition();
            break;
         case BinaryStream.SEEK_SET:
         default:
            position = offset;
            break;
      }

      if (!setPosition(position)) {
         return -1L;
      } else {
         return position;
      }
   }

   public String getResourceType() {
      return "stream";
   }

   @Override
   public Value stat() {
      return BooleanValue.FALSE;
   }
}
