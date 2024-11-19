package org.gosvea;


import org.gosvea.pojo.CourseSchedule;
import org.gosvea.service.impl.CourseServiceImpl;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ADCycleCalculatorTest {

    @Test
    public void testCurrentDateInWhichADCycleDate() {
        // 创建 CourseServiceImpl 实例
        CourseServiceImpl courseService = new CourseServiceImpl();

        // 定义测试日期和预期结果
        LocalDate testDate = LocalDate.of(2024, 11, 15);
        LocalDate expectedCycleStartDate = LocalDate.of(2024, 11, 22 );

        // 调用方法并获取结果
        LocalDate actualCycleStartDate = courseService.currentDateInWhichADCycleDate(testDate);

        // 断言结果是否与预期一致
        assertEquals(expectedCycleStartDate, actualCycleStartDate);
    }

    @Test
    public void testcheckNeededToNoticeJurinToAddAd(){
        CourseServiceImpl courseService=new CourseServiceImpl();
        List<CourseSchedule> courseScheduleList=new ArrayList<>();
        CourseSchedule courseSchedule=new CourseSchedule();
        courseSchedule.setDate(LocalDate.of(2024,11,30));
        courseScheduleList.add(courseSchedule);
        courseService.checkNeededToNoticeJurinToAddAd(courseScheduleList);
    }

    @Test
    public void testisDateInCurrentADCyle(){
        CourseServiceImpl courseService=new CourseServiceImpl();


       boolean istrue= courseService.isDateInCurrentADCyle(LocalDate.of(2024,11,30));

        assertTrue(istrue);
    }


}
