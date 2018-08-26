package com.leeup.common;


import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * @ClassName TokenCache
 * @Description Token缓存有效期
 * @Author 李闯
 * @Date 2018/8/19 14:51
 * @Version 1.0
 **/
public class TokenCache {

    public static final String TOKEN_PREFIX="token_";

    private static Logger logger = LoggerFactory.getLogger(TokenCache.class);

    //声明静态的内存块
    //initialCapacity设置缓存的初始化时间
    //maximumSize设置缓存的最大容量，当超过这个容量的时候，这个guava cache, 会使用LRU算法，也就是最小使用算法，来移除缓存项
    //设置成10000 因为他是调用链的模式，这两个方法是没有顺序而言的先set前面的值和后面的值都是可以的。
    //expireAfterAccess(12, TimeUnit.HOURS) 设置缓存的有效期为12小时
    //.build(CacheLoader),这个CacheLoader是一个抽象类，所以我们需要写一个匿名的实现，或者我们单独再写一个实现类，然后将实现类写在这里也可以
    private static LoadingCache<String,String> localCache = CacheBuilder.newBuilder().initialCapacity(1000).maximumSize(10000).expireAfterAccess(12, TimeUnit.HOURS)
            .build(new CacheLoader<String, String>() {
                //这个方法是默认的数据加载实现，当调用get取值的时候，如果key没有对应的值就调用这个方法加载
                @Override
                public String load(String key) throws Exception {
                    return "null";
                }
            });

    public static void setKey(String key,String value){
        localCache.put(key,value);
    }

    public static String getKey(String key){
        String value = null;
        try{
            value = localCache.get(key);
            if ("null".equals(value)){
                return null;
            }
            return value;
        }catch (Exception e){
            logger.error("localCache get error",e);
        }
        return null;
    }
}
