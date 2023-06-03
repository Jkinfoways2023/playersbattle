#include <jni.h>

JNIEXPORT jstring JNICALL
Java_com_GlaDius_war_common_Config_getNativeKey1(JNIEnv *env, jclass clazz) {
    // TODO: implement getNativeKey1()
    // TODO: REST API LINK https://blackwarofficial.co.in/blackplay/api/
    return (*env)->  NewStringUTF(env, "aHR0cHM6Ly9HcmluZGJhdHRsZS5jb20vYXBpcy8=");
}

JNIEXPORT jstring JNICALL
Java_com_GlaDius_war_common_Config_getNativeKey2(JNIEnv *env, jclass clazz) {
    // TODO: implement getNativeKey2()
    // TODO: FILE PATH LINK https://blackwarofficial.co.in/blackplay/admin
    return (*env)->  NewStringUTF(env, "aHR0cHM6Ly9ibGFja3dhcm9mZmljaWFsLmNvLmluL2JsYWNrcGxheS9hZG1pbi8=");
}

JNIEXPORT jstring JNICALL
Java_com_GlaDius_war_common_Config_getNativeKey3(JNIEnv *env, jclass clazz) {
    // TODO: implement getNativeKey3()
    // TODO: PAYTM PATH LINK https://blackwarofficial.co.in/blackplay/paytm/
    return (*env)->  NewStringUTF(env, "aHR0cDovL0dyaW5kYmF0dGxlLmNvbS9wYXkv");
}

JNIEXPORT jstring JNICALL
Java_com_GlaDius_war_common_Config_getNativeKey4(JNIEnv *env, jclass clazz) {
    // TODO: implement getNativeKey4()
    // TODO: BRAINTREE PATH LINK
    return (*env)->  NewStringUTF(env, "aHR0cDovL2dsYWRpdXN3YXIuaW4vV2FyR2xhZGl1cy9icmFpbnRyZS8=");
}

JNIEXPORT jstring JNICALL
Java_com_GlaDius_war_common_Config_getNativeKey5(JNIEnv *env, jclass clazz) {
    // TODO: implement getNativeKey5()
    // TODO: PURCHASE CODE
    return (*env)->  NewStringUTF(env, "NzQyNGVjZjctZjBhZC00MzViLTlkNTMtNjUyZTMyZmQyYzdh");
}

