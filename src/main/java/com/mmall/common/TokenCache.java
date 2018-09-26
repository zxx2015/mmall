package com.mmall.common;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Create by zhouxin on 2018/8/23
 **/
public  class  TokenCache {

    public static final String TOKEN_PREFIX = "token_";
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(TokenCache.class);

    //LRU算法
    private static LoadingCache<String,String> localCache = CacheBuilder.newBuilder().initialCapacity(1000).
            expireAfterAccess(12,TimeUnit.HOURS).maximumSize(10000).build(new CacheLoader<String, String>() {
        @Override
        //load方法是在get方法get不到值的时候会执行load方法。
        public String load(String s) throws Exception {
            return "null";
        }
    });

    public static void setKey(String key,String value){
        localCache.put(key,value);
    }

    public static String getKey(String key){
        String value = null;
        try {
            value = localCache.get(key);
        } catch (ExecutionException e) {
            logger.error("localCache get error",e);
            return null;
        }
        if("null".equals(value)){
            return null;
        }
        return value;
    }
}
