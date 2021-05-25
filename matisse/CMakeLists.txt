## For more information about using CMake with Android Studio, read the
## documentation: https://d.android.com/studio/projects/add-native-code.html
#
## Sets the minimum version of CMake required to build the native library.
#
#cmake_minimum_required(VERSION 3.4.1)
#
## Creates and names a library, sets it as either STATIC
## or SHARED, and provides the relative paths to its source code.
## You can define multiple libraries, and CMake builds them for you.
## Gradle automatically packages shared libraries with your APK.
## 设置生成的so动态库最后输出的路径
#set(CMAKE_LIBRARY_OUTPUT_DIRECTORY ${PROJECT_SOURCE_DIR}/src/main/jniLibs/${ANDROID_ABI})
#
#add_library( # Sets the name of the library.  设置生成so文件名
#            ucrop
#
#            # Sets the library as a shared library.
#            SHARED
#
#            # Provides a relative path to your source file(s).  要编译的C/C++文件，可能会有N多个相关调用的 cpp文件，都要在这里声明
#            src_crop/main/jni/CImg.h
#            src_crop/main/jni/com_yalantis_ucrop_task_BitmapCropTask.h
#            src_crop/main/jni/uCrop.cpp
#            )
#
## Searches for a specified prebuilt library and stores the path as a
## variable. Because CMake includes system libraries in the search path by
## default, you only need to specify the name of the public NDK library
## you want to add. CMake verifies that the library exists before
## completing its build.
#
#find_library( # Sets the name of the path variable.
#             log-lib
#
#             # Specifies the name of the NDK library that
#             # you want CMake to locate.
#             log )
#
#add_library(libjpeg
#        SHARED
#        IMPORTED)
#
#add_library(libpng
#        SHARED
#        IMPORTED)
#
## Specifies libraries CMake should link to your target library. You
## can link multiple libraries, such as libraries you define in this
## build script, prebuilt third-party libraries, or system libraries.
#
#target_link_libraries( # Specifies the target library
#                      ucrop
#                      libjpeg
#                      libpng
#                      # Links the target library to the log library included in the NDK.
#                      ${log-lib} )
