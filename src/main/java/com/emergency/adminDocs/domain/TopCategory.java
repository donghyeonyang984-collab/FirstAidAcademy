package com.emergency.adminDocs.domain;

public enum TopCategory {

    RESCUER("구조자"),   // 구조자
    SELF("자가");       // 자가

    private final String dbValue;

    TopCategory(String dbValue) {
        this.dbValue = dbValue;
    }

    // DB에 저장할 값
    public String getDbValue() {
        return dbValue;
    }

    // DB에서 읽어온 한글 값을 enum 으로 변환
    public static TopCategory fromDbValue(String dbValue) {
        for (TopCategory tc : values()) {
            if (tc.dbValue.equals(dbValue)) {
                return tc;
            }
        }
        throw new IllegalArgumentException("Unknown top_category: " + dbValue);
    }
}