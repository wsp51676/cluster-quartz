package com.wsp.service.impl;

import com.wsp.bootquartz.QuartzJob;
import com.wsp.service.JobService;
import org.quartz.*;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class JobServiceImpl implements JobService {

    @Autowired
    Scheduler scheduler;

    @Override
    public void addJob(String clazzName, String jobName, String jobGroupName,String triggerName, String triggerGroupName, String cronExp, Map<String, Object> param) {
        //构建job信息
        Class<? extends QuartzJobBean> jobClass = null;
        try {
            jobClass = (Class<? extends QuartzJobBean>) Class.forName(clazzName);
        } catch (ClassNotFoundException e) {
            System.out.println("找不到对应的Job类");
        }
        TriggerKey triggerKey = TriggerKey.triggerKey(triggerName,triggerGroupName);
        try {
            Trigger trigger =  scheduler.getTrigger(triggerKey);
            if(trigger!=null){
                System.out.println("存在数据，加入对应的trigger失败");
                return;
            }
            System.out.println("有新的数据加入进来");
            trigger = TriggerBuilder.newTrigger().withIdentity(triggerKey).usingJobData(triggerName, triggerGroupName).withSchedule(CronScheduleBuilder.cronSchedule(cronExp)).startNow().build();
            ((CronTriggerImpl)trigger).setMisfireInstruction(CronTrigger.MISFIRE_INSTRUCTION_DO_NOTHING);
            if (param != null) {
                trigger.getJobDataMap().putAll(param);
            }
            JobDetail jobDetail = JobBuilder.newJob(jobClass).usingJobData(jobName, jobGroupName).withIdentity(jobName, jobGroupName).build();
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        return;
    }

    @Override
    public void pauseJob(String jobName, String groupName) {
        try {
            scheduler.pauseJob(JobKey.jobKey(jobName, groupName));
        } catch (SchedulerException e) {
            System.out.println("暂停任务失败"+ e);
        }
    }

    @Override
    public void resumeJob(String jobName, String groupName) {
        try {
            scheduler.resumeJob(JobKey.jobKey(jobName, groupName));
        } catch (SchedulerException e) {
            System.out.println("恢复任务失败"+ e);
        }
    }

    @Override
    public void runOnce(String jobName, String groupName) {
        try {
            scheduler.triggerJob(JobKey.jobKey(jobName, groupName));
        } catch (SchedulerException e) {
            System.out.println("立即运行一次定时任务失败"+e);
        }
    }

    /**
     * 必须走update操作，直接改cron或者传递数据不会成功，
     * 因为启动的时候是直接从数据库中去的数据；代码只是把定时任务数据同步到库中而已
     * @param triggerName
     * @param triggerGroupName
     * @param cronExp
     * @param param
     */
    @Override
    public void updateJob(String triggerName, String triggerGroupName, String cronExp, Map<String, Object> param) {
        try {
            TriggerKey triggerKey = TriggerKey.triggerKey(triggerName, triggerGroupName);
            CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
            if (cronExp != null) {
                // 表达式调度构建器
                CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cronExp);
                // 按新的cronExpression表达式重新构建trigger
                trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();
            }
            //修改map
            if (param != null) {
                trigger.getJobDataMap().putAll(param);
            }
            // 按新的trigger重新设置job执行
            scheduler.rescheduleJob(triggerKey, trigger);
        } catch (Exception e) {
            System.out.println("更新任务失败"+ e);
        }
    }

    @Override
    public void deleteJob(String jobName, String groupName,String triggerName, String triggerGroupName) {
        try {
            //暂停、移除、删除
            scheduler.pauseTrigger(TriggerKey.triggerKey(triggerName, triggerGroupName));
            scheduler.unscheduleJob(TriggerKey.triggerKey(triggerName, triggerGroupName));
            scheduler.deleteJob(JobKey.jobKey(jobName, groupName));
        } catch (Exception e) {
            System.out.println("删除任务失败"+ e);
        }
    }

    @Override
    public void startAllJobs() {
        try {
            scheduler.start();
        } catch (Exception e) {
            System.out.println("开启所有的任务失败"+e);
        }
    }

    @Override
    public void pauseAllJobs() {
        try {
            scheduler.pauseAll();
        } catch (Exception e) {
            System.out.println("暂停所有任务失败"+e);
        }
    }

    @Override
    public void resumeAllJobs() {
        try {
            scheduler.resumeAll();
        } catch (Exception e) {
            System.out.println("恢复所有任务失败"+ e);
        }
    }

    @Override
    public void shutdownAllJobs() {
        try {

            if (!scheduler.isShutdown()) {
                // 需谨慎操作关闭scheduler容器
                // scheduler生命周期结束，无法再 start() 启动scheduler
                scheduler.shutdown(true);
            }
        } catch (Exception e) {
            System.out.println("关闭所有的任务失败"+e);
        }
    }
}
