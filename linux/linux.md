# 常用软件

chrome

v2raya:vmess+软件+chrome插件

pycharm

idea

飞书

typora

git



# 环境变量

## 读取环境变量

- **`export`**:读取系统所有的环境变量

```shell
sz@ubuntu22:~/Desktop$ export
declare -x HOME="/home/sz"
declare -x LOGNAME="sz"
declare -x PATH="/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/usr/games:/usr/local/games:/snap/bin"
declare -x PWD="/home/sz/Desktop"
declare -x SHELL="/bin/bash"
declare -x SHLVL="1"
declare -x SSH_CLIENT="192.168.222.1 50218 22"
declare -x SSH_CONNECTION="192.168.222.1 50218 192.168.222.132 22"
declare -x SSH_TTY="/dev/pts/1"
declare -x USER="sz"
declare -x XDG_SESSION_CLASS="user"
declare -x XDG_SESSION_ID="3"
declare -x XDG_SESSION_TYPE="tty"
```

- **`export $PATH`**: 读取当前的PATH的环境变量

```shell
sz@ubuntu22:~/Desktop$ export $PATH
-bash: export: `/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/usr/games:/usr/local/games:/snap/bin': not a valid identifier
```

其中`PATH`变量定义了运行命令的查找路径，以冒号`:`分割不同的路径，使用`export`定义的时候可加双引号也可不加。

## 配置环境变量

### 方法一：`export PATH`

使用export命令直接修改PATH的值

```shell
export PATH=$PATH:/home/sz/Desktop/soft/jdk17/bin
```

特点：

- 生效时间：立即生效
- 生效期限：当前终端有效，窗口关闭后无效
- 生效范围：仅对当前用户有效
- 配置的环境变量中不要忘了加上原来的配置，即`$PATH`部分，避免覆盖原来配置

### 方法二：vim ~/.bashrc

通过修改用户目录下的`~/.bashrc`文件进行配置：

```shell
vim ~/.bashrc

# 在最后一行加上
export PATH=$PATH:/home/sz/Desktop/soft/jdk17/bin
```

特点：

- 生效时间：使用相同的用户打开新的终端时生效，或者手动`source ~/.bashrc`生效
- 生效期限：永久有效
- 生效范围：仅对当前用户有效
- 如果有后续的环境变量加载文件覆盖了`PATH`定义，则可能不生效

### 方法三：`vim ~/.bash_profile` 或 `.profile`

和修改`~/.bashrc`文件类似，也是要在文件最后加上新的路径即可：

```shell
vim ~/.bash_profile

# 在最后一行加上
export PATH=$PATH:/home/uusama/mysql/bin
```

注意事项：

- 生效时间：使用相同的用户打开新的终端时生效，或者手动`source ~/.bash_profile`生效
- 生效期限：永久有效
- 生效范围：仅对当前用户有效
- 如果没有`~/.bash_profile`文件，则可以编辑`~/.profile`文件或者新建一个

### 方法四：`vim /etc/bashrc`

该方法是修改系统配置，需要管理员权限（如root）或者对该文件的写入权限：

```shell
# 如果/etc/bashrc文件不可编辑，需要修改为可编辑
chmod -v u+w /etc/bashrc

vim /etc/bashrc

# 在最后一行加上
export PATH=$PATH:/home/uusama/mysql/bin
```

注意事项：

- 生效时间：新开终端生效，或者手动`source /etc/bashrc`生效
- 生效期限：永久有效
- 生效范围：对所有用户有效

### 方法五：`vim /etc/profile`

该方法修改系统配置，需要管理员权限或者对该文件的写入权限，和`vim /etc/bashrc`类似：

```shell
# 如果/etc/profile文件不可编辑，需要修改为可编辑
chmod -v u+w /etc/profile

vim /etc/profile

# 在最后一行加上
export PATH=$PATH:/home/uusama/mysql/bin
```

注意事项：

- 生效时间：新开终端生效，或者手动`source /etc/profile`生效
- 生效期限：永久有效
- 生效范围：对所有用户有效

### 方法六：`vim /etc/environment`

该方法是修改系统环境配置文件，需要管理员权限或者对该文件的写入权限：

```shell
# 如果/etc/bashrc文件不可编辑，需要修改为可编辑
chmod -v u+w /etc/environment

vim /etc/profile

# 在最后一行加上
export PATH=$PATH:/home/uusama/mysql/bin
```

注意事项：

- 生效时间：新开终端生效，或者手动`source /etc/environment`生效
- 生效期限：永久有效
- 生效范围：对所有用户有效

上面列出了环境变量的各种配置方法，那么Linux是如何加载这些配置的呢？是以什么样的顺序加载的呢？

特定的加载顺序会导致相同名称的环境变量定义被覆盖或者不生效。

### 环境变量的分类

环境变量可以简单的分成用户自定义的环境变量以及系统级别的环境变量。

- 用户级别环境变量定义文件：`~/.bashrc`、`~/.profile`（部分系统为：`~/.bash_profile`）
- 系统级别环境变量定义文件：`/etc/bashrc`、`/etc/profile`(部分系统为：`/etc/bash_profile`）、`/etc/environment`

另外在用户环境变量中，系统会首先读取`~/.bash_profile`（或者`~/.profile`）文件，如果没有该文件则读取`~/.bash_login`，根据这些文件中内容再去读取`~/.bashrc`。

### 测试Linux环境变量加载顺序的方法

为了测试各个不同文件的环境变量加载顺序，我们在每个环境变量定义文件中的第一行都定义相同的环境变量`UU_ORDER`，该变量的值为本身的值连接上当前文件名称。

需要修改的文件如下：

- `/etc/environment`
- `/etc/profile`
- `/etc/profile.d/test.sh`，新建文件，没有文件夹可略过
- `/etc/bashrc`，或者`/etc/bash.bashrc`
- `~/.bash_profile`，或者`~/.profile`
- `~/.bashrc`

在每个文件中的第一行都加上下面这句代码，并相应的把冒号后的内容修改为当前文件的绝对文件名。

```
export UU_ORDER="$UU_ORDER:~/.bash_profile"
```

修改完之后保存，新开一个窗口，然后`echo $UU_ORDER`观察变量的值：

```shell
echo $UU_ORDER
/etc/environment:/etc/profile:/etc/bash.bashrc:/etc/profile.d/test.sh:~/.profile:~/.bashrc
```

可以推测出Linux加载环境变量的顺序如下：

1. `/etc/environment`
2. `/etc/profile`
3. `/etc/bash.bashrc`
4. `/etc/profile.d/test.sh`
5. `~/.profile`
6. `~/.bashrc`

# shell脚本

基本使用：

输入输出执行基本命令

```shell
#!/bin/bash
#执行命令
java --version
#输出提示
echo "PATH:"
#输出变量
export $PATH
#这种执行read 命令  从客户端输入的，执行时候需要使用bash执行，使用sh执行会有问题
read -p "input your first name:" firstname
read -p "input your last name:" lastname
#输出文字和变量
echo -e "full name =  ${firstname} ${lastname}"
exit 0
```





取变量:${varname}

变量计算:$((a*b))



脚本的执行方式区别：bash(新开子程序) 和 source（当前bash环境执行）



# 常用命令

## find

```shell
# 查找文件
find / -name test.txt
```



## grep

1. 搜索匹配模式的行：

   ```shell
   grep "pattern" file
   ```

2. 搜索忽略大小写的匹配模式的行：

   ```shell
   grep -i "pattern" file
   ```

3. 显示匹配模式的行以及它们之前或之后的几行：

   ```shell
   grep -A num "pattern" file  # 显示匹配行和后面的 num 行
   grep -B num "pattern" file  # 显示匹配行和前面的 num 行
   grep -C num "pattern" file  # 显示匹配行和前后的 num 行
   ```

4. 搜索多个模式的行：

   ```shell
   grep -e "pattern1" -e "pattern2" file  # 匹配 pattern1 或 pattern2 的行
   grep -E "pattern1|pattern2" file       # 同上，使用扩展正则表达式
   ```

5. 反向搜索，即显示不匹配模式的行：

   ```shell
   grep -v "pattern" file
   ```

6. 仅显示匹配模式的计数：

   ```shell
   grep -c "pattern" file
   ```

7. 仅显示匹配模式的行号：

   ```shell
   grep -n "pattern" file
   ```

8. 递归搜索目录中的文件：

   ```shell
   grep "pattern" -r directory
   ```

## awk

## sed



















# 脚本搭建docker环境

```shell
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

```





# 其他服务类

## 更换源

<font color="red">编辑/etc/apt/sources.list文件</font>

```linux
deb http://mirrors.aliyun.com/ubuntu/ focal main restricted universe multiverse
deb http://mirrors.aliyun.com/ubuntu/ focal-security main restricted universe multiverse
deb http://mirrors.aliyun.com/ubuntu/ focal-updates main restricted universe multiverse
deb http://mirrors.aliyun.com/ubuntu/ focal-proposed main restricted universe multiverse
deb http://mirrors.aliyun.com/ubuntu/ focal-backports main restricted universe multiverse
deb https://mirrors.tuna.tsinghua.edu.cn/ubuntu/ focal main restricted universe multiverse
deb https://mirrors.tuna.tsinghua.edu.cn/ubuntu/ focal-updates main restricted universe multiverse
deb https://mirrors.tuna.tsinghua.edu.cn/ubuntu/ focal-backports main restricted universe multiverse
deb https://mirrors.tuna.tsinghua.edu.cn/ubuntu/ focal-security main restricted universe multiverse
deb https://mirrors.ustc.edu.cn/ubuntu/ focal main restricted universe multiverse
deb https://mirrors.ustc.edu.cn/ubuntu/ focal-updates main restricted universe multiverse
deb https://mirrors.ustc.edu.cn/ubuntu/ focal-backports main restricted universe multiverse
deb https://mirrors.ustc.edu.cn/ubuntu/ focal-security main restricted universe multiverse
```

## 安装ssh服务

```shell
sudo apt install openssh-server
```

