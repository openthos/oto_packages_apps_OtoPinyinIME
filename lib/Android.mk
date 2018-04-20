LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_SRC_FILES := \
         $(call all-subdir-java-files) \
         org/openthos/inputmethod/pinyin/IPinyinDecoderService.aidl

LOCAL_MODULE := org.openthos.inputmethod.pinyin.lib

include $(BUILD_STATIC_JAVA_LIBRARY)
