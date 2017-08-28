package com.rh.core.util;

import org.junit.Test;
import com.rh.core.TestEnv;
import com.rh.core.comm.workday.WorkTime;
import static org.junit.Assert.*;

public class TestWorkTimeCal extends TestEnv {
    @Test
    public void testCalTime() {
        // 08:30:00,11:30:00,13:00:00,18:00:00
        
        WorkTime workTime = new WorkTime();
        String  xxx = workTime.addMinute("2013-03-20 07:30:00", 480 * 3 + 60);
        assertEquals( "2013-03-25 09:30:00", xxx);
        
        // 加一个小时  -----------------------
        
        //当天  
        xxx = workTime.addMinute("2013-03-20 07:30:00", 60);
        assertEquals( "2013-03-20 09:30:00", xxx);
        xxx = workTime.addMinute("2013-03-20 09:30:00", 60);
        assertEquals( "2013-03-20 10:30:00", xxx);
        xxx = workTime.addMinute("2013-03-20 11:00:00", 60);
        assertEquals( "2013-03-20 13:30:00", xxx);
        xxx = workTime.addMinute("2013-03-20 12:30:00", 60);
        assertEquals( "2013-03-20 14:00:00", xxx);
        xxx = workTime.addMinute("2013-03-20 11:30:00", 60);
        assertEquals( "2013-03-20 14:00:00", xxx);
        xxx = workTime.addMinute("2013-03-20 13:00:00", 60);
        assertEquals( "2013-03-20 14:00:00", xxx);
        xxx = workTime.addMinute("2013-03-20 14:30:00", 60);
        assertEquals( "2013-03-20 15:30:00", xxx);
        xxx = workTime.addMinute("2013-03-20 18:00:00", 60);
        assertEquals( "2013-03-21 09:30:00", xxx);
        xxx = workTime.addMinute("2013-03-20 19:00:00", 60);
        assertEquals( "2013-03-21 09:30:00", xxx);
        
        // 加三天 + 一个小时 --------------------
        xxx = workTime.addMinute("2013-03-20 07:30:00", 480 * 3 + 60);
        assertEquals( "2013-03-25 09:30:00", xxx);
        xxx = workTime.addMinute("2013-03-20 09:30:00", 480 * 3 + 60);
        assertEquals( "2013-03-25 10:30:00", xxx);
        xxx = workTime.addMinute("2013-03-20 11:00:00", 480 * 3 + 60);
        assertEquals( "2013-03-25 13:30:00", xxx);
        xxx = workTime.addMinute("2013-03-20 12:30:00", 480 * 3 + 60);
        assertEquals( "2013-03-25 14:00:00", xxx);
        xxx = workTime.addMinute("2013-03-20 11:30:00", 480 * 3 + 60);
        assertEquals( "2013-03-25 14:00:00", xxx);
        xxx = workTime.addMinute("2013-03-20 13:00:00", 480 * 3 + 60);
        assertEquals( "2013-03-25 14:00:00", xxx);
        xxx = workTime.addMinute("2013-03-20 14:30:00", 480 * 3 + 60);
        assertEquals( "2013-03-25 15:30:00", xxx);
        xxx = workTime.addMinute("2013-03-20 18:00:00", 480 * 3 + 60);
        assertEquals( "2013-03-26 09:30:00", xxx);
        xxx = workTime.addMinute("2013-03-20 19:00:00", 480 * 3 + 60);
        assertEquals( "2013-03-26 09:30:00", xxx);
        
        
        // 加七天 + 一个小时 --------------------
        xxx = workTime.addMinute("2013-03-20 07:30:00", 480 * 7 + 60);
        assertEquals( "2013-03-29 09:30:00", xxx);
        xxx = workTime.addMinute("2013-03-20 09:30:00", 480 * 7 + 60);
        assertEquals( "2013-03-29 10:30:00", xxx);
        xxx = workTime.addMinute("2013-03-20 11:00:00", 480 * 7 + 60);
        assertEquals( "2013-03-29 13:30:00", xxx);
        xxx = workTime.addMinute("2013-03-20 12:30:00", 480 * 7 + 60);
        assertEquals( "2013-03-29 14:00:00", xxx);
        xxx = workTime.addMinute("2013-03-20 11:30:00", 480 * 7 + 60);
        assertEquals( "2013-03-29 14:00:00", xxx);
        xxx = workTime.addMinute("2013-03-20 13:00:00", 480 * 7 + 60);
        assertEquals( "2013-03-29 14:00:00", xxx);
        xxx = workTime.addMinute("2013-03-20 14:30:00", 480 * 7 + 60);
        assertEquals( "2013-03-29 15:30:00", xxx);
        xxx = workTime.addMinute("2013-03-20 18:00:00", 480 * 7 + 60);
        assertEquals( "2013-04-01 09:30:00", xxx);
        xxx = workTime.addMinute("2013-03-20 19:00:00", 480 * 7 + 60);
        assertEquals( "2013-04-01 09:30:00", xxx);
        
        // 加十天 + 一个小时 --------------------
        xxx = workTime.addMinute("2013-03-20 07:30:00", 480 * 10 + 60);
        assertEquals( "2013-04-03 09:30:00", xxx);
        xxx = workTime.addMinute("2013-03-20 09:30:00", 480 * 10 + 60);
        assertEquals( "2013-04-03 10:30:00", xxx);
        xxx = workTime.addMinute("2013-03-20 11:00:00", 480 * 10 + 60);
        assertEquals( "2013-04-03 13:30:00", xxx);
        xxx = workTime.addMinute("2013-03-20 12:30:00", 480 * 10 + 60);
        assertEquals( "2013-04-03 14:00:00", xxx);
        xxx = workTime.addMinute("2013-03-20 11:30:00", 480 * 10 + 60);
        assertEquals( "2013-04-03 14:00:00", xxx);
        xxx = workTime.addMinute("2013-03-20 13:00:00", 480 * 10 + 60);
        assertEquals( "2013-04-03 14:00:00", xxx);
        xxx = workTime.addMinute("2013-03-20 14:30:00", 480 * 10 + 60);
        assertEquals( "2013-04-03 15:30:00", xxx);
        xxx = workTime.addMinute("2013-03-20 18:00:00", 480 * 10 + 60);
        assertEquals( "2013-04-04 09:30:00", xxx);
        xxx = workTime.addMinute("2013-03-20 19:00:00", 480 * 10 + 60);
        assertEquals( "2013-04-04 09:30:00", xxx);
        
        
        // 周末  + 一个小时 -----------------
        xxx = workTime.addMinute("2013-03-23 07:30:00", 60);
        assertEquals( "2013-03-25 09:30:00", xxx);
        xxx = workTime.addMinute("2013-03-23 09:30:00", 60);
        assertEquals( "2013-03-25 09:30:00", xxx);
        xxx = workTime.addMinute("2013-03-23 11:00:00", 60);
        assertEquals( "2013-03-25 09:30:00", xxx);
        xxx = workTime.addMinute("2013-03-23 12:30:00", 60);
        assertEquals( "2013-03-25 09:30:00", xxx);
        xxx = workTime.addMinute("2013-03-23 11:30:00", 60);
        assertEquals( "2013-03-25 09:30:00", xxx);
        xxx = workTime.addMinute("2013-03-23 13:00:00", 60);
        assertEquals( "2013-03-25 09:30:00", xxx);
        xxx = workTime.addMinute("2013-03-23 14:30:00", 60);
        assertEquals( "2013-03-25 09:30:00", xxx);
        xxx = workTime.addMinute("2013-03-23 18:00:00", 60);
        assertEquals( "2013-03-25 09:30:00", xxx);
        xxx = workTime.addMinute("2013-03-23 19:00:00", 60);
        assertEquals( "2013-03-25 09:30:00", xxx);
        
        // 周末  + 三天 + 一个小时 -----------------
        xxx = workTime.addMinute("2013-03-23 07:30:00", 480 * 3 + 60);
        assertEquals( "2013-03-28 09:30:00", xxx);
        xxx = workTime.addMinute("2013-03-23 09:30:00", 480 * 3 + 60);
        assertEquals( "2013-03-28 09:30:00", xxx);
        xxx = workTime.addMinute("2013-03-23 11:00:00", 480 * 3 + 60);
        assertEquals( "2013-03-28 09:30:00", xxx);
        xxx = workTime.addMinute("2013-03-23 12:30:00", 480 * 3 + 60);
        assertEquals( "2013-03-28 09:30:00", xxx);
        xxx = workTime.addMinute("2013-03-23 11:30:00", 480 * 3 + 60);
        assertEquals( "2013-03-28 09:30:00", xxx);
        xxx = workTime.addMinute("2013-03-23 13:00:00", 480 * 3 + 60);
        assertEquals( "2013-03-28 09:30:00", xxx);
        xxx = workTime.addMinute("2013-03-23 14:30:00", 480 * 3 + 60);
        assertEquals( "2013-03-28 09:30:00", xxx);
        xxx = workTime.addMinute("2013-03-23 18:00:00", 480 * 3 + 60);
        assertEquals( "2013-03-28 09:30:00", xxx);
        xxx = workTime.addMinute("2013-03-23 19:00:00", 480 * 3 + 60);
        assertEquals( "2013-03-28 09:30:00", xxx);
    }
}
