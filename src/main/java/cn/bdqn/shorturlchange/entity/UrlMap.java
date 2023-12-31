package cn.bdqn.shorturlchange.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

/**
 * 链接本身的实体
 */
@NoArgsConstructor
@Getter
@Setter
@ToString
public class UrlMap {
	private Long id;
	private String surl;//短链接
	private String lurl;//长链接
	private Integer views;//访问次数
	private Date createTime;//创建时间

	public UrlMap(String surl, String lurl) {
		this.surl = surl;
		this.lurl = lurl;
		this.views = 0;
		this.createTime = new Date();
	}
}
