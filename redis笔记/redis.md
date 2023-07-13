# 安装

1. gcc
2. make
3. cd 目录，  make   make install  默认安装到/usr/local
4. 复制解压的文件里的redis.conf  到/etc/  修改daemonize 参数
5. 启动   cd /usr/local ,  redis-server  /etc/redis/conf
6. 关闭/usr/local/bin$ redis-cli 连接 然后127.0.0.1:6379> shutdown




# 基础

- 默认16个数据库，默认使用0号库，密码相同
- 单线程+多路IO复用

# 五种数据类型

字符串 String

列表List

集合Set

哈希Hash

有序集合Zset





