package org.talend.job.audit;

import org.talend.logging.audit.AuditEvent;
import org.talend.logging.audit.Context;
import org.talend.logging.audit.EventAuditLogger;
import org.talend.logging.audit.LogLevel;

public interface JobAuditLogger extends EventAuditLogger {
	
	@AuditEvent(category = "job", message = "Run job : job_name:{jobName}, job_version:{jobVersion}, job_id:{jobId}", level = LogLevel.INFO)
    void runjob(Context context);
	
	@AuditEvent(category = "connector", message = "Component run : job_name:{jobName}, job_version:{jobVersion}, job_id:{jobId}, connector_type:{connectorType}, connector_id:{connectorId}, parameters:{parameters}", level = LogLevel.INFO)
    void runcomponent(Context context);

    @AuditEvent(category = "connector", message = "Component {connectorType} received {rows} rows", level = LogLevel.INFO)
    void flow(Context context);

}
