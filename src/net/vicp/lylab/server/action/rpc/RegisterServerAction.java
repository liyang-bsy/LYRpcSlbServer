package net.vicp.lylab.server.action.rpc;

import java.util.List;

import net.vicp.lylab.core.CoreDef;
import net.vicp.lylab.core.model.RPCBaseAction;
import net.vicp.lylab.server.action.manager.SwithModificationAction;
import net.vicp.lylab.server.rpc.RpcConnector;

public class RegisterServerAction extends RPCBaseAction {

	@Override
	public void exec() {
		do {
			if (SwithModificationAction.changeable.get()) {
				getResponse().fail("Current mode is unchangeable");
				break;
			}
			String ip = clientSocket.getInetAddress().getHostAddress();
			int port = clientSocket.getLocalPort();
			@SuppressWarnings("unchecked")
			List<String> procedures = (List<String>) getRequest().getBody().get("procedures");
			String server = (String) getRequest().getBody().get("server");

			RpcConnector connector = (RpcConnector) CoreDef.config.getConfig("Singleton").getObject("connector");

			connector.addServer(server, ip, port);
			connector.addProcedures(server, procedures);

			// No sync, stand alone mode
			// TODO comunication and sync with other RPC server
			getResponse().success();
		} while (false);
	}

}
