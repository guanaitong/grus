#
# openjdk 11.0.4_11
# PLEASE DO NOT EDIT IT DIRECTLY.
#

FROM registry.wuxingdev.cn/base/centos-gat:7.4.1708

MAINTAINER jie.zhang@guanaitong.com

ENV JAVA_HOME=/usr/lib/jvm/java \
    PATH=${JAVA_HOME}/bin:${PATH}

RUN set -ex && \
    rm -rf /var/cache/yum && \
    yum makecache && \
    yum install -y java-11-openjdk-devel && \
    rm -rf /var/cache/yum && \
    sed -i 's#securerandom.source=file:/dev/random#securerandom.source=file:/dev/./urandom#g' ${JAVA_HOME}/conf/security/java.security
