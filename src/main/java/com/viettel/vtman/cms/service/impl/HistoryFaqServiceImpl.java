package com.viettel.vtman.cms.service.impl;

import com.google.gson.Gson;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.viettel.vtman.cms.dao.BannedContentDAO;
import com.viettel.vtman.cms.dao.DayOffDAO;
import com.viettel.vtman.cms.dao.EmployeeDAO;
import com.viettel.vtman.cms.dao.FunctionConfigDAO;
import com.viettel.vtman.cms.dao.HistoryFaqDAO;
import com.viettel.vtman.cms.dao.HistoryFaqDetailDAO;
import com.viettel.vtman.cms.dao.NotificationFaqDAO;
//import com.viettel.vtman.cms.dao.TokenFcmDAO;
import com.viettel.vtman.cms.dao.TopicDAO;
import com.viettel.vtman.cms.dao.WorkCalendarDAO;
import com.viettel.vtman.cms.dto.AppInfoDTO;
import com.viettel.vtman.cms.dto.FireBaseDataDTO;
import com.viettel.vtman.cms.dto.HisFaqDetailInputDTO;
import com.viettel.vtman.cms.dto.HisFaqDetailOutDTO;
import com.viettel.vtman.cms.dto.HisFaqNotificationDTO;
import com.viettel.vtman.cms.dto.HisFaqPushFirebaseDTO;
import com.viettel.vtman.cms.dto.HistoryFaqCMSDTO;
import com.viettel.vtman.cms.dto.HistoryFaqDTO;
import com.viettel.vtman.cms.dto.HistoryFaqDetailDTO;
import com.viettel.vtman.cms.dto.HistoryFaqPostReqDTO;
import com.viettel.vtman.cms.dto.NotificationFaqDTO;
import com.viettel.vtman.cms.dto.ResponseVTPostDTO;
import com.viettel.vtman.cms.dto.WorkTimeWeekendDTO;
import com.viettel.vtman.cms.entity.BannedContent;
import com.viettel.vtman.cms.entity.Employee;
import com.viettel.vtman.cms.entity.FunctionConfig;
import com.viettel.vtman.cms.entity.HistoryFaq;
import com.viettel.vtman.cms.entity.HistoryFaqDetail;
import com.viettel.vtman.cms.entity.NotificationFaq;
//import com.viettel.vtman.cms.entity.TokenFcm;
import com.viettel.vtman.cms.entity.Topic;
import com.viettel.vtman.cms.entity.WorkCalendar;
import com.viettel.vtman.cms.infrastructure.util.DateTimeUtil;
import com.viettel.vtman.cms.infrastructure.util.RandomUtil;
import com.viettel.vtman.cms.message.Const;
import com.viettel.vtman.cms.service.HistoryFaqService;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
//import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class HistoryFaqServiceImpl implements HistoryFaqService {

    private static final Logger LOGGER = LogManager.getLogger(HistoryFaqServiceImpl.class);

    @Autowired
    private HistoryFaqDAO historyFaqDAO;

    @Autowired
    private HistoryFaqDetailDAO historyFaqDetailDAO;

    @Autowired
    private TopicDAO topicDAO;

    @Autowired
    private FunctionConfigDAO functionConfigDAO;

    @Autowired
    private WorkCalendarDAO workCalendarDAO;

    @Autowired
    private DayOffDAO dayOffDAO;

    @Autowired
    private EmployeeDAO employeeDAO;

//    @Autowired
//    private TokenFcmDAO tokenFcmDAO;

    @Autowired
    private NotificationFaqDAO notificationDAO;

    @Autowired
    private BannedContentDAO bannedContentDAO;

//    @Value("${firebase.server.key}")
//    private String fireBaseServerKey;
//
//    @Value("${firebase.api.url}")
//    private String fireBaseApiUrl;

    @Value("${vtp.notification.url}")
    private String vtpNotificationUrl;

    @Value("${vtp.notification.token}")
    private String vtpNotificationToken;

    @Override
    public List<HistoryFaqDTO> getHistoryFaqs(Long employeeId, String keySearch, Boolean isAnswer, Long type) {
        List<HistoryFaqDTO> result = historyFaqDAO.getHistoryFaqs(employeeId, isAnswer, keySearch, type);
        List<HistoryFaqDTO> temp = historyFaqDAO.getHistoryFaqWithoutTopic(employeeId, isAnswer, keySearch, type);
        result.addAll(temp);
        result = result.stream().sorted(Comparator.comparing(HistoryFaqDTO::getCreatedDate).reversed()).peek(item -> item.setType(type)).collect(Collectors.toList());
        return result;
    }

    @Override
    public Long countReceived(List<HistoryFaqDTO> lstHis) {
        return lstHis.stream().filter(item -> Const.STATUS_QUESTION.QUESTION_NEW.equals(item.getStatus())).count();
    }

    @Override
    public String insertHisFaq(HistoryFaqPostReqDTO input) throws Exception {
        // validate input
        if (!saveCheck(input)) {
            return StringUtils.EMPTY;
        }

        List<FunctionConfig> configs = functionConfigDAO.findAll();
        if (CollectionUtils.isEmpty(configs)) {
            return StringUtils.EMPTY;
        }
        FunctionConfig config = configs.get(0);

        Date currentDate = new Date();
        String employeeId;
        HistoryFaq historyFaq = new HistoryFaq();
        if (Objects.isNull(input.getTopicId())) {
            historyFaq.setDepartmentId(config.getDepartmentId());
            employeeId = config.getEmployeeId();
        } else {
            Topic topic = topicDAO.findById(input.getTopicId());
            historyFaq.setTopicId(topic.getTopicId());
            historyFaq.setDepartmentId(topic.getDepartmentId());
            employeeId = topic.getAnswerEmployeeId();
        }
        String hisFaqCode = RandomUtil.generateRandomString(StringUtils.EMPTY, 8, 2);
        historyFaq.setHistoryFaqCode(hisFaqCode);
        historyFaq.setHistoryFaqName(input.getHistoryFaqName().trim());
        historyFaq.setStatus(Const.STATUS_QUESTION.QUESTION_NEW);
        historyFaq.setCreatedDate(currentDate);
        historyFaq.setCreatedBy(input.getCreatedBy());
        historyFaq.setCountRating(0L);
        Date maximumDate = calculateDate(currentDate, config.getMaximumResponseTime().intValue());
        Date responseDate = calculateDate(currentDate, config.getResponseRemindingTime().intValue());
        historyFaq.setMaximumDate(maximumDate);
        historyFaq.setResponseDate(responseDate);
        Employee employee = employeeDAO.findById(input.getCreatedBy());
        String body = employee.getEmployeeCode() + " - " + employee.getEmployeeName() + ": " + '"' + input.getHistoryFaqName() + '"';
        Long hisFaqId = historyFaqDAO.insertHisFaq(historyFaq);
        pushNotificationQuestion(employeeId, hisFaqId, input.getTopicId(), body, currentDate);

        return historyFaq.getHistoryFaqName();
    }

    @Override
    public String checkInsertHisFaq(HistoryFaqPostReqDTO input) {
        // validate input
        if (!saveCheck(input)) {
            return StringUtils.EMPTY;
        }

        List<FunctionConfig> configs = functionConfigDAO.findAll();
        if (CollectionUtils.isEmpty(configs)) {
            return StringUtils.EMPTY;
        }
        FunctionConfig config = configs.get(0);

        // check max QA session
        if (checkMaxQASession(input, config)) {
            return Const.ERROR_MAX_QA_SESSION;
        }

        // check nhap 1 tu hoac chi nhap so va ky tu dac biet
        if (!input.getHistoryFaqName().trim().contains(StringUtils.SPACE) || checkRegex(input.getHistoryFaqName())) {
            return Const.ERROR_EVALUATE;
        }

        // check content banned
        List<String> lstContentBanned = checkContentBanned(input.getHistoryFaqName());
        if (!CollectionUtils.isEmpty(lstContentBanned)){
            return Const.ERROR_CONTENT_BANNED;
        }

        return Const.SUCCESS;
    }

    private boolean checkMaxQASession(HistoryFaqPostReqDTO input, FunctionConfig config) {
        List<HistoryFaq> historyFaqs = historyFaqDAO.findByAnswerIsNull(input.getCreatedBy());
        if (CollectionUtils.isEmpty(historyFaqs)) {
            return false;
        }
        return (config.getMaximumQASession().intValue() - 1) < historyFaqs.size();
    }

    // check regex number or special character
    private boolean checkRegex(String input) {
        input = input.replaceAll(StringUtils.SPACE, StringUtils.EMPTY);
        return Pattern.matches(Const.PATTERN, input);
    }

    private String pushNotificationQuestion(String employeeId, Long hisFaqId, Long topicId, String body, Date currentDate) {
        try {
            List<String> listAnswerEmployeeId = Pattern.compile(",").splitAsStream(employeeId).collect(Collectors.toList());
            List<Long> employeeIds = listAnswerEmployeeId.stream().map(Long::valueOf).collect(Collectors.toList());
            List<Employee> lstEmployee = employeeDAO.findByIds(employeeIds);
            List<HisFaqPushFirebaseDTO> result = new ArrayList<>();
            for (Long employee : employeeIds) {
                NotificationFaq notification = NotificationFaq.builder().notificationTitle(Const.FIREBASE.TITLE_TYPE_1).notificationName(body)
                        .notificationDate(currentDate).employeeId(employee).isView(1L).historyFaqId(hisFaqId)
                        .topicId(topicId).notificationType(Const.TYPE_HAS_RECEIVED).typeCms(1L).build();
                notificationDAO.insertNotificationFaq(notification);
            }

            if (!CollectionUtils.isEmpty(lstEmployee)) {
                List<Long> userIds = lstEmployee.stream().map(Employee::getUserId).filter(Objects::nonNull).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(userIds)) {
                    FireBaseDataDTO data = FireBaseDataDTO.builder().hisFaqId(String.valueOf(hisFaqId))
                            .topicId(String.valueOf(topicId)).notificationType(String.valueOf(Const.TYPE_HAS_RECEIVED)).typeCms(String.valueOf(1L)).build();
                    for (Long uid : userIds) {
                        HisFaqPushFirebaseDTO dto = HisFaqPushFirebaseDTO.builder().title(Const.FIREBASE.TITLE_TYPE_1).body(body).userId(uid).data(data).build();
                        result.add(dto);
                    }
                }
            }
            for (HisFaqPushFirebaseDTO dto : result) {
                pushNotification(dto);
            }
            return Const.SUCCESS;
        } catch (Exception e) {
            LOGGER.error("Push Notification error" + e.getMessage());
            return e.getMessage();
        }
    }


    private boolean saveCheck(HistoryFaqPostReqDTO input) {
        if (Objects.isNull(input)) {
            return false;
        }
        if (StringUtils.isEmpty(input.getHistoryFaqName())) {
            return false;
        }
        if (input.getHistoryFaqName().length() > 500) {
            return false;
        }
        if (Objects.isNull(input.getCreatedBy()) || input.getCreatedBy().equals(0L)) {
            return false;
        }

        return true;
    }

    @Override
    public HistoryFaqDTO getHisFaqDetailById(Long emId, Long hisFaqId, Long topicId) {
        HistoryFaqDTO historyFaqDTO = historyFaqDAO.getHisFaqById(hisFaqId, topicId);
        if (Objects.nonNull(historyFaqDTO)) {
            historyFaqDTO.setIsRoleQuestion(emId.equals(historyFaqDTO.getEmployeeId()));
            List<HistoryFaqDetailDTO> hisFaqDetails = historyFaqDAO.getHisFaqDetailByHisFaqId(hisFaqId);
            historyFaqDTO.setLstHisFaqDetail(hisFaqDetails);
        }
        return historyFaqDTO;
    }

    @Override
    public List<HistoryFaqCMSDTO> search(HistoryFaqCMSDTO historyFaqDTO) throws ParseException {
        return historyFaqDAO.search(historyFaqDTO);
    }

    @Override
    public HisFaqDetailOutDTO insertDetail(HisFaqDetailInputDTO dto) throws Exception {
        // validate input
        if (!validateInsertDetailInput(dto)) {
            return null;
        }

        // check content banned
        List<String> lstContentBanned = checkContentBanned(dto.getTextInput());
        if (!CollectionUtils.isEmpty(lstContentBanned)) {
            return new HisFaqDetailOutDTO(Const.ERROR_CONTENT_BANNED, null, lstContentBanned);
        }

        HistoryFaq historyFaq = historyFaqDAO.findById(dto.getHisFaqId());
        if (Objects.isNull(historyFaq)) {
            return new HisFaqDetailOutDTO(Const.ERROR_NOT_FOUND, null, null);
        }

        if (Const.STATUS_QUESTION.QUESTION_EXPIRED.equals(historyFaq.getStatus())) {
            return new HisFaqDetailOutDTO(Const.ERROR_ANSWER_EXPIRED, null, null);
        }

        if (Const.STATUS_QUESTION.QUESTION_CLOSED.equals(historyFaq.getStatus())) {
            return new HisFaqDetailOutDTO(Const.ERROR_ANSWER_CLOSED, null, null);
        }

        if (Const.STATUS_QUESTION.QUESTION_CANCELED.equals(historyFaq.getStatus())) {
            return new HisFaqDetailOutDTO(Const.ERROR_ANSWER_CANCEL, null, null);
        }

        if (dto.getIsAnswer() && (Const.TYPE_HAS_RECEIVED.equals(dto.getType()) || !dto.getEmployeeId().equals(historyFaq.getCreatedBy()))) {
            return insertAnswer(dto, historyFaq);
        }
        return insertQuestion(dto);
    }

    // Check input text has contains content banned
    private List<String> checkContentBanned(String inputText) {
        inputText = StringUtils.normalizeSpace(inputText);
        List<String> result = new ArrayList<>();
        List<BannedContent> contentByName = bannedContentDAO.findBannedContent(inputText);
        if (!CollectionUtils.isEmpty(contentByName)) {
            if (!CollectionUtils.isEmpty(contentByName)) {
                result = contentByName.stream().map(BannedContent::getBannedContentName).limit(2).collect(Collectors.toList());
            }
        }
        return result;
    }

    private HisFaqDetailOutDTO insertQuestion(HisFaqDetailInputDTO dto) {
        Date date = new Date();
        HistoryFaqDetail historyFaqDetail = new HistoryFaqDetail();
        historyFaqDetail.setHistoryFaqId(dto.getHisFaqId());
        historyFaqDetail.setHistoryFaqDetailName(dto.getTextInput().trim());
        historyFaqDetail.setCreatedDate(date);
        historyFaqDetail.setCreatedBy(dto.getEmployeeId());
        historyFaqDetailDAO.insertHisFaqDetail(historyFaqDetail);
        return new HisFaqDetailOutDTO(dto.getTextInput(), DateTimeUtil.format(date, Const.FORMAT_DATE), null);
    }

    private HisFaqDetailOutDTO insertAnswer(HisFaqDetailInputDTO dto, HistoryFaq historyFaq) {
        Date current = new Date();
        String strCurrent = DateTimeUtil.format(current, Const.FORMAT_DATE);
        if (StringUtils.isEmpty(historyFaq.getAnswer())) {
            historyFaq.setAnswer(dto.getTextInput().trim());
            historyFaq.setAnswerEmployeeId(dto.getEmployeeId());
            historyFaq.setAnswerDate(current);
            historyFaq.setStatus(Const.STATUS_QUESTION.QUESTION_HAD_ANSWER);
            historyFaqDAO.updateHisFaq(historyFaq);
            pushNotificationAnswer(historyFaq, current);
            return new HisFaqDetailOutDTO(dto.getTextInput(), strCurrent, null);
        }

        HistoryFaqDetail historyFaqDetail = new HistoryFaqDetail();
        historyFaqDetail.setAnswer(dto.getTextInput().trim());
        historyFaqDetail.setAnswerEmployeeId(dto.getEmployeeId());
        historyFaqDetail.setAnswerDate(current);
        historyFaqDetail.setHistoryFaqId(dto.getHisFaqId());
        historyFaqDetailDAO.insertHisFaqDetail(historyFaqDetail);
        return new HisFaqDetailOutDTO(dto.getTextInput(), strCurrent, null);
    }

    private String pushNotificationAnswer(HistoryFaq item, Date current) {
        try {
            List<Employee> lstEmployee = employeeDAO.findByIds(Arrays.asList(item.getCreatedBy()));
            String body = "Câu hỏi " + item.getHistoryFaqCode() + ": " + '"' + item.getAnswer() + '"' + ".";
            NotificationFaq notification = NotificationFaq.builder().notificationTitle(Const.FIREBASE.TITLE_TYPE_2).notificationName(body)
                    .notificationDate(current).employeeId(item.getCreatedBy()).isView(1L).historyFaqId(item.getHistoryFaqId())
                    .topicId(item.getTopicId()).notificationType(Const.TYPE_HAS_SEND).typeCms(2L).build();
            notificationDAO.insertNotificationFaq(notification);
            if (!CollectionUtils.isEmpty(lstEmployee)) {
                List<Long> userIds = lstEmployee.stream().map(Employee::getUserId).filter(Objects::nonNull).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(userIds)) {
                    FireBaseDataDTO data = FireBaseDataDTO.builder().hisFaqId(String.valueOf(item.getHistoryFaqId()))
                            .topicId(String.valueOf(item.getTopicId())).notificationType(String.valueOf(Const.TYPE_HAS_SEND)).typeCms(String.valueOf(2L)).build();
                    for (Long uid : userIds) {
                        HisFaqPushFirebaseDTO dto = HisFaqPushFirebaseDTO.builder().title(Const.FIREBASE.TITLE_TYPE_2).body(body).userId(uid).data(data).build();
                        pushNotification(dto);
                    }
                }
            }
            return Const.SUCCESS;
        } catch (Exception e) {
            LOGGER.error("Push Notification error " + e.getMessage());
            return e.getMessage();
        }
    }

    @Override
    public HistoryFaqCMSDTO getHisFaqByIdFaq(Long historyFaqId) {
        return historyFaqDAO.getHisFaqByIdFaq(historyFaqId);
    }

    private boolean validateInsertDetailInput(HisFaqDetailInputDTO dto) {
        if (Objects.isNull(dto)) {
            return false;
        }
        if (Objects.isNull(dto.getHisFaqId()) || Objects.isNull(dto.getEmployeeId())) {
            return false;
        }
        if (dto.getHisFaqId() <= 0 || dto.getEmployeeId() <= 0) {
            return false;
        }
        if (StringUtils.isEmpty(dto.getTextInput())) {
            return false;
        }
        if (StringUtils.isEmpty(dto.getTextInput().trim())) {
            return false;
        }
        if (dto.getTextInput().length() > 500) {
            return false;
        }
        return true;
    }

    @Override
    public Long countNotificationHisFaqs(Long employeeId) {
        List<NotificationFaqDTO> lstHisFaqs = getNotificationFaqs(employeeId);
        if (CollectionUtils.isEmpty(lstHisFaqs)) return 0L;
        return lstHisFaqs.stream().filter(item -> item.getIsView().equals(1L)).count();
    }

    @Override
    public List<NotificationFaqDTO> getNotificationFaqs(Long employeeId) {
        return notificationDAO.findByEmployeeId(employeeId);
    }

    @Override
    public String activeNotification(Long notificationId) throws Exception{
        NotificationFaq notificationFaq = notificationDAO.findById(notificationId);
        if (Objects.isNull(notificationFaq)) {
            throw new Exception(String.format(Const.NOT_FOUND_DATA, NotificationFaq.class.getName()));
        }
        notificationFaq.setIsView(0L);
        return notificationDAO.updateNotificationFaq(notificationFaq);
    }

    @Override
    public String updateEvaluate(HistoryFaqPostReqDTO input) throws Exception {
        if (!checkUpdateEvaluate(input)) {
            return StringUtils.EMPTY;
        }
        HistoryFaq historyFaq = historyFaqDAO.findById(input.getHistoryFaqId());
        if (Objects.isNull(historyFaq)) {
            return Const.NOT_FOUND_QUESTION;
        }
        historyFaq.setRating(input.getRating());
        historyFaq.setComment(StringUtils.isNotEmpty(input.getComment()) ? input.getComment().trim() : StringUtils.EMPTY);
        historyFaq.setCountRating(historyFaq.getCountRating() + 1);
        return historyFaqDAO.updateHisFaq(historyFaq);
    }

    private boolean checkUpdateEvaluate(HistoryFaqPostReqDTO input) {
        if (Objects.isNull(input)) {
            return false;
        }
        if (Objects.isNull(input.getHistoryFaqId()) || input.getHistoryFaqId() <= 0) {
            return false;
        }
        if (Objects.isNull(input.getRating()) || input.getRating() <= 0) {
            return false;
        }
        if (StringUtils.isNotEmpty(input.getComment()) && input.getComment().length() > 500) {
            return false;
        }
        return true;
    }

    @Override
    public String forwardHisFaqByTopic(HistoryFaqPostReqDTO input) throws Exception{
        Date currentDate = new Date();
        HistoryFaq historyFaq = historyFaqDAO.findById(input.getHistoryFaqId());
        FunctionConfig config = functionConfigDAO.findAll().get(0);
        String employeeId;
        if (Objects.isNull(historyFaq)) {
            return Const.NOT_FOUND_QUESTION;
        }
        if (StringUtils.isEmpty(config.getEmployeeId())) {
            return Const.ERROR_FORWARD_QUESTION;
        }

        if (Const.STATUS_QUESTION.QUESTION_EXPIRED.equals(historyFaq.getStatus())) {
            return Const.ERROR_FORWARD_EXPIRED;
        }

        if (Const.STATUS_QUESTION.QUESTION_CLOSED.equals(historyFaq.getStatus())) {
            return Const.ERROR_FORWARD_CLOSED;
        }

        if (Const.STATUS_QUESTION.QUESTION_CANCELED.equals(historyFaq.getStatus())) {
            return Const.ERROR_FORWARD_CANCEL;
        }

        if (Objects.isNull(input.getTopicId())) {
            historyFaq.setTopicId(null);
            historyFaq.setDepartmentId(config.getDepartmentId());
            employeeId = config.getEmployeeId();
        } else {
            Topic topic = topicDAO.findById(input.getTopicId());
            historyFaq.setTopicId(topic.getTopicId());
            historyFaq.setDepartmentId(topic.getDepartmentId());
            employeeId = topic.getAnswerEmployeeId();
        }
        // find and remove old notification
        List<NotificationFaq> oldNotifications = notificationDAO.findByHisFaqId(historyFaq.getHistoryFaqId());
        if (!CollectionUtils.isEmpty(oldNotifications)) {
            List<Long> notificationIds = oldNotifications.stream().map(NotificationFaq::getNotificationId).collect(Collectors.toList());
            notificationDAO.deleteIds(notificationIds);
        }
        Date maximumDate = calculateDate(currentDate, config.getMaximumResponseTime().intValue());
        Date responseDate = calculateDate(currentDate, config.getResponseRemindingTime().intValue());
        historyFaq.setForwardDate(currentDate);
        historyFaq.setMaximumDate(maximumDate);
        historyFaq.setResponseDate(responseDate);
        Employee employee = employeeDAO.findById(historyFaq.getCreatedBy());
        String body = employee.getEmployeeCode() + " - " + employee.getEmployeeName() + ": " + '"' + historyFaq.getHistoryFaqName() + '"';
        pushNotificationQuestion(employeeId, historyFaq.getHistoryFaqId(), historyFaq.getTopicId(), body, currentDate);
        return historyFaqDAO.updateHisFaq(historyFaq);
    }

    // tinh thoi gian het han hoac thoi gian phan hoi
    public Date calculateDate(Date createdDate, int timeConfig) throws Exception {
        try {
            List<String> holidays = dayOffDAO.getHolidays();
            List<WorkCalendar> lstWorkCalendar = workCalendarDAO.getAllCalendars();
            int tempHour;
            int tempMinute;
            int tempSecond;
            int dayOfWeek;
            Calendar cal = Calendar.getInstance();
            cal.setTime(createdDate);
            dayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1;
            if (isWorkingDay(cal, holidays, lstWorkCalendar, dayOfWeek)) { // neu la ngay nghi le
                createdDate = getDateExcludeHolidays(createdDate, holidays, lstWorkCalendar, dayOfWeek);
                cal.setTime(createdDate);
            }
            // neu ko phai ngay nghi le
            dayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1;
            int hour = cal.get(Calendar.HOUR_OF_DAY);
            int minute = cal.get(Calendar.MINUTE);
            int second = cal.get(Calendar.SECOND);
            WorkTimeWeekendDTO workTime = getTimeWork(lstWorkCalendar, dayOfWeek);
            // neu thoi gian dat cau hoi lon hon thoi gian lam viec buoi chieu --> tinh thoi gian tu ngay tiep theo
            if (dayOfWeek == Calendar.SATURDAY - 1 && workTime.getBeginMinutePm() == 0) { // Neu la thu 7
                if (hour > workTime.getEndHourAm() || (hour == workTime.getEndHourAm() && minute >= workTime.getEndMinuteAm())) {
                    cal.add(Calendar.DAY_OF_MONTH, 1);
                    Date tempDate = cal.getTime();
                    if (isWorkingDay(cal, holidays, lstWorkCalendar, dayOfWeek)) {
                        tempDate = getDateExcludeHolidays(tempDate, holidays, lstWorkCalendar, dayOfWeek);
                    }
                    createdDate = tempDate;
                    cal.setTime(createdDate);
                    dayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1;
                    workTime = getTimeWork(lstWorkCalendar, dayOfWeek);
                    tempHour = workTime.getBeginHourAm();
                    tempMinute = workTime.getBeginMinuteAm();
                    tempSecond = workTime.getSecond();
                } else {
                    if (hour < workTime.getBeginHourAm() || (hour == workTime.getBeginHourAm() && minute == workTime.getBeginMinuteAm())) {
                        tempHour = workTime.getBeginHourAm();
                        tempMinute = workTime.getBeginMinuteAm();
                        tempSecond = workTime.getSecond();
                    } else {
                        tempHour = hour;
                        tempMinute = minute;
                        tempSecond = second;
                    }
                }
            } else {
                if (hour > workTime.getEndHourPm() || (hour == workTime.getEndHourPm() && minute >= workTime.getEndMinutePm())) {
                    cal.add(Calendar.DAY_OF_MONTH, 1);
                    Date tempDate = cal.getTime();
                    if (isWorkingDay(cal, holidays, lstWorkCalendar, dayOfWeek)) {
                        tempDate = getDateExcludeHolidays(tempDate, holidays, lstWorkCalendar, dayOfWeek);
                    }
                    createdDate = tempDate;
                    cal.setTime(createdDate);
                    dayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1;
                    workTime = getTimeWork(lstWorkCalendar, dayOfWeek);
                    tempHour = workTime.getBeginHourAm();
                    tempMinute = workTime.getBeginMinuteAm();
                    tempSecond = workTime.getSecond();
                } else {
                    if (hour < workTime.getBeginHourAm() || (hour == workTime.getBeginHourAm() && minute == workTime.getBeginMinuteAm())) {
                        tempHour = workTime.getBeginHourAm();
                        tempMinute = workTime.getBeginMinuteAm();
                        tempSecond = workTime.getSecond();
                    } else if ((hour >= workTime.getEndHourAm() && minute >= workTime.getEndMinuteAm() &&
                            hour < workTime.getBeginHourPm()) || (hour == workTime.getBeginHourPm() && minute <= workTime.getBeginMinutePm())) {
                        tempHour = workTime.getBeginHourPm();
                        tempMinute = workTime.getBeginMinutePm();
                        tempSecond = workTime.getSecond();
                    } else {
                        tempHour = hour;
                        tempMinute = minute;
                        tempSecond = second;
                    }
                }
            }
            // Tinh khoang thoi gian lam viec buoi sang
            Date endTimeAms = DateTimeUtil.setDateTime(createdDate, workTime.getEndHourAm(), workTime.getEndMinuteAm(), workTime.getSecond());
            // Tinh khoang thoi gian nghi trua
            Date startTimePms = DateTimeUtil.setDateTime(createdDate, workTime.getBeginHourPm(), workTime.getBeginMinutePm(), workTime.getSecond());
            long durationLunch = startTimePms.getTime() - endTimeAms.getTime();
            int minuteLunch = (int) TimeUnit.MILLISECONDS.toMinutes(durationLunch);
            // Tinh khoang thoi gian lam viec buoi chieu
            Date endTimePms = DateTimeUtil.setDateTime(createdDate, workTime.getEndHourPm(), workTime.getEndMinutePm(), workTime.getSecond());
            // Tinh thoi diem bat dau
            Date startTime = DateTimeUtil.setDateTime(createdDate, tempHour, tempMinute, tempSecond);
            long tempDuration;
            if (dayOfWeek == Calendar.SATURDAY - 1 && workTime.getBeginMinutePm() == 0) {
                tempDuration = endTimeAms.getTime() - startTime.getTime();
            } else {
                if (tempHour >= 13) {
                    tempDuration = endTimePms.getTime() - startTime.getTime();
                } else {
                    tempDuration = endTimePms.getTime() - durationLunch - startTime.getTime();
                }
            }
            int timeWorkDay = (int) TimeUnit.MILLISECONDS.toMinutes(tempDuration); // tong so phut lam viec con lai trong ngay
            if (timeConfig <= timeWorkDay) {
                Calendar cal1 = Calendar.getInstance();
                cal1.setTime(startTime);
                if (tempHour >= 13) {
                    cal1.add(Calendar.MINUTE, timeConfig);
                } else {
                    int minx = (int) TimeUnit.MILLISECONDS.toMinutes(endTimeAms.getTime() - startTime.getTime());
                    if (minx >= timeConfig) {
                        cal1.add(Calendar.MINUTE, timeConfig);
                    } else {
                        cal1.add(Calendar.MINUTE, minx + minuteLunch + (timeConfig - minx));
                    }
                }
                return cal1.getTime();
            } else {
                int lackTime = timeConfig - timeWorkDay; // thoi gian con thieu
                cal.add(Calendar.DAY_OF_MONTH, 1);
                Date newDate = cal.getTime();
                dayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1;
                if (isWorkingDay(cal, holidays, lstWorkCalendar, dayOfWeek)) {
                    newDate = getDateExcludeHolidays(newDate, holidays, lstWorkCalendar, dayOfWeek);
                }
                cal.setTime(newDate);
                dayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1;
                WorkTimeWeekendDTO workDTO = getTimeWork(lstWorkCalendar, dayOfWeek);
                Date startTimeAmx = DateTimeUtil.setDateTime(newDate, workDTO.getBeginHourAm(), workDTO.getBeginMinuteAm(), workDTO.getSecond());
                Date endTimeAmx = DateTimeUtil.setDateTime(newDate, workDTO.getEndHourAm(), workDTO.getEndMinuteAm(), workDTO.getSecond());
                long durationAmx = endTimeAmx.getTime() - startTimeAmx.getTime();
                int timeAmx = (int) TimeUnit.MILLISECONDS.toMinutes(durationAmx);
                cal.setTime(startTimeAmx);
                if (lackTime <= timeAmx) {
                    cal.add(Calendar.MINUTE, lackTime);
                }
                if (lackTime > timeAmx && workDTO.getBeginHourPm() != 0) {
                    Date startTimePmx = DateTimeUtil.setDateTime(newDate, workDTO.getBeginHourPm(), workDTO.getBeginMinutePm(), workDTO.getSecond());
                    int lunchTimex = (int) TimeUnit.MILLISECONDS.toMinutes(startTimePmx.getTime() - endTimeAmx.getTime());
                    cal.add(Calendar.MINUTE, (lackTime - timeAmx) + timeAmx + lunchTimex);
                }
                if (lackTime > timeAmx && workDTO.getBeginHourPm() == 0) {
                    lackTime = lackTime - timeAmx;
                    cal.add(Calendar.DAY_OF_MONTH, 1);
                    Date tempDate = cal.getTime();
                    dayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1;
                    if (isWorkingDay(cal, holidays, lstWorkCalendar, dayOfWeek)) {
                        tempDate = getDateExcludeHolidays(tempDate, holidays, lstWorkCalendar, dayOfWeek);
                    }
                    cal.setTime(tempDate);
                    dayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1;
                    WorkTimeWeekendDTO tempDTO = getTimeWork(lstWorkCalendar, dayOfWeek);
                    Date tempSAmx = DateTimeUtil.setDateTime(tempDate, tempDTO.getBeginHourAm(), tempDTO.getBeginMinuteAm(), tempDTO.getSecond());
                    Date tempFAmx = DateTimeUtil.setDateTime(tempDate, tempDTO.getEndHourAm(), tempDTO.getEndMinuteAm(), tempDTO.getSecond());
                    long tempDurationAmx = tempFAmx.getTime() - tempSAmx.getTime();
                    int tempTimeAmx = (int) TimeUnit.MILLISECONDS.toMinutes(tempDurationAmx);
                    cal.setTime(tempSAmx);
                    if (lackTime <= tempTimeAmx) {
                        cal.add(Calendar.MINUTE, lackTime);
                    } else {
                        Date tempTimePmx = DateTimeUtil.setDateTime(tempDate, tempDTO.getBeginHourPm(), tempDTO.getBeginMinutePm(), tempDTO.getSecond());
                        int lunchTimex = (int) TimeUnit.MILLISECONDS.toMinutes(tempTimePmx.getTime() - tempFAmx.getTime());
                        cal.add(Calendar.MINUTE, tempTimeAmx  + lunchTimex + (lackTime - tempTimeAmx));
                    }
                }
                return cal.getTime();
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    private WorkTimeWeekendDTO getTimeWork(List<WorkCalendar> lstWorkCalendar, int dayOfWeek) {
        WorkCalendar workCal = lstWorkCalendar.stream().filter(i -> i.getWorkCalendarId().intValue() == dayOfWeek).findFirst().orElse(null);
        if (Objects.isNull(workCal)) {
            return new WorkTimeWeekendDTO(0, 0, 0, 0, 0, 0, 0, 0, 0);
        }
        String beginTimeAm = workCal.getBeginWorkTimeAM();
        String endTimeAm = workCal.getEndWorkTimeAM();
        String beginTimePm = workCal.getBeginWorkTimePM();
        String endTimePm = workCal.getEndWorkTimePM();
        int beginHourAm = 0;
        int beginMinuteAm = 0;
        int endHourAm = 0;
        int endMinuteAm = 0;
        int beginHourPm = 0;
        int beginMinutePm = 0;
        int endHourPm = 0;
        int endMinutePm = 0;
        if (StringUtils.isNotEmpty(beginTimeAm)) {
            beginHourAm = Integer.parseInt(beginTimeAm.split(Const.SPLIT_CHARACTER)[0]);
            beginMinuteAm = Integer.parseInt(beginTimeAm.split(Const.SPLIT_CHARACTER)[1]);
        }
        if (StringUtils.isNotEmpty(endTimeAm)) {
            endHourAm = Integer.parseInt(endTimeAm.split(Const.SPLIT_CHARACTER)[0]);
            endMinuteAm = Integer.parseInt(endTimeAm.split(Const.SPLIT_CHARACTER)[1]);
        }
        if (StringUtils.isNotEmpty(beginTimePm)) {
            beginHourPm = Integer.parseInt(beginTimePm.split(Const.SPLIT_CHARACTER)[0]);
            beginMinutePm = Integer.parseInt(beginTimePm.split(Const.SPLIT_CHARACTER)[1]);
        }
        if (StringUtils.isNotEmpty(endTimePm)) {
            endHourPm = Integer.parseInt(endTimePm.split(Const.SPLIT_CHARACTER)[0]);
            endMinutePm = Integer.parseInt(endTimePm.split(Const.SPLIT_CHARACTER)[1]);
        }
        return new WorkTimeWeekendDTO(beginHourAm, beginMinuteAm, endHourAm, endMinuteAm, beginHourPm, beginMinutePm, endHourPm, endMinutePm, 0);
    }

    // calculate date exclude holidays
    private Date getDateExcludeHolidays(Date createdDate, List<String> holidays, List<WorkCalendar> lstWorkCalendar, int dayOfWeek) {
        Calendar endCal = Calendar.getInstance();
        endCal.setTime(createdDate);
        endCal.set(Calendar.HOUR_OF_DAY, 0);
        endCal.set(Calendar.MINUTE, 0);
        endCal.set(Calendar.SECOND, 0);

        while (isWorkingDay(endCal, holidays, lstWorkCalendar, dayOfWeek)) {
            endCal.add(Calendar.DAY_OF_MONTH, 1);
            dayOfWeek = endCal.get(Calendar.DAY_OF_WEEK) - 1;
        }

        return endCal.getTime();
    }

    // check date is working day
    private boolean isWorkingDay(Calendar calendar, List<String> holidays, List<WorkCalendar> lstWorkCalendar, int dayOfWeek) {
        return holidays.contains(DateTimeUtil.format(calendar.getTime(), DateTimeUtil.PATTERN))
                || isNotWorkingDay(lstWorkCalendar, dayOfWeek);
    }

    // check day is not working in table workCalendar
    private boolean isNotWorkingDay(List<WorkCalendar> lstWorkCalendar, int dayOfWeek) {
        if (dayOfWeek == 0) { //chu nhat
            return true;
        }
        return lstWorkCalendar.stream().anyMatch(item -> item.getWorkCalendarId().intValue() == dayOfWeek && Const.HOLIDAY_STATUS.equals(item.getStatus()));
    }

    @Override
    public List<HisFaqPushFirebaseDTO> getHisFaqPushFireBase() throws Exception {
        List<HisFaqPushFirebaseDTO> result = new ArrayList<>();
        List<NotificationFaq> lstNotification = new ArrayList<>();
        String title;
        String body;
        List<HisFaqNotificationDTO> hisFaqDTOs = historyFaqDAO.getHistoryFaqPushNotification();
        FunctionConfig config = functionConfigDAO.findAll().get(0);
        Date date = new Date();
        String currentDate = DateTimeUtil.format(date, Const.FORMAT_DATE2);
        List<Topic> topics = topicDAO.findAllNotCheckStatus();
        String employeeId;
        List<Employee> employees = new ArrayList<>();
        List<Employee> employeeAns = new ArrayList<>();
        for (HisFaqNotificationDTO item : hisFaqDTOs) {
            employees = employeeDAO.findByIds(Collections.singletonList(item.getEmployeeId()));
            if (Objects.isNull(item.getTopicId())) {
                employeeId = config.getEmployeeId();
            } else {
                Topic topic = topics.stream().filter(i -> item.getTopicId().equals(i.getTopicId())).findFirst().orElse(null);
                employeeId = topic != null ? topic.getAnswerEmployeeId() : StringUtils.EMPTY;
            }
            List<String> listAnswerEmployeeId = Pattern.compile(",").splitAsStream(employeeId).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(listAnswerEmployeeId)) {
                continue;
            }
            List<Long> employeeIds = listAnswerEmployeeId.stream().distinct().map(Long::valueOf).collect(Collectors.toList());
            employeeAns = employeeDAO.findByIds(employeeIds);

            Date maximum = DateTimeUtil.parseDate(item.getMaximumDate(), Const.FORMAT_DATE2);
            String maximumDate = DateTimeUtil.format(maximum, Const.FORMAT_DATE2);
            Date response = DateTimeUtil.parseDate(item.getResponseDate(), Const.FORMAT_DATE2);
            String responseDate = DateTimeUtil.format(response, Const.FORMAT_DATE2);
            long timeRemain = (maximum.getTime() - response.getTime()) / 60000;

            // check thời gian hiện tại bằng thời gian chờ
            if (currentDate.equals(responseDate)) {
                title = Const.FIREBASE.TITLE_TYPE_3;
                body = "Câu hỏi " + item.getHistoryFaqCode() + " sắp hết hạn sau " + timeRemain + " phút nữa! Bạn dành ít phút để trả lời nhé";
                for (Long employee3 : employeeIds) {
                    NotificationFaq notification3 = NotificationFaq.builder().notificationTitle(title).notificationName(body)
                            .notificationDate(response).employeeId(employee3).isView(1L).historyFaqId(item.getHistoryFaqId())
                            .topicId(item.getTopicId()).notificationType(Const.TYPE_HAS_RECEIVED).typeCms(3L).build();
                    lstNotification.add(notification3);
                }
                addHisFaqPushFirebaseDTO(title, body, employeeAns, item, Const.TYPE_HAS_RECEIVED, 3L, result);
            }

            // check thời gian hiện tại bằng thời gian hết hạn
            if (currentDate.equals(maximumDate)) {
                title = Const.FIREBASE.TITLE_TYPE_4;
                body = "Câu hỏi " + item.getHistoryFaqCode() + " đã hết hạn. Bạn không thể trả lời được nữa rồi!";
                for (Long employee4 : employeeIds) {
                    NotificationFaq notification4 = NotificationFaq.builder().notificationTitle(title).notificationName(body)
                            .notificationDate(maximum).employeeId(employee4).isView(1L).historyFaqId(item.getHistoryFaqId())
                            .topicId(item.getTopicId()).notificationType(Const.TYPE_HAS_RECEIVED).typeCms(4L).build();
                    lstNotification.add(notification4);
                }
                addHisFaqPushFirebaseDTO(title, body, employeeAns, item, Const.TYPE_HAS_RECEIVED, 4L, result);

                // Nguoi hoi
                title = Const.FIREBASE.TITLE_TYPE_5;
                body = "Vì lý do khách quan, câu hỏi " + item.getHistoryFaqCode() + " của bạn chưa nhận được phản hồi. Đừng lo lắng, hãy đặt lại câu hỏi với VTMan Hero nhé!";
                NotificationFaq notification5 = NotificationFaq.builder().notificationTitle(title).notificationName(body)
                        .notificationDate(maximum).employeeId(item.getEmployeeId()).isView(1L).historyFaqId(item.getHistoryFaqId())
                        .topicId(item.getTopicId()).notificationType(Const.TYPE_HAS_SEND).typeCms(5L).build();
                lstNotification.add(notification5);
                addHisFaqPushFirebaseDTO(title, body, employees, item, Const.TYPE_HAS_SEND, 5L, result);
            }
        }
        LOGGER.info("lstNotification: " + lstNotification.toString());
        if (!CollectionUtils.isEmpty(lstNotification)) {
            notificationDAO.insertAll(lstNotification);
        }
        LOGGER.info("result: " + result.toString());
        return result;
    }

    // add HisFaqPushFirebaseDTO
    private void addHisFaqPushFirebaseDTO(String title, String body, List<Employee> employees, HisFaqNotificationDTO item, Long type, Long typeCms, List<HisFaqPushFirebaseDTO> result) {
        if (!CollectionUtils.isEmpty(employees)) {
            List<Long> userIds = employees.stream().map(Employee::getUserId).filter(Objects::nonNull).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(userIds)) {
                FireBaseDataDTO data = FireBaseDataDTO.builder().hisFaqId(String.valueOf(item.getHistoryFaqId()))
                        .topicId(String.valueOf(item.getTopicId())).notificationType(String.valueOf(type)).typeCms(String.valueOf(typeCms)).build();
                for (Long uid : userIds) {
                    HisFaqPushFirebaseDTO dto = HisFaqPushFirebaseDTO.builder().title(title).body(body).userId(uid).data(data).build();
                    result.add(dto);
                }
            }
        }
    }

//    @Override
//    public String insertTokenFcm(TokenFcm tokenFcm) {
//        if (!validationTokenFcm(tokenFcm)) {
//            return StringUtils.EMPTY;
//        }
//        TokenFcm isCheck = tokenFcmDAO.findByToken(tokenFcm.getToken());
//        if (Objects.isNull(isCheck)) {
//            tokenFcmDAO.insertToken(tokenFcm);
//        } else {
//            isCheck.setEmployeeId(tokenFcm.getEmployeeId());
//            isCheck.setToken(tokenFcm.getToken());
//            tokenFcmDAO.updateToken(isCheck);
//        }
//        return tokenFcm.getToken();
//    }

//    // validate input token fcm
//    private boolean validationTokenFcm(TokenFcm tokenFcm) {
//        if (Objects.isNull(tokenFcm)) {
//            return false;
//        }
//        if (Objects.isNull(tokenFcm.getEmployeeId()) || tokenFcm.getEmployeeId() <= 0L) {
//            return false;
//        }
//        if (StringUtils.isEmpty(tokenFcm.getToken())) {
//            return false;
//        }
//        return true;
//    }

    @Override
    @Scheduled(cron = "${cron.job.push.notification}")
    public String pushFirebaseNotification() {
        try {
            List<HisFaqPushFirebaseDTO> lstHisFaqDTO = getHisFaqPushFireBase();
            if (!CollectionUtils.isEmpty(lstHisFaqDTO)) {
                for (HisFaqPushFirebaseDTO hisDTO : lstHisFaqDTO) {
                    pushNotification(hisDTO);
                }
            }
            return Const.SUCCESS;
        } catch (Exception e) {
            LOGGER.error("Push Notification error: " + e.getMessage());
            return e.getMessage();
        }
    }

//    private String pushNotificationOld(HisFaqPushFirebaseDTO dto) throws Exception {
//        JSONObject body = new JSONObject();
//        body.put("to", dto.getUid());
//        body.put("priority", "high");
//
//        JSONObject notification = new JSONObject();
//        notification.put("title",dto.getTitle());
//        notification.put("body",dto.getBody());
//
//        body.put("notification", notification);
//        body.put("data", new JSONObject(new Gson().toJson(dto.getData())));
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.add("Authorization", "key=" + fireBaseServerKey);
//        headers.add("Content-Type", "application/json;charset=utf-8");
//        HttpEntity<String> request = new HttpEntity<>(body.toString(), headers);
//        RestTemplate restTemplate = new RestTemplate();
//        String firebaseResponse = restTemplate.postForObject(fireBaseApiUrl, request, String.class);
//        return firebaseResponse;
//    }

    // call api push notification viettel post
    private String pushNotification(HisFaqPushFirebaseDTO dto) {
        Gson gson = new Gson();
        try {
            JSONObject body = new JSONObject();
            body.put("action", "CHATBOT");
            body.put("app", "evtman");
            body.put("content", dto.getBody());
            body.put("icon", "");
            body.put("receiverID", dto.getUserId());
            body.put("senderID", 0);
            body.put("title", dto.getTitle());
            body.put("mapExt", new JSONObject(gson.toJson(dto.getData())));

            OkHttpClient client = new OkHttpClient();
            MediaType mediaType = MediaType.parse("application/json");
            LOGGER.info("body push notification: " + body.toString());
            RequestBody requestBody = RequestBody.create(mediaType, body.toString());
            Request request = new Request.Builder()
                    .url(vtpNotificationUrl)
                    .method("POST", requestBody)
                    .addHeader("token", vtpNotificationToken)
                    .addHeader("Content-Type", "application/json")
                    .build();
            Response responseFirebase = client.newCall(request).execute();
            LOGGER.info("response push notification:" + responseFirebase.body().string());

            ResponseVTPostDTO response = gson.fromJson(responseFirebase.body().string(), ResponseVTPostDTO.class);
            if (response != null && response.getData() != null && response.getError().getCode() == 200) {
                return Const.SUCCESS;
            }
            return Const.ERROR;

        } catch (Exception e) {
            LOGGER.error("Push notification error: " + e.getMessage());
            return e.getMessage();
        }
    }

    @Override
    @Scheduled(cron = "${cron.job.push.notification}")
    public String jobUpdateStatusExpireDate() {
        try {
            List<HisFaqNotificationDTO> hisFaqDTOs = historyFaqDAO.getHistoryFaqPushNotification();
            String currentDate = DateTimeUtil.format(new Date(), Const.FORMAT_DATE2);
            for (HisFaqNotificationDTO item : hisFaqDTOs) {
                Date maximum = DateTimeUtil.parseDate(item.getMaximumDate(), Const.FORMAT_DATE2);
                String maximumDate = DateTimeUtil.format(maximum, Const.FORMAT_DATE2);

                // Update lai status = 3: Het han tra loi
                if (currentDate.equals(maximumDate)) {
                    HistoryFaq hisFaq = historyFaqDAO.findById(item.getHistoryFaqId());
                    hisFaq.setStatus(Const.STATUS_QUESTION.QUESTION_EXPIRED);
                    historyFaqDAO.updateHisFaq(hisFaq);
                }
            }
            return Const.SUCCESS;
        } catch (Exception e) {
            LOGGER.error("Update status expire date question error: " + e.getMessage());
            return e.getMessage();
        }
    }

    @Override
    public String closeHistoryFaq(HistoryFaqPostReqDTO input) throws Exception {
        if (!validateCancelAndCloseHistoryFaq(input)) {
            return StringUtils.EMPTY;
        }
        HistoryFaq historyFaq = historyFaqDAO.findById(input.getHistoryFaqId());
        if (Objects.isNull(historyFaq)) {
            return Const.NOT_FOUND_QUESTION;
        }
        if (Const.STATUS_QUESTION.QUESTION_CLOSED.equals(historyFaq.getStatus())) {
            return Const.ERROR_CLOSE_QUESTION_CLOSED;
        }
        if (Const.STATUS_QUESTION.QUESTION_CANCELED.equals(historyFaq.getStatus())) {
            return Const.ERROR_CLOSE_QUESTION_CANCEL;
        }
        if (Const.STATUS_QUESTION.QUESTION_EXPIRED.equals(historyFaq.getStatus())) {
            return Const.ERROR_CLOSE_QUESTION_EXPIRED;
        }
        historyFaq.setStatus(Const.STATUS_QUESTION.QUESTION_CLOSED);
        return historyFaqDAO.updateHisFaq(historyFaq);
    }

    @Override
    public String cancelHistoryFaq(HistoryFaqPostReqDTO input) throws Exception {
        if (!validateCancelAndCloseHistoryFaq(input)) {
            return StringUtils.EMPTY;
        }
        HistoryFaq historyFaq = historyFaqDAO.findById(input.getHistoryFaqId());
        if (Objects.isNull(historyFaq)) {
            return Const.NOT_FOUND_QUESTION;
        }
        if (Const.STATUS_QUESTION.QUESTION_EXPIRED.equals(historyFaq.getStatus())) {
            return Const.ERROR_CANCEL_QUESTION_EXPIRED;
        }
        if (Const.STATUS_QUESTION.QUESTION_CLOSED.equals(historyFaq.getStatus())) {
            return Const.ERROR_CANCEL_QUESTION_CLOSED;
        }
        if (Const.STATUS_QUESTION.QUESTION_CANCELED.equals(historyFaq.getStatus())) {
            return Const.ERROR_CANCEL_QUESTION_CANCELED;
        }
        historyFaq.setStatus(Const.STATUS_QUESTION.QUESTION_CANCELED);
        return historyFaqDAO.updateHisFaq(historyFaq);
    }

    // validate input cancel and close history faq
    private boolean validateCancelAndCloseHistoryFaq(HistoryFaqPostReqDTO input) {
        if (Objects.isNull(input)) {
            return false;
        }
        return Objects.nonNull(input.getHistoryFaqId());
    }

    @Override
    public AppInfoDTO getAppInfo(Long employeeId) {
        AppInfoDTO result = new AppInfoDTO();
        List<Topic> topics = topicDAO.findByEmployeeId(employeeId);
        List<FunctionConfig> configs = functionConfigDAO.findAll();
        result.setIsAnswer(!CollectionUtils.isEmpty(topics) && !CollectionUtils.isEmpty(configs));
        result.setEmployeeId(employeeId);
        result.setMaximumTime(configs.get(0).getMaximumWaitingTime());
        result.setRemindingTime(configs.get(0).getRemindingWaitingTime());
        result.setMaximumQASession(configs.get(0).getMaximumQASession());
        return result;
    }

    @Override
    public List<HistoryFaqDTO> countTopicId(Long topicId) {
        return historyFaqDAO.countTopicId(topicId);
    }

    @Override
    public List<NotificationFaqDTO> getCmsNotificationFaqs(Long employeeId, int pageIndex, int pageSize) throws Exception {
        return notificationDAO.cmsNotificationByEmployeeId(employeeId, pageIndex, pageSize);
    }

    @Override
    public int getCmsUnreadNotificationCount(Long employeeId) {
        return notificationDAO.getCmsUnreadNotificationCount(employeeId);
    }

    @Override
    public String readCmsNotificationFaqs(Long notificationId) {
        NotificationFaq notificationFaq = notificationDAO.findById(notificationId);
        notificationFaq.setIsView(0L);
        return notificationDAO.updateNotificationFaq(notificationFaq);
    }

    @Override
    public List<HistoryFaqDTO> checkQuestionStatus(Long departmentId){
        return historyFaqDAO.checkQuestionStatus(departmentId);
    }

    @Override
    public List<NotificationFaq> findNotificationFaqByParams(Long employeeId, Long hisFaqId, Long topicId, Long typeCms) {
        return notificationDAO.findByMultipleParams(employeeId, hisFaqId, topicId, typeCms);
    }
}
