package com.emergency.adminDocs.domain;

public enum MidCategory {

    BLEEDING("출혈"),
    AIRWAY_OBSTRUCTION("기도막힘"),
    CARDIAC_ARREST("심정지"),
    BURN("화상");

    private final String dbValue;

    MidCategory(String dbValue) {
        this.dbValue = dbValue;
    }

    public String getDbValue() {
        return dbValue;
    }

    public static MidCategory fromDbValue(String dbValue) {
        for (MidCategory mc : values()) {
            if (mc.dbValue.equals(dbValue)) {
                return mc;
            }
        }
        throw new IllegalArgumentException("Unknown mid_category: " + dbValue);
    }
}