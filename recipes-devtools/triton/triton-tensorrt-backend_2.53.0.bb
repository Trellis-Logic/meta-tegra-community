DESCRIPTION = "The Triton backend for TensorRT"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4e068ed5ea6f9bce3de86a872b056d93"

SRC_URI = " \
    git://github.com/triton-inference-server/tensorrt_backend.git;protocol=https;branch=r24.12 \
    file://0001-Build-fixups.patch \
"

SRCREV = "301d10b08a080010552013b0fcfc09bbfc3c424b"

S = "${WORKDIR}/git"

DEPENDS = "\
    triton-common \
    triton-core \
    triton-backend \
    tensorrt-core \
    tensorrt-plugins \
    cuda-cudart \
"

COMPATIBLE_MACHINE = "(cuda)"

inherit cuda cmake

PACKAGECONFIG ??= "gpu"
PACKAGECONFIG[gpu] = "-DTRITON_ENABLE_GPU=ON,-DTRITON_ENABLE_GPU=OFF"
PACKAGECONFIG[stats] = "-DTRITON_ENABLE_STATS=ON,-DTRITON_ENABLE_STATS=OFF"
PACKAGECONFIG[nvtx] = "-DTRITON_ENABLE_NVTX=ON,-DTRITON_ENABLE_NVTX=OFF"

FILES:${PN} += "${libdir}/tensorrt/*"
