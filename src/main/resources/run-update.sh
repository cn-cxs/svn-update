#!/bin/bash
#########################################################
#########################文件更新脚本######################
########################################################

function update() {
	files=$1
	path=$2
	#多项目名,先拆分项目名为数据,循环遍历对应更新文件
	farray=(${files//,/ })
	# shellcheck disable=SC2068
	for var in ${farray[@]}; do
		for file in $(#遍历要更新的文件夹
			find ${var}
		); do
		  #组装成服务器的文件路径
			spath=${path}${file:0}

			if [ -d "$file" ]; then #如果遍历处要更新的是文件夹
				if [ ! -d "$spath" ]; then #文件夹不存在则创建
					mkdir -p ${spath}
				fi
			elif [ -f "${file}" ]; then #如果是文件
				echo "cp ${file} ${spath}"
				cp ${file} ${spath} #复制文件到目标位置
			fi
		done
	done
}

echo "------------------------------------update start----------------------------------------"
update $1 $2
echo "------------------------------------update end----------------------------------------"
