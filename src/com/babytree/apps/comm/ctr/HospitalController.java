package com.babytree.apps.comm.ctr;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import com.babytree.apps.biz.home.model.Banner;
import com.babytree.apps.comm.config.InterfaceConstants;
import com.babytree.apps.comm.config.UrlConstrants;
import com.babytree.apps.comm.model.AddHospitalInfo;
import com.babytree.apps.comm.model.Discuz;
import com.babytree.apps.comm.model.Doctor;
import com.babytree.apps.comm.model.Hospital;
import com.babytree.apps.comm.model.HospitalMother;
import com.babytree.apps.comm.model.SortedHospital;
import com.babytree.apps.comm.model.UserCountInfo;
import com.babytree.apps.comm.net.BabytreeHttp;
import com.babytree.apps.comm.util.DataResult;
import com.babytree.apps.comm.util.ExceptionUtil;

public class HospitalController extends BaseController {
	// =============================================================

	// =============================================================
	private static final String SUCCESS_STATUS = "success";
	private static final String FAILED_STATUS = "failed";

	// =============================================================

	// 获取医院列表or选择地区获取医院信息
	public static DataResult getListByRegion(String hospitalId, String provinceName, String cityName) {
		DataResult result = new DataResult();
		ArrayList<Hospital> data = new ArrayList<Hospital>();
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		if (hospitalId != null)
			params.add(new BasicNameValuePair("hospital_id", hospitalId));
		if (provinceName != null)
			params.add(new BasicNameValuePair("province_name", provinceName));
		if (cityName != null)
			params.add(new BasicNameValuePair("city_name", cityName));
		String jsonString = null;
		try {
			jsonString = BabytreeHttp.post(UrlConstrants.hospital_get_list_by_region, params);
			JSONObject jsonObject = new JSONObject(jsonString);
			if (jsonObject.has(STATUS)) {
				String status = jsonObject.getString(STATUS);
				if (status.equals(SUCCESS_STATUS)) {
					result.status = SUCCESS_CODE;
					if (jsonObject.has(TOTAL)) {
						result.totalSize = Integer.parseInt(jsonObject.getString(TOTAL));
					} else {
						result.totalSize = Integer.MAX_VALUE;
					}
					if (jsonObject.has("data")) {
						JSONObject object = jsonObject.getJSONObject("data");
						JSONArray array = object.getJSONArray("list");
						for (int i = 0; i < array.length(); i++) {
							Hospital hospital = new Hospital();
							hospital = Hospital.parse(array.getJSONObject(i));
							data.add(hospital);
						}
						result.data = data;
					}
				}
			}
		} catch (Exception e) {
			return ExceptionUtil.switchException(result, e, params, jsonString);
		}

		return result;
	}

	// 获取热门医院及分区医院列表
	public static DataResult getSortedListByRegion(String cityName) {
		DataResult result = new DataResult();
		ArrayList<SortedHospital> data = new ArrayList<SortedHospital>();
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("city_name", cityName));
		String jsonString = null;
		try {
			jsonString = BabytreeHttp.post(UrlConstrants.get_sorted_list_by_region, params);
			JSONObject jsonObject = new JSONObject(jsonString);
			if (jsonObject.has(STATUS)) {
				String status = jsonObject.getString(STATUS);
				if (status.equals(SUCCESS_STATUS)) {
					result.status = SUCCESS_CODE;
					if (jsonObject.has("data")) {
						JSONObject object = jsonObject.getJSONObject("data");
						JSONArray array = object.getJSONArray("list");
						for (int i = 0; i < array.length(); i++) {
							JSONObject objects = array.getJSONObject(i);
							JSONArray arrays = objects.getJSONArray("list");
							for (int j = 0; j < arrays.length(); j++) {
								SortedHospital sortHospital = new SortedHospital();
								if (objects.has("name")) {
									sortHospital.setSortName(objects.getString("name"));
								}
								Hospital hospital = new Hospital();
								hospital = Hospital.parse(arrays.getJSONObject(j));
								sortHospital.setHospital(hospital);
								data.add(sortHospital);
							}
						}
						result.data = data;
					}
				}
			}
		} catch (Exception e) {
			return ExceptionUtil.switchException(result, e, params, jsonString);
		}

		return result;
	}

	// 医院交流圈帖子列表
	public static DataResult getDiscuzList(String hospitalId, String isElite, int page) {
		DataResult result = new DataResult();
		ArrayList<Discuz> data = new ArrayList<Discuz>();
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(0, new BasicNameValuePair(ACTION, InterfaceConstants.GET_DISCUZ_LIST));
		params.add(new BasicNameValuePair(GROUP_ID, hospitalId));
		params.add(new BasicNameValuePair(IS_ELITE, isElite));
		params.add(new BasicNameValuePair(PAGE, String.valueOf(page)));
		String jsonString = null;
		try {
			jsonString = BabytreeHttp.get(UrlConstrants.NET_URL, params);
			JSONObject jsonObject = new JSONObject(jsonString);
			if (jsonObject.has(STATUS)) {
				int status = jsonObject.getInt(STATUS);
				result.status = status;
				if (status == SUCCESS_CODE) {
					if (jsonObject.has(TOTAL)) {
						result.totalSize = Integer.parseInt(jsonObject.getString(TOTAL));
					} else {
						result.totalSize = Integer.MAX_VALUE;
					}

					if (jsonObject.has(LIST)) {
						JSONArray jsonArray = jsonObject.getJSONArray(LIST);
						for (int i = 0; i < jsonArray.length(); i++) {
							JSONObject object = jsonArray.getJSONObject(i);
							Discuz discuz = Discuz.parse(object);
							data.add(discuz);
						}
						result.data = data;
					}

				}
			}
		} catch (Exception e) {
			return ExceptionUtil.switchException(result, e, params, jsonString);
		}

		return result;
	}

	// 医院搜索
	public static DataResult search(String key, String provinceId, String cityId) {
		DataResult result = new DataResult();
		ArrayList<Hospital> data = new ArrayList<Hospital>();
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("key", key));
		if (provinceId != null) {
			params.add(new BasicNameValuePair("province_id", provinceId));
		}
		if (cityId != null) {
			params.add(new BasicNameValuePair("city_id", cityId));
		}
		params.add(new BasicNameValuePair("start", "0"));
		params.add(new BasicNameValuePair("limit", "70"));
		String jsonString = null;
		try {
			jsonString = BabytreeHttp.post(UrlConstrants.hospital_search, params);
			JSONObject jsonObject = new JSONObject(jsonString);
			if (jsonObject.has(STATUS)) {
				String status = jsonObject.getString(STATUS);
				if (status.equals(SUCCESS_STATUS)) {
					result.status = SUCCESS_CODE;
					if (jsonObject.has(TOTAL)) {
						result.totalSize = Integer.parseInt(jsonObject.getString(TOTAL));
					} else {
						result.totalSize = Integer.MAX_VALUE;
					}
					if (jsonObject.has("data")) {
						JSONObject object = jsonObject.getJSONObject("data");
						JSONArray array = object.getJSONArray("list");
						for (int i = 0; i < array.length(); i++) {
							Hospital hospital = Hospital.parse(array.getJSONObject(i));
							data.add(hospital);
						}
						result.data = data;
					}
				}
			}
		} catch (Exception e) {
			return ExceptionUtil.switchException(result, e, params, jsonString);
		}

		return result;
	}

	// 医生列表
	public static DataResult getDoctorList(String hospitalId) {
		DataResult result = new DataResult();
		ArrayList<Doctor> data = new ArrayList<Doctor>();
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("hospital_id", hospitalId));
		String jsonString = null;
		try {
			jsonString = BabytreeHttp.get(UrlConstrants.hospital_get_doctor_list, params);
			JSONObject jsonObject = new JSONObject(jsonString);
			if (jsonObject.has(STATUS)) {
				String status = jsonObject.getString(STATUS);
				if (status.equals(SUCCESS_STATUS)) {
					result.status = SUCCESS_CODE;
					if (jsonObject.has("data")) {
						String json = jsonObject.getString("data");
						JSONObject jObject = new JSONObject(json);
						if (jObject.has(TOTAL)) {
							result.totalSize = Integer.parseInt(jObject.getString(TOTAL));
						} else {
							result.totalSize = Integer.MAX_VALUE;
						}
						if (jObject.has(LIST)) {
							JSONArray jsArray = jObject.getJSONArray(LIST);
							for (int i = 0; i < jsArray.length(); i++) {
								JSONObject object = jsArray.getJSONObject(i);
								Doctor doctor = Doctor.parse(object);
								data.add(doctor);
							}
							result.data = data;
						}
					}
				}
			}
		} catch (Exception e) {
			return ExceptionUtil.switchException(result, e, params, jsonString);
		}

		return result;
	}

	// 医生帖子列表
	public static DataResult getDoctorDiscuzList(int page, String doctorTag, String groupId) {
		DataResult result = new DataResult();
		ArrayList<Discuz> data = new ArrayList<Discuz>();
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("doctor", doctorTag));
		params.add(new BasicNameValuePair(GROUP_ID, groupId));
		params.add(new BasicNameValuePair(PAGE, String.valueOf(page)));
		String jsonString = null;
		try {
			jsonString = BabytreeHttp.post(UrlConstrants.hospital_get_doctor_discuz_list, params);
			JSONObject jsonObject = new JSONObject(jsonString);
			if (jsonObject.has(STATUS)) {
				String status = jsonObject.getString(STATUS);
				if (status.equals(SUCCESS_STATUS)) {
					result.status = SUCCESS_CODE;
					if (jsonObject.has("data")) {
						String json = jsonObject.getString("data");
						JSONObject jObject = new JSONObject(json);
						if (jObject.has(TOTAL)) {
							result.totalSize = Integer.parseInt(jObject.getString(TOTAL));
						} else {
							result.totalSize = Integer.MAX_VALUE;
						}
						if (jObject.has(LIST)) {
							JSONArray jsArray = jObject.getJSONArray(LIST);
							for (int i = 0; i < jsArray.length(); i++) {
								JSONObject object = jsArray.getJSONObject(i);
								Discuz discuz = Discuz.parse(object);
								data.add(discuz);
							}
							result.data = data;
						}
					}
				}
			}
		} catch (Exception e) {
			return ExceptionUtil.switchException(result, e, params, jsonString);
		}

		return result;
	}

	// 同医院孕妈列表
	public static DataResult getUserList(String hospitalId, int pageNo) {
		DataResult result = new DataResult();
		ArrayList<HospitalMother> data = new ArrayList<HospitalMother>();
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("hospital_id", hospitalId));
		params.add(new BasicNameValuePair(START, String.valueOf(((pageNo - 1) * PAGE_SIZE))));
		params.add(new BasicNameValuePair(LIMIT, String.valueOf(PAGE_SIZE)));
		String jsonString = null;
		try {
			jsonString = BabytreeHttp.get(UrlConstrants.hospital_get_user_list, params);
			JSONObject jsonObject = new JSONObject(jsonString);
			if (jsonObject.has(STATUS)) {
				String status = jsonObject.getString(STATUS);
				if (status.equals(SUCCESS_STATUS)) {
					result.status = SUCCESS_CODE;
					if (jsonObject.has("data")) {
						String json = jsonObject.getString("data");
						JSONObject jObject = new JSONObject(json);
						if (jObject.has(TOTAL)) {
							result.totalSize = Integer.parseInt(jObject.getString(TOTAL));
						} else {
							result.totalSize = Integer.MAX_VALUE;
						}
						if (jObject.has(LIST)) {
							JSONArray jsArray = jObject.getJSONArray(LIST);
							for (int i = 0; i < jsArray.length(); i++) {
								JSONObject object = jsArray.getJSONObject(i);
								HospitalMother mother = HospitalMother.parse(object);
								data.add(mother);
							}
							result.data = data;
						}
					}
				}
			}
		} catch (Exception e) {
			return ExceptionUtil.switchException(result, e, params, jsonString);
		}
		return result;
	}

	// 查询医院ID
	public static DataResult getInfoByName() {
		DataResult result = new DataResult();
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		// params.add(new BasicNameValuePair("hospital_id", hospital_id));
		String jsonString = null;
		try {
			jsonString = BabytreeHttp.post(UrlConstrants.hospital_get_info, params);
			JSONObject jsonObject = new JSONObject(jsonString);
			if (jsonObject.has(STATUS)) {
				String status = jsonObject.getString(STATUS);
				if (status.equals(SUCCESS_STATUS)) {
					result.status = SUCCESS_CODE;
					if (jsonObject.has("data")) {
					}
				}
			}
		} catch (Exception e) {
			return ExceptionUtil.switchException(result, e, params, jsonString);
		}

		return result;
	}

	// 设置用户医院信息
	public static DataResult setHospital(String login_string, String hospitalId, String hospitalName, String cityId) {
		DataResult result = new DataResult();
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("login_string", login_string));
		if (hospitalId != null)
			params.add(new BasicNameValuePair("hospital_id", hospitalId));
		if (hospitalName != null)
			params.add(new BasicNameValuePair("hospital_name", hospitalName));
		params.add(new BasicNameValuePair("city_code", cityId));
		String jsonString = null;
		try {
			jsonString = BabytreeHttp.post(UrlConstrants.hospital_set_hospital, params);
			JSONObject jsonObject = new JSONObject(jsonString);
			if (jsonObject.has(STATUS)) {
				String status = jsonObject.getString(STATUS);
				if (status.equals(SUCCESS_STATUS)) {
					result.status = SUCCESS_CODE;
					if (jsonObject.has("data")) {
						JSONObject object = jsonObject.getJSONObject("data");
						AddHospitalInfo info = new AddHospitalInfo();
						info = AddHospitalInfo.parse(object);
						result.data = info;
					}

				}
				if (status.equals(FAILED_STATUS)) {
					result.status = FAILED_CODE;
				}
			}
		} catch (Exception e) {
			return ExceptionUtil.switchException(result, e, params, jsonString);
		}

		return result;
	}

	public static DataResult getInfo(String hospital_id) {
		DataResult result = new DataResult();
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("hospital_id", hospital_id));
		String jsonString = null;
		try {
			jsonString = BabytreeHttp.get(UrlConstrants.hospital_get_info, params);
			JSONObject jsonObject = new JSONObject(jsonString);
			if (jsonObject.has(STATUS)) {
				String status = jsonObject.getString(STATUS);
				if (status.equals(SUCCESS_STATUS)) {
					result.status = SUCCESS_CODE;
					if (jsonObject.has("data")) {
						JSONObject jsonBean = jsonObject.getJSONObject("data");
						Hospital data = Hospital.parse(jsonBean);
						result.data = data;
					}
				}
			}
		} catch (Exception e) {
			return ExceptionUtil.switchException(result, e, params, jsonString);
		}
		return result;
	}

	public static DataResult getUserCount(String login_str, String hospital_id) {
		DataResult result = new DataResult();
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		if (login_str != null)
			params.add(new BasicNameValuePair("login_string", login_str));
		params.add(new BasicNameValuePair("hospital_id", hospital_id));
		String jsonString = null;
		try {
			jsonString = BabytreeHttp.get(UrlConstrants.get_user_count, params);
			JSONObject jsonObject = new JSONObject(jsonString);
			if (jsonObject.has(STATUS)) {
				String status = jsonObject.getString(STATUS);
				if (status.equals(SUCCESS_STATUS)) {
					result.status = SUCCESS_CODE;
					if (jsonObject.has("data")) {
						JSONObject jsonBean = jsonObject.getJSONObject("data");
						UserCountInfo data = UserCountInfo.parse(jsonBean);
						result.data = data;
					}
				}
			}
		} catch (Exception e) {
			return ExceptionUtil.switchException(result, e, params, jsonString);
		}

		return result;
	}
}
