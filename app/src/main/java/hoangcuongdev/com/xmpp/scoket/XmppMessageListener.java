package hoangcuongdev.com.xmpp.scoket;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;

import org.greenrobot.eventbus.EventBus;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smackx.delay.packet.DelayInformation;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;

import java.text.SimpleDateFormat;
import java.util.Date;

import hoangcuongdev.com.xmpp.base.MyApplication;
import hoangcuongdev.com.xmpp.model.bean.ChatItem;
import hoangcuongdev.com.xmpp.model.bean.Constants;
import hoangcuongdev.com.xmpp.model.bean.MessageEvent;
import hoangcuongdev.com.xmpp.model.bean.Room;
import hoangcuongdev.com.xmpp.model.bean.SoundData;
import hoangcuongdev.com.xmpp.model.dao.MsgDbHelper;
import hoangcuongdev.com.xmpp.model.dao.NewMsgDbHelper;
import hoangcuongdev.com.xmpp.utils.DateUtil;
import hoangcuongdev.com.xmpp.utils.FileUtil;
import hoangcuongdev.com.xmpp.utils.ImageUtil;
import hoangcuongdev.com.xmpp.utils.JsonUtil;
import hoangcuongdev.com.xmpp.utils.MyAndroidUtil;

/**
 * Created by GreenLove on 2017
 */

public class XmppMessageListener implements StanzaListener {
    @Override
    public void processPacket(Stanza stanza) throws SmackException.NotConnectedException {
        final Message nowMessage = (Message) stanza;
        if (String.valueOf( nowMessage.toXML() ).contains( "<invite" )) {
            String noti = "Bạn được mời tham gia vào nhóm" + XmppConnection.getRoomName( nowMessage.getFrom() );
            String userName = XmppConnection.getRoomName( nowMessage.getFrom() );
            String nickName = "";
            String userHead = "";
            Bitmap head = null;
            VCard vCard = XmppConnection.getInstance().getUserInfo( userName );
            if (vCard != null) {
                nickName = vCard.getField( "nickName" );
                userHead = vCard.getField( "avatar" );
                head = ImageUtil.getBitmapFromBase64String( userHead );
                if (nickName == null) {
                    nickName = userName;
                }
            }
            ChatItem msg = new ChatItem( ChatItem.NOTI, "", userName, nickName, userName, userHead, noti, DateUtil.getNow(), 0 );
            MyAndroidUtil.showNoti( ChatItem.GROUP, "group", noti, null, nickName, head );
            NewMsgDbHelper.getInstance( MyApplication.getInstance() ).saveNewMsg( userName );
            MsgDbHelper.getInstance( MyApplication.getInstance() ).saveChatMsg( msg );
            EventBus.getDefault().post( new MessageEvent( "ChatNewMsg", "" ) );
            XmppConnection.getInstance().joinMultiUserChat( Constants.USER_NAME, XmppConnection.getRoomName( nowMessage.getFrom() ), true );
        }

        Message.Type type = nowMessage.getType();
        if ((type == Message.Type.groupchat || type == Message.Type.chat) && nowMessage.getBody() != null) {
            String chatName = "";
            String userName = "";
            String nickName = "";
            String userHead = "";
            Bitmap head = null;
            int chatType = ChatItem.CHAT;
            if (type == Message.Type.groupchat) {
                chatName = XmppConnection.getRoomName( nowMessage.getFrom() );
                userName = XmppConnection.getRoomUserName( nowMessage.getFrom() );
                chatType = ChatItem.GROUP_CHAT;
                VCard vCard = XmppConnection.getInstance().getUserInfo( userName );
                if (vCard != null) {
                    nickName = vCard.getField( "nickName" );
                    userHead = vCard.getField( "avatar" );
                    head = ImageUtil.getBitmapFromBase64String( userHead );
                    if (nickName == null) {
                        nickName = chatName;
                    }
                }
            } else {
                chatName = userName = XmppConnection.getUsername( nowMessage.getFrom() );
                VCard vCard = XmppConnection.getInstance().getUserInfo( userName );
                if (vCard != null) {
                    nickName = vCard.getField( "nickName" );
                    userHead = vCard.getField( "avatar" );
                    head = ImageUtil.getBitmapFromBase64String( userHead );
                    if (nickName == null) {
                        nickName = userName;
                    }
                }
            }

            if (!userName.equals( Constants.USER_NAME )) {
                long dateString;
                DelayInformation inf = nowMessage.getExtension( "x", "jabber:x:delay" );
                if (inf == null)
                    dateString = DateUtil.getNow();
                else
                    dateString = inf.getStamp().getTime();
                //msg
                ChatItem msg = null;
                String msgBody = nowMessage.getBody();
                String subject = nowMessage.getSubject();
                if (subject.equals( Constants.SEND_SOUND )) {
                    SoundData soundData = JsonUtil.jsonToObject( msgBody, SoundData.class );
                    FileUtil.saveFileByBase64( soundData.getMsg(), soundData.getPathname() );
                }
                if (type == Message.Type.groupchat && XmppConnection.leaveRooms.contains( new Room( chatName ) )) {
                    System.out.println( "Tôi đã rời khỏi phòng chat" );
                } else if (nowMessage.getBody().contains( "[RoomChange" )) {
                    XmppConnection.getInstance().reconnect();
                } else {
                    msg = new ChatItem( chatType, subject, chatName, nickName, userName, userHead, msgBody, dateString, 0 );
                    NewMsgDbHelper.getInstance( MyApplication.getInstance().getContext() ).saveNewMsg( chatName );
                    MsgDbHelper.getInstance( MyApplication.getInstance() ).saveChatMsg( msg );
                    EventBus.getDefault().post( new MessageEvent( "ChatNewMsg", "" ) );
                    SharedPreferences preferences = MyApplication.getInstance().getSharedPreferences( "user", Context.MODE_PRIVATE );
                    SharedPreferences.Editor editor = preferences.edit();
                    Date date = new Date();
                    String dateStr = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" ).format( date );
                    editor.putString( "time", dateStr );
                    editor.commit();
                    MyAndroidUtil.showNoti( chatType, subject, msgBody, chatName, nickName, head );
                }
            }
        }
    }
}
