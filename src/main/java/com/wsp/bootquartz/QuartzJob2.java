package com.wsp.bootquartz;

import com.alibaba.fastjson.JSON;
import org.quartz.*;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.Date;

@DisallowConcurrentExecution
@PersistJobDataAfterExecution
public class QuartzJob2 extends QuartzJobBean {


    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {

//        try {
//            //这个是获取调度实例
//            System.out.println(jobExecutionContext.getScheduler().getSchedulerInstanceId());
//        } catch (SchedulerException e) {
//            e.printStackTrace();
//        }
        System.out.println("我是:job2=======执行时间:"+new Date()+"task-nbame=:"+jobExecutionContext.getJobDetail().getKey().getName());
       // System.out.println("job2获取到的传递参数值是:"+ JSON.toJSONString(jobExecutionContext.getMergedJobDataMap()));

    }
}
