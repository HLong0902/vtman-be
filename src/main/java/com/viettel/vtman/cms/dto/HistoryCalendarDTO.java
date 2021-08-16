package com.viettel.vtman.cms.dto;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;
@Data
public class HistoryCalendarDTO {
    private Long historyCalendarId;
    private Long workCalendarId;
    private Date createDate;
    private Long status;
    private Date date;
    private String dateStr;

}
