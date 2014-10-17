package com.kritikalerror.twittext;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class MessagesFragment extends Fragment {

    ListView messageView;
    SimpleCursorAdapter adapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_messages, container, false);
        Context viewContext = rootView.getContext();
        messageView = (ListView) rootView.findViewById(R.id.messageList);

        // Create Inbox box URI
        Uri inboxURI = Uri.parse("content://sms/inbox");

        // List required columns
        String[] reqCols = new String[] { "_id", "address", "body" };

        // Get Content Resolver object, which will deal with Content Provider
        ContentResolver cr = viewContext.getContentResolver();

        // Fetch Inbox SMS Message from Built-in Content Provider
        Cursor c = cr.query(inboxURI, reqCols, null, null, null);

        // Attached Cursor with adapter and display in listview
        adapter = new SimpleCursorAdapter(viewContext, R.layout.row_layout, c,
                new String[] { "body", "address" }, new int[] {
                R.id.text, R.id.name });
        messageView.setAdapter(adapter);
		
		return rootView;
	}

}

/*
10-14 23:58:58.153  27665-27665/com.kritikalerror.twittext E/AndroidRuntime﹕ FATAL EXCEPTION: main
    java.lang.NullPointerException
            at com.kritikalerror.twittext.MessagesFragment.onCreateView(MessagesFragment.java:26)
            at android.support.v4.app.Fragment.performCreateView(Fragment.java:1478)
            at android.support.v4.app.FragmentManagerImpl.moveToState(FragmentManager.java:927)
            at android.support.v4.app.FragmentManagerImpl.moveToState(FragmentManager.java:1104)
            at android.support.v4.app.BackStackRecord.run(BackStackRecord.java:682)
            at android.support.v4.app.FragmentManagerImpl.execPendingActions(FragmentManager.java:1460)
            at android.support.v4.app.FragmentManagerImpl.executePendingTransactions(FragmentManager.java:472)
            at android.support.v4.app.FragmentPagerAdapter.finishUpdate(FragmentPagerAdapter.java:141)
            at android.support.v4.view.ViewPager.populate(ViewPager.java:1068)
            at android.support.v4.view.ViewPager.populate(ViewPager.java:914)
            at android.support.v4.view.ViewPager.onMeasure(ViewPager.java:1436)
            at android.view.View.measure(View.java:16841)
            at android.view.ViewGroup.measureChildWithMargins(ViewGroup.java:5245)
            at android.widget.FrameLayout.onMeasure(FrameLayout.java:310)
            at android.view.View.measure(View.java:16841)
            at android.view.ViewGroup.measureChildWithMargins(ViewGroup.java:5245)
            at com.android.internal.widget.ActionBarOverlayLayout.onMeasure(ActionBarOverlayLayout.java:302)
            at android.view.View.measure(View.java:16841)
            at android.view.ViewGroup.measureChildWithMargins(ViewGroup.java:5245)
            at android.widget.FrameLayout.onMeasure(FrameLayout.java:310)
            at com.android.internal.policy.impl.PhoneWindow$DecorView.onMeasure(PhoneWindow.java:2586)
            at android.view.View.measure(View.java:16841)
            at android.view.ViewRootImpl.performMeasure(ViewRootImpl.java:2189)
            at android.view.ViewRootImpl.measureHierarchy(ViewRootImpl.java:1352)
            at android.view.ViewRootImpl.performTraversals(ViewRootImpl.java:1535)
            at android.view.ViewRootImpl.doTraversal(ViewRootImpl.java:1249)
            at android.view.ViewRootImpl$TraversalRunnable.run(ViewRootImpl.java:6364)
            at android.view.Choreographer$CallbackRecord.run(Choreographer.java:791)
            at android.view.Choreographer.doCallbacks(Choreographer.java:591)
            at android.view.Choreographer.doFrame(Choreographer.java:561)
            at android.view.Choreographer$FrameDisplayEventReceiver.run(Choreographer.java:777)
            at android.os.Handler.handleCallback(Handler.java:730)
            at android.os.Handler.dispatchMessage(Handler.java:92)
            at android.os.Looper.loop(Looper.java:137)
            at android.app.ActivityThread.main(ActivityThread.java:5455)
            at java.lang.reflect.Method.invokeNative(Native Method)
            at java.lang.reflect.Method.invoke(Method.java:525)
            at com.android.internal.os.ZygoteInit$MethodAndArgsCaller.run(ZygoteInit.java:1187)
            at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:1003)
            at dalvik.system.NativeStart.main(Native Method)

    10-14 23:58:57.593     423-1008/? E/DatabaseUtils﹕ Writing exception to parcel
    java.lang.SecurityException: Permission Denial: get/set setting for user asks to run as user -2 but is calling from user 0; this requires android.permission.INTERACT_ACROSS_USERS_FULL
            at com.android.server.am.ActivityManagerService.handleIncomingUser(ActivityManagerService.java:13090)
            at android.app.ActivityManager.handleIncomingUser(ActivityManager.java:2038)
            at com.android.providers.settings.SettingsProvider.callFromPackage(SettingsProvider.java:607)
            at android.content.ContentProvider$Transport.call(ContentProvider.java:279)
            at android.content.ContentProviderNative.onTransact(ContentProviderNative.java:273)
            at android.os.Binder.execTransact(Binder.java:388)
            at dalvik.system.NativeStart.run(Native Method)

    10-14 23:58:47.712  27497-27497/? E/EnterpriseKnoxManager﹕ Failed at EnterpriseContainerManager API getEnterpriseContainerManager
    java.lang.NoSuchFieldException: Container with Id 1 does not exists
            at com.sec.enterprise.knox.EnterpriseContainerManager.<init>(EnterpriseContainerManager.java:706)
            at com.sec.enterprise.knox.EnterpriseKnoxManager.getEnterpriseContainerManager(EnterpriseKnoxManager.java:243)
            at com.sec.knox.containeragent.core.ContainerServiceAdapter.getInstance(ContainerServiceAdapter.java:55)
            at com.sec.knox.containeragent.upgrade.knox.UpgradeInstallReceiver.onReceive(UpgradeInstallReceiver.java:36)
            at android.app.ActivityThread.handleReceiver(ActivityThread.java:2535)
            at android.app.ActivityThread.access$1600(ActivityThread.java:165)
            at android.app.ActivityThread$H.handleMessage(ActivityThread.java:1402)
            at android.os.Handler.dispatchMessage(Handler.java:99)
            at android.os.Looper.loop(Looper.java:137)
            at android.app.ActivityThread.main(ActivityThread.java:5455)
            at java.lang.reflect.Method.invokeNative(Native Method)
            at java.lang.reflect.Method.invoke(Method.java:525)
            at com.android.internal.os.ZygoteInit$MethodAndArgsCaller.run(ZygoteInit.java:1187)
            at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:1003)
            at dalvik.system.NativeStart.main(Native Method)

    10-14 23:51:45.882      423-968/? E/SQLiteDatabase﹕ Error inserting packageName=com.sec.knox.containeragent controlState=1 adminUid=1000
    android.database.sqlite.SQLiteConstraintException: Foreign Key Violation (code 19)
            at android.database.sqlite.SQLiteConnection.nativeExecuteForLastInsertedRowId(Native Method)
            at android.database.sqlite.SQLiteConnection.executeForLastInsertedRowId(SQLiteConnection.java:973)
            at android.database.sqlite.SQLiteSession.executeForLastInsertedRowId(SQLiteSession.java:788)
            at android.database.sqlite.SQLiteStatement.executeInsert(SQLiteStatement.java:86)
            at android.database.sqlite.SQLiteDatabase.insertWithOnConflict(SQLiteDatabase.java:1591)
            at android.database.sqlite.SQLiteDatabase.insert(SQLiteDatabase.java:1461)
            at com.android.server.enterprise.storage.EdmStorageHelper.putValuesForAdminAndField(EdmStorageHelper.java:1213)
            at com.android.server.enterprise.storage.EdmStorageProviderBase.putValuesForAdminAndField(EdmStorageProviderBase.java:625)
            at com.android.server.enterprise.application.ApplicationPolicy.setApplicationPkgNameControlState(ApplicationPolicy.java:725)
            at com.android.server.enterprise.application.ApplicationPolicy.setApplicationPkgNameControlState(ApplicationPolicy.java:568)
            at com.android.server.enterprise.application.ApplicationPolicy._setApplicationUninstallationDisabled(ApplicationPolicy.java:1858)
            at com.android.server.enterprise.application.ApplicationPolicy.setApplicationUninstallationDisabled(ApplicationPolicy.java:1827)
            at android.app.enterprise.IApplicationPolicy$Stub.onTransact(IApplicationPolicy.java:166)
            at android.os.Binder.execTransact(Binder.java:388)
            at dalvik.system.NativeStart.run(Native Method)
 */