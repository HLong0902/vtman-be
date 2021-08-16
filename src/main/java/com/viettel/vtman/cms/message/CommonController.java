package com.viettel.vtman.cms.message;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public abstract class CommonController {
    protected <T> ResponseEntity<?> toSuccessResult(T data) {
        ResponseMessage<T> message = new ResponseMessage<>();

        message.setCode(Const.API_RESPONSE.RETURN_CODE_SUCCESS);
        message.setSuccess(Const.API_RESPONSE.STATUS_TRUE);
        message.setDescription("SUCCESS");
        message.setData(data);

        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    protected <T> ResponseEntity<?> toSuccessResultErrorTopicId(T data) {
        ResponseMessage<T> message = new ResponseMessage<>();

        message.setCode(Const.API_RESPONSE.TOPIC_ID);
        message.setSuccess(Const.API_RESPONSE.STATUS_TRUE);
        message.setData(data);


        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    protected <T> ResponseEntity<?> toSuccessResultErrorTopicName(T data) {
        ResponseMessage<T> message = new ResponseMessage<>();

        message.setCode(Const.API_RESPONSE.TOPIC_NAME);
        message.setSuccess(Const.API_RESPONSE.STATUS_TRUE);
        message.setData(data);


        return new ResponseEntity<>(message, HttpStatus.OK);
    }


    protected <T> ResponseEntity<?> toSuccessResultTopicId(T data) {
        ResponseMessage<T> message = new ResponseMessage<>();

        message.setCode(Const.API_RESPONSE.ANSWER_DEFINITION);
        message.setSuccess(Const.API_RESPONSE.STATUS_TRUE);
        message.setData(data);


        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    protected <T> ResponseEntity<?> toDupplicateDayOff(T data) {
        ResponseMessage<T> message = new ResponseMessage<>();

        message.setCode(Const.API_RESPONSE.DUPLICATE_DAY_OFF);
        message.setSuccess(Const.API_RESPONSE.STATUS_TRUE);
        message.setData(data);
        return new ResponseEntity<>(message, HttpStatus.OK);
    }


    protected <T> ResponseEntity<?> toExceptionResult(String errorMessage, String code) {
        ResponseMessage<T> message = new ResponseMessage<>();

        message.setSuccess(Const.API_RESPONSE.STATUS_FALSE);
        message.setCode(code);
        message.setDescription(errorMessage);

        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    protected  <T> ResponseEntity<?> toFixData(T data){
        ResponseMessage<T> message = new ResponseMessage<>();
        message.setDescription("CHANGE_IS_NOT_ALLOWED, PLEASE_FIX_DATA_BEFORE");
        message.setCode(Const.API_RESPONSE.DATA_NEED_TO_FIX);
        message.setSuccess(Const.API_RESPONSE.STATUS_FALSE);
        message.setData(data);
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    protected <T> ResponseEntity<?> toAnswerQuestions(T data){
        ResponseMessage<T> message = new ResponseMessage<>();
        message.setDescription("MAKE_SURE_ALL_QUESTIONS_WERE_ANSWERED");
        message.setSuccess(Const.API_RESPONSE.STATUS_FALSE);
        message.setCode(Const.API_RESPONSE.QUESTION_NEED_TO_ANSWER);
        message.setData(data);
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    protected <T> ResponseEntity<?> toExceptionResult(String errorMessage, T data, String code) {
        ResponseMessage<T> message = new ResponseMessage<>();

        message.setSuccess(Const.API_RESPONSE.STATUS_FALSE);
        message.setCode(code);
        message.setDescription(errorMessage);
        message.setData(data);

        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }
}
