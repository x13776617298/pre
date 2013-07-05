package com.babytree.apps.biz.father.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 任务
 * 
 * @author pengxh
 * 
 */
public class Task extends BabytreeModel implements Comparable<Task>, Parcelable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 任务id
	 */
	public String task_id = "";

	/**
	 * 任务标题
	 */
	public String task_title = "";

	/**
	 * 任务内容 Html格式
	 */
	public String task_content = "";

	/**
	 * 任务 纯文本格式
	 */
	public String task_send_text = "";

	/**
	 * 任务的孕气值
	 */
	public String task_yunqi = "";

	/**
	 * 任务类型
	 */
	public String task_type = "";

	// 标示妈妈任务还是爸爸任务
	// public String task_role = "";
	
	public int task_status = 0;//0未完成，1完成
	
	public int send_status = 0;//0未发送，1已发送

	/**
	 * 绑定的妈妈用户(用于完成任务，存储总孕气值)
	 */
	public BindUser bindUser;

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(task_id);
		dest.writeString(task_title);
		dest.writeString(task_content);
		dest.writeString(task_send_text);
		dest.writeString(task_yunqi);
		dest.writeString(task_type);
		dest.writeInt(task_status);
		dest.writeInt(send_status);
		
		if (bindUser != null) {
			dest.writeInt(1);
			bindUser.writeToParcel(dest, flags);
		} else {
			dest.writeInt(0);
		}
	}

	@Override
	public int compareTo(Task another) {
		return 0;
	}
	
	public static final Creator<Task> CREATOR = new Creator<Task>() {
		@Override
		public Task createFromParcel(Parcel source) {
			Task task = new Task();
			task.task_id = source.readString();
			task.task_title = source.readString();
			task.task_content = source.readString();
			task.task_send_text = source.readString();
			task.task_yunqi = source.readString();
			task.task_type = source.readString();
			task.task_status = source.readInt();
			task.send_status = source.readInt();
			int hasTask = source.readInt();
			if (hasTask != 0) {
				task.bindUser = BindUser.CREATOR.createFromParcel(source);
			}
			return task;
		}

		@Override
		public Task[] newArray(int size) {
			return new Task[size];
		}
	};

	@Override
	public String toString() {
		return "Task [task_id=" + task_id + ", task_title=" + task_title
				+ ", task_content=" + task_content + ", task_send_text="
				+ task_send_text + ", task_yunqi=" + task_yunqi
				+ ", task_type=" + task_type + ", task_status=" + task_status
				+ ", send_status=" + send_status + ", bindUser=" + bindUser
				+ "]";
	}
	
	
}
