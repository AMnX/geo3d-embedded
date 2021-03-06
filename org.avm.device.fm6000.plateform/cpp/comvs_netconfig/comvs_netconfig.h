/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class org_avm_device_fm6000_network_jni_COMVS_0005fNETCONFIGJNI */

#ifndef _Included_org_avm_device_fm6000_network_jni_COMVS_0005fNETCONFIGJNI
#define _Included_org_avm_device_fm6000_network_jni_COMVS_0005fNETCONFIGJNI
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     org_avm_device_fm6000_network_jni_COMVS_0005fNETCONFIGJNI
 * Method:    Comvs_AddRouteToInterface
 * Signature: (Ljava/lang/String;Lorg/avm/device/fm6000/network/jni/COMVS_NETCONFIG_ROUTE;)I
 */
JNIEXPORT jint JNICALL Java_org_avm_device_fm6000_network_jni_COMVS_1NETCONFIGJNI_Comvs_1AddRouteToInterface
  (JNIEnv *, jclass, jstring, jobject);

/*
 * Class:     org_avm_device_fm6000_network_jni_COMVS_0005fNETCONFIGJNI
 * Method:    Comvs_DeleteRouteOfInterface
 * Signature: (Ljava/lang/String;Lorg/avm/device/fm6000/network/jni/COMVS_NETCONFIG_ROUTE;)I
 */
JNIEXPORT jint JNICALL Java_org_avm_device_fm6000_network_jni_COMVS_1NETCONFIGJNI_Comvs_1DeleteRouteOfInterface
  (JNIEnv *, jclass, jstring, jobject);

/*
 * Class:     org_avm_device_fm6000_network_jni_COMVS_0005fNETCONFIGJNI
 * Method:    Comvs_ModifyRouteOfInterface
 * Signature: (Ljava/lang/String;Lorg/avm/device/fm6000/network/jni/COMVS_NETCONFIG_ROUTE;Lorg/avm/device/fm6000/network/jni/COMVS_NETCONFIG_ROUTE;)I
 */
JNIEXPORT jint JNICALL Java_org_avm_device_fm6000_network_jni_COMVS_1NETCONFIGJNI_Comvs_1ModifyRouteOfInterface
  (JNIEnv *, jclass, jstring, jobject, jobject);

/*
 * Class:     org_avm_device_fm6000_network_jni_COMVS_0005fNETCONFIGJNI
 * Method:    Comvs_RenewDHCPLease
 * Signature: (Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_org_avm_device_fm6000_network_jni_COMVS_1NETCONFIGJNI_Comvs_1RenewDHCPLease
  (JNIEnv *, jclass, jstring);

/*
 * Class:     org_avm_device_fm6000_network_jni_COMVS_0005fNETCONFIGJNI
 * Method:    Comvs_ReleaseDHCPLease
 * Signature: (Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_org_avm_device_fm6000_network_jni_COMVS_1NETCONFIGJNI_Comvs_1ReleaseDHCPLease
  (JNIEnv *, jclass, jstring);

/*
 * Class:     org_avm_device_fm6000_network_jni_COMVS_0005fNETCONFIGJNI
 * Method:    Comvs_SetGlobalProperty
 * Signature: (ILjava/lang/Object;)I
 */
JNIEXPORT jint JNICALL Java_org_avm_device_fm6000_network_jni_COMVS_1NETCONFIGJNI_Comvs_1SetGlobalProperty
  (JNIEnv *, jclass, jint, jobject);

/*
 * Class:     org_avm_device_fm6000_network_jni_COMVS_0005fNETCONFIGJNI
 * Method:    Comvs_GetGlobalProperty
 * Signature: (ILjava/lang/Object;)I
 */
JNIEXPORT jint JNICALL Java_org_avm_device_fm6000_network_jni_COMVS_1NETCONFIGJNI_Comvs_1GetGlobalProperty
  (JNIEnv *, jclass, jint, jobject);

/*
 * Class:     org_avm_device_fm6000_network_jni_COMVS_0005fNETCONFIGJNI
 * Method:    Comvs_SetDHCPConfiguration
 * Signature: (Ljava/lang/String;Lorg/avm/device/fm6000/network/jni/COMVS_NETCONFIG_DCHP_CONFIGURATION;)I
 */
JNIEXPORT jint JNICALL Java_org_avm_device_fm6000_network_jni_COMVS_1NETCONFIGJNI_Comvs_1SetDHCPConfiguration
  (JNIEnv *, jclass, jstring, jobject);

/*
 * Class:     org_avm_device_fm6000_network_jni_COMVS_0005fNETCONFIGJNI
 * Method:    Comvs_SetDNSConfiguration
 * Signature: (Ljava/lang/String;Lorg/avm/device/fm6000/network/jni/COMVS_NETCONFIG_DNS_CONFIGURATION;)I
 */
JNIEXPORT jint JNICALL Java_org_avm_device_fm6000_network_jni_COMVS_1NETCONFIGJNI_Comvs_1SetDNSConfiguration
  (JNIEnv *, jclass, jstring, jobject);

/*
 * Class:     org_avm_device_fm6000_network_jni_COMVS_0005fNETCONFIGJNI
 * Method:    Comvs_SetWINSConfiguration
 * Signature: (Ljava/lang/String;Lorg/avm/device/fm6000/network/jni/COMVS_NETCONFIG_WINS_CONFIGURATION;)I
 */
JNIEXPORT jint JNICALL Java_org_avm_device_fm6000_network_jni_COMVS_1NETCONFIGJNI_Comvs_1SetWINSConfiguration
  (JNIEnv *, jclass, jstring, jobject);

/*
 * Class:     org_avm_device_fm6000_network_jni_COMVS_0005fNETCONFIGJNI
 * Method:    Comvs_SetProperty
 * Signature: (Ljava/lang/String;ILjava/lang/Object;)I
 */
JNIEXPORT jint JNICALL Java_org_avm_device_fm6000_network_jni_COMVS_1NETCONFIGJNI_Comvs_1SetProperty
  (JNIEnv *, jclass, jstring, jint, jobject);

/*
 * Class:     org_avm_device_fm6000_network_jni_COMVS_0005fNETCONFIGJNI
 * Method:    Comvs_GetProperty
 * Signature: (Ljava/lang/String;I)Ljava/lang/Object;
 */
JNIEXPORT jobject JNICALL Java_org_avm_device_fm6000_network_jni_COMVS_1NETCONFIGJNI_Comvs_1GetProperty
  (JNIEnv *, jclass, jstring, jint);

/*
 * Class:     org_avm_device_fm6000_network_jni_COMVS_0005fNETCONFIGJNI
 * Method:    Comvs_ReleaseIPAddress
 * Signature: (Ljava/lang/String;Lorg/avm/device/fm6000/network/jni/COMVS_NETCONFIG_IP;)I
 */
JNIEXPORT jint JNICALL Java_org_avm_device_fm6000_network_jni_COMVS_1NETCONFIGJNI_Comvs_1ReleaseIPAddress
  (JNIEnv *, jclass, jstring, jobject);

/*
 * Class:     org_avm_device_fm6000_network_jni_COMVS_0005fNETCONFIGJNI
 * Method:    Comvs_RegisterIPAddress
 * Signature: (Ljava/lang/String;Lorg/avm/device/fm6000/network/jni/COMVS_NETCONFIG_IP;Lorg/avm/device/fm6000/network/jni/COMVS_NETCONFIG_IP;)I
 */
JNIEXPORT jint JNICALL Java_org_avm_device_fm6000_network_jni_COMVS_1NETCONFIGJNI_Comvs_1RegisterIPAddress
  (JNIEnv *, jclass, jstring, jobject, jobject);

/*
 * Class:     org_avm_device_fm6000_network_jni_COMVS_0005fNETCONFIGJNI
 * Method:    Comvs_ListRoutesOfInterface
 * Signature: (Ljava/lang/String;)[Lorg/avm/device/fm6000/network/jni/COMVS_NETCONFIG_ROUTE;
 */
JNIEXPORT jobjectArray JNICALL Java_org_avm_device_fm6000_network_jni_COMVS_1NETCONFIGJNI_Comvs_1ListRoutesOfInterface
  (JNIEnv *, jclass, jstring);

/*
 * Class:     org_avm_device_fm6000_network_jni_COMVS_0005fNETCONFIGJNI
 * Method:    Comvs_ListRoutes
 * Signature: ()[Lorg/avm/device/fm6000/network/jni/COMVS_NETCONFIG_ROUTE;
 */
JNIEXPORT jobjectArray JNICALL Java_org_avm_device_fm6000_network_jni_COMVS_1NETCONFIGJNI_Comvs_1ListRoutes
  (JNIEnv *, jclass);

/*
 * Class:     org_avm_device_fm6000_network_jni_COMVS_0005fNETCONFIGJNI
 * Method:    Comvs_ListIPAddresses
 * Signature: (Ljava/lang/String;)[Lorg/avm/device/fm6000/network/jni/COMVS_NETCONFIG_IP;
 */
JNIEXPORT jobjectArray JNICALL Java_org_avm_device_fm6000_network_jni_COMVS_1NETCONFIGJNI_Comvs_1ListIPAddresses
  (JNIEnv *, jclass, jstring);

/*
 * Class:     org_avm_device_fm6000_network_jni_COMVS_0005fNETCONFIGJNI
 * Method:    Comvs_ListIP
 * Signature: ()[Lorg/avm/device/fm6000/network/jni/COMVS_NETCONFIG_IP;
 */
JNIEXPORT jobjectArray JNICALL Java_org_avm_device_fm6000_network_jni_COMVS_1NETCONFIGJNI_Comvs_1ListIP
  (JNIEnv *, jclass);

/*
 * Class:     org_avm_device_fm6000_network_jni_COMVS_0005fNETCONFIGJNI
 * Method:    Comvs_ListInterfaces
 * Signature: ()[Ljava/lang/String;
 */
JNIEXPORT jobjectArray JNICALL Java_org_avm_device_fm6000_network_jni_COMVS_1NETCONFIGJNI_Comvs_1ListInterfaces
  (JNIEnv *, jclass);

#ifdef __cplusplus
}
#endif
#endif
