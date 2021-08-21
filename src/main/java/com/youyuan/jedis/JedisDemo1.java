package com.youyuan.jedis;

import org.junit.jupiter.api.Test;
import redis.clients.jedis.GeoUnit;
import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 类名称：JedisDemo1 <br>
 * 类描述： jedis操作redis基础命令 <br>
 *
 * @author zhangyu
 * @version 1.0.0
 * @date 创建时间：2021/8/17 9:51<br>
 */
public class JedisDemo1 {

    private Jedis jedis = new Jedis("192.168.1.22", 6379);

    /**
     * 方法名: test1 <br>
     * 方法描述: 测试redis连通性 <br>
     *
     * @date 创建时间: 2021/8/17 9:53 <br>
     * @author zhangyu
     */
    @Test
    public void test1() {
        String pong = jedis.ping();
        System.out.println(pong);
    }

    /**
     * 方法名: test2 <br>
     * 方法描述: 操作String <br>
     *
     * @date 创建时间: 2021/8/17 9:54 <br>
     * @author zhangyu
     */
    @Test
    public void test2() {
        jedis.set("k1", "北京");
        String v1 = jedis.get("k1");
        System.out.println(v1);
    }

    /**
     * 方法名: test3 <br>
     * 方法描述: 操作List <br>
     *
     * @date 创建时间: 2021/8/17 9:55 <br>
     * @author zhangyu
     */
    @Test
    public void test3() {
        jedis.lpush("list", "北京", "上海", "广州");
        List<String> list = jedis.lrange("list", 0, -1);
        for (String s : list) {
            System.out.println(s);
        }
    }

    /**
     * 方法名: test5 <br>
     * 方法描述: 测试Set <br>
     *
     * @date 创建时间: 2021/8/17 9:57 <br>
     * @author zhangyu
     */
    @Test
    public void test5() {
        jedis.sadd("subwx", "北京", "上海", "广州", "北京", "西安", "成都");
        Set<String> subwx = jedis.smembers("subwx");
        for (String s : subwx) {
            System.out.println(s);
        }
    }

    /**
     * 方法名: test6 <br>
     * 方法描述: 测试Hash <br>
     *
     * @date 创建时间: 2021/8/17 10:02 <br>
     * @author zhangyu
     */
    @Test
    public void test6() {
        jedis.hset("user", "name", "王芮");
        jedis.hset("user", "sex", "女");
        jedis.hset("user", "age", "30");
        Map<String, String> map = jedis.hgetAll("user");
        System.out.println(map);
    }

    /**
     * 方法名: test7 <br>
     * 方法描述: 操作ZSet <br>
     *
     * @date 创建时间: 2021/8/17 10:04 <br>
     * @author zhangyu
     */
    @Test
    public void test7() {
        jedis.zadd("sortSist", 100d, "java");
        jedis.zadd("sortSist", 98d, "php");
        jedis.zadd("sortSist", 80d, "c");
        Set<String> sortSist = jedis.zrange("sortSist", 0, -1);
        System.out.println(sortSist);
    }

    /**
     * 方法名: test8 <br>
     * 方法描述: 操作Bitmaps <br>
     *
     * @date 创建时间: 2021/8/17 10:06 <br>
     * @author zhangyu
     */
    @Test
    public void test8() {
        jedis.setbit("bits", 2L, Boolean.TRUE);
        jedis.setbit("bits", 9L, Boolean.TRUE);
        jedis.setbit("bits", 12L, Boolean.TRUE);
        System.out.println(jedis.getbit("bits", 20L));
        System.out.println(jedis.getbit("bits", 9L));
    }

    /**
     * 方法名: test9 <br>
     * 方法描述: 操作HyperLogLog <br>
     *
     * @date 创建时间: 2021/8/17 10:09 <br>
     * @author zhangyu
     */
    @Test
    public void test9() {
        jedis.pfadd("hll", "redis", "mysql", "rocketmq", "spring", "kafka", "mysql");
        long hll = jedis.pfcount("hll");
        System.out.println(hll);
    }

    /**
     * 方法名: test10 <br>
     * 方法描述: 操作Key <br>
     *
     * @date 创建时间: 2021/8/17 10:11 <br>
     * @author zhangyu
     */
    @Test
    public void test10() {
        Set<String> keys = jedis.keys("*");
        for (String key : keys) {
            System.out.println(key);
        }
    }


    /**
     * 方法名: test12 <br>
     * 方法描述: 操作GeoSpatial <br>
     *
     * @date 创建时间: 2021/8/17 10:12 <br>
     * @author zhangyu
     */
    @Test
    public void test12() {
        jedis.geoadd("city", 121.47, 31.23, "上海");
        jedis.geoadd("city", 116.38, 39.90, "北京");
        //计算两个城市距离 km
        Double geodist = jedis.geodist("city", "北京", "上海", GeoUnit.KM);
        System.out.println("北京到上海距离:" + geodist + " 公里");
    }

}
