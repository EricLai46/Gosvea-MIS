package org.gosvea;

import org.gosvea.pojo.Icpie;
import org.gosvea.pojo.Venue;
import org.gosvea.service.EmailService;
import org.gosvea.service.IccpraService;
import org.gosvea.service.VenueService;
import org.gosvea.task.CourseCompletionTask;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

public class ScheduledTaskTest {

    @Mock
    private VenueService venueService;

    @Mock
    private IccpraService iccpraService;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private CourseCompletionTask scheduledTask; // 替换为包含 @Scheduled 方法的类的实际类名

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCheckIfNeedAddedAD() {
        // 创建模拟数据
        Venue venue = new Venue();
        venue.setIcpisManager("TestManager");
        venue.setVenueStatus(Venue.VenueStatus.INVESTIGATION);

        List<Venue> investigationVenueList = Arrays.asList(venue);
        when(venueService.getAllSpecStatusVenues(Venue.VenueStatus.INVESTIGATION)).thenReturn(investigationVenueList);

        Icpie icpie = new Icpie();
        icpie.setEmail("test@example.com");
        when(iccpraService.findByIcpieName("TestManager")).thenReturn(icpie);

        // 调用测试方法
        scheduledTask.CheckIfNeedAddedAD();

        // 验证 `venueService.getAllSpecStatusVenues` 被调用了一次
        verify(venueService, times(1)).getAllSpecStatusVenues(Venue.VenueStatus.INVESTIGATION);

        // 验证 `emailService.noticeIcpisManagerAddAD` 被调用并传递正确参数
        verify(emailService, times(1)).noticeIcpisManagerAddAD("test@example.com", venue);
    }
}