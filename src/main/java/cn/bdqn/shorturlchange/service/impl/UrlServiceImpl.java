package cn.bdqn.shorturlchange.service.impl;

import cn.bdqn.shorturlchange.entity.UrlMap;
import cn.bdqn.shorturlchange.mapper.UrlMapper;
import cn.bdqn.shorturlchange.service.UrlService;
import cn.bdqn.shorturlchange.util.HashUtils;
import cn.hutool.bloomfilter.BitMapBloomFilter;
import cn.hutool.bloomfilter.BloomFilter;
import cn.hutool.bloomfilter.BloomFilterUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class UrlServiceImpl implements UrlService {
    @Autowired
    UrlMapper urlMapper;

    @Autowired
    StringRedisTemplate redisTemplate;

    //自定义长链接以防止重复字符串
    private static final String DUPLICATE = "*";

    //查询Redis后延迟时间(分钟)
    private static final Long TIMOUT = 10L;

    //定义布隆过滤器
    private static final BitMapBloomFilter FILTER = BloomFilterUtil.createBitMap(10);

    /**
     * 根据短链接。查询长链接
     * @param shortURL
     * @return
     */
    @Override
    public String getLongUrlByShortUrl(String shortURL) {
        //先查询Redis
        String longURL =redisTemplate.opsForValue().get(shortURL);
        if(longURL != null){
            //在redis查询到的情况
            //TimeUnit.MINUTES设置时间粒度转换，延时
            redisTemplate.expire(shortURL,TIMOUT, TimeUnit.MINUTES);
            //返回长链接
            return longURL;
        }
        //如果在Redis没有查询到，则从数据库查询
        longURL = urlMapper.getLongUrlByShortUrl(shortURL);
        //查询到，存入Redis
        if(longURL != null){
            redisTemplate.opsForValue().set(shortURL,longURL,TIMOUT,TimeUnit.MINUTES);
        }
        return longURL;
    }

    /**
     * 转换链接之后对链接进行存储
     * @param shortURL
     * @param longURL
     * @param originalURL
     * @return
     */
    @Override
    public String saveUrlMap(String shortURL, String longURL, String originalURL) {
        //长链接长度为1时候，防止链接字符串会重复
        if(longURL.length() == 1){
            longURL += DUPLICATE;
            //在Map中算法储存
            shortURL = saveUrlMap(HashUtils.hashToBase62(longURL),longURL,originalURL);
        } else if (FILTER.contains(shortURL) ) {//长度非1时候,在Bloom过滤器查询是否存在
            //查询Redis缓存中是否存在
            String redisLongURL = redisTemplate.opsForValue().get(shortURL);
            //查询到，且originalURL和Redis中longURL不一致
            if(redisLongURL != null &&  originalURL.equals(redisLongURL)){
                //重置过期时间
                redisTemplate.expire(shortURL,TIMOUT,TimeUnit.MINUTES);
                return shortURL;
            }
            //没有缓存的情况，重新Hash
            longURL += DUPLICATE;
            shortURL = saveUrlMap(HashUtils.hashToBase62(longURL),longURL,originalURL);
        }else {
            try {
                //都没有查询到，存入数据库
                urlMapper.saveUrlMap(new UrlMap(shortURL,originalURL));
                //布隆过滤器添加
                FILTER.add(shortURL);
                //Redis缓存添加
                redisTemplate.opsForValue().set(shortURL,originalURL,TIMOUT,TimeUnit.MINUTES);
            }catch (Exception e){
                //抛出Key异常
                if(e instanceof DuplicateKeyException){
                    //数据库存在，但是Bloom过滤器存在异常误判，重新Hash
                    longURL += DUPLICATE;
                    shortURL = saveUrlMap(HashUtils.hashToBase62(longURL),longURL,originalURL);
                }else {
                    throw e;
                }
            }
        }
        return shortURL;
    }

    @Override
    public void updateUrlViews(String shortURL) {
        urlMapper.updateUrlViews(shortURL);
    }
}
