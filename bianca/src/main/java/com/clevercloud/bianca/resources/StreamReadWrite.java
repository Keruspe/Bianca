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
package com.clevercloud.bianca.resources;

import com.clevercloud.bianca.BiancaException;
import com.clevercloud.bianca.env.Env;
import com.clevercloud.bianca.env.EnvCleanup;
import com.clevercloud.bianca.env.StringValue;
import com.clevercloud.vfs.ReadStream;
import com.clevercloud.vfs.WriteStream;

import java.io.IOException;

/**
 * Represents read/write stream
 */
public class StreamReadWrite extends StreamResource
   implements EnvCleanup {

   private Env _env;
   private ReadStream _is;
   private WriteStream _os;

   public StreamReadWrite(Env env) {
      _env = env;

      _env.addCleanup(this);
   }

   public StreamReadWrite(Env env, ReadStream is, WriteStream os) {
      this(env);

      init(is, os);
   }

   protected final void init(ReadStream is, WriteStream os) {
      _is = is;
      _os = os;
   }

   /**
    * Reads the next byte, returning -1 on eof.
    */
   @Override
   public int read()
      throws IOException {
      if (_is != null) {
         return _is.read();
      } else {
         return -1;
      }
   }

   /**
    * Reads a buffer, returning -1 on eof.
    */
   @Override
   public int read(byte[] buffer, int offset, int length)
      throws IOException {
      if (_is != null) {
         return _is.read(buffer, offset, length);
      } else {
         return -1;
      }
   }

   /**
    * Reads the optional linefeed character from a \r\n
    */
   @Override
   public boolean readOptionalLinefeed()
      throws IOException {
      if (_is != null) {
         int ch = _is.read();

         if (ch == '\n') {
            return true;
         } else {
            _is.unread();
            return false;
         }
      } else {
         return false;
      }
   }

   /**
    * Reads a line from the buffer.
    */
   @Override
   public StringValue readLine(Env env)
      throws IOException {
      if (_is != null) {
         return env.createString(_is.readLineNoChop());
      } else {
         return StringValue.EMPTY;
      }
   }

   /**
    * Reads a line from the stream into a buffer.
    */
   public int readLine(char[] buffer) {
      try {
         if (_is != null) {
            return _is.readLine(buffer, buffer.length, false);
         } else {
            return -1;
         }
      } catch (IOException e) {
         return -1;
      }
   }

   /**
    * Writes to a buffer.
    */
   @Override
   public int write(byte[] buffer, int offset, int length)
      throws IOException {
      if (_os != null) {
         _os.write(buffer, offset, length);

         return length;
      } else {
         return -1;
      }
   }

   /**
    * prints
    */
   @Override
   public void print(char ch)
      throws IOException {
      print(String.valueOf(ch));
   }

   /**
    * prints
    */
   @Override
   public void print(String s)
      throws IOException {
      if (_os != null) {
         _os.print(s);
      }
   }

   /**
    * Returns true on the end of file.
    */
   @Override
   public boolean isEOF() {
      return true;
   }

   /**
    * Flushes the output
    */
   @Override
   public void flush() {
      try {
         if (_os != null) {
            _os.flush();
         }
      } catch (IOException e) {
         throw new BiancaException(e);
      }
   }

   /**
    * Returns the current location in the file.
    */
   @Override
   public long getPosition() {
      return 0;
   }

   /**
    * Closes the stream for reading.
    */
   @Override
   public void closeRead() {
      ReadStream is = _is;
      _is = null;

      if (is != null) {
         is.close();
      }
   }

   /**
    * Closes the stream for writing
    */
   @Override
   public void closeWrite() {
      WriteStream os = _os;
      _os = null;

      try {
         if (os != null) {
            os.close();
         }
      } catch (IOException e) {
      }
   }

   /**
    * Closes the stream.
    */
   @Override
   public void close() {
      _env.removeCleanup(this);

      cleanup();
   }

   /**
    * Implements the EnvCleanup interface.
    */
   @Override
   public void cleanup() {
      ReadStream is = _is;
      _is = null;

      WriteStream os = _os;
      _os = null;

      if (is != null) {
         is.close();
      }

      try {
         if (os != null) {
            os.close();
         }
      } catch (IOException e) {
      }
   }
}
