package com.rh.core.base.start;

import java.util.Date;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rh.core.util.scheduler.RhJob;
import com.rh.core.util.scheduler.RhJobContext;
import com.rh.core.util.scheduler.RhSchedulerException;

public class SimpleRecoveryJob extends RhJob {

    private Log log = LogFactory.getLog(SimpleRecoveryJob.class);

    private static final String COUNT = "count";

    /**
     * Quartz requires a public empty constructor so that the scheduler can instantiate the class whenever it needs.
     */
    public SimpleRecoveryJob() {
    }

    /**
     * <p>
     * Called by the <code>{@link org.quartz.Scheduler}</code> when a <code>{@link org.quartz.Trigger}</code> fires that
     * is associated with the <code>Job</code>.
     * </p>
     * 
     * @throws RhSchedulerException if there is an exception while executing the job.
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void executeJob(RhJobContext context) throws RhSchedulerException {
        context.put("CURRENT_JOB_DESC", "test code...");
        log.info("SimpleRecoveryJob: " + context.getRhJobDetail().getJobCode() + " starting at " + new Date());

        // delay for ten seconds
        long delay = 60L * 1000L;
        try {
            Thread.sleep(delay);
        } catch (Exception e) {
        }

        Map data = context.getRhJobDetail().getJobData();
        int count;
        if (data.containsKey(COUNT)) {
            count = (Integer) data.get(COUNT);
        } else {
            count = 0;
        }
        count++;
        data.put(COUNT, count);

        log.info("SimpleRecoveryJob: " + context.getRhJobDetail().getJobCode() + " done at " + new Date()
                + "\n Execution #" + count);

    }

    @Override
    public void interrupt() {
        // TODO Auto-generated method stub
    }
}
