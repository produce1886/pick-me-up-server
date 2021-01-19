package com.produce.pickmeup.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ErrorCase {
	public static final String DATABASE_CONNECTION_ERROR = "데이터베이스 등 인터넷 연결에 문제가 있습니다. ";
	public static final String INVALID_FIELD_ERROR = "필수항목을 입력해주세요. ";
	public static final String FAIL_FILE_SAVE_ERROR = "파일 저장에 실패했습니다. ";
	public static final String FAIL_FILE_CONVERT_ERROR = "파일 변환에 실패했습니다. ";
	public static final String FAIL_FILE_DELETE_ERROR = "파일 삭제에 실패했습니다. ";
	public static final String INVALID_FILE_TYPE = "지원하지 않는 파일 형식입니다. ";
	public static final String NO_SUCH_USER = "존재하지 않는 계정입니다. ";

	public static List<String> getInternalErrorList() {
		return Arrays.asList(
			DATABASE_CONNECTION_ERROR,
			FAIL_FILE_SAVE_ERROR,
			FAIL_FILE_CONVERT_ERROR,
			FAIL_FILE_DELETE_ERROR
		);
	}

	public static List<String> getRequestErrorList() {
		return Arrays.asList(
			INVALID_FIELD_ERROR,
			INVALID_FILE_TYPE,
			NO_SUCH_USER
		);
	}

	public static List<String> getAllErrorList() {
		List<String> allErrorList = new ArrayList<>(getInternalErrorList());
		allErrorList.addAll(getRequestErrorList());
		return allErrorList;
	}
}
