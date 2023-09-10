echo "------------------------------------------------------------------------------------------------------------------------"
echo "参考链接如下："
echo "装mysql:    https://www.linjiangxiong.com/2022/04/05/how-to-install-mysql57-on-centos7/"
echo "不能登录问题：https://tableplus.com/blog/2018/08/solved-cant-connect-to-local-mysql-ser-socket.html"
echo "防火墙 开放端口：https://blog.csdn.net/qq_39408664/article/details/118732528"
echo "------------------------------------------------------------------------------------------------------------------------"
if [ -f "mysql57-community-release-el7-9.noarch.rpm" ]; then
	echo "文件已存在，无需下载"
else
	echo "开始下载：mysql57-community-release-el7-9.noarch.rpm"
	wget https://dev.mysql.com/get/mysql57-community-release-el7-9.noarch.rpm
fi

if command -v mysql&> /dev/null; then
	echo "mysql已安装，无需重复安装"
else
	rpm -ivh mysql57-community-release-el7-9.noarch.rpm
	echo "如果出现公钥问题，把该版本的gpgcheck改为0，vim mysql-community.repo 该文件在/etc/yum.repos.d/中"
	cd /etc/yum.repos.d/
	yum install mysql-server
	systemctl start mysqld
	systemctl status mysqld
	grep 'temporary password' /var/log/mysqld.log
	echo "使用localhost可能出问题无法登录"
	mysql -uroot -h127.0.0.1 -p
	echo "远程连接问题：开放防火墙，3306：firewall-cmd --zone=public --add-port=3306/tcp --permanent， firewall-cmd --reload "
fi


