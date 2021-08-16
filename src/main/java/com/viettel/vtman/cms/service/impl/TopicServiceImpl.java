package com.viettel.vtman.cms.service.impl;

import com.viettel.vtman.cms.dao.TopicDAO;
import com.viettel.vtman.cms.dto.ObjectResult;
import com.viettel.vtman.cms.dto.TopicDto;
import com.viettel.vtman.cms.entity.Topic;
import com.viettel.vtman.cms.service.TopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TopicServiceImpl implements TopicService {
    @Autowired
    private TopicDAO topicDAO;

    @Override
    public List<Topic> findAll() {
        return topicDAO.findAll();
    }

    @Override
    public List<Topic> findAllWithNotNullAnsEmpId() {
        return topicDAO.findAllWithNotNullAnsEmpId();
    }

    @Override
    public List<TopicDto> searchByName(String topicName, Long departmentId, ObjectResult objectResult) {
        return topicDAO.searchByName(topicName, departmentId, objectResult);
    }

    @Override
    public void deleteById(Long topicId) {
        topicDAO.deleteById(topicId);
    }

    @Override
    public Topic findById(Long topicId) {
        return topicDAO.findById(topicId);
    }

    @Override
    public Topic save(Topic topic) {
        return topicDAO.insert(topic);
    }

    @Override
    public Topic update(Topic topic) {
        return topicDAO.updateTopic(topic);
    }

    @Override
    public TopicDto findByName(String topicName) {
        return topicDAO.findByName(topicName);
    }

    @Override
    public TopicDto findByCode(String topicCode) {
        return topicDAO.findByCode(topicCode);
    }

    @Override
    public TopicDto findId(Long topicId) {
        return topicDAO.findId(topicId);
    }

    @Override
    public List<TopicDto> findAllByStatus() {
        return topicDAO.findAllByStatus();
    }

    @Override
    public List<TopicDto> getForwardTopic(Long topicId){
        List<TopicDto> forwardTopic = topicDAO.getForwardTopic(topicId);
        return forwardTopic;
    }

}
