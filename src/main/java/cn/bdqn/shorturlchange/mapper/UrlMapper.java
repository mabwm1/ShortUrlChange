package cn.bdqn.shorturlchange.mapper;

import cn.bdqn.shorturlchange.entity.UrlMap;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface UrlMapper {

    @Select("select lurl from url_map where surl = #{surl}")
    String getLongUrlByShortUrl(String surl);

    @Insert("insert into url_map (surl, lurl, views, create_time) values (#{surl},#{lurl}, #{views}, #{createTime})")
    int saveUrlMap(UrlMap urlMap);

    @Update("update url_map set views=views + 1 where surl = #{surl}")
    int updateUrlViews(String surl);
}
