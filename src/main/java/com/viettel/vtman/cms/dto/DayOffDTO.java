package com.viettel.vtman.cms.dto;

import com.viettel.vtman.cms.entity.DayOff;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.type.LongType;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

@Data
@NoArgsConstructor
public class DayOffDTO {
    private String days;
    private String months;
    private Long dayOffId;
    private Date date;
    private Date createdDate;
    private Long createdBy;
    private Date updatedDate;
    private Long updatedBy;
    private String years;
    private Date fullYears;
    private String currentDatStr;
    private String dateTime;
    private String checkDayOff;
    private String dateStr;
    private Long dayOfWeek;
    private String dayName;

    public DayOffDTO(DayOff dayOff) {
        if (Objects.nonNull(dayOff)) {
            this.dayOffId = dayOff.getDayOffId();
            this.date = dayOff.getDate();
            this.createdDate = dayOff.getCreatedDate();
            this.createdBy = dayOff.getCreatedBy();
            this.updatedDate = dayOff.getUpdatedDate();
            this.updatedBy = dayOff.getUpdatedBy();
            this.dateStr = dayOff.getDateStr();
            Calendar cal = Calendar.getInstance();
            cal.setTime(this.date);
            this.days = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
            this.months = String.valueOf(cal.get(Calendar.MONTH));
            this.years = String.valueOf(cal.get(Calendar.YEAR));
            this.fullYears = this.date;
            this.dayOfWeek = (long) cal.get(Calendar.DAY_OF_WEEK);
        }
    }
}
