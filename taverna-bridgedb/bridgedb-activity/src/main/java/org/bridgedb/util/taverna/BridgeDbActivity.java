package org.bridgedb.util.taverna;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bridgedb.BridgeDb;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;
import org.bridgedb.bio.BioDataSource;

import net.sf.taverna.t2.invocation.InvocationContext;
import net.sf.taverna.t2.reference.ReferenceService;
import net.sf.taverna.t2.reference.T2Reference;
import net.sf.taverna.t2.workflowmodel.processor.activity.AbstractAsynchronousActivity;
import net.sf.taverna.t2.workflowmodel.processor.activity.ActivityConfigurationException;
import net.sf.taverna.t2.workflowmodel.processor.activity.AsynchronousActivity;
import net.sf.taverna.t2.workflowmodel.processor.activity.AsynchronousActivityCallback;

public class BridgeDbActivity extends
		AbstractAsynchronousActivity<BridgeDbActivityConfigurationBean>
		implements AsynchronousActivity<BridgeDbActivityConfigurationBean> {

	/*
	 * Best practice: Keep port names as constants to avoid misspelling. This
	 * would not apply if port names are looked up dynamically from the service
	 * operation, like done for WSDL services.
	 */
	private static final String IN_ID = "identifier";
	private static final String IN_DS = "source datasource";
	private static final String IN_DESTDS = "target datasource";
	private static final String OUT_IDS = "identifiers";
	private static final String OUT_DSS = "datasources";
	
	private BridgeDbActivityConfigurationBean configBean;

	private IDMapper mapper;
	
	private boolean isTargetted;
	
	@Override
	public void configure(BridgeDbActivityConfigurationBean configBean)
			throws ActivityConfigurationException {

		// Any pre-config sanity checks
		if (configBean.getConnectionString().equals("")) {
		}
		
		// Store for getConfiguration(), but you could also make
		// getConfiguration() return a new bean from other sources
		this.configBean = configBean;

		// OPTIONAL: 

		synchronized(this) 
		{
			String className = configBean.getDriverClass();
			String connectionString = configBean.getConnectionString();
			BioDataSource.init();
			try
			{
				Class.forName(className);
				mapper = BridgeDb.connect (connectionString);
			}
			catch (ClassNotFoundException ex)
			{
				throw new ActivityConfigurationException(
					"Could not find BridgeDb driver class '" +
					className + "'", ex);
			}
			catch (IDMapperException ex)
			{
				throw new ActivityConfigurationException(
					"Could not establish connection to mapping source '" +
					connectionString + "'", ex);
			}

		}
		
		// REQUIRED: (Re)create input/output ports depending on configuration
		configurePorts();
	}

	protected void configurePorts() {
		// In case we are being reconfigured - remove existing ports first
		// to avoid duplicates
		removeInputs();
		removeOutputs();

		// FIXME: Replace with your input and output port definitions
		
		// Hard coded input port, expecting a single String
		addInput(IN_ID, 0, true, null, String.class);
		addInput(IN_DS, 0, true, null, String.class);

		// Optional ports depending on configuration
		if (isTargetted) {
			addInput(IN_DESTDS, 0, true, null, String.class);
		}
		
		// Single value output port (depth 0)
		// Output port with list of values (depth 1)
		
		addOutput(OUT_IDS, 1);
		addOutput(OUT_DSS, 1);

	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void executeAsynch(final Map<String, T2Reference> inputs,
			final AsynchronousActivityCallback callback) {
		// Don't execute service directly now, request to be run ask to be run
		// from thread pool and return asynchronously
		callback.requestRun(new Runnable() {
			
			public void run() {
				InvocationContext context = callback
						.getContext();
				ReferenceService referenceService = context
						.getReferenceService();
				// Resolve inputs 				
				String id = (String) referenceService.renderIdentifier(inputs.get(IN_ID), 
						String.class, context);
				String ds = (String) referenceService.renderIdentifier(inputs.get(IN_DS), 
						String.class, context);
				
				String targetDs = null;
				if (isTargetted)
				{
					targetDs = (String)referenceService.renderIdentifier(inputs.get(IN_DESTDS),
							String.class, context);
				}
				
				Set<Xref> result;
				Xref srcRef = new Xref (id, DataSource.getByFullName(ds));
				try
				{					
					synchronized (this)
					{
						if (isTargetted)
						{
							result = mapper.mapID(srcRef, DataSource.getByFullName(targetDs));	
						}
						else
						{
							result = mapper.mapID(srcRef);
						}
					}
				}
				catch (IDMapperException ex)
				{
					callback.fail("ID mapping of " + srcRef + " failed.", ex);
					return;
				}

				// Register outputs
				Map<String, T2Reference> outputs = new HashMap<String, T2Reference>();
				String simpleValue = "simple";
				T2Reference simpleRef = referenceService.register(simpleValue, 0, true, context);
				outputs.put(OUT_IDS, simpleRef);

				// For list outputs, only need to register the top level list
				List<String> ids = new ArrayList<String>();
				List<String> dss = new ArrayList<String>();
				for (Xref ref : result)
				{
					ids.add (ref.getId());
					dss.add (ref.getDataSource().getFullName());
				}
				T2Reference idsRef = referenceService.register(ids, 1, true, context);
				outputs.put(OUT_IDS, idsRef);
				T2Reference dssRef = referenceService.register(dss, 1, true, context);
				outputs.put(OUT_DSS, dssRef);

//				if (optionalPorts) {
//					// Populate our optional output port					
//					// NOTE: Need to return output values for all defined output ports
//					String report = "Everything OK";
//					outputs.put(OUT_DSS, referenceService.register(report,
//							0, true, context));
//				}
				
				// return map of output data, with empty index array as this is
				// the only and final result (this index parameter is used if
				// pipelining output)
				callback.receiveResult(outputs, new int[0]);
			}
		});
	}

	@Override
	public BridgeDbActivityConfigurationBean getConfiguration() {
		return this.configBean;
	}

}
