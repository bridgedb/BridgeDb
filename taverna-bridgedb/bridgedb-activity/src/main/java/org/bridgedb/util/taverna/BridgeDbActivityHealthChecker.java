package org.bridgedb.util.taverna;

import java.util.ArrayList;
import java.util.List;

import net.sf.taverna.t2.visit.VisitReport;
import net.sf.taverna.t2.visit.VisitReport.Status;
import net.sf.taverna.t2.workflowmodel.health.HealthCheck;
import net.sf.taverna.t2.workflowmodel.health.HealthChecker;

/**
 * Example health checker
 * 
 */
public class BridgeDbActivityHealthChecker implements
		HealthChecker<BridgeDbActivity> {

	public boolean canVisit(Object o) {
		// Return True if we can visit the object. We could do
		// deeper (but not time consuming) checks here, for instance
		// if the health checker only deals with ExampleActivity where
		// a certain configuration option is enabled.
		return o instanceof BridgeDbActivity;
	}

	public boolean isTimeConsuming() {
		// Return true if the health checker does a network lookup
		// or similar time consuming checks, in which case
		// it would only be performed when using File->Validate workflow
		// or File->Run.
		return false;
	}

	public VisitReport visit(BridgeDbActivity activity, List<Object> ancestry) {
		BridgeDbActivityConfigurationBean config = activity.getConfiguration();

		// We'll build a list of subreports
		List<VisitReport> subReports = new ArrayList<VisitReport>();

//		if (!config.getExampleUri().isAbsolute()) {
//			// Report Severe problems we know won't work
//			VisitReport report = new VisitReport(HealthCheck.getInstance(),
//					activity, "Example URI must be absolute", HealthCheck.INVALID_URL,
//					Status.SEVERE);
//			subReports.add(report);
//		}
//
//		if (config.getExampleString().equals("")) {
//			// Warning on possible problems
//			subReports.add(new VisitReport(HealthCheck.getInstance(), activity,
//					"Example string empty", HealthCheck.NO_CONFIGURATION,
//					Status.WARNING));
//		}

		// The default explanation here will be used if the subreports list is
		// empty
		return new VisitReport(HealthCheck.getInstance(), activity,
				"Example service OK", HealthCheck.NO_PROBLEM, subReports);
	}

}
