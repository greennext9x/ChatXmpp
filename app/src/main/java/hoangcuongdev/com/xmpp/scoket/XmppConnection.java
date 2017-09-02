package hoangcuongdev.com.xmpp.scoket;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Handler;

import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.filter.StanzaTypeFilter;
import org.jivesoftware.smack.packet.Element;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.provider.IQProvider;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.packet.RosterPacket.ItemType;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jivesoftware.smackx.muc.DiscussionHistory;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.receipts.DeliveryReceiptRequest;
import org.jivesoftware.smackx.search.ReportedData;
import org.jivesoftware.smackx.search.ReportedData.Row;
import org.jivesoftware.smackx.search.UserSearchManager;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;
import org.jivesoftware.smackx.xdata.Form;
import org.jivesoftware.smackx.xdata.FormField;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import hoangcuongdev.com.xmpp.base.MyApplication;
import hoangcuongdev.com.xmpp.model.bean.ChatItem;
import hoangcuongdev.com.xmpp.model.bean.Constants;
import hoangcuongdev.com.xmpp.model.bean.Friend;
import hoangcuongdev.com.xmpp.model.bean.Room;
import hoangcuongdev.com.xmpp.utils.DataHelper;
import hoangcuongdev.com.xmpp.utils.ImageUtil;
import hoangcuongdev.com.xmpp.utils.PinyinComparator;
import rx.Observable;

/**
 * Created by GreenLove on 2017
 */

public class XmppConnection {
    private static XMPPTCPConnection connection;
    private static XmppConnection xmppConnection;
    private static Chat newchat;
    private static MultiUserChat mulChat;
    private static List<Friend> friendList = new ArrayList<>();
    private XmppConnecionListener connectionListener;
    private XmppMessageInterceptor xmppMessageInterceptor;
    private XmppMessageListener messageListener;
    public static List<Room> myRooms = new ArrayList<>();
    public static List<Room> leaveRooms = new ArrayList<>();

    public static XmppConnection getInstance() {
        if (xmppConnection == null) {
            synchronized (XmppConnection.class) {
                if (xmppConnection == null) {
                    xmppConnection = new XmppConnection();
                }
            }
        }
        return xmppConnection;
    }

    public void setNull() {
        connection = null;
    }


    public XMPPTCPConnection getConnection() {
        if (connection == null) {
            openConnection();
        }
        return connection;
    }


    public boolean openConnection() {
        try {
            if (null == connection || !connection.isAuthenticated()) {
                XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration.builder()
                        .setHost( Constants.SERVER_HOST )
                        .setServiceName( Constants.SERVER_NAME )
                        .setPort( Constants.SERVER_PORT )
                        .setSecurityMode( XMPPTCPConnectionConfiguration.SecurityMode.disabled )
                        .setDebuggerEnabled( true )
                        .setCompressionEnabled( false )
                        .setSendPresence( true )
                        .build();
                connection = new XMPPTCPConnection( config );
                connection.connect();

                connectionListener = new XmppConnecionListener();
                connection.addConnectionListener( connectionListener );
                xmppMessageInterceptor = new XmppMessageInterceptor();
                messageListener = new XmppMessageListener();
                connection.addPacketInterceptor( xmppMessageInterceptor, new StanzaTypeFilter( Message.class ) );
                connection.addSyncStanzaListener( messageListener, new StanzaTypeFilter( Message.class ) );
                connection.addSyncStanzaListener( new XmppPresenceListener(), new StanzaTypeFilter( Presence.class ) );
                connection.addPacketInterceptor( new XmppPresenceInterceptor(), new StanzaTypeFilter( Presence.class ) );
                ProviderManager.addIQProvider( "muc", "MZH", new MUCPacketExtensionProvider() );
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public void closeConnection() {
        if (connection != null) {
            connection.removeConnectionListener( connectionListener );
            ProviderManager.removeIQProvider( "muc", "MZH" );
            try {
                connection.disconnect();
            } catch (Exception e) {
                if (Constants.IS_DEBUG)
                    e.printStackTrace();
            } finally {
                connection = null;
                xmppConnection = null;
            }
        }
    }

    public void reconnect() {
        new Handler().postDelayed( () -> {
            closeConnection();
            login( Constants.USER_NAME, Constants.USER_PWD );
            loadFriendAndJoinRoom();
        }, 1000 );
    }

    public Observable loadFriendAndJoinRoom() {
        return Observable.from( getMyRoom() )
                .map( room ->
                        joinMultiUserChat( Constants.USER_NAME, room.name, false ) );
    }

    /**
     *
     * @param account
     * @param password
     */
    public Observable<List<Friend>> login(String account, String password) {
        return Observable.create( (Observable.OnSubscribe<Boolean>) subscriber -> {
            try {
                if (getConnection() == null) {
                    subscriber.onError( new Throwable( "Kết nối không thành công" ) );
                }
                if (!getConnection().isAuthenticated() && getConnection().isConnected()) {
                    getConnection().login( account, password );
                    Presence presence = new Presence( Presence.Type.available );
                    presence.setMode( Presence.Mode.available );
                    Constants.USER_MODE = presence.getMode().toString();
                    Constants.USER_NAME = account;
                    Constants.USER_PWD = password;
                    getConnection().sendStanza( presence );
                    subscriber.onNext( true );
                    subscriber.onCompleted();
                } else {
                    subscriber.onError( new Throwable( "Kết nối lại" ) );
                }
            } catch (Exception e) {
                e.printStackTrace();
                subscriber.onError( new Throwable( e.getMessage() ) );
            }
        } ).map( aBoolean -> {
            VCard vCard = getUserInfo( null );
            if (vCard != null) {
                Friend mfriend = new Friend();
                String username = account;
                String nikename = vCard.getField( "nickName" );
                String userheard = vCard.getField( "avatar" );
                if (nikename == null) {
                    nikename = username;
                }
                mfriend.setUsername( username );
                mfriend.setUserHead( userheard );
                mfriend.setNickname( nikename );
                DataHelper.saveDeviceData( MyApplication.getInstance(), Constants.USERINFO, mfriend );
                return vCard;
            } else {
                return new Throwable( "Không thể lấy thông tin người dùng" );
            }
        } ).flatMap( vCard -> getFriend() );
    }

    /**
     *
     * @param account
     * @param password
     * @return
     */
    public Observable<Boolean> regist(String account, String password) {
        return Observable.create( subscriber -> {
            if (getConnection() == null)
                subscriber.onError( new Throwable( "Kết nối không thành công" ) );
            else {
                try {
                    AccountManager.getInstance( getConnection() ).createAccount( account, password );
                    subscriber.onNext( true );
                    subscriber.onCompleted();
                } catch (SmackException.NoResponseException e) {
                    e.printStackTrace();
                    subscriber.onError( new Throwable( "Đăng ký không thành công" ) );
                } catch (XMPPException.XMPPErrorException e) {
                    e.printStackTrace();
                    subscriber.onError( new Throwable( "Tài khoản đã tồn tại" ) );
                } catch (SmackException.NotConnectedException e) {
                    e.printStackTrace();
                    subscriber.onError( new Throwable( "Đăng ký không thành công" ) );
                }
            }
        } );
    }

    /**
     * 修改密码
     *
     * @param pwd
     * @return
     */
    public Observable<Boolean> changPwd(String pwd) {
        return Observable.create( subscriber -> {
            try {
                AccountManager.getInstance( getConnection() ).changePassword( pwd );
                subscriber.onNext( true );
                subscriber.onCompleted();
            } catch (SmackException.NoResponseException e) {
                e.printStackTrace();
                subscriber.onError( new Throwable( "Đổi mật khẩu không thành công" ) );
            } catch (XMPPException.XMPPErrorException e) {
                e.printStackTrace();
                subscriber.onError( new Throwable( "Đổi mật khẩu không thành công" ) );
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
                subscriber.onError( new Throwable( "Đổi mật khẩu không thành công" ) );
            }
        } );
    }

    public void setRecevier(String chatName, int chatType) {
        if (getConnection() == null)
            return;
        if (chatType == ChatItem.CHAT) {
            newchat = ChatManager.getInstanceFor( getConnection() )
                    .createChat( getFullUsername( chatName ) );
        } else if (chatType == ChatItem.GROUP_CHAT) {
            mulChat = MultiUserChatManager.getInstanceFor( getConnection() )
                    .getMultiUserChat( chatName + "@conference." + getConnection().getServiceName() );
        }
    }

    /**
     *
     * @param chatname
     * @return
     */
    @SuppressLint("NewApi")
    public Observable<Boolean> sendMsg(String chatname, String suject, String msg, int chatType) {
        return Observable.create( subscriber -> {
            if (getConnection() == null)
                subscriber.onError( new Throwable( "Kết nối không thành công" ) );
            if (!msg.isEmpty()) {
                Message message = new Message();
                message.setSubject( suject );
                message.setBody( msg );
                message.setFrom( getConnection().getUser() );
                message.setTo( chatname + "@" + Constants.SERVER_NAME );
                DeliveryReceiptRequest.addTo( message );
                try {
                    if (chatType == ChatItem.CHAT) {
                        newchat.sendMessage( message );
                        subscriber.onNext( true );
                        subscriber.onCompleted();
                    } else if (chatType == ChatItem.GROUP_CHAT) {
                        mulChat.sendMessage( message );
                        subscriber.onNext( true );
                        subscriber.onCompleted();
                    }
                } catch (SmackException.NotConnectedException e) {
                    e.printStackTrace();
                    subscriber.onError( new Throwable( "Không thể gửi" ) );
                }
            }
        } );
    }

    /**
     *
     * @param key
     * @return
     */
    public Observable<List<String>> searchUser(String key) {
        return Observable.create( subscriber -> {
            List<String> userList = new ArrayList<>();
            try {
                UserSearchManager search = new UserSearchManager( getConnection() );
                Form searchForm = search.getSearchForm( "search." + Constants.SERVER_NAME );
                Form answerForm = searchForm.createAnswerForm();
                answerForm.setAnswer( "Username", true );
                answerForm.setAnswer( "search", key );
                ReportedData data = search.getSearchResults( answerForm, "search." + Constants.SERVER_NAME );
                Iterator<Row> it = data.getRows().iterator();
                Row row = null;
                while (it.hasNext()) {
                    row = it.next();
                    String username = row.getValues( "Username" ).toString();
                    userList.add( username.substring( 1, username.length() - 1 ) );
                }
                subscriber.onNext( userList );
                subscriber.onCompleted();
            } catch (Exception e) {
                subscriber.onError( new Throwable( e.getMessage() ) );
            }
        } );
    }

    /**
     *
     * @param userName id
     * @param
     */
    public Observable<Boolean> addUser(String userName) {
        return Observable.create( subscriber -> {
            if (getConnection() == null)
                subscriber.onError( new Throwable( "Kết nối không thành công" ) );
            try {
                Roster.getInstanceFor( getConnection() ).createEntry( getFullUsername( userName ), getFullUsername( userName ), null );
                subscriber.onNext( true );
                subscriber.onCompleted();
            } catch (Exception e) {
                e.printStackTrace();
                subscriber.onError( new Throwable( "Thêm bạn không thành công" ) );
            }
        } );
    }

    /**
     *
     * @param userName
     * @return
     */
    public Observable<Boolean> removeUser(String userName) {
        return Observable.create( subscriber -> {
            if (getConnection() == null)
                subscriber.onError( new Throwable( "Kết nối không thành công" ) );
            try {
                RosterEntry entry = null;
                if (userName.contains( "@" ))
                    entry = Roster.getInstanceFor( getConnection() ).getEntry( userName );
                else
                    entry = Roster.getInstanceFor( getConnection() ).getEntry( getFullUsername( userName ) );
                if (entry == null)
                    entry = Roster.getInstanceFor( getConnection() ).getEntry( userName );
                Roster.getInstanceFor( getConnection() ).removeEntry( entry );
                subscriber.onNext( true );
                subscriber.onCompleted();
            } catch (Exception e) {
                e.printStackTrace();
                subscriber.onError( new Throwable( "Xóa bạn bè không thành công" ) );
            }
        } );
    }

    /**
     *
     * @param vcard
     * @return
     */
    public Observable<Boolean> changeVcard(VCard vcard) {
        return Observable.create( subscriber -> {
            if (getConnection() == null)
                subscriber.onError( new Throwable( "Kết nối không thành công" ) );
            try {
                vcard.save( getConnection() );
                subscriber.onNext( true );
                subscriber.onCompleted();
            } catch (Exception e) {
                e.printStackTrace();
                subscriber.onError( new Throwable( "Thay đổi không thành công" ) );
            }
        } );
    }


    /**
     *
     * @param user
     * @return
     */
    public VCard getUserInfo(String user) {
        try {
            VCard vcard = new VCard();
            if (user == null) {
                vcard.load( getConnection() );
            } else {
                vcard.load( getConnection(), user + "@" + Constants.SERVER_NAME );
            }
            if (vcard != null)
                return vcard;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *
     *
     * @param user
     * @return
     */
    public Observable<Bitmap> getUserImage(String user) {
        return Observable.create( (Observable.OnSubscribe<VCard>) subscriber -> {
            try {
                VCard vcard = new VCard();
                if (user == null) {
                    vcard.load( getConnection() );
                } else {
                    vcard.load( getConnection(), user + "@" + Constants.SERVER_NAME );
                }
                if (vcard == null)
                    subscriber.onError( new Throwable( "Không có thông tin người dùng" ) );
                else {
                    subscriber.onNext( vcard );
                    subscriber.onCompleted();
                }
            } catch (Exception e) {
                e.printStackTrace();
                subscriber.onError( new Throwable( "Nhận avatar không thành công" ) );
            }
        } ).map( vCard -> {
            String avatar = vCard.getField( "avatar" );
            return ImageUtil.getBitmapFromBase64String( avatar );
        } );
    }

    /**
     *
     * @param roomName
     * @return
     */
    public MultiUserChat createRoom(String roomName) {
        if (getConnection() == null)
            return null;
        MultiUserChat muc = null;
        try {
            muc = MultiUserChatManager.getInstanceFor( getConnection() ).getMultiUserChat( roomName + "@conference."
                    + getConnection().getServiceName() );
            muc.create( roomName );
            Form form = muc.getConfigurationForm();
            Form submitForm = form.createAnswerForm();
            List<FormField> fields = form.getFields();
            for (int i = 0; fields != null && i < fields.size(); i++) {
                if (FormField.Type.hidden != fields.get( i ).getType() && fields.get( i ).getVariable() != null) {
                    submitForm.setDefaultAnswer( fields.get( i ).getVariable() );
                }
                submitForm.setAnswer( "muc#roomconfig_persistentroom", true );
                submitForm.setAnswer( "muc#roomconfig_membersonly", false );
                submitForm.setAnswer( "muc#roomconfig_allowinvites", true );
                submitForm.setAnswer( "muc#roomconfig_enablelogging", true );
                submitForm.setAnswer( "x-muc#roomconfig_reservednick", true );
                submitForm.setAnswer( "x-muc#roomconfig_canchangenick", false );
                submitForm.setAnswer( "x-muc#roomconfig_registration", false );
                muc.sendConfigurationForm( submitForm );
            }
        } catch (XMPPException | SmackException e) {
            e.printStackTrace();
            return null;
        }
        return muc;
    }

    public Observable<List<Friend>> getFriend() {
        friendList.clear();
        return Observable.just( Roster.getInstanceFor( getConnection() ).getEntries() )
                .map( entries -> {
                    List<Friend> friendsTemp = new ArrayList<>();
                    for (RosterEntry entry : entries) {
                        Friend friend = new Friend();
                        String username = getUsername( entry.getUser() );
                        ItemType type = entry.getType();
                        String nickName = "";
                        String userHead = "";
                        VCard vCard = getUserInfo( username );
                        if (vCard != null) {
                            nickName = vCard.getField( "nickName" );
                            userHead = vCard.getField( "avatar" );
                            if (nickName == null) {
                                nickName = username;
                            }
                        }
                        friend.setUserHead( userHead );
                        friend.setUsername( username );
                        friend.setNickname( nickName );
                        friend.setType( type );
                        friendsTemp.add( friend );
                    }
                    return friendsTemp;
                } )
                .map( list -> {
                    Friend[] usersArray = new Friend[list.size()];
                    for (int i = 0; i < list.size(); i++) {
                        Friend friend = new Friend();
                        friend.setUsername( list.get( i ).getUsername() );
                        friend.setNickname( list.get( i ).getNickname() );
                        friend.setType( list.get( i ).getType() );
                        friend.setUserHead( list.get( i ).getUserHead() );
                        usersArray[i] = friend;
                    }
                    Arrays.sort( usersArray, new PinyinComparator() );
                    friendList = new ArrayList<>( Arrays.asList( usersArray ) );
                    return friendList;
                } );
    }

    public List<Friend> getFriendList() {
        return friendList;
    }

    public void changeFriend(Friend friend, ItemType type) {
        getFriendList().get( getFriendList().indexOf( friend ) ).setType( type );
    }


    public List<Room> getMyRoom() {
        return myRooms;
    }

    /**
     *
     * @param user
     * @param roomsName
     * @return
     */
    public MultiUserChat joinMultiUserChat(String user, String roomsName, boolean restart) {
        if (getConnection() == null)
            return null;
        try {
            MultiUserChat muc = MultiUserChatManager.getInstanceFor( getConnection() ).getMultiUserChat( roomsName
                    + "@conference." + getConnection().getServiceName() );
            DiscussionHistory history = new DiscussionHistory();
            SimpleDateFormat sim = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
            SharedPreferences preferences = MyApplication.getInstance().getSharedPreferences( "user", Context.MODE_PRIVATE );
            String str = preferences.getString( "time", "defaultname" );
            Date d = sim.parse( str );
            history.setSince( d );
            muc.join( user, null, history, SmackConfiguration.getDefaultPacketReplyTimeout() );
            return muc;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (restart) {
                reconnect();
            }
        }
    }

    public void leaveMuc(String roomName) {
        MultiUserChat muc = MultiUserChatManager.getInstanceFor( getConnection() ).getMultiUserChat( roomName
                + "@conference." + getConnection().getServiceName() );
        try {
            muc.leave();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param fullUsername
     * @return
     */
    public static String getUsername(String fullUsername) {
        return fullUsername.split( "@" )[0];
    }

    /**
     *
     * @param username
     * @return
     */
    public static String getFullUsername(String username) {
        return username + "@" + Constants.SERVER_NAME;
    }

    /**
     *
     * @param fullRoomname
     * @return
     */
    public static String getRoomName(String fullRoomname) {
        return fullRoomname.split( "@" )[0];
    }

    /**
     *
     * @param fullRoomname
     * @return
     */
    public static String getRoomUserName(String fullRoomname) {
        return fullRoomname.split( "/" )[1];
    }

    /**
     *
     * @param roomName
     * @return
     */
    public static String getFullRoomname(String roomName) {
        return roomName + "@conference." + Constants.SERVER_NAME;
    }

    class MUCPacketExtensionProvider extends IQProvider {
        @Override
        public Element parse(XmlPullParser parser, int i) throws XmlPullParserException, IOException, SmackException {
            int eventType = parser.getEventType();
            myRooms.clear();
            leaveRooms.clear();
            Room info = null;
            while (true) {
                if (eventType == XmlPullParser.START_TAG) {
                    if ("room".equals( parser.getName() )) {
                        String roomName = parser.getAttributeValue( "", "roomName" );
                        String roomJid = parser.getAttributeValue( "", "roomJid" );
                        info = new Room();
                        info.name = roomName;
                        info.roomid = roomJid;
                        myRooms.add( info );
                    }
                    if ("friend".equals( parser.getName() )) {
                        info.friendList.add( XmppConnection.getUsername( parser.nextText() ) );
                    }
                } else if (eventType == XmlPullParser.END_TAG) {
                    if ("muc".equals( parser.getName() )) {
                        break;
                    }
                }
                eventType = parser.next();
            }
            return null;
        }
    }
}
