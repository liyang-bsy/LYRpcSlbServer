package client;

import java.text.DecimalFormat;

import net.vicp.lylab.core.interfaces.Protocol;
import net.vicp.lylab.core.model.CacheMessage;
import net.vicp.lylab.core.model.Pair;
import net.vicp.lylab.core.model.SimpleHeartBeat;
import net.vicp.lylab.core.pool.AutoGeneratePool;
import net.vicp.lylab.utils.Utils;
import net.vicp.lylab.utils.atomic.AtomicInteger;
import net.vicp.lylab.utils.creator.AutoCreator;
import net.vicp.lylab.utils.creator.InstanceCreator;
import net.vicp.lylab.utils.internet.SyncSession;
import net.vicp.lylab.utils.internet.protocol.CacheMessageProtocol;
import net.vicp.lylab.utils.tq.Task;

public class RDMADirCmdHub extends Task {
	private static final long serialVersionUID = -1319408007756814179L;

	public static AtomicInteger access = new AtomicInteger(0);
	public static AtomicInteger total = new AtomicInteger(0);
	protected static final Protocol p = new CacheMessageProtocol();

	public void action() {
		SyncSession session = pool.accessOne();

		CacheMessage message = new CacheMessage();

		//--------------------------
		StringBuilder sb = new StringBuilder();
		for (int j = 0; j < 20; j++)
			for (int i = 0; i < 100; i++)
				sb.append("1234567890");
		Pair<String, byte[]> pair = new Pair<>(Utils.createUUID(), sb.toString().getBytes());
		
		message.setKey("Set");
		message.setPair(pair);
		//----------------------------
		session.send(p.encode(message));
//		CacheMessage m = (CacheMessage) 
				p.decode(session.receive().getLeft());
//		System.out.println(m);
//		System.out.println(Arrays.toString(m.getPair().getRight()));
		pool.recycle(session);
	}

	static AutoGeneratePool<SyncSession> pool;

	public static void main(String[] args) throws InterruptedException {
//		CoreDef.config.reload("C:/config.txt");
		AutoCreator<SyncSession> creator = new InstanceCreator<>(SyncSession.class
				, "10.163.100.87", 2000, p, new SimpleHeartBeat());
		pool = new AutoGeneratePool<>(creator);
//		CoreDef.config.getInteger("thread")
		for (int i = 0; i < 4; i++)
			new RDMADirCmdHub().begin();
		// 稳定以后才开始进行计算
		Integer recalcTimeInteger = 0;
		boolean recalc = true;

		for (int j = 0; j < Integer.MAX_VALUE; j += 1) {
			access.set(0);
			Thread.sleep(1000);

			if (recalc && j > 8) {
				recalcTimeInteger = j;
				recalc = false;
				total.set(0);
				System.out.println("recalc");
			}
			System.out.println("second:" + j + "\t\ttotal:" + total.get() + "\t\taverage:"
					+ new DecimalFormat("0.00").format(1.0 * total.get() / (j - recalcTimeInteger)));
			System.out.println("access:" + access.get());
		}
	}

	@Override
	public void exec() {
		while (!isStopped()) {
			try {
				action();
				access.incrementAndGet();
				total.incrementAndGet();
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}

}