package br.com.dojot.mutualauthentication.kerberosintegration.messaging.consumer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import br.com.dojot.mutualauthentication.communication.constants.CommunicationKeysConstants;
import br.com.dojot.mutualauthentication.communication.facade.api.CommunicationFacade;
import br.com.dojot.mutualauthentication.communication.facade.impl.CommunicationFacadeBean;
import br.com.dojot.mutualauthentication.kerberosintegration.dao.api.ConfigDAO;
import br.com.dojot.mutualauthentication.kerberosintegration.messaging.api.ConsumerStartup;
import br.com.dojot.mutualauthentication.kerberosintegration.utils.KerberosIntegrationConstants;

@Startup
@Singleton
public class ConsumerStartupImpl implements ConsumerStartup {
	
    @Resource
    ManagedExecutorService managedExecutorService;
    
    @Inject
    Instance<ConsumerLoopKerberosManagerProcessing> consumerLoopKerberosManagerInstance;
    
    @Inject
    Instance<ConsumerLoopKerberosProtocolProcessing> consumerLoopKerberosProtocolInstance;
    
    private List<ConsumerLoopGeneric> tasks;
    
    @EJB
    private ConfigDAO configDAO;

    public void executeAsync() throws ExecutionException, InterruptedException {
    	tasks = new ArrayList<>();
    	CommunicationFacade facade = new CommunicationFacadeBean();
    	String node = (String) facade.requestNodeConfigs().get(CommunicationKeysConstants.KEY_NODE);
    	String version = (String) facade.requestNodeConfigs().get(CommunicationKeysConstants.KEY_VERSION);
    	String bootstrapServers = configDAO.findConfigurationByKey(KerberosIntegrationConstants.PARAM_KAFKA_BOOTSTRAP_SERVERS);
    	int numberOfConsumers = Integer.valueOf(configDAO.findConfigurationByKey(KerberosIntegrationConstants.PARAM_KAFKA_NUMBER_OF_CONSUMERS));
	    for(int i=0; i<numberOfConsumers; i++) {
	    	ConsumerLoopKerberosManagerProcessing consumerKerberosManagerLoop = consumerLoopKerberosManagerInstance.get();
	    	consumerKerberosManagerLoop.setUniqueIdentifier(node, i);
	    	consumerKerberosManagerLoop.setVersion(version);
	    	consumerKerberosManagerLoop.setBootstrapServers(bootstrapServers);
	    	tasks.add(consumerKerberosManagerLoop);
	        this.managedExecutorService.submit(consumerKerberosManagerLoop);
	        
	        ConsumerLoopKerberosProtocolProcessing consumerKerberosProtocolLoop = consumerLoopKerberosProtocolInstance.get();
	        consumerKerberosProtocolLoop.setUniqueIdentifier(node, i);
	        consumerKerberosProtocolLoop.setVersion(version);
	        consumerKerberosProtocolLoop.setBootstrapServers(bootstrapServers);
	        tasks.add(consumerKerberosProtocolLoop);
	        this.managedExecutorService.submit(consumerKerberosProtocolLoop);
	    }
    }

	@PostConstruct
	public void init() {
		try {
			this.executeAsync();
			System.out.println("Started all kerberos consumers...");
		} catch (ExecutionException e) {
			System.out.println("ExecutionException" + e.getMessage());
		} catch (InterruptedException e) {
			System.out.println("InterruptedException" + e.getMessage());
		}
	}
	
    @PreDestroy
	public void destroy() {
		System.out.println("Removing all Kerberos consumers");
		for (ConsumerLoopGeneric task : tasks) {
			task.shutdown();
		}
		System.out.println("All Kerberos consumers were terminated.");
	}
}