package com.guet.ARC.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.guet.ARC.domain.AccessRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;
import springfox.documentation.spring.web.json.Json;

import java.time.Duration;
import java.time.temporal.TemporalUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
public class RedisCacheUtil<T> {

    @Autowired
    public RedisTemplate redisTemplate;

    public <T> ValueOperations<String, T> setCacheObject(String key, T value, long timeout, TimeUnit timeUnit) {
        ValueOperations<String, T> operation = redisTemplate.opsForValue();
        operation.set(key, value, timeout, timeUnit);
        return operation;
    }

    /**
     * 获得缓存的基本对象。
     *
     * @param key 缓存键值
     * @return 缓存键值对应的数据
     */
    public <T> T getCacheObject(String key) {
        ValueOperations<String, T> operation = redisTemplate.opsForValue();
        return operation.get(key);
    }

    public void incrementIntegerObject(String key) {
        redisTemplate.opsForValue().increment(key);
    }

    /**
     * 删除单个对象
     *
     * @param key
     */
    public void deleteObject(String key) {
        redisTemplate.delete(key);
    }

    public void pushDataToCacheList(String key, T data, long timeout, TemporalUnit timeUnit) {
        String valueWithExpiration = "{\"data\":" + JSON.toJSON(data) + ",\"expire\":"
                + (System.currentTimeMillis() + Duration.of(timeout, timeUnit).toMillis()) + "}";
        redisTemplate.opsForList().leftPush(key, valueWithExpiration);
    }

    public void resetExpiration(String key, long timeout, TimeUnit timeUnit) {
        // 重置 key 的过期时间
        redisTemplate.expire(key, timeout, timeUnit);
    }

    public Boolean hasKey(String key) {
       return redisTemplate.hasKey(key);
    }

    public void removeAccessRecordFromList(String key, AccessRecord accessRecord) {
        ListOperations<String, String> listOperation = redisTemplate.opsForList();
        for (int i = 0; i < Integer.parseInt(listOperation.size(key) + ""); i++) {
            String jsonData = listOperation.index(key, i);
            JSONObject jsonObject = JSON.parseObject(jsonData);
            if (Objects.nonNull(jsonObject)) {
                AccessRecord record = jsonObject.getObject("data", AccessRecord.class);
                if (accessRecord.getId().equals(record.getId())) {
                    listOperation.remove(key, 0, jsonData);
                    break;
                }
            }
        }
    }


    /**
     * 获得缓存的list对象
     *
     * @param key 缓存的键值
     * @return 缓存键值对应的数据
     */
    public List<AccessRecord> getCachedAccessRecordList(String key) {
        List<AccessRecord> dataList = new ArrayList<>();
        Long now = System.currentTimeMillis();
        ListOperations<String, String> listOperation = redisTemplate.opsForList();
        for (int i = 0; i < Integer.parseInt(listOperation.size(key) + ""); i++) {
            String jsonData = listOperation.index(key, i);
            JSONObject jsonObject = JSON.parseObject(jsonData);
            if (Objects.nonNull(jsonObject)) {
                AccessRecord accessRecord = jsonObject.getObject("data", AccessRecord.class);
                Long timeoutTimeStamp = jsonObject.getLong("expire");
                if (now.compareTo(timeoutTimeStamp) < 0) {
                    dataList.add(accessRecord);
                } else {
                    // 超时的数据从列表中删除
                    listOperation.remove(key, 0, jsonData);
                }
            }

        }
        return dataList;
    }

    /**
     * 缓存Set
     *
     * @param key     缓存键值
     * @param dataSet 缓存的数据
     * @return 缓存数据的对象
     */
    public <T> BoundSetOperations<String, T> setCacheSet(String key, Set<T> dataSet) {
        BoundSetOperations<String, T> setOperation = redisTemplate.boundSetOps(key);
        Iterator<T> it = dataSet.iterator();
        while (it.hasNext()) {
            setOperation.add(it.next());
        }
        return setOperation;
    }

    /**
     * 获得缓存的set
     *
     * @param key
     * @return
     */
    public Set<T> getCacheSet(String key) {
        Set<T> dataSet = new HashSet<T>();
        BoundSetOperations<String, T> operation = redisTemplate.boundSetOps(key);
        Long size = operation.size();
        for (int i = 0; i < size; i++) {
            dataSet.add(operation.pop());
        }
        return dataSet;
    }

    /**
     * 缓存Map
     *
     * @param key
     * @param dataMap
     * @return
     */
    public <T> HashOperations<String, String, T> setCacheMap(String key, Map<String, T> dataMap) {

        HashOperations hashOperations = redisTemplate.opsForHash();
        if (null != dataMap) {
            for (Map.Entry<String, T> entry : dataMap.entrySet()) {
                hashOperations.put(key, entry.getKey(), entry.getValue());
            }
        }
        return hashOperations;
    }

    /**
     * 获得缓存的Map
     *
     * @param key
     * @return
     */
    public <T> Map<String, T> getCacheMap(String key) {
        Map<String, T> map = redisTemplate.opsForHash().entries(key);
        return map;
    }


    /**
     * 缓存Map
     *
     * @param key
     * @param dataMap
     * @return
     */
    public <T> HashOperations<String, Integer, T> setCacheIntegerMap(String key, Map<Integer, T> dataMap) {
        HashOperations hashOperations = redisTemplate.opsForHash();
        if (null != dataMap) {
            for (Map.Entry<Integer, T> entry : dataMap.entrySet()) {
                hashOperations.put(key, entry.getKey(), entry.getValue());
            }
        }
        return hashOperations;
    }

    /**
     * 获得缓存的Map
     *
     * @param key
     * @return
     */
    public <T> Map<Integer, T> getCacheIntegerMap(String key) {
        Map<Integer, T> map = redisTemplate.opsForHash().entries(key);
        return map;
    }

    public Long getExpire(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }
}
