#!/bin/bash
#安装chrome浏览器
install_chrome(){
	if [ command -v google-chrome >/dev/null 2>&1 ];then
		echo "已经安装过chrome..."
	else
		wget https://dl.google.com/linux/direct/google-chrome-stable_current_amd64.deb
		sudo dpkg -i google-chrome-stable_current_amd64.deb 
	fi
}



#安装jdk8
install_jdk8(){
	jdk8_zip_file="jdk-8u141-linux-x64.tar.gz"
	jdk8_directory="jdk1.8.0_141"
	install_directory="/usr/local/jdk8"
	#检查安装路径是否已经存在，如果存在则不安装
	if [ -d "$install_directory" ];then
		echo "已经安装过/usr/local/jdk8,无需重复安装"
	else
		#下载jdk8_zip_file
		if [ -f "$jdk8_zip_file" ]; then 
			echo "当前路径已经存在文件$jdk8_zip_file"
		else
			echo "开始下载$jdk8_zip_file"
			wget --no-cookies --no-check-certificate --header "Cookie: gpw_e24=http%3A%2F%2Fwww.oracle.com%2F; oraclelicense=accept-securebackup-cookie" "http://download.oracle.com/otn-pub/java/jdk/8u141-b15/336fa29ff2bb4ef291e347e091f7f4a7/jdk-8u141-linux-x64.tar.gz"
		fi
		#等待下载完成再执行后面的操作
		#while [ ! -f $jdk8_zip_file ];do
		#	echo "正在等待下载完成....."
		#	sleep 2
		#done
	
		#解压jdk
		if [ ! -d "$jdk8_directory" ];then
			tar -xzvf $jdk8_zip_file
		fi
		#将jdk文件夹 移动到/usr/local
		sudo mv "$jdk8_directory" "$install_directory"
		#删除压缩文件
		rm  $jdk8_zip_file
	fi
	#提示环境变量
	echo "$install_directory 已准备jdk,如果无法使用， 请手动检查是否已经设置环境变量，否则请在~/.bashrc或者~/.profile下进行设置"
	echo "比如追加以下两行"
	echo "export JAVA_HOME=$install_directory"
	echo export PATH='$PATH':"$install_directory"/bin
	echo "然后使用source .xxxx使文件生效"
}

#卸载docker
remove_docker(){
	echo "开始卸载docker并删除各种相关文件..."
	#将docker相关应用容器杀死：
	docker kill $(docker ps -a -q)
	#删除所有docker容器：
	docker rm $(docker ps -a -q)
	#删除所有docker镜像：
	docker rmi $(docker images -q)
	#停止 docker 服务：
	systemctl stop docker
	#进行umount操作：
	umount /var/lib/docker/devicemapper
	#删除docker相关存储目录：
	rm -rf /etc/docker
	rm -rf /run/docker
	rm -rf /var/lib/dockershim
	rm -rf /var/lib/docker
	#删除docker、卸载相关包：
	sudo apt-get purge docker-ce docker-ce-cli containerd.io
	#检查卸载结果：返回空则为成功卸载
	docker version
}
#安装docker
install_docker(){
	if command -v docker &> /dev/null; then
		echo "已经安装了docker,无需重复安装..."	
	else
		echo "开始安装docker..."
		curl -fsSL https://get.docker.com | bash -s docker --mirror Aliyun
		while [ command -v docker &>/dev/null ]; do
			sleep 2
		done
		#设置仓库
		sudo apt-get install apt-transport-https ca-certificates curl gnupg-agent software-properties-common
		#安装docker engine-community
		sudo apt-get install docker-ce docker-ce-cli containerd.io
	fi
}



#安装docker mysql
install_mysql(){
	if [ -x "$(command -v docker)" ];then
		echo "开始安装mysql..."
		
		#创建目录
		#if [ -d "/usr/local/mysql/etc" ];then
		#	rm -r /usr/local/mysql/etc
		#else
		#	mkdir /usr/local/mysql/etc
		#fi

		#开始创建容器
		if docker inspect mysql8 >/dev/null 2>&1; then
			echo "mysql8容器已创建..."
		else
			docker run -d -p 3306:3306 --name mysql8 -e MYSQL_ROOT_PASSWORD=sz@mysql mysql:8.0
		fi
	else
		echo "docker环境未就绪，请先装docker"
	fi
	
}


#安装docker redis
install_redis(){
	if [ -x "$(command -v docker)" ]; then
		echo "docker环境已准备就绪..."
		#下载镜像
		docker pull redis:6.2.6
		#创建目录用于配置文件的映射
		if [ -d '/usr/local/redis' ];then
			echo "/usr/local/redis路径已存在..."
		else
			sudo mkdir /usr/local/redis
		fi
		#镜像默认没有redis.conf这个配置文件 自己进行下载
		if [ -f "redis-3.2.12.tar.gz" ];then
			echo "redis-3.2.12.tar.gz文件已存在..."
		else
			echo "下载redis,解压获取配置文件到/usr/local/redis路径，可以此redis.conf中设置密码等配置..."
			wget    http://download.redis.io/releases/redis-3.2.12.tar.gz
		fi
		tar -xzvf redis-3.2.12.tar.gz
		cp redis-3.2.12/redis.conf /usr/local/redis/redis.conf
		rm -r redis-3.2.12
		rm redis-3.2.12.tar.gz
		# 启动容器 进行端口映射 文件映射
		docker run -d -p 6379:6379 -v /usr/local/redis:/usr/local/etc/redis --name my_redis redis:6.2.6 redis-server /usr/local/etc/redis/redis.conf
		#cp my_redis:/usr/local/etc/redis/redis.conf /usr/local/redis
		docker restart my_redis

	else
		echo "docker环境未就绪，请先安装docker"
	fi
}



read -p "是否安装chrome浏览器?y/n" to_install
if [ $to_install =="y" ];then
	install_chrome
fi


read -p "是否安装jdk8?y/n" to_install
if [ $to_install == "y" ];then
	install_jdk8
fi

read -p "是否安装docker?y/n" to_install
if [ $to_install == "y" ];then
	install_docker
fi

read -p "是否使用docker安装mysql?y/n" to_install
if [ $to_install == "y" ];then
	install_mysql
fi

read -p "是否使用docker安装redis?y/n" to_install
if [ $to_install == "y" ];then
	install_redis
fi
