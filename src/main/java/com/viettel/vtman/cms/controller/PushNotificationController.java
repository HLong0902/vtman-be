package com.viettel.vtman.cms.controller;

import com.viettel.vtman.cms.dto.HistoryFaqCMSDTO;
import com.viettel.vtman.cms.dto.ObjectResultPage;
import com.viettel.vtman.cms.dto.PushNotificationDTO;
import com.viettel.vtman.cms.message.CommonController;
import com.viettel.vtman.cms.message.Const;
import com.viettel.vtman.cms.service.HistoryFaqService;
import com.viettel.vtman.cms.service.PushNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/pushNotification")
public class PushNotificationController extends CommonController {
    @Autowired
    private PushNotificationService pushNotificationService;

    @Autowired
    private HistoryFaqService historyFaqService;

    @GetMapping(value = "/get")
    public ResponseEntity<?> getNotification(){
        try{
            List<PushNotificationDTO> pushNotificationDTOList = pushNotificationService.getNotificationList();
            return toSuccessResult(pushNotificationDTOList);
        }catch (Exception e){
            return toExceptionResult(e.getMessage(), Const.API_RESPONSE.RETURN_CODE_ERROR);
        }
    }

    @GetMapping(value = "/getAll")
    public ResponseEntity<?> getAllNotification (@RequestParam("page") Long page,
                                                 @RequestParam("pageSize") Long pageSize,
                                                 @RequestParam("answerEmployeeId") Long answerEmployeeId){
        try {
            ObjectResultPage objectResultPage = new ObjectResultPage();
            objectResultPage.setPage(page);
            objectResultPage.setPageSize(pageSize);

            List<PushNotificationDTO> pushNotificationDTOList = pushNotificationService.getAll(answerEmployeeId, objectResultPage);

            PushNotificationDTO data = new PushNotificationDTO();
            data.setData(pushNotificationDTOList);
            data.setTotalRecord(objectResultPage.getTotalRecord());
            return toSuccessResult(pushNotificationDTOList);
        }catch (Exception e){
            return toExceptionResult(e.getMessage(), Const.API_RESPONSE.RETURN_CODE_ERROR);
        }
    }

    @GetMapping(value = "/historyFAQ")
    public ResponseEntity<?> getHistoryFAQ(@RequestParam (value = "historyFaqId") Long historyFaqId){
        try{
            HistoryFaqCMSDTO historyFaqCMSDTO = historyFaqService.getHisFaqByIdFaq(historyFaqId);
            return toSuccessResult(historyFaqCMSDTO);
        }catch (Exception e){
            return toExceptionResult(e.getMessage(), Const.API_RESPONSE.RETURN_CODE_ERROR);
        }
    }

}
