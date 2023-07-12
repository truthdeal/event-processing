package base.esper;
import com.espertech.esper.common.client.EPCompiled;
import com.espertech.esper.common.client.configuration.Configuration;
import com.espertech.esper.compiler.client.CompilerArguments;
import com.espertech.esper.compiler.client.EPCompileException;
import com.espertech.esper.compiler.client.EPCompiler;
import com.espertech.esper.compiler.client.EPCompilerProvider;
import com.espertech.esper.runtime.client.*;

import java.util.HashMap;
import java.util.Map;

public class EsperFactory {
    public static EPCompiler compiler;
    public static Configuration configuration;
    public static EPRuntime runtime;

    public Map<String, EPStatement> statements = new HashMap<String, EPStatement>();
    public Map<String, EPDeployment> deployments = new HashMap<String, EPDeployment>();
    public  EsperFactory() {
        compiler = EPCompilerProvider.getCompiler();
        configuration = new Configuration();

    }

    public void AddEventType(Class eventType) {

        configuration.getCommon().addEventType(eventType);
    }

    //ex. parameter "@name('my-statement') select name, age from PersonEvent"
    public EPEventService DeployingQuery(String statementName, String query, UpdateListener updateListener ) throws EPCompileException, EPDeployException {
        CompilerArguments args = new CompilerArguments(configuration);
        EPCompiled epCompiled;

        //query = "SELECT * FROM BaseEvent WHERE eventType='E'";
        //query = "SELECT * FROM pattern [every (event1=BaseEvent(eventType = 'A') -> event2=BaseEvent(eventType = 'B'))]";
        query = String.format("@name('%s') %s", statementName, query);

        epCompiled = compiler.compile(query, args);

        //DEPLOYMENT
        runtime = EPRuntimeProvider.getDefaultRuntime(configuration);
        EPDeployment deployment;

        EPDeploymentService deploymentService = runtime.getDeploymentService();
        deployment = deploymentService.deploy(epCompiled);
        deployments.put(statementName, deployment);

        EPStatement statement = deploymentService.getStatement(deployment.getDeploymentId(), statementName);

        if(statement == null){
            System.out.println(statementName + " is null");
        }
        else {
            statement.addListener(updateListener);
            statements.put(statementName, statement);
        }
        return runtime.getEventService();
    }
}
