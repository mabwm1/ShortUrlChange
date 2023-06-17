package cn.bdqn.shorturlchange.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public interface UrlService {
    /**
     * 从数据库根据短链接查询长链接
     * @param shortURL
     * @return
     */
    String getLongUrlByShortUrl(String shortURL);

    /**
     * 存储链接Map
     * @param shortURL
     * @param longURL
     * @param originalURL
     * @return
     */
    String saveUrlMap(String shortURL, String longURL, String originalURL);

    /**
     * 更新
     * @param shortURL
     */
    @Async
    void updateUrlViews(String shortURL);
}
