package com.youyuan.jedis;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

import java.util.HashSet;
import java.util.Set;

/**
 * 类名称：RedisClusterTest <br>
 * 类描述： jedis操作redis cluster <br>
 *
 * @author zhangyu
 * @version 1.0.0
 * @date 创建时间：2021/8/21 12:12<br>
 */
public class RedisClusterTest {

    public static void main(String[] args) {
        Set<HostAndPort> hostAndPortSet = new HashSet<>();
        hostAndPortSet.add(new HostAndPort("192.168.1.22", 6379));
        hostAndPortSet.add(new HostAndPort("192.168.1.22", 6380));
        hostAndPortSet.add(new HostAndPort("192.168.1.22", 6381));
        hostAndPortSet.add(new HostAndPort("192.168.1.22", 6389));
        hostAndPortSet.add(new HostAndPort("192.168.1.22", 6390));
        hostAndPortSet.add(new HostAndPort("192.168.1.22", 6391));
        JedisCluster jedisCluster = new JedisCluster(hostAndPortSet);
        jedisCluster.set("city", "北京");
        System.out.println(jedisCluster.get("city"));
    }

}
