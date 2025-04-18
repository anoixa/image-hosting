package moe.imtop1.imagehosting.framework.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * spring utils 工具类
 * 
 *
 **/
@SuppressWarnings(value = { "unchecked", "rawtypes" })
@Component
public class RedisCache
{
    private static final Logger log = LoggerFactory.getLogger(RedisCache.class);

    @Autowired
    public RedisTemplate redisTemplate;

    /**
     * 缓存基本的对象，Integer、String、实体类等
     *
     * @param key 缓存的键值
     * @param value 缓存的值
     * @return 缓存的对象
     */
    public <T> ValueOperations<String, T> setCacheObject(String key, T value)
    {
        ValueOperations<String, T> operation = redisTemplate.opsForValue();
        operation.set(key, value);
        return operation;
    }

    /**
     * 缓存基本的对象，Integer、String、实体类等
     *
     * @param key 缓存的键值
     * @param value 缓存的值
     * @param timeout 时间
     * @param timeUnit 时间颗粒度
     * @return 缓存的对象
     */
    public <T> ValueOperations<String, T> setCacheObject(String key, T value, Integer timeout, TimeUnit timeUnit)
    {
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
    public <T> T getCacheObject(String key)
    {
        ValueOperations<String, Object> operation = redisTemplate.opsForValue();
        Object value = operation.get(key);
        if (value == null) {
            return null;
        }
        try {
            return (T) value;
        } catch (ClassCastException e) {
            log.warn("类型转换失败，key: {}, 期望类型: {}, 实际类型: {}", 
                key, 
                value.getClass().getName(),
                e.getMessage());
            return null;
        }
    }

    /**
     * 删除单个对象
     *
     * @param key
     */
    public void deleteObject(String key)
    {
        redisTemplate.delete(key);
    }

    /**
     * 删除集合对象
     *
     * @param collection
     */
    public void deleteObject(Collection collection)
    {
        redisTemplate.delete(collection);
    }

    /**
     * 缓存List数据
     *
     * @param key 缓存的键值
     * @param dataList 待缓存的List数据
     * @return 缓存的对象
     */
    public <T> ListOperations<String, T> setCacheList(String key, List<T> dataList)
    {
        ListOperations listOperation = redisTemplate.opsForList();
        if (null != dataList)
        {
            int size = dataList.size();
            for (int i = 0; i < size; i++)
            {
                listOperation.leftPush(key, dataList.get(i));
            }
        }
        return listOperation;
    }

    /**
     * 获得缓存的list对象
     *
     * @param key 缓存的键值
     * @return 缓存键值对应的数据
     */
    public <T> List<T> getCacheList(String key)
    {
        List<T> dataList = new ArrayList<T>();
        ListOperations<String, T> listOperation = redisTemplate.opsForList();
        Long size = listOperation.size(key);

        for (int i = 0; i < size; i++)
        {
            dataList.add(listOperation.index(key, i));
        }
        return dataList;
    }

    /**
     * 缓存Set
     *
     * @param key 缓存键值
     * @param dataSet 缓存的数据
     * @return 缓存数据的对象
     */
    public <T> BoundSetOperations<String, T> setCacheSet(String key, Set<T> dataSet)
    {
        BoundSetOperations<String, T> setOperation = redisTemplate.boundSetOps(key);
        Iterator<T> it = dataSet.iterator();
        while (it.hasNext())
        {
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
    public <T> Set<T> getCacheSet(String key)
    {
        Set<T> dataSet = new HashSet<T>();
        BoundSetOperations<String, T> operation = redisTemplate.boundSetOps(key);
        dataSet = operation.members();
        return dataSet;
    }

    /**
     * 缓存Map
     *
     * @param key
     * @param dataMap
     * @return
     */
    public <T> HashOperations<String, String, T> setCacheMap(String key, Map<String, T> dataMap)
    {
        HashOperations hashOperations = redisTemplate.opsForHash();
        if (null != dataMap)
        {
            for (Map.Entry<String, T> entry : dataMap.entrySet())
            {
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
    public <T> Map<String, T> getCacheMap(String key)
    {
        Map<String, T> map = redisTemplate.opsForHash().entries(key);
        return map;
    }

    /**
     * 获得缓存的基本对象列表
     * 
     * @param pattern 字符串前缀
     * @return 对象列表
     */
    public Collection<String> keys(String pattern)
    {
        return redisTemplate.keys(pattern);
    }


    /**
     * 获取指定前缀的值
     *
     * @param prefix key前缀
     * @return
     */
    public <T> List<T> getPrefixKeyValue(String prefix) {
        List<T> values = new ArrayList<>();
        // 获取所有固定前缀的key
        Set<String> keys = redisTemplate.keys(prefix + "*");
        if (null != keys) {
            // 批量获取数据
            values = redisTemplate.opsForValue().multiGet(keys);
        }
        return values;
    }

    /**
     * 缓存 byte 数组
     *
     * @param key   缓存的键值
     * @param value 缓存的 byte 数组
     */
    public void setCacheBytes(String key, byte[] value) {
        ValueOperations<String, Object> operation = redisTemplate.opsForValue();
        operation.set(key, value);
    }

    /**
     * 缓存 byte 数组并设置过期时间
     *
     * @param key      缓存的键
     * @param value    缓存的 byte 数组
     * @param timeout  过期时间
     * @param timeUnit 时间单位
     */
    public void setCacheBytes(String key, byte[] value, long timeout, TimeUnit timeUnit) {
        ValueOperations<String, Object> operation = redisTemplate.opsForValue();
        operation.set(key, value, timeout, timeUnit);
    }

    /**
     * 获取 byte 数组缓存
     *
     * @param key 缓存的键值
     * @return 缓存的 byte 数组
     */
    public byte[] getCacheBytes(String key) {
        ValueOperations<String, Object> operation = redisTemplate.opsForValue();
        Object value = operation.get(key);
        if (value == null) {
            return null;
        }
        if (value instanceof byte[]) {
            return (byte[]) value;
        }
        if (value instanceof String) {
            return ((String) value).getBytes(StandardCharsets.UTF_8);
        }
        log.warn("无法将类型 {} 转换为 byte[]", value.getClass().getName());
        return null;
    }
}
