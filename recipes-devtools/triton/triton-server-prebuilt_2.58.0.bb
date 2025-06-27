DESCRIPTION = "The Triton Inference Server provides an optimized cloud and edge inferencing solution."
LICENSE = "BSD-3-Clause"

COMPATIBLE_MACHINE = "(tegra)"

LIC_FILES_CHKSUM = "file://LICENSE;md5=cbffd7f72014cdf056bc4e6f176b0c6b"

SRC_URI = "\
    https://raw.githubusercontent.com/triton-inference-server/server/refs/tags/v2.58.0/LICENSE;sha256sum=0870d6498ffe578e38cfe1031eda7757b9fb52337f663598831ecec8f596a32a \
    https://github.com/triton-inference-server/server/releases/download/v2.58.0/tritonserver2.58.0-igpu.tar;sha256sum=78b4572ef39fcbb31f3d00b6f913ae9e44277dfb0546ba089e1bb87ce6f02bc3 \
"

DEEPSTREAM_DIR = "/opt/nvidia/deepstream/deepstream-7.1"
TRITON_BACKEND_DIR = "/opt/tritonserver/backends"

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

DEPENDS = "\
    libb64 \
"

RDEPENDS:${PN} = "\
    re2 \
    python3-pybind11 \
    boost \
    cnmem \
    numactl \
    protobuf \
    re2 \
    onnxruntime \
    cuda-cudart \
    zlib \
    dlpack \
    libarchive \
    python3 \
    cuda-cupti \
    cudnn \
"

do_configure() {
    :
}

do_compile() {
    :
}


do_install() {
   install -d ${D}${DEEPSTREAM_DIR}/lib
   install -m 0755 ${S}/tritonserver/lib/libtritonserver.so ${D}${DEEPSTREAM_DIR}/lib/
   install -d ${D}${TRITON_BACKEND_DIR}
   cp -r ${S}/tritonserver/backends/* ${D}${TRITON_BACKEND_DIR}/
   rm ${D}${TRITON_BACKEND_DIR}/pytorch/libopencv_video.so # Depends on libopencv_dnn.so.410
   rm ${D}${TRITON_BACKEND_DIR}/python/triton_python_backend_stub # Depends on libpython3.12.so.1.0
   install -d ${D}${bindir}
   install -m 0755 ${S}/tritonserver/bin/tritonserver ${D}${bindir}
   install -d ${D}${base_libdir}
   # Prebuilt binaries are linked to libb64.so with this specific name so add a symlink to get past runtime error
   ln -s ${base_libdir}/libb64.so.0 ${D}${base_libdir}/libb64.so.0d
}

FILES:${PN} = " \
    ${DEEPSTREAM_DIR}/lib/* \
    ${TRITON_BACKEND_DIR}/* \
    ${bindir}/tritonserver \
    ${base_libdir}/libb64.so.0d \
"

PROVIDES = "triton-server"
RPROVIDES:${PN} = "triton-server libb64.so.0d()(64bit)"
RCONFICTS:${PN} = "triton-server"

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INHIBIT_SYSROOT_STRIP = "1"
INSANE_SKIP:${PN} = "file-rdeps"
PACKAGE_ARCH = "${TEGRA_PKGARCH}"
