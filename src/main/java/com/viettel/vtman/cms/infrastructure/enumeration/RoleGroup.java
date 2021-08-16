package com.viettel.vtman.cms.infrastructure.enumeration;

//Quyền
public enum RoleGroup {

    GROUP_GD("Giám đốc"), // Giám đốc
    GROUP_TPB("Trưởng phòng"); // Trưởng phòng ban

    public final String label;

    private RoleGroup(String label) {
        this.label = label;
    }

}
