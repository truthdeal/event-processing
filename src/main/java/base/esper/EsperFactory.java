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
    EPCompiler compiler;
    Configuration configuration;
    EPRuntime runtime;

    Map<String, EPStatement> statements = new HashMap<String, EPStatement>();
    Map<String, EPDeployment> deployments = new HashMap<String, EPDeployment>();
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

        query = String.format("@name('%s') %s", statementName, query);

        epCompiled = compiler.compile(query, args);

        //DEPLOYMENT
        runtime = EPRuntimeProvider.getDefaultRuntime(configuration);
        EPDeployment deployment;

        deployment = runtime.getDeploymentService().deploy(epCompiled);
        deployments.put(statementName, deployment);

        EPStatement statement = runtime.getDeploymentService().getStatement(deployment.getDeploymentId(), statementName);
        statement.addListener( updateListener);
        statements.put(statementName, statement);

        return runtime.getEventService();
    }
}
