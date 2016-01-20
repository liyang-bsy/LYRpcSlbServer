package net.vicp.lylab.server.action.manager;

import java.util.Date;

import net.vicp.lylab.core.model.RPCBaseAction;
import net.vicp.lylab.utils.Utils;

public class CheckRuntimeAction extends RPCBaseAction {

	@Override
	public void exec() {
		try {
			do {
				getResponse().getBody().put("当前时间", Utils.format(new Date(), "yyyy-MM-dd HH-mm:ss"));
				getResponse().success();
			} while (false);
		} catch (Exception e) {
			log.error("Exception detected:" + Utils.getStringFromException(e));
		}
	}

}
