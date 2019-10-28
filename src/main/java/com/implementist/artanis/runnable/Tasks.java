package com.implementist.artanis.runnable;

import com.implementist.artanis.entity.taskdata.BaseTaskData;
import com.implementist.artanis.entity.taskdata.InitializeTaskData;
import com.implementist.artanis.entity.taskdata.SummaryTaskData;
import com.implementist.artanis.entity.taskdata.UrgeTaskData;
import org.springframework.context.ApplicationContext;

/**
 * @author Implementist
 */
public class Tasks {
    public static BaseTask newInitializeTask(ApplicationContext context, BaseTaskData taskData){
        return new InitializeTask(context, (InitializeTaskData) taskData);
    }

    public static BaseTask newSummaryTask(ApplicationContext context, BaseTaskData taskData){
        return new SummaryTask(context, (SummaryTaskData) taskData);
    }

    public static BaseTask newUrgeTask(ApplicationContext context, BaseTaskData taskData){
        return new UrgeTask(context, (UrgeTaskData) taskData);
    }
}
