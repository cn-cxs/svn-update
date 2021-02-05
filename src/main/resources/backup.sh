#!/bin/bash
#########################################################
#########################文件备份脚本######################
########################################################

#获取当前时间
now=$(date +%Y%m%d%H%M%S)
#回滚脚本文件名
rollback=rollback.sh

function backup() {
	files=$1
	path=$2
	#备份回滚脚本
	if [ -f "${rollback}" ]; then
		mv ${rollback} ${rollback}.${now}
	fi
	#新建回滚脚本
	$(echo "#! /bin/bash" >${rollback})
  `echo "echo '------------------------------------rollback start--------------------------------------'">>${rollback}`
	#多项目名,先拆分项目名为数据,循环遍历对应更新文件
	farray=(${files//,/ })
	# shellcheck disable=SC2068
	for var in ${farray[@]}; do
		for file in $(#遍历要更新的文件夹
			find ${var}
		); do
			#组装成服务器的文件路径
			spath=${path}${file:0}
			#旧文件名
			oldf=${spath}.${now}

			if [ -f "${file}" ]; then #如果是文件
				if [ -f "$spath" ]; then #如果文件存在,则需要备份
					#echo "cp -b ${spath} ${oldf}"
					cp -b ${spath} ${oldf}                   #备份要被更新的文件
					echo "mv ${oldf} ${spath}" >>${rollback} #更新动作生成回滚脚本
				fi
			fi
		done
	done
  `echo "echo '------------------------------------rollback end----------------------------------------'">>${rollback}`
	#修改回滚脚本为可执行
	chmod +x ${rollback}
}

echo "------------------------------------backup start----------------------------------------"
backup $1 $2
echo "------------------------------------backup end----------------------------------------"
