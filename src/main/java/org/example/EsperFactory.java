package org.example;
import com.espertech.esper.common.client.EPCompiled;
import com.espertech.esper.common.client.configuration.Configuration;
import com.espertech.esper.compiler.client.CompilerArguments;
import com.espertech.esper.compiler.client.EPCompileException;
import com.espertech.esper.compiler.client.EPCompiler;
import com.espertech.esper.compiler.client.EPCompilerProvider;
import com.espertech.esper.runtime.client.*;

public class EsperFactory {
    EPCompiler compiler;
    Configuration configuration;
    public void  Init() {
        compiler = EPCompilerProvider.getCompiler();
        configuration = new Configuration();
    }

    public void AddEventType(Class eventType) {
        configuration.getCommon().addEventType(eventType);
    }

    //ex. parameter "@name('my-statement') select name, age from PersonEvent"
    public EPEventService DeployingQuery(String statementName, String query ) throws EPCompileException, EPDeployException {
        CompilerArguments args = new CompilerArguments(configuration);
        EPCompiled epCompiled;

        query = String.format("@name('%s') %s", statementName, query);
        epCompiled = compiler.compile(query, args);

        //DEPLOYMENT
        EPRuntime runtime = EPRuntimeProvider.getDefaultRuntime(configuration);
        EPDeployment deployment;

        deployment = runtime.getDeploymentService().deploy(epCompiled);

        EPStatement statement = runtime.getDeploymentService().getStatement(deployment.getDeploymentId(), statementName);
        statement.addListener( (newData, oldData, _statement, _runtime) -> {
            String name = (String) newData[0].get("name");
            int age = (int) newData[0].get("age");
            System.out.println(String.format("%s::: Name:%s, Age: %d", statementName, name, age));
        });

        return runtime.getEventService();
    }
}
