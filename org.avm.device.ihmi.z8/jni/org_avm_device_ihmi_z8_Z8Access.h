/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class org_avm_device_ihmi_z8_Z8Access */

#ifndef _Included_org_avm_device_ihmi_z8_Z8Access
#define _Included_org_avm_device_ihmi_z8_Z8Access
#ifdef __cplusplus
extern "C" {
#endif
#undef org_avm_device_ihmi_z8_Z8Access_SYSTEM_POWERED_DOWN
#define org_avm_device_ihmi_z8_Z8Access_SYSTEM_POWERED_DOWN 1L
#undef org_avm_device_ihmi_z8_Z8Access_SYSTEM_POWERED_DOWN_WITH_WAKEUP_TIMER
#define org_avm_device_ihmi_z8_Z8Access_SYSTEM_POWERED_DOWN_WITH_WAKEUP_TIMER 2L
#undef org_avm_device_ihmi_z8_Z8Access_SYSTEM_POWERED_UP_WITH_IGNITION_SIGNAL
#define org_avm_device_ihmi_z8_Z8Access_SYSTEM_POWERED_UP_WITH_IGNITION_SIGNAL 3L
#undef org_avm_device_ihmi_z8_Z8Access_SYSTEM_STANDBY
#define org_avm_device_ihmi_z8_Z8Access_SYSTEM_STANDBY 4L
#undef org_avm_device_ihmi_z8_Z8Access_SYSTEM_POWERED_UP_AFTER_WAKEUP
#define org_avm_device_ihmi_z8_Z8Access_SYSTEM_POWERED_UP_AFTER_WAKEUP 5L
#undef org_avm_device_ihmi_z8_Z8Access_SYSTEM_WAITING_FOR_SHUTDOWN
#define org_avm_device_ihmi_z8_Z8Access_SYSTEM_WAITING_FOR_SHUTDOWN 6L
#undef org_avm_device_ihmi_z8_Z8Access_SYSTEM_WAITING_FOR_SHUTDOWN_TOO_HOT
#define org_avm_device_ihmi_z8_Z8Access_SYSTEM_WAITING_FOR_SHUTDOWN_TOO_HOT 7L
#undef org_avm_device_ihmi_z8_Z8Access_SYSTEM_WAITING_FOR_SYSTEM_TO_COOL_DOWN_POWER_DOWN
#define org_avm_device_ihmi_z8_Z8Access_SYSTEM_WAITING_FOR_SYSTEM_TO_COOL_DOWN_POWER_DOWN 8L
#undef org_avm_device_ihmi_z8_Z8Access_SYSTEM_WAITING_FOR_SYSTEM_TO_COOL_STANDBY
#define org_avm_device_ihmi_z8_Z8Access_SYSTEM_WAITING_FOR_SYSTEM_TO_COOL_STANDBY 9L
/*
 * Class:     org_avm_device_ihmi_z8_Z8Access
 * Method:    getSystemCurrentState
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_org_avm_device_ihmi_z8_Z8Access_getSystemCurrentState
  (JNIEnv *, jclass);

/*
 * Class:     org_avm_device_ihmi_z8_Z8Access
 * Method:    getBoardTemperature
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_org_avm_device_ihmi_z8_Z8Access_getBoardTemperature
  (JNIEnv *, jclass);

/*
 * Class:     org_avm_device_ihmi_z8_Z8Access
 * Method:    getVIN
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_org_avm_device_ihmi_z8_Z8Access_getVIN
  (JNIEnv *, jclass);

/*
 * Class:     org_avm_device_ihmi_z8_Z8Access
 * Method:    getBacklightLevel
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_org_avm_device_ihmi_z8_Z8Access_getBacklightLevel
  (JNIEnv *, jclass);

/*
 * Class:     org_avm_device_ihmi_z8_Z8Access
 * Method:    setBacklightLevel
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_org_avm_device_ihmi_z8_Z8Access_setBacklightLevel
  (JNIEnv *, jclass, jint);

/*
 * Class:     org_avm_device_ihmi_z8_Z8Access
 * Method:    getLightLevel
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_org_avm_device_ihmi_z8_Z8Access_getLightLevel
  (JNIEnv *, jclass);

/*
 * Class:     org_avm_device_ihmi_z8_Z8Access
 * Method:    setWatchdog
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_org_avm_device_ihmi_z8_Z8Access_setWatchdog
  (JNIEnv *, jclass, jint);

/*
 * Class:     org_avm_device_ihmi_z8_Z8Access
 * Method:    getWatchdog
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_org_avm_device_ihmi_z8_Z8Access_getWatchdog
  (JNIEnv *, jclass);

/*
 * Class:     org_avm_device_ihmi_z8_Z8Access
 * Method:    opendevice
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_org_avm_device_ihmi_z8_Z8Access_opendevice
  (JNIEnv *, jclass);

/*
 * Class:     org_avm_device_ihmi_z8_Z8Access
 * Method:    closedevice
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_org_avm_device_ihmi_z8_Z8Access_closedevice
  (JNIEnv *, jclass, jint);

/*
 * Class:     org_avm_device_ihmi_z8_Z8Access
 * Method:    readSystemCurrentState
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_org_avm_device_ihmi_z8_Z8Access_readSystemCurrentState
  (JNIEnv *, jclass, jint);

#ifdef __cplusplus
}
#endif
#endif
