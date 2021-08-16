package com.viettel.vtman.cms.controller;

import com.viettel.vtman.cms.dto.DayOffDTO;
import com.viettel.vtman.cms.dto.EmployeeDto;
import com.viettel.vtman.cms.dto.HistoryCalendarDTO;
import com.viettel.vtman.cms.dto.ListDayInWeekDTO;
import com.viettel.vtman.cms.dto.WorkCalendarDTO;
import com.viettel.vtman.cms.entity.DayOff;
import com.viettel.vtman.cms.entity.HistoryCalendar;
import com.viettel.vtman.cms.entity.WorkCalendar;
import com.viettel.vtman.cms.message.CommonController;
import com.viettel.vtman.cms.message.Const;
import com.viettel.vtman.cms.service.DayOffService;
import com.viettel.vtman.cms.service.EmployeeService;
import com.viettel.vtman.cms.service.HistoryCalendarService;
import com.viettel.vtman.cms.service.WorkCalendarService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


@RestController
@RequestMapping("/api/workCalendar")
public class WorkCalendarController extends CommonController {
    private static final Logger LOGGER = LogManager.getLogger(WorkCalendarController.class);
    @Autowired
    private WorkCalendarService workCalendarService;
    @Autowired
    private DayOffService dayOffService;
    @Autowired
    private HistoryCalendarService historyCalendarService;
    @Autowired
    private EmployeeService employeeService;

    @GetMapping("/getCalendar")
    public ResponseEntity<?> getWorkCalendar(HttpServletRequest request, HttpServletResponse response) {

        HttpSession session = request.getSession();
        WorkCalendar result = new WorkCalendar();
        List<WorkCalendarDTO> listResultData = new ArrayList<>();
        List<WorkCalendarDTO> listId = new ArrayList<>();
        Map<String, Long> listCurrentValue = new HashMap<>();
        Calendar checkWeekInYears = Calendar.getInstance();
        checkWeekInYears.setTime(new Date());
        Date currentDate = new Date();
        List<DayOffDTO> listCheck = new ArrayList<>();
        List<DayOffDTO> list = new ArrayList<>();
        List<WorkCalendarDTO> listResult = new ArrayList<>();
        List<WorkCalendarDTO> listData = new ArrayList<>();
        /*List<Long> listCheckUpdate = checkSessionDayOffWeek(request, response);*/
        int weekOfYears = checkWeekInYears.get(Calendar.WEEK_OF_YEAR);
        int countSt = checkSatudayFirtMonth(currentDate);
        try {
            List<HistoryCalendarDTO> listUpdate = historyCalendarService.getHistoryCalendar();
            List<DayOffDTO> listDayOff;
            List<WorkCalendarDTO> listCheckExist = new ArrayList<>();
            List<WorkCalendarDTO> listNotExist = new ArrayList<>();
            List<WorkCalendarDTO> delList = new ArrayList<>();
            List<WorkCalendarDTO> listIdUpdate = new ArrayList<>();
            if (listUpdate.size() > 0) {
                for (HistoryCalendarDTO getIdUpdate : listUpdate) {
                    if (!"".equals(getIdUpdate.getWorkCalendarId()) && getIdUpdate.getWorkCalendarId() != null) {
                        WorkCalendarDTO dto = new WorkCalendarDTO();
                        dto.setWorkCalendarId(getIdUpdate.getWorkCalendarId());
                        listIdUpdate.add(dto);
                    }
                }
            }
            //lay all list o work calendar
            listData = workCalendarService.findAll();
            // lay ngay hien tai khi ng dung load trang
            ListDayInWeekDTO listDayInWeekDTO = checkinWeek(currentDate);
//            listDayOff = dayOffService.findAll();
            listDayOff = dayOffService.findAllInYear();
            for (DayOffDTO checkDayOffWeek : listDayOff) {
                if (checkDayOffWeek.getFullYears().compareTo(listDayInWeekDTO.getMinDate()) >= 0
                        && checkDayOffWeek.getFullYears().compareTo(listDayInWeekDTO.getMaxDate()) <= 0) {
                    listCheck.add(checkDayOffWeek);
                }
            }
            if (listCheck.size() > 0 && listUpdate.size() > 0) {
                list = getNameDaysOfWeek(listCheck);
                for (WorkCalendarDTO listAllDay : listData) {
                    for (DayOffDTO listDayOff1 : list) {
                        try {
                            if (listDayOff1.getCheckDayOff().toUpperCase().equals(listAllDay.getDays())) {
                                WorkCalendarDTO dto = new WorkCalendarDTO();
                                dto.setWorkCalendarId(listAllDay.getWorkCalendarId());
                                listId.add(dto);
                            }
                        } catch (Exception e) {
                            return toExceptionResult(e.getMessage(), Const.API_RESPONSE.RETURN_CODE_ERROR);
                        }
                    }
                }
                for (WorkCalendarDTO listIdDayOff : listId) {
                    for (WorkCalendarDTO bean : listData) {
                        if (bean.getWorkCalendarId().equals(listIdDayOff.getWorkCalendarId())) {
                            Long workCalendarId = bean.getWorkCalendarId();
                            WorkCalendar calendar = workCalendarService.findById(workCalendarId);
                            calendar.setStatus(3L);
                            result = workCalendarService.updateWorkCalendar(calendar);
                            break;
                        }
                    }
                }

                listData.removeAll(listId);
                listData.removeAll(listIdUpdate);
                for (WorkCalendarDTO bean : listData) {
                    if (bean.getDays().equals("SAT")) {

                        Long workCalendarId = bean.getWorkCalendarId();
                        WorkCalendar calendar = workCalendarService.findById(workCalendarId);
                        calendar.setStatus(2L);
                        result = workCalendarService.updateWorkCalendar(calendar);
                    } else if (bean.getDays().equals("SUN")) {

                        Long workCalendarId = bean.getWorkCalendarId();
                        WorkCalendar calendar = workCalendarService.findById(workCalendarId);
                        calendar.setStatus(3L);
                        result = workCalendarService.updateWorkCalendar(calendar);

                    } else {
                        Long workCalendarId = bean.getWorkCalendarId();
                        WorkCalendar calendar = workCalendarService.findById(workCalendarId);
                        calendar.setStatus(1L);
                        result = workCalendarService.updateWorkCalendar(calendar);
                    }

                }

            } else if (listCheck.size() == 0 && listUpdate.size() > 0) {
                listData.removeAll(listIdUpdate);
                for (WorkCalendarDTO bean : listData) {

                    if (bean.getDays().equals("SAT")) {
                        Long workCalendarId = bean.getWorkCalendarId();
                        WorkCalendar calendar = workCalendarService.findById(workCalendarId);
                        calendar.setStatus(2L);
                        result = workCalendarService.updateWorkCalendar(calendar);
                    } else if (bean.getDays().equals("SUN")) {
                        Long workCalendarId = bean.getWorkCalendarId();
                        WorkCalendar calendar = workCalendarService.findById(workCalendarId);
                        calendar.setStatus(3L);
                        result = workCalendarService.updateWorkCalendar(calendar);
                    } else {
                        Long workCalendarId = bean.getWorkCalendarId();
                        WorkCalendar calendar = workCalendarService.findById(workCalendarId);
                        calendar.setStatus(1L);
                        result = workCalendarService.updateWorkCalendar(calendar);
                    }
                }

            } else if (listCheck.size() == 0 && listUpdate.size() == 0) {
                for (WorkCalendarDTO bean : listData) {
                    if (bean.getDays().equals("SAT")) {
                        Long workCalendarId = bean.getWorkCalendarId();
                        WorkCalendar calendar = workCalendarService.findById(workCalendarId);
                        calendar.setStatus(2L);
                        result = workCalendarService.updateWorkCalendar(calendar);
                    } else if (bean.getDays().equals("SUN")) {
                        Long workCalendarId = bean.getWorkCalendarId();
                        WorkCalendar calendar = workCalendarService.findById(workCalendarId);
                        calendar.setStatus(3L);
                        result = workCalendarService.updateWorkCalendar(calendar);
                    } else {
                        Long workCalendarId = bean.getWorkCalendarId();
                        WorkCalendar calendar = workCalendarService.findById(workCalendarId);
                        calendar.setStatus(1L);
                        result = workCalendarService.updateWorkCalendar(calendar);
                    }
                }
            } else if (listCheck.size() > 0 && listUpdate.size() == 0) {
                list = getNameDaysOfWeek(listCheck);
                for (WorkCalendarDTO listAllDay : listData) {
                    for (DayOffDTO listDayOff1 : list) {
                        try {
                            if (listDayOff1.getCheckDayOff().toUpperCase().equals(listAllDay.getDays())) {
                                WorkCalendarDTO dto = new WorkCalendarDTO();
                                dto.setWorkCalendarId(listAllDay.getWorkCalendarId());
                                listId.add(dto);
                            }
                        } catch (Exception e) {
                            return toExceptionResult(e.getMessage(), Const.API_RESPONSE.RETURN_CODE_ERROR);
                        }
                    }
                }
                for (WorkCalendarDTO listIdDayOff : listId) {
                    for (WorkCalendarDTO bean : listData) {
                        if (bean.getWorkCalendarId().equals(listIdDayOff.getWorkCalendarId())) {
                            Long workCalendarId = bean.getWorkCalendarId();
                            WorkCalendar calendar = workCalendarService.findById(workCalendarId);
                            calendar.setStatus(3L);
                            result = workCalendarService.updateWorkCalendar(calendar);
                            break;
                        }
                    }
                }
                listData.removeAll(listId);
                for (WorkCalendarDTO bean : listData) {
                    if (bean.getDays().equals("SAT")) {
                        Long workCalendarId = bean.getWorkCalendarId();
                        WorkCalendar calendar = workCalendarService.findById(workCalendarId);
                        calendar.setStatus(2L);
                        result = workCalendarService.updateWorkCalendar(calendar);
                    } else if (bean.getDays().equals("SUN")) {
                        Long workCalendarId = bean.getWorkCalendarId();
                        WorkCalendar calendar = workCalendarService.findById(workCalendarId);
                        calendar.setStatus(3L);
                        result = workCalendarService.updateWorkCalendar(calendar);
                    } else {
                        Long workCalendarId = bean.getWorkCalendarId();
                        WorkCalendar calendar = workCalendarService.findById(workCalendarId);
                        calendar.setStatus(1L);
                        result = workCalendarService.updateWorkCalendar(calendar);
                    }
                }
            }

            listResultData = workCalendarService.findAll();

        } catch (Exception e) {
            LOGGER.error("load calendar error: " + e.getMessage());
            return toExceptionResult(e.getMessage(), Const.API_RESPONSE.RETURN_CODE_ERROR);
        }
        listResultData = workCalendarService.findAll();
        return toSuccessResult(listResultData);
    }

    public static boolean checkSession(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        Integer valueSessionWeek = (Integer) session.getAttribute("week");
        Integer valueSessionYears = (Integer) session.getAttribute("years");
        Boolean check = false;
        if (valueSessionWeek == null && valueSessionYears == null) {
            Calendar checkWeekInYears = Calendar.getInstance();
            checkWeekInYears.setTime(new Date());
            int weekOfYears = checkWeekInYears.get(Calendar.WEEK_OF_YEAR);
            int years = checkWeekInYears.get(Calendar.YEAR);
            session.setAttribute("week", weekOfYears);
            session.setAttribute("years", years);
            check = true;
        } else {
            Calendar checkWeekInYears = Calendar.getInstance();
            checkWeekInYears.setTime(new Date());
            int weekOfYears = checkWeekInYears.get(Calendar.WEEK_OF_YEAR);
            int years = checkWeekInYears.get(Calendar.YEAR);

            if (weekOfYears == valueSessionWeek && years == valueSessionYears) {
                check = true;
                session.setAttribute("week", weekOfYears);
                session.setAttribute("years", years);
            }
            if (weekOfYears == valueSessionWeek && years != valueSessionYears) {
                check = false;
                session.setAttribute("week", weekOfYears);
                session.setAttribute("years", years);
            }
        }
        return check;
    }

    public ListDayInWeekDTO checkinWeek(Date listCheck) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar min = Calendar.getInstance();
        Calendar max = Calendar.getInstance();
        Calendar currentDate = Calendar.getInstance();
        min.setTime(new Date());
        min.set(Calendar.HOUR_OF_DAY, 0);
        min.set(Calendar.MINUTE, 0);
        min.set(Calendar.SECOND, 0);
        min.set(Calendar.MILLISECOND, 0);
        max.setTime(new Date());
        max.set(Calendar.HOUR_OF_DAY, 23);
        max.set(Calendar.MINUTE, 59);
        max.set(Calendar.SECOND, 59);
        max.set(Calendar.MILLISECOND, 999);
        currentDate.setTime(listCheck);
        int dayOfWeek = currentDate.get(Calendar.DAY_OF_WEEK);
        Date minDate = null;
        Date maxDate = null;
        switch (dayOfWeek) {

            case 1:
                min.add(Calendar.DATE, -6);
                max.add(Calendar.DATE, 0);
                minDate = min.getTime();
                maxDate = max.getTime();
                break;
            case 2:
                min.add(Calendar.DATE, 0);
                max.add(Calendar.DATE, 6);
                minDate = min.getTime();
                maxDate = max.getTime();

                break;
            case 3:
                min.add(Calendar.DATE, -1);
                max.add(Calendar.DATE, 5);
                minDate = min.getTime();
                maxDate = max.getTime();
                break;
            case 4:
                min.add(Calendar.DATE, -2);
                max.add(Calendar.DATE, 4);
                minDate = min.getTime();
                maxDate = max.getTime();

                break;
            case 5:
                min.add(Calendar.DATE, -3);
                max.add(Calendar.DATE, 3);
                minDate = min.getTime();
                maxDate = max.getTime();
                break;
            case 6:
                min.add(Calendar.DATE, -4);
                max.add(Calendar.DATE, 2);
                minDate = min.getTime();
                maxDate = max.getTime();
                break;
            case 7:
                min.add(Calendar.DATE, -5);
                max.add(Calendar.DATE, 1);
                minDate = min.getTime();
                maxDate = max.getTime();
                break;
        }
        ListDayInWeekDTO listDayInWeekDTO = new ListDayInWeekDTO();
        listDayInWeekDTO.setMinDate(minDate);
        listDayInWeekDTO.setMaxDate(maxDate);
        return listDayInWeekDTO;
    }

    @GetMapping("/checkDay")
    public String checkDayGlobal(String dateEditCurrent) {
        String dayOfWeek = "";
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Calendar tmpCal = Calendar.getInstance();
        try {
            tmpCal.setTime(formatter.parse(dateEditCurrent));
            Integer check = tmpCal.get(Calendar.DAY_OF_WEEK);
            switch (check) {
                case 1:
                    dayOfWeek = "SUN";
                    break;
                case 2:
                    dayOfWeek = "MON";
                    break;
                case 3:
                    dayOfWeek = "TUE";
                    break;
                case 4:
                    dayOfWeek = "WED";
                    break;
                case 5:
                    dayOfWeek = "THU";
                    break;
                case 6:
                    dayOfWeek = "FRI";
                    break;

                case 7:
                    dayOfWeek = "SAT";
                    break;
            }
        } catch (ParseException e) {
            LOGGER.error("check day global calendar error: " + e.getMessage());
        }
        return dayOfWeek;
    }


    public List<DayOffDTO> getNameDaysOfWeek(List<DayOffDTO> offDTOList) {
        for (DayOffDTO bean : offDTOList) {
            String daysRs = bean.getFullYears().toString();
            SimpleDateFormat dateFormat = new SimpleDateFormat("E");
            Calendar c1 = Calendar.getInstance();
            c1.setTime(bean.getFullYears());
            String days = dateFormat.format(c1.getTime());
            bean.setCheckDayOff(days);
        }
        return offDTOList;
    }

    public Integer checkSatudayFirtMonth(Date date) {
        Calendar checkST = Calendar.getInstance();
        checkST.setTime(date);
        Integer count = checkST.get(Calendar.WEEK_OF_MONTH);
        return count;
    }

    @GetMapping("/getDayOff")
    public ResponseEntity<?> finAll() {
        try {
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat formatterDate = new SimpleDateFormat("dd/MM/yyyy");
//            List<DayOffDTO> listData = dayOffService.findAll();
            List<DayOffDTO> listData = dayOffService.findAllInYear();
            for (DayOffDTO list : listData) {
                String date = list.getFullYears().toString();
                try {
                    Date dateConvert = (Date) formatter.parse(date);
                    String strDate = formatterDate.format(dateConvert);
                    list.setDateTime(strDate);
                } catch (ParseException e) {
                    return toExceptionResult(e.getMessage(), Const.API_RESPONSE.RETURN_CODE_ERROR);
                }
            }
            return toSuccessResult(listData);
        } catch (Exception e) {
            LOGGER.error("load getAll dayoff error: " + e.getMessage());
            return toExceptionResult(e.getMessage(), Const.API_RESPONSE.RETURN_CODE_ERROR);
        }
    }

    @GetMapping("/deleteDayOff")
    public ResponseEntity<?> deleteByDate(@RequestParam("dateStr") String dateStr) {
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        DateFormat formatt = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat formatterDate = new SimpleDateFormat("yyyy-MM-dd");

        try {
            if (!dateStr.equals("") && dateStr != null) {
                Date dateConvert = (Date) formatter.parse(dateStr);
                String strDate = formatterDate.format(dateConvert);
                DayOffDTO dayOff = dayOffService.findByDate(strDate);
                if (Objects.nonNull(dayOff)) {
                    Long workCalendarId = null;
                    dayOffService.deleteByDate(strDate);
                    String check = checkDayGlobal(dateStr);
                    WorkCalendarDTO workCalendarDTO = workCalendarService.getByDay(check);
                    List<HistoryCalendarDTO> getHistory = historyCalendarService.getHistoryCalendar();
                    WorkCalendar objectGetStatus = new WorkCalendar();
                    if (Objects.nonNull(workCalendarDTO) && getHistory.size() > 0) {
                        for (HistoryCalendarDTO objCheck : getHistory) {
                            if (workCalendarDTO.getWorkCalendarId().equals(objCheck.getWorkCalendarId())) {
                                objectGetStatus.setStatus(objCheck.getStatus());
                                workCalendarId = objCheck.getWorkCalendarId();
                            }
                        }


                    }
                    if (workCalendarId != null) {
                        WorkCalendar calendar = workCalendarService.findById(workCalendarId);
                        calendar.setStatus(objectGetStatus.getStatus());
                        calendar = workCalendarService.updateWorkCalendar(calendar);
                    }

                } else {
                    return toExceptionResult(null, Const.API_RESPONSE.TOPIC_ID);
                }
            }

        } catch (Exception e) {
            LOGGER.error("delete dayoff calendar error: " + e.getMessage());
            return toExceptionResult(e.getMessage(), Const.API_RESPONSE.RETURN_CODE_ERROR);
        }
        return toSuccessResult(null);
    }

    @PostMapping("/edit")
    public ResponseEntity<?> updateById(@RequestParam("workCalendarId") Long workCalendarId, @RequestBody WorkCalendarDTO calendarDTO) throws Exception {
        WorkCalendar data = new WorkCalendar();
        try {
            Date currentDate = new Date();
            List<DayOffDTO> listDayOff;
            List<DayOffDTO> listCheck = new ArrayList<>();
            List<WorkCalendarDTO> listData = new ArrayList<>();
            listData = workCalendarService.findAll();
            // lay ngay hien tai khi ng dung load trang
            ListDayInWeekDTO listDayInWeekDTO = checkinWeek(currentDate);
//            listDayOff = dayOffService.findAll();
            listDayOff = dayOffService.findAllInYear();
            for (DayOffDTO checkDayOffWeek : listDayOff) {
                if (checkDayOffWeek.getFullYears().compareTo(listDayInWeekDTO.getMinDate()) >= 0
                        && checkDayOffWeek.getFullYears().compareTo(listDayInWeekDTO.getMaxDate()) <= 0) {
                    listCheck.add(checkDayOffWeek);
                }
            }
            if (listCheck != null && listCheck.size() > 0) {
                for (DayOffDTO getName : listCheck) {
                    Integer check = getName.getDayOfWeek().intValue();
                    String daysName = "";
                    switch (check) {
                        case 1:
                            getName.setDayName("SUN");
                            break;
                        case 2:
                            getName.setDayName("MON");
                            break;
                        case 3:
                            getName.setDayName("TUE");
                            break;
                        case 4:
                            getName.setDayName("WED");
                            break;
                        case 5:
                            getName.setDayName("THU");
                            break;
                        case 6:
                            getName.setDayName("FRI");
                            break;

                        case 7:
                            getName.setDayName("SAT");
                            break;
                    }
                }
            }
//            Date date = new Date();
//            WorkCalendar workCalendar = workCalendarService.findById(workCalendarId);
//            if (Objects.nonNull(workCalendar) && listCheck.size() > 0 && listCheck != null) {
//                for (DayOffDTO checkDayEdit : listCheck) {
//                    if (workCalendar.getDays().equals(checkDayEdit.getDayName())) {
//                        return toDupplicateDayOff(checkDayEdit);
//                    }
//                }
//                if (Objects.nonNull(calendarDTO)) {
//                    workCalendar.setStatus(calendarDTO.getStatus());
//                    workCalendar.setEndWorkTimePM(calendarDTO.getEndWorkTimePM());
//                    workCalendar.setEndWorkTimeAM(calendarDTO.getEndWorkTimeAM());
//                    workCalendar.setBeginWorkTimePM(calendarDTO.getBeginWorkTimePM());
//                    workCalendar.setBeginWorkTimeAM(calendarDTO.getBeginWorkTimeAM());
//                    workCalendar.setUpdatedDate(date);
//                    EmployeeDto employeeDto = employeeService.getOrCreateFromJwt();
//                    if (Objects.nonNull(employeeDto)) {
//                        workCalendar.setUpdatedBy(employeeDto.getEmployeeId());
//                    }
//                    data = workCalendarService.updateWorkCalendar(workCalendar);
//                }
//
//            }
//            HistoryCalendar historyCalendar = new HistoryCalendar();
//            historyCalendar.setStatus(workCalendar.getStatus());
//            historyCalendar.setWorkCalendarId(workCalendarId);
//            historyCalendar.setCreateDate(new Date());
//            historyCalendar = historyCalendarService.insert(historyCalendar);
            Date date = new Date();
            WorkCalendar workCalendar = workCalendarService.findById(workCalendarId);
            if (Objects.nonNull(workCalendar) && listCheck.size() > 0 && listCheck != null) {
                for (DayOffDTO checkDayEdit : listCheck) {
                    if (workCalendar.getDays().equals(checkDayEdit.getDayName())) {
                        return toDupplicateDayOff(checkDayEdit);
                    }
                }

            }
            workCalendar.setStatus(calendarDTO.getStatus());
            workCalendar.setEndWorkTimePM(calendarDTO.getEndWorkTimePM());
            workCalendar.setEndWorkTimeAM(calendarDTO.getEndWorkTimeAM());
            workCalendar.setBeginWorkTimePM(calendarDTO.getBeginWorkTimePM());
            workCalendar.setBeginWorkTimeAM(calendarDTO.getBeginWorkTimeAM());
            EmployeeDto employeeDto = employeeService.getOrCreateFromJwt();
            if (Objects.nonNull(employeeDto)) {
                workCalendar.setUpdatedBy(employeeDto.getEmployeeId());
            }
            workCalendar.setUpdatedDate(date);
            data = workCalendarService.updateWorkCalendar(workCalendar);
            HistoryCalendar historyCalendar = new HistoryCalendar();
            historyCalendar.setStatus(workCalendar.getStatus());
            historyCalendar.setWorkCalendarId(workCalendarId);
            historyCalendar.setCreateDate(new Date());
            historyCalendar = historyCalendarService.insert(historyCalendar);


        } catch (Exception ex) {
            LOGGER.error("edit calendar error: " + ex.getMessage());
            return toExceptionResult(ex.getMessage(), Const.API_RESPONSE.RETURN_CODE_ERROR);
        }
        return toSuccessResult(data);
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody DayOffDTO dayOffDTO) throws Exception {
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        DateFormat formatt = new SimpleDateFormat("yyyy-MM-dd");

        SimpleDateFormat formatterDate = new SimpleDateFormat("yyyy-MM-dd");
        DayOff data = new DayOff();
        try {

            Date dateConvert = (Date) formatter.parse(dayOffDTO.getDateStr());
            String strDate = formatterDate.format(dateConvert);
            Date date = formatt.parse(strDate);
            String dateStr = formatt.format(date);
            dayOffDTO.setDate(date);
            data.setDate(dayOffDTO.getDate());
            data.setCreatedDate(new Date());
            data.setDateStr(dateStr);
            data = dayOffService.insert(data);
        } catch (Exception ex) {
            LOGGER.error("created dayoff calendar error: " + ex.getMessage());
            return toExceptionResult(ex.getMessage(), Const.API_RESPONSE.RETURN_CODE_ERROR);
        }
        return toSuccessResult(data);
    }

}
