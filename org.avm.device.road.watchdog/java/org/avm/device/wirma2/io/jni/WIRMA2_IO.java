/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 1.3.40
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.avm.device.wirma2.io.jni;

public class WIRMA2_IO {
  public static int dio_open(String name) {
    return WIRMA2_IOJNI.dio_open(name);
  }

  public static void dio_close(int handle) {
    WIRMA2_IOJNI.dio_close(handle);
  }

  public static int dio_read_input(int handle) {
    return WIRMA2_IOJNI.dio_read_input(handle);
  }

  public static int dio_write_ouput(int handle, int value) {
    return WIRMA2_IOJNI.dio_write_ouput(handle, value);
  }

}
