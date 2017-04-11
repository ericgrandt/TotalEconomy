package com.erigitic.commands;

/**
 * Created by Life4YourGames on 11.04.17.
 */
public class TEPermissions {

    public static final String JOB_INFO         = "totaleconomy.command.job.info";
    public static final String JOB_SET          = "totaleconomy.command.job.set";
    // Some PermissionManagers seem to set defaults like this:
    // (Due to a lack of time I was unable to verify this. But the suspicion is reasonable)
    // 'some.permission.one' == 'some.permission.one.*'
    //  - MarkL4YG
    public static final String JOB_SET_OTHERS   = "totaleconomy.command.job.setother";
}
