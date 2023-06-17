package cn.bdqn.shorturlchange.entity;

import lombok.*;

/**
 * 请求回应实体
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class R {
	private Integer code;
	private String msg;
	private Object data;

	public R(Integer code, String msg) {
		this.code = code;
		this.msg = msg;
	}

	public static R ok(String msg, Object data) {
		return new R(200, msg, data);
	}

	public static R create(Integer code, String msg, Object data) {
		return new R(code, msg, data);
	}

	public static R create(Integer code, String msg) {
		return new R(code, msg);
	}
}
