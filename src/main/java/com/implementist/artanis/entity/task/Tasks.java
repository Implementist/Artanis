package com.implementist.artanis.entity.task;

import com.implementist.artanis.entity.taskdata.BaskTaskData;
import com.implementist.artanis.entity.taskdata.InitializeTaskData;
import com.implementist.artanis.entity.taskdata.SummaryTaskData;
import com.implementist.artanis.entity.taskdata.UrgeTaskData;

import javax.servlet.ServletContext;

public class Tasks {
    public static BaseTask newInitializeTask(ServletContext context, BaskTaskData taskData){
        return new InitializeTask(context, (InitializeTaskData) taskData);
    }

    public static BaseTask newSummaryTask(ServletContext context, BaskTaskData taskData){
        return new SummaryTask(context, (SummaryTaskData) taskData);
    }

    public static BaseTask newUrgeTask(ServletContext context, BaskTaskData taskData){
        return new UrgeTask(context, (UrgeTaskData) taskData);
    }
}
