package com.wsp.controller;

import com.wsp.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class QuartzController {

    @Autowired
    JobService jobService;

    @PostMapping("/quartz/addJob")
    public boolean addJob(@RequestParam("className")String className,
                        @RequestParam("jobName")String jobName,
                        @RequestParam("jobGroupName")String jobGroupName,
                        @RequestParam("triggerName")String triggerName,
                        @RequestParam("triggerGroupName")String triggerGroupName,
                        @RequestParam("cronExp")String cronExp,
                        @RequestParam(value = "param",required = false)Map<String, Object> param) {
        jobService.addJob(className,jobName,jobGroupName,triggerName,triggerGroupName,cronExp,param);
        return true;
    }
    @PostMapping("/quartz/updateJob")
    public boolean updateJob(
            @RequestParam("triggerName") String triggerName,
            @RequestParam("triggerGroupName") String triggerGroupName,
            @RequestParam("cronExp") String cronExp,
            @RequestParam(value = "param", required = false) Map<String, Object> param) {
        jobService.updateJob(triggerName,triggerGroupName,cronExp,param);
        return true;
    }
}
