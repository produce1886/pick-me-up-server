package com.produce.pickmeup.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCase {
    DATABASE_CONNECTION_ERROR("데이터베이스 등 인터넷 연결에 문제가 있습니다. "),

    INVALID_FIELD_ERROR("필수항목을 입력해주세요. "),
    INVALID_ACCESS_ERROR("잘못된 경로로 접근했습니다. "),
    INVALID_FILE_TYPE_ERROR("지원하지 않는 파일 형식입니다. "),

    FAIL_FILE_CONVERT_ERROR("파일 변환에 실패했습니다. "),
    FAIL_FILE_DELETE_ERROR("파일 삭제에 실패했습니다. "),
    EMPTY_FILE_ERROR("빈 파일은 업로드할 수 없습니다. "),

    NO_SUCH_USER_ERROR("존재하지 않는 계정입니다. "),
    NO_SUCH_PROJECT_ERROR("존재하지 않는 프로젝트입니다. "),
    NO_SUCH_PORTFOLIO_ERROR("존재하지 않는 포트폴리오입니다. "),
    NO_SUCH_IMAGE_ERROR("존재하지 않는 이미지입니다. "),
    NO_SUCH_COMMENT_ERROR("존재하지 않는 댓글입니다. "),

    FORBIDDEN_ERROR("권한이 없습니다. ");

    private final String message;
}
