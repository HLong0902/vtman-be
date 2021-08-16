package com.viettel.vtman.cms.message;

public class Const {
    public static final String XLS = "xls";
    public static final String XLSX = "xlsx";
    public static final String SUCCESS = "SUCCESS";
    public static final String ERROR = "ERROR";
    public static final String FAIL = "FAIL";
    public static final String ERROR_CONTENT_BANNED = "ERROR_CONTENT_BANNED";
    public static final String SPLIT_CHARACTER = ":";
    public static final Long TYPE_HAS_RECEIVED = 1L;
    public static final Long TYPE_HAS_SEND = 2L;
    public static final String FORMAT_DATE = "yyyy-MM-dd HH:mm:ss";
    public static final String FORMAT_DATE2 = "yyyy-MM-dd HH:mm";
    public static final Long HOLIDAY_STATUS = 3L;
    public static final String NOT_FOUND_DATA = "Find not found %s";
    public static final String PATTERN = "^[\\d~` |•√π÷×¶∆@#$%&-\\+()\\/\\*\"':;!\\?£¢€¥^°={}\\©®™℅\\[\\]\\-\\\\_,<>…]+$";
    public static final String ERROR_EVALUATE = "ERROR_EVALUATE";
    public static final String ERROR_MAX_QA_SESSION = "ERROR_MAX_QA_SESSION";
    public static final String NOT_FOUND_QUESTION = "Câu hỏi không tồn tại trên hệ thống.";
//    public static final String ERROR_CANCEL_QUESTION = "Trạng thái câu hỏi không hợp lệ. Chỉ có thể hủy câu hỏi chưa trả lời.";
    public static final String ERROR_FORWARD_QUESTION = "Hero không thể chuyển tiếp câu hỏi vào thời điểm này. Bạn có thể đặt câu hỏi khác hoặc vui lòng đặt lại câu hỏi sau";
    public static final String ERROR_ANSWER_CLOSED = "ERROR_ANSWER_CLOSED";
    public static final String ERROR_ANSWER_CANCEL = "ERROR_ANSWER_CANCEL";
    public static final String ERROR_ANSWER_EXPIRED = "ERROR_ANSWER_EXPIRED";
    public static final String ERROR_NOT_FOUND = "ERROR_NOT_FOUND";
    public static final String ERROR_FORWARD_CLOSED = "Câu hỏi đã đóng, bạn không thể chuyển tiếp câu hỏi.";
    public static final String ERROR_FORWARD_CANCEL = "Câu hỏi đã huỷ, bạn không thể chuyển tiếp câu hỏi.";
    public static final String ERROR_FORWARD_EXPIRED = "Câu hỏi đã hết hạn, bạn không thể chuyển tiếp câu hỏi.";
    public static final String ERROR_CLOSE_QUESTION_CLOSED= "Câu hỏi đã đóng, bạn không thể đóng câu hỏi.";
    public static final String ERROR_CLOSE_QUESTION_CANCEL= "Câu hỏi đã huỷ, bạn không thể đóng câu hỏi.";
    public static final String ERROR_CLOSE_QUESTION_EXPIRED= "Câu hỏi đã hết hạn, bạn không thể đóng câu hỏi.";
    public static final String ERROR_CANCEL_QUESTION_CLOSED= "Câu hỏi đã đóng, bạn không thể huỷ câu hỏi.";
    public static final String ERROR_CANCEL_QUESTION_CANCELED= "Câu hỏi đã huỷ, bạn không thể huỷ câu hỏi.";
    public static final String ERROR_CANCEL_QUESTION_EXPIRED= "Câu hỏi đã hết hạn, bạn không thể huỷ câu hỏi.";

    public static final Long MAX_LENGTH_INVOICE_NO = 20L;

    public static class FIREBASE {
        public static final String TITLE_TYPE_1 = "Bạn đã nhận được câu hỏi";
        public static final String TITLE_TYPE_2 = "Bạn đã nhận được phản hồi";
        public static final String TITLE_TYPE_3 = "Câu hỏi sắp hết hạn";
        public static final String TITLE_TYPE_4 = "Câu hỏi đã hết hạn";
        public static final String TITLE_TYPE_5 = "Không nhận được phản hồi";
    }

    public static class STATUS_QUESTION {
        public static final Long QUESTION_NEW = 1L;
        public static final Long QUESTION_HAD_ANSWER = 2L;
        public static final Long QUESTION_EXPIRED = 3L;
        public static final Long QUESTION_CLOSED = 4L;
        public static final Long QUESTION_CANCELED = 5L;
    }

    public static class API_RESPONSE {
        private API_RESPONSE() {
            throw new IllegalStateException();
        }
        public static final String DUPLICATE_NUMBER_ORDER = "600";
        public static final String NOT_FOUND_TOPIC_CODE= "700";
        public static final String RETURN_CODE_SUCCESS = "200";
        public static final String TOPIC_ID_NOT_FOUND = "401";
        public static final String QUESTION_ID_NOT_FOUND = "402";
        public static final String RETURN_CODE_ERROR = "400";
        public static final String HISTORY_FAQ_ID_NOT_FOUND = "411";
        public static final String RETURN_CODE_ERROR_NOTFOUND = "404";
        public static final String DUPLICATE_PAGE_CODE = "405";
        public static final String DUPLICATE_PAGE_NAME = "406";
        public static final String DUPLICATE_MENU_NAME = "407";
        public static final String TOPIC_NAME_DUPLICATE = "30";
        public static final String TOPIC_CODE_DUPLICATE = "40";
        public static final String ANSWER_DEFINITION = "50";
        public static final String QUESTION_DEFINITION_NAME = "60";
        public static final String DUPLICATE_DAY_OFF = "55";
        public static final String TOPIC_ID = "70";
        public static final String TOPIC_NAME = "71";
        public static final Boolean STATUS_TRUE = true;
        public static final Boolean STATUS_FALSE = false;
        public static final String PAGE_ID_NOT_FOUND = "403";
        public static final String PAGE_ID_IS_USED = "413";
        public static final String EMPLOYEE_ID_NOT_FOUND = "415";
        public static final String ROLE_ID_IS_USED = "417";
        public static final String ROLE_ID_NOT_FOUND = "418";
        public static final String DATA_NEED_TO_FIX = "419";
        public static final String BANNED_CONTENT_ID_NOT_FOUND = "420";
        public static final String QUESTION_NEED_TO_ANSWER = "421";
        public static final String DEPARTMENT_IS_USED = "422";
        public static final String DUPLICATE_MENU_PATH = "423";
        public static final String DUPLICATE_PATH = "424";
        public static final String DUPLICATE_BANNED_CONTENT_NAME = "433";
        public static final String DEPARTMENT_NOT_EXIT = "425";
        public static final String DEPARTMENT_EXIT_TOPIC = "426"; //Không thể dừng hoạt động Phòng ban đã có chủ đề
        public static final String DEPARTMENT_EXIT_CODE = "427"; //Mã phòng ban bị trùng
        public static final String DEPARTMENT_EXIT_NAME = "428"; //Tên phòng ban bị trùng
        public static final String DEPARTMENT_EXIT_FUNC = "429"; //Phòng ban thuộc phòng ban cấu hình

    }
    public static class EXPORT_EXCEL {
        private EXPORT_EXCEL() {
            throw new IllegalStateException();
        }

        public static final String STT = "No.";
        public static final String QUESTION_DEFINITION_NAME = "Câu hỏi";
        public static final String ANSWER_DEFINITION = "Câu trả lời";
        public static final String TOPIC_CODE = "Mã chủ đề";
        public static final String TOPIC_NAME = "Tên chủ đề";
        public static final String TOPIC = "Chủ đề";
        public static final String NUMBER_ORDER = "STT";
        public static final String TT = "Thứ tự";
        public static final String DESCRIPTION = "Ghi chú";
        public static final String STATUS = "Trạng thái";
        public static final String FILE_NAME = "VTMan_Import_Definition_Question";
        public static final String FILE_NAME_QUESTION_DEFINITION = "DANH SÁCH CÂU HỎI TỰ ĐỊNH NGHĨA";
        public static final String FILE_NAME_HISTORY = "Danh sách lịch sử hỏi đáp";
        public static final String FILE_NAME_QUESTION_DEFINITION_ERROR = "Danh sách bản ghi lỗi";
        public static final String DEPARTMENT_NAME = "Phòng ban";
        public static final String QUESTION_NAME = "Người hỏi";
        public static final String ANSWER_NAME = "Người trả lời";
        public static final String QUESTION_DATE = "Thời gian hỏi";
        public static final String ANSWER_DATE = "Thời gian trả lời";
        public static final String TOPIC_QUESTION = "Mã câu hỏi";
        public static final String NAME_QUESTION = "Thông tin người hỏi";
        public static final String NAME_ANSWER = "Thông tin người trả lời";
        public static final String DEPARTMENT_ANSWER = "Phòng ban trả lời";
        public static final String ERROR_DETAIL = "Chi tiết lỗi";
        public static final String KHONG_HOAT_DONG = "Không hoạt động";
        public static final String DANG_HOAT_DONG = "Đang hoạt động";





    }

    public static class QUESTION_DEFINITION_STATUS {
        private QUESTION_DEFINITION_STATUS() {
            throw new IllegalStateException();
        }

        public static final long KHONG_HOAT_DONG = 0L;
        public static final long DANG_HOAT_DONG = 1L;
    }

    public static class PAGE_COMPONENT {
        public static final String TOPIC = "Topic";
        public static final String QUESTION_DEFINITION = "QuestionDefinition";
        public static final String BANNED_CONTENT = "BannedContent";
        public static final String CONTENT_AUTOMATIC = "ContentAutomatic";
        public static final String HISTORY_FAQ = "HistoryFaq";
        public static final String WORK_CALENDAR = "WorkCalendar";
        public static final String FUNCTION_CONFIG = "FunctionConfig";
        public static final String REPORT = "Report";
        public static final String MENU_MANAGEMENT = "Menu";
        public static final String PAGE_MANAGEMENT = "Page";
        public static final String PERMISSION = "Permission";
        public static final String USER_AUTHORIZATION = "UserAuthorization";
        public static final String ROLE = "Role";
        public static final String DEPARTMENT = "Department";
    }

    public static class PAGE_COMPONENT_GROUP {
        public static final String[] SYSTEM_PAGE = {
                PAGE_COMPONENT.MENU_MANAGEMENT
                , PAGE_COMPONENT.PAGE_MANAGEMENT
                , PAGE_COMPONENT.PERMISSION
                , PAGE_COMPONENT.USER_AUTHORIZATION
                , PAGE_COMPONENT.ROLE
                , PAGE_COMPONENT.DEPARTMENT
        };
    }

    public static class ROLE_PERMISSION {
        private ROLE_PERMISSION() {
            throw new IllegalStateException();
        }

        public static final String READ = "1";
        public static final String WRITE = "2";
        public static final String UPDATE = "3";
        public static final String DELETE = "4";
    }

    public static class ROLE_GROUP {
        private ROLE_GROUP() {
            throw new IllegalStateException();
        }

        public static final String[] SYSTEM_ROLE = {"ADMIN"};
    }

    public static class PAGE_PERMISSION {
        private PAGE_PERMISSION() {
            throw new IllegalStateException();
        }

        public static final String[] TOPIC_READ_API = {
                "/api/topic/search"
        };
        public static final String[] TOPIC_WRITE_API = {
                "/api/topic/create"
        };
        public static final String[] TOPIC_UPDATE_API = {
        };
        public static final String[] TOPIC_DELETE_API = {
        };

        public static final String[] QUESTION_DEFINITION_READ_API = {
                "/api/question/search",
                "/api/question/exportQuestion",
                "/api/question/export/Excel/failQuestionTemplate",
                "/api/question/editRecordError"
        };
        public static final String[] QUESTION_DEFINITION_WRITE_API = {
                "/api/question/create",
                "/api/question/importExcel"
        };
        public static final String[] QUESTION_DEFINITION_UPDATE_API = {
        };
        public static final String[] QUESTION_DEFINITION_DELETE_API = {
        };

        public static final String[] BANNED_CONTENT_READ_API = {
                "/api/bannedContent"
        };
        public static final String[] BANNED_CONTENT_WRITE_API = {
                "/api/bannedContent/create"
        };
        public static final String[] BANNED_CONTENT_UPDATE_API = {
        };
        public static final String[] BANNED_CONTENT_DELETE_API = {
        };

        public static final String[] CONTENT_AUTOMATIC_READ_API = {
                "/api/autoContent/find",
                "/api/autoContent/export"
        };
        public static final String[] CONTENT_AUTOMATIC_WRITE_API = {
                "/api/autoContent/create"
        };
        public static final String[] CONTENT_AUTOMATIC_UPDATE_API = {
                "/api/autoContent/update"
        };
        public static final String[] CONTENT_AUTOMATIC_DELETE_API = {
                "/api/autoContent/delete"
        };

        public static final String[] HISTORY_FAQ_READ_API = {
                "/api/historyFaqs/search",
                "/api/historyFaqs/exportHistory",
                "/api/historyFaqs/notification/cmsReaded"
        };
        public static final String[] HISTORY_FAQ_WRITE_API = {
        };
        public static final String[] HISTORY_FAQ_UPDATE_API = {
        };
        public static final String[] HISTORY_FAQ_DELETE_API = {
        };

        public static final String[] WORK_CALENDAR_READ_API = {
                "/api/workCalendar/getCalendar",
                "/api/workCalendar/getDayOff"
        };
        public static final String[] WORK_CALENDAR_WRITE_API = {
                "/api/workCalendar/create"
        };
        public static final String[] WORK_CALENDAR_UPDATE_API = {
                "/api/workCalendar/edit"
        };
        public static final String[] WORK_CALENDAR_DELETE_API = {
                "/api/workCalendar/deleteDayOff"
        };

        public static final String[] FUNCTION_CONFIG_READ_API = {
                "/api/functionConfig/display"
        };
        public static final String[] FUNCTION_CONFIG_WRITE_API = {
        };
        public static final String[] FUNCTION_CONFIG_UPDATE_API = {
                "/api/functionConfig/update"
        };
        public static final String[] FUNCTION_CONFIG_DELETE_API = {
        };

        public static final String[] REPORT_READ_API = {
                "/api/report/answerPercent",
                "/api/report/answerPercent/export"
        };
        public static final String[] REPORT_WRITE_API = {
        };
        public static final String[] REPORT_UPDATE_API = {
        };
        public static final String[] REPORT_DELETE_API = {
        };

        public static final String[] MENU_MANAGEMENT_READ_API = {
                "/api/menu"
        };
        public static final String[] MENU_MANAGEMENT_WRITE_API = {
                "/api/menu/create"
        };
        public static final String[] MENU_MANAGEMENT_UPDATE_API = {
                "/api/menu/update"
        };
        public static final String[] MENU_MANAGEMENT_DELETE_API = {
                "/api/menu/delete"
        };

        public static final String[] PAGE_MANAGEMENT_READ_API = {
                "/api/page/search"
        };
        public static final String[] PAGE_MANAGEMENT_WRITE_API = {
                "/api/page/create"
        };
        public static final String[] PAGE_MANAGEMENT_UPDATE_API = {
                "/api/page/update"
        };
        public static final String[] PAGE_MANAGEMENT_DELETE_API = {
                "/api/page/deletePage"
        };

        public static final String[] PERMISSION_READ_API = {
                "/api/role/listAll",
                "/api/permissionAction/byRoleId"
        };
        public static final String[] PERMISSION_WRITE_API = {
        };
        public static final String[] PERMISSION_UPDATE_API = {
                "/api/permissionAction/update"
        };
        public static final String[] PERMISSION_DELETE_API = {
        };

        public static final String[] USER_AUTHORIZATION_READ_API = {
                "/api/userAuthorization/get",
                "/api/userAuthorization/employeeId",
        };
        public static final String[] USER_AUTHORIZATION_WRITE_API = {
        };
        public static final String[] USER_AUTHORIZATION_UPDATE_API = {
                "/api/userAuthorization/update"
        };
        public static final String[] USER_AUTHORIZATION_DELETE_API = {
        };

        public static final String[] ROLE_READ_API = {
                "/api/role/get"
        };
        public static final String[] ROLE_WRITE_API = {
                "/api/role/create"
        };
        public static final String[] ROLE_UPDATE_API = {
                "api/role/update"
        };
        public static final String[] ROLE_DELETE_API = {
                "api/role/delete"
        };

        public static final String[] DEPARTMENT_READ_API = {
                "api/department/search"
        };
        public static final String[] DEPARTMENT_WRITE_API = {
                "api/department/create"
        };
        public static final String[] DEPARTMENT_UPDATE_API = {
                "api/department/update"
        };
        public static final String[] DEPARTMENT_DELETE_API = {
                "api/department/delete"
        };
    }


}
