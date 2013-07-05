package com.babytree.apps.biz.father.ui;

import java.util.HashMap;

import com.babytree.apps.biz.father.WelcomeFatherActivity;
import com.babytree.apps.biz.father.ctr.FatherController;
import com.babytree.apps.biz.father.model.BindBoth;
import com.babytree.apps.comm.config.ShareKeys;
import com.babytree.apps.comm.net.BabytreeAsyncTask;
import com.babytree.apps.comm.util.DataResult;
import com.babytree.apps.comm.util.SharedPreferencesUtil;
import com.babytree.apps.pregnancy.R;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

/**
 * 绑定爸爸版
 * 
 * @author gaierlin
 */
public class UnaccreditedFatherFragment extends Fragment implements OnClickListener {
	public static final String CALSS_NAME = UnaccreditedFatherFragment.class.getSimpleName();
	private static final String TAG = CALSS_NAME;
	private boolean isCommit = false;// 是否正在提交
	private View mFragmentRoot;
	private EditText mEditCode;
	private Button mSure;
	private ListItemBar mItemBar;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mFragmentRoot = inflater.inflate(R.layout.unaccredited_father_fragment, null);
		mEditCode = (EditText) mFragmentRoot.findViewById(R.id.code_edit);
		mEditCode.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					commit();
					return true;
				}
				return false;
			}
		});

		mSure = (Button) mFragmentRoot.findViewById(R.id.sure);
		mSure.setOnClickListener(this);

		mItemBar = (ListItemBar) mFragmentRoot.findViewById(R.id.father_tip);
		mItemBar.setListItemLeftImage(R.drawable.undone);
		mItemBar.setItemTitleColor(0xff8c8c8c);
		mItemBar.setItemTitle(getString(R.string.input_code));
		mItemBar.setItemBarBg(R.drawable.unaccredite_tip_but_selector);
		mItemBar.setRightImageVisible(View.GONE);
		mItemBar.setOnClickListener(this);

		return mFragmentRoot;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}

	@Override
	public void onClick(View v) {
		int viewId = v.getId();
		switch (viewId) {
		case R.id.sure:
			commit();
			break;
		case R.id.father_tip:
			inputMethod(true);
			break;
		}
	}

	/**
	 * 提交绑定code，并隐藏键盘，保存配置信息
	 */
	private void commit() {
		final String code = mEditCode.getText().toString();
		if (TextUtils.isEmpty(code)) {
			Toast.makeText(getActivity(), R.string.code_empty, Toast.LENGTH_SHORT).show();
			return;
		}

		if (isCommit)
			return;

		inputMethod(false);

		new CommitCode(getActivity()).execute(code);
	}

	/**
	 * 隐藏与展示键盘
	 * 
	 * @param isShow
	 */
	public void inputMethod(boolean isShow) {
		if (isShow) {
			((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(
					mEditCode, 0);
		} else {
			((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE))
					.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	/**
	 * 提交Code授权
	 * 
	 * @author gaierlin
	 */
	class CommitCode extends BabytreeAsyncTask {
		private String json = null;
		private String code = null;

		public CommitCode(Context context) {
			super(context);
		}

		@Override
		protected DataResult toNet(String[] params) {
			isCommit = true;
			code = params[0];
			json = FatherController.bind(code);
			return FatherController.parseBindBoth(json);
		}

		@Override
		protected void success(DataResult result) {
			isCommit = false;
			if (result.status == 0 && result.data != null) {// 成功
				BindBoth both = (BindBoth) result.data;
				Context context = getActivity();
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put(ShareKeys.FATHER_BIND_KEY, json);
				map.put(ShareKeys.LOGIN_STRING, both.bindUser.login_string);
				map.put(ShareKeys.MOM_ID_KEY, both.mom_user_id);
				map.put(ShareKeys.USER_ENCODE_ID, both.father_user_id);
				map.put(ShareKeys.MOM_NICK_NAME_KEY, both.bindUser.nickname);
				map.put(ShareKeys.INVITE_CODE_KEY, code);
				map.put(ShareKeys.BABY_BIRTHDAY_TS_KEY, both.bindUser.baby_birthday_ts);
				SharedPreferencesUtil.setValue(context, map);

				WelcomeFatherActivity fca = (WelcomeFatherActivity) getActivity();
				if (fca != null) {
					fca.refreshMenu();
					fca.switchFragment(new FatherHomeFragment(both));
				}
			}
		}

		@Override
		protected void failure(DataResult result) {
			isCommit = false;
			if ("father_edition_invalid_code".equals(result.error)) {
				Toast.makeText(getActivity(), "邀请码无效", Toast.LENGTH_SHORT).show();
			} else if ("nickname_blocked".equals(result.error)) {
				Toast.makeText(getActivity(), "准妈妈昵称里有敏感字符,导致暂时无法绑定,抱歉.", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(getActivity(), R.string.bind_error, Toast.LENGTH_SHORT).show();
			}

		}

		public String getLoginString() {
			return "传送中……";
		}

	}
}
