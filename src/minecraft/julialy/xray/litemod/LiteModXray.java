package julialy.xray.litemod;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import julialy.xray.main.XrayMain;
import net.minecraft.src.Minecraft;
import net.minecraft.src.KeyBinding;
import net.minecraft.src.NetHandler;
import net.minecraft.src.Packet1Login;

import com.mumfrey.liteloader.InitCompleteListener;
import com.mumfrey.liteloader.LiteMod;
import com.mumfrey.liteloader.LoginListener;
import com.mumfrey.liteloader.core.LiteLoader;

public class LiteModXray implements LiteMod, InitCompleteListener, LoginListener {

	@Override
	public String getName() {
		return "X-Ray";
	}

	@Override
	public String getVersion() {
		return "1.5.2";
	}

	@Override
	public void init() {
		XrayMain.getXrayInstance();
	}

	@Override
	public void onTick(Minecraft var1, float var2, boolean var3, boolean var4) {
		XrayMain.getXrayInstance().onTick(var1, var2, var3, var4);
	}

	@Override
	public void onInitCompleted(Minecraft minecraft, LiteLoader loader) {
		// TODO Nothing
	}

	@Override
	public void onLogin(NetHandler netHandler, Packet1Login loginPacket) {
		XrayMain.getXrayInstance().onLoginToServer();
	}

}
