package hoangcuongdev.com.xmpp.model.bean;

import hoangcuongdev.com.xmpp.utils.Util;

public class Constants {
	public final static boolean IS_DEBUG = true;
	public final static String SERVER_HOST = "192.168.1.24";
	public static String SERVER_NAME = "xmppservier";
	public final static int SERVER_PORT = 5222;
	public final static String PATH =  Util.getInstance().getExtPath()+"/xmpp";
	public final static String SAVE_IMG_PATH = PATH + "/images";
	public final static String SAVE_SOUND_PATH = PATH + "/sounds";
	public final static String LOGIN_CHECK = "check";
	public final static String LOGIN_ACCOUNT = "account";
	public final static String LOGIN_PWD = "pwd";
	public final static String USERINFO = "userinfo";
	public final static String SEND_TXT = "sendtxt";
	public final static String SEND_SOUND = "sendsound";
	public final static String SEND_IMG = "sendimg";
	public final static String SEND_LOCATION = "sendlocation";

	public static String USER_NAME = "";
	public static String USER_PWD = "";
	public static String USER_MODE = "";
}
