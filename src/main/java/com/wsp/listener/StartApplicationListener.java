package com.wsp.listener;

import com.wsp.bootquartz.QuartzJob;
import com.wsp.bootquartz.QuartzJob2;
import org.quartz.*;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.quartz.impl.triggers.SimpleTriggerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class StartApplicationListener implements ApplicationListener<ContextRefreshedEvent> {
    @Autowired
    Scheduler scheduler;
    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {

        initJob1();
        initJob2();
        try {
//            try {
//                //5s后再运行
//                Thread.sleep(5000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            scheduler.start();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    private void initJob2() {
        TriggerKey treggerKey2 = TriggerKey.triggerKey("trigger2","group2");
        try {
            Trigger trigger2 =  scheduler.getTrigger(treggerKey2);
            if(trigger2==null){
                trigger2 = TriggerBuilder.newTrigger().withIdentity(treggerKey2).usingJobData("trigger2-trans","trigger2-trans-value")
                        .withSchedule(CronScheduleBuilder.cronSchedule("0/5 * * * * ?")).startNow().build();
                //修改misfire的默认值，此值为忽略已经错过的时间的定时任务
                ((CronTriggerImpl)trigger2).setMisfireInstruction(CronTrigger.MISFIRE_INSTRUCTION_DO_NOTHING);
                JobDetail jobDetail2 = JobBuilder.newJob(QuartzJob2.class).usingJobData("job2-trans","job2-trans-value").withIdentity("job2","group2").build();
                scheduler.scheduleJob(jobDetail2,trigger2);
            }
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    private void initJob1() {
        TriggerKey treggerKey = TriggerKey.triggerKey("trigger1","group1");
        try {
            Trigger trigger =  scheduler.getTrigger(treggerKey);
            if(trigger==null){
                System.out.println("空的数据，没有加入进来");
                 trigger = TriggerBuilder.newTrigger().withIdentity(treggerKey).usingJobData("trigger1-trans","trigger1-trans-value")
                        .withSchedule(CronScheduleBuilder.cronSchedule("0/3 * * * * ?")).startNow().build();
                ((CronTriggerImpl)trigger).setMisfireInstruction(CronTrigger.MISFIRE_INSTRUCTION_DO_NOTHING);
                 JobDetail jobDetail = JobBuilder.newJob(QuartzJob.class).usingJobData("job1-trans","job1-trans-value").withIdentity("job1","group1").build();
                scheduler.scheduleJob(jobDetail,trigger);
            }
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }
}
