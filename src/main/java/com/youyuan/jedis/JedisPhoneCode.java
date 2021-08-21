package com.youyuan.jedis;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import redis.clients.jedis.Jedis;

import java.util.Date;
import java.util.Objects;
import java.util.Random;

/**
 * 类名称：JedisPhoneCode <br>
 * 类描述： 测试项目之手机验证码 <br>
 * <p>
 * 需求：
 * 1.验证码长度为6位随机数字  (Random实现)
 * 2.一个手机号一天最多发送3次验证码  (incr计数器)
 * 3.验证码有效期2分钟  (过期时间)
 *
 * @author zhangyu
 * @version 1.0.0
 * @date 创建时间：2021/8/17 10:52<br>
 */
public class JedisPhoneCode {
    /**
     * jedis对象
     */
    private static final Jedis jedis = new Jedis("192.168.1.22", 6379);

    /**
     * 短信下发次数redis的key的前缀  key规则:SEND_COUND_PREFIX+yyyyMMdd:+phone
     */
    private static final String SEND_COUND_PREFIX = "validate_send_";

    /**
     * 验证码redis的key的前缀  key规则:SEND_CODE_PREFIX+phone
     */
    private static final String SEND_CODE_PREFIX = "validate_code_";

    /**
     * 设置验证码过期时间
     */
    private static final Integer EXPIRE_TIME = 2 * 60;

    public static void main(String[] args) {
        String phone = "13222223399";
        String code = "916126";
        //下发验证码
//        sendMsg(phone, getCode(6));

        //验证验证码
        Boolean verifyCodeResult = verifyCode(phone, code);
        System.out.println("验证码验证结果:" + verifyCodeResult);
    }

    /**
     * 方法名: getCode <br>
     * 方法描述: 随机生成验证码 <br>
     *
     * @param codeLength 验证码长度
     * @return {@link String 返回指定位数字验证码 }
     * @date 创建时间: 2021/8/17 10:54 <br>
     * @author zhangyu
     */
    public static String getCode(Integer codeLength) {
        if (codeLength < 1) {
            System.out.println("验证码长度不合法");
            return null;
        }
        StringBuilder stringBuilder = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < codeLength; i++) {
            stringBuilder.append(random.nextInt(10));
        }
        return stringBuilder.toString();
    }

    /**
     * 方法名: sendMsg <br>
     * 方法描述: 下发验证码 <br>
     *
     * @param phone 手机号
     * @param code  验证码
     * @return {@link Boolean 返回下发结果 true成功 false失败 }
     * @date 创建时间: 2021/8/17 11:00 <br>
     * @author zhangyu
     */
    public static Boolean sendMsg(String phone, String code) {
        if (StrUtil.isBlank(phone) || StrUtil.isBlank(code)) {
            return Boolean.FALSE;
        }
        //1.判断下发次数是否超过3次
        String count = jedis.get(buildSendCountKey(phone));
        if (StrUtil.isBlank(count)) {
            //发送短信redis记录验证码且设置2分钟过期时间
            jedis.setex(buildCodeKey(phone), EXPIRE_TIME, code);
            //记录下发次数,下发次数key过期时间是当前时间距离当天23:59:59秒时间+随机60秒 (防止缓存雪崩)
            jedis.setex(buildSendCountKey(phone), calSendCountExpire(), "1");
        } else if (Integer.valueOf(count) < 3) {
            //发送短信redis记录验证码且设置2分钟过期时间
            jedis.setex(buildCodeKey(phone), EXPIRE_TIME, code);
            //下发次数+1
            jedis.incr(buildSendCountKey(phone));
        } else {
            System.out.println("短信下发次数已经超过3次");
            jedis.close();
        }
        return Boolean.TRUE;
    }

    /**
     * 方法名: verifyCode <br>
     * 方法描述: 验证验证码 <br>
     *
     * @param phone 手机号
     * @param code  验证码
     * @return {@link Boolean 返回验证结果 true是 false失败 }
     * @date 创建时间: 2021/8/17 11:40 <br>
     * @author zhangyu
     */
    public static Boolean verifyCode(String phone, String code) {
        if (StrUtil.isBlank(phone) || StrUtil.isBlank(code)) {
            return Boolean.FALSE;
        }
        return Objects.equals(jedis.get(buildCodeKey(phone)), code);
    }

    /**
     * 方法名: buildSendCountKey <br>
     * 方法描述: 构建下发短信次数redis中key <br>
     *
     * @param phone 手机号
     * @return {@link String 返回构建好的key }
     * @date 创建时间: 2021/8/17 11:08 <br>
     * @author zhangyu
     */
    private static String buildSendCountKey(String phone) {
        return SEND_COUND_PREFIX + DateUtil.format(new Date(), DatePattern.PURE_DATE_PATTERN) + ":" + phone;
    }

    /**
     * 方法名: buildCodeKey <br>
     * 方法描述: 构建验证码key <br>
     *
     * @param phone 手机号
     * @return {@link String 返回验证码key }
     * @date 创建时间: 2021/8/17 11:14 <br>
     * @author zhangyu
     */
    private static String buildCodeKey(String phone) {
        return SEND_CODE_PREFIX + phone;
    }

    /**
     * 方法名: calSendCountExpire <br>
     * 方法描述: 计算redis存储短信发送次数的过期时间 <br>
     *
     * @return {@link Integer 返回过期时间 }
     * @date 创建时间: 2021/8/17 11:28 <br>
     * @author zhangyu
     */
    private static Integer calSendCountExpire() {
        //计算当前时间距离当天23:59:59秒的秒数
        long expireSecond = DateUtil.between(new Date(), DateUtil.endOfDay(new Date()), DateUnit.SECOND);
        //随机数
        Random random = new Random();
        //用当前时间距离当天23:59:59秒的秒数+60秒随机数作为手机号发送次数key的过期时间,第一可以每天的key过期后自动删除 第二可以防止缓存雪崩
        return (int) expireSecond + random.nextInt(60);
    }
}
