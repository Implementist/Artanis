package com.implementist.artanis.runnable;

import com.implementist.artanis.entity.taskdata.BaskTaskData;
import com.implementist.artanis.entity.taskdata.InitializeTaskData;
import com.implementist.artanis.entity.taskdata.SummaryTaskData;
import com.implementist.artanis.entity.taskdata.UrgeTaskData;
import org.springframework.context.ApplicationContext;

/**
 * @author Implementist
 */
public class Tasks {
    public static BaseTask newInitializeTask(ApplicationContext context, BaskTaskData taskDataUnit){
        return new InitializeTask(context, (InitializeTaskData) taskDataUnit);
    }

    public static BaseTask newSummaryTask(ApplicationContext context, BaskTaskData taskDataUnit){
        return new SummaryTask(context, (SummaryTaskData) taskDataUnit);
    }

    public static BaseTask newUrgeTask(ApplicationContext context, BaskTaskData taskDataUnit){
        return new UrgeTask(context, (UrgeTaskData) taskDataUnit);
    }
}
