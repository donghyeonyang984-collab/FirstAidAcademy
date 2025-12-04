package com.emergency.enrollment.service;

/**
 * 이미 동일 강의를 수강 중인 경우 발생시키는 예외
 *
 * 사용 목적
 *  - 비즈니스 로직에서 중복 수강신청을 깔끔하게 처리하기 위해 사용
 *  - 컨트롤러에서는 이 예외를 catch 해서 "이미 수강 중" 메시지를 보여준다.
 */
public class AlreadyEnrolledException extends RuntimeException {

    public AlreadyEnrolledException() {
        super();
    }

    public AlreadyEnrolledException(String message) {
        super(message);
    }
}
