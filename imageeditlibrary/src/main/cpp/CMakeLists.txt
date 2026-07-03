cmake_minimum_required(VERSION 3.22.1)

project("photoprocessing")

add_library(${CMAKE_PROJECT_NAME} SHARED
        src/nanojpeg.c
        src/mem_utils.c
        src/bitmap.c
        src/bicubic_resize.c
        src/filter.c
        src/transform.c
        src/colour_space.c
        src/matrix.c
        src/blur.c
        src/photo_processing.c)

include_directories(${CMAKE_SOURCE_DIR}/include)

target_compile_definitions(
        photoprocessing
        PRIVATE
        ANDROID_NDK
        DISABLE_IMPORTGL
)

target_link_options(photoprocessing PRIVATE
        "-Wl,-z,max-page-size=16384"
        "-Wl,-z,common-page-size=16384"
)

target_link_libraries(${CMAKE_PROJECT_NAME}
        android
        log
        jnigraphics
        m
)