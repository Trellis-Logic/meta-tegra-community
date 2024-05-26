DESCRIPTION = "Python bindings for Deepstream-6.3"
HOMEPAGE = "https://github.com/NVIDIA-AI-IOT/deepstream_python_apps"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7a01a47514ea2d404b8db41b6cfe6db0"

SRC_REPO = "github.com/NVIDIA-AI-IOT/deepstream_python_apps.git;protocol=https"
SRCBRANCH = "master"
SRC_URI = "git://${SRC_REPO};branch=${SRCBRANCH} \
           file://0001-Fixes-for-cross-building.patch \
           file://0002-Allow-apps-to-be-run-from-other-working-directories.patch \
           file://deepstream-nvdsanalytics-demo.service \
           "
# v1.1.8 tag
SRCREV = "8178b5e611ccdc23bef39caf2f1f07b14d39a7cb"

#PV .= "+git"

COMPATIBLE_MACHINE = "(tegra)"

S = "${WORKDIR}/git"

DEPENDS = "deepstream-6.3 python3-pybind11 gstreamer1.0-python gstreamer1.0 glib-2.0"
DS_PATH = "/opt/nvidia/deepstream/deepstream-6.3"

inherit setuptools3 cmake pkgconfig ptest systemd

OECMAKE_SOURCEPATH = "${S}/bindings"
SETUPTOOLS_SETUP_PATH = "${B}"
EXTRA_OECMAKE = "-DDS_PATH=${DS_PATH}"

do_configure() {
    cmake_do_configure
    cp ${S}/bindings/packaging/setup.py ${B}/    
}

do_compile() {
    cmake_do_compile
    setuptools3_do_compile
}

do_install() {
    setuptools3_do_install
    install -d ${D}${DS_PATH}/sources/deepstream_python_apps
    cp -Rf --preserve=mode,timestamps ${S}/apps ${D}${DS_PATH}/sources/deepstream_python_apps/
    find ${D}${DS_PATH}/sources/deepstream_python_apps/apps -type f -name '*config.txt' |
	xargs sed -i -r -e's,=(\.\./)+samples/,=${DS_PATH}/samples/,' \
	      -e's,^ll-config-file=[[:space:]]*(.*)$,ll-config-file=${DS_PATH}/samples/configs/deepstream-app/\1,' \
	      -e's,^ll-lib-file=[[:space:]]*/opt.*/(lib/.*),ll-lib-file=${DS_PATH}/\1,'
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/deepstream-nvdsanalytics-demo.service ${D}${systemd_system_unitdir}
}

PACKAGES += "${PN}-samples"
PACKAGES += "${PN}-nvdsanalytics-demo"
SYSTEMD_SERVICE:${PN}-nvdsanalytics-demo = "deepstream-nvdsanalytics-demo.service"
SYSTEMD_PACKAGES = "${PN}-nvdsanalytics-demo"
RDEPENDS:${PN} = "python3-pygobject gstreamer1.0-python"
FILES:${PN}-samples = "${DS_PATH}/sources/deepstream_python_apps"
FILES:${PN}-nvdsanalytics-demo = "${systemd_system_unitdir}/deepstream-nvdsanalytics-demo.service"
RDEPENDS:${PN}-samples = "${PN} deepstream-6.3-samples-data python3-opencv python3-numpy gobject-introspection"
RDEPENDS:${PN}-nvdsanalytics-demo = "${PN} ${PN}-samples"
PACKAGE_ARCH = "${TEGRA_PKGARCH}"
