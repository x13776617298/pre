package com.babytree.apps.biz.father.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

/**
 * 绑定用户
 * 
 * @author pengxh
 * 
 */
public class BindUser extends BabytreeModel implements Parcelable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 绑定的妈妈的userId
	 */
	public String enc_user_id = "";

	/**
	 * 昵称
	 */
	public String nickname = "";

	/**
	 * 宝宝的名字
	 */
	public String baby_name = "";

	/**
	 * 注册邮箱
	 */
	public String email = "";

	/**
	 * 邮箱状态
	 */
	public String email_status = "";

	/**
	 * 头像地址
	 */
	public String avatar_url = "";

	/**
	 * 宝宝性别
	 */
	public String baby_sex = "";

	/**
	 * 宝宝年龄
	 */
	public String baby_age = "";

	/**
	 * 宝宝生日 单位/s
	 */
	public String baby_birthday = "";

	/**
	 * 关注总数
	 */
	public String follow_count = "";

	/**
	 * 粉丝总数
	 */
	public String fans_count = "";

	/**
	 * 所在位置编码
	 */
	public String location = "";

	/**
	 * 绑定用户的性别 0:妈妈 1:爸爸
	 */
	public String gender = "";

	/**
	 * 注册时间 单位/s
	 */
	public String reg_ts = "";

	/**
	 * 
	 */
	// TODO
	public String is_followed = "";

	/**
	 *  
	 */
	// TODO
	public String status = "";

	/**
	 * 位置信息名 例如北京 海淀
	 */
	public String location_name = "";

	/**
	 * 圈子编号
	 */
	public String group_id = "";

	/**
	 * 医院编号
	 */
	public String hospital_id = "";

	/**
	 * 医院名字
	 */
	public String hospital_name = "";

	/**
	 * 生日时间 单位/s
	 */
	public String baby_birthday_ts = "";

	/**
	 * 用户登录标识
	 */
	public String login_string = "";

	/**
	 * 总孕气值
	 */
	public String total_yunqi = "";
	
	public String image_url;
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(enc_user_id);
		dest.writeString(nickname);
		dest.writeString(baby_name);
		dest.writeString(email);
		dest.writeString(email_status);
		dest.writeString(avatar_url);
		dest.writeString(baby_sex);
		dest.writeString(baby_age);
		dest.writeString(baby_birthday);
		dest.writeString(follow_count);
		dest.writeString(fans_count);
		dest.writeString(location);
		dest.writeString(gender);
		dest.writeString(reg_ts);
		dest.writeString(is_followed);
		dest.writeString(status);
		dest.writeString(location_name);
		dest.writeString(group_id);
		dest.writeString(hospital_id);
		dest.writeString(hospital_name);
		dest.writeString(baby_birthday_ts);
		dest.writeString(login_string);
		dest.writeString(total_yunqi);
		dest.writeString(image_url);
	}

	@Override
	public int describeContents() {
		return 0;
	}
	
	public static final Creator<BindUser> CREATOR = new Creator<BindUser>() {
		@Override
		public BindUser createFromParcel(Parcel source) {
			BindUser bindUser = new BindUser();
			bindUser.enc_user_id = source.readString();
			bindUser.nickname = source.readString();
			bindUser.baby_name = source.readString();
			bindUser.email = source.readString();
			bindUser.email_status = source.readString();
			bindUser.avatar_url = source.readString();
			bindUser.baby_sex = source.readString();
			bindUser.baby_age = source.readString();
			bindUser.baby_birthday = source.readString();
			bindUser.follow_count = source.readString();
			bindUser.fans_count = source.readString();
			bindUser.location = source.readString();
			bindUser.gender = source.readString();
			bindUser.reg_ts = source.readString();
			bindUser.is_followed = source.readString();
			bindUser.status = source.readString();
			bindUser.location_name = source.readString();
			bindUser.group_id = source.readString();
			bindUser.hospital_id = source.readString();
			bindUser.hospital_name = source.readString();
			bindUser.baby_birthday_ts = source.readString();
			bindUser.login_string = source.readString();
			bindUser.total_yunqi = source.readString();
			bindUser.image_url = source.readString();
			return bindUser;
		}

		@Override
		public BindUser[] newArray(int size) {
			return new BindUser[size];
		}
		
	};
	
	@Override
	public String toString() {
		return "BindUser [enc_user_id=" + enc_user_id + ", nickname="
				+ nickname + ", baby_name=" + baby_name + ", email=" + email
				+ ", email_status=" + email_status + ", avatar_url="
				+ avatar_url + ", baby_sex=" + baby_sex + ", baby_age="
				+ baby_age + ", baby_birthday=" + baby_birthday
				+ ", follow_count=" + follow_count + ", fans_count="
				+ fans_count + ", location=" + location + ", gender=" + gender
				+ ", reg_ts=" + reg_ts + ", is_followed=" + is_followed
				+ ", status=" + status + ", location_name=" + location_name
				+ ", group_id=" + group_id + ", hospital_id=" + hospital_id
				+ ", hospital_name=" + hospital_name + ", baby_birthday_ts="
				+ baby_birthday_ts + ", login_string=" + login_string
				+ ", total_yunqi=" + total_yunqi + "]";
	}
	
}
