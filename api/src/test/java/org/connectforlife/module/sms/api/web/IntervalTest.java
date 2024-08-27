package org.connectforlife.module.sms.api.web;

import org.connectforlife.module.sms.builder.IntervalBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.connectforlife.module.sms.api.util.DateUtil;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(PowerMockRunner.class)
@PrepareForTest(DateUtil.class)
public class IntervalTest {
  private final Date testNow = new Date(1670248800L);

  @Before
  public void setupMock() throws Exception {
    PowerMockito.mockStatic(DateUtil.class);
    PowerMockito.when(DateUtil.class, "now", new Object[0]).thenReturn(testNow);
  }

  @Test
  public void intervalFunctionality() {
    Interval createIntervalUsingString = new Interval("", "");
    Interval createIntervalUsingDate = new Interval(new Date(0), DateUtil.now());
    assertThat(createIntervalUsingString.getFrom(), is(createIntervalUsingDate.getFrom()));
    assertThat(createIntervalUsingString.getTo(), is(createIntervalUsingDate.getTo()));
  }

  @Test
  public void setAndGetFunctionality() {
    Date from = createDate(2010, Calendar.NOVEMBER, 16, 15, 43, 59, "UTC");
    Date to = createDate(2011, Calendar.NOVEMBER, 17, 15, 43, 59, "UTC");
    Interval interval = new IntervalBuilder().build();
    interval.setFrom(from);
    interval.setTo(to);
    assertThat(interval.getFrom(), is(from));
    assertThat(interval.getTo(), is(to));
  }

  private Date createDate(
      int year, int month, int day, int hour, int minute, int second, String timezone) {
    Calendar calendar = Calendar.getInstance();
    calendar.set(year, month, day, hour, minute, second);
    calendar.set(Calendar.MILLISECOND, 0);
    calendar.setTimeZone(TimeZone.getTimeZone(timezone));
    return calendar.getTime();
  }
}
