#!/bin/bash

set -ex

env=${WORK_ENV}
idc=${WORK_IDC}
force=${JAVA_OPTS_MEM_FORCE}
origin_file=%s
#skipJacoco=${SKIP_JACOCO}

echo "start init $(date +'%%Y-%%m-%%d %%H:%%M:%%S')"
echo "author: august.zhou@guanaitong.com,jie.zhang@guanaitong.com,liang.yao@guanaitong.com"

# only env[test] run jacoco
if [ "X${env}Y" == "XtestY" ];then
  if [ "X${SKIP_JACOCO}Y" != "XTRUEY" ];then
    export JAVA_OPTS="-javaagent:/usr/lib/tomcat/jacocoagent.jar=includes=*,output=tcpserver,port=2020,address=0.0.0.0 ${JAVA_OPTS} "
  fi
fi

if [[ $force -eq 1 ]]; then
    InitialRAMPercentage=85.0
else
    InitialRAMPercentage=50.0
fi

export JAVA_OPTS="${JAVA_OPTS} \
  -XshowSettings:vm \
  -Dclient.encoding.override=UTF-8 \
  -Dfile.encoding=UTF-8 \
  -Duser.language=zh \
  -Duser.region=CN \
  -XX:InitialRAMPercentage=${InitialRAMPercentage} \
  -XX:MaxRAMPercentage=85.0 \
  -XX:MetaspaceSize=128M \
  -XX:+HeapDumpOnOutOfMemoryError \
  -XX:HeapDumpPath=logs/heapDump.hprof  \
  -XX:+ExitOnOutOfMemoryError \
  -XX:+UnlockDiagnosticVMOptions \
  -Xlog:gc*:file=logs/gc.log:time,tags:filecount=10,filesize=10240000"

java_params="--server.port=80 \
      --server.tomcat.threads.max=200 \
      --server.tomcat.threads.min-spare=5 \
      --server.tomcat.basedir=. \
      --server.tomcat.accesslog.directory=logs \
      --server.tomcat.accesslog.enabled=true \
      --server.tomcat.accesslog.pattern=%%t^%%I^%%a^%%{x-app-name}o^%%{x-app-instance}o^%%{x-req-app-name}i^%%{x-req-app-instance}i^%%{x-trace-id}o^%%{x-span-id}o^%%{x-parent-id}o^%%m^%%U^%%H^%%s^%%b^%%D^%%q \
      --server.tomcat.accesslog.prefix=access \
      --server.tomcat.accesslog.suffix=.log \
      --server.tomcat.accesslog.rotate=true \
      --server.tomcat.accesslog.rename-on-rotate=true \
      --server.tomcat.uri-encoding=utf-8 \
      --spring.boot.admin.client.url=http://spring-boot-admin-server/ \
      --spring.boot.admin.client.instance.prefer-ip=true \
      --management.server.port=8181 \
      --management.endpoints.web.exposure.include=* \
      --spring.cloud.sentinel.transport.dashboard=sentinel-dashboard \
      --server.servlet.encoding.charset=utf-8 \
      --server.servlet.encoding.enabled=true \
      --logging.file.name=logs/application.log \
      --logging.file.max-size=2048MB"

printf "env[${env}],app[${origin_file}] starting...\n"
if ([ ${env} = "product" ] && [ ${idc} = "sh" ]) || ([ ${env} = "product" ] && [ ${idc} = "ali" ]); then
    exec ${JAVA_HOME}/bin/java ${JAVA_OPTS} -jar ${origin_file} ${java_params} \
      --logging.pattern.console \
      --logging.pattern.file='^V^ [%%p] [%%d{yyyy-MM-dd HH:mm:ss.SSS}] [%%t] [%%c:%%L] [%%X{traceId}:%%X{spanId}:%%X{parentId}] [%%X{req.xRequestId}] %%m%%n'
else
    exec ${JAVA_HOME}/bin/java ${JAVA_OPTS} -jar ${origin_file} ${java_params} \
      --logging.pattern.file='^V^ [%%p] [%%d{yyyy-MM-dd HH:mm:ss.SSS}] [%%t] [%%c:%%L] [%%X{traceId}:%%X{spanId}:%%X{parentId}] [%%X{req.xRequestId}] %%m%%n'
fi