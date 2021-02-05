#!/bin/bash
#########################################################
######################更新代码主运行脚本####################
########################################################

#获取服务器IP
server_ip=`ip addr | grep 'state UP' -A2 | tail -n1 | awk '{print $2}' | cut -f1 -d '/'`

shell="./bin/${server_ip}.sh"
if [ ! -f "${shell}" ]; then
    echo "no config for ${server_ip}"
    exit
fi
chmod +x ./bin/*.sh
echo "`${shell}`"